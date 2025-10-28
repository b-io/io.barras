#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

# GENERIC SECTIONED CACHE ##############################################################################################
# Goal
#   Provide a reusable, sectioned, on-disk JSON cache with atomic writes, optional periodic persistence, and
#   write policies controlled by `CachePolicy`. Keys are strings; values must be JSON-serializable.
#
# Terminology
#   • a `section` is a top-level JSON key that groups related entries (e.g., 'en', 'fr', 'de_def').
#   • a `key` is the domain identifier inside a section (e.g., a lemma string).
#   • a `value` is the JSON-serializable payload stored for a `key` (e.g., translations or a definition).
#
# Behavior
#   • namespacing by sections avoids collisions for the same `key` across heterogeneous datasets.
#   • the API selects entries by `(section, key)`; missing entries return `None`.
#   • negative caching is a caller-level choice: empty lists/strings may be stored intentionally.
#   • persistence is atomic (`os.replace`) with best-effort `fsync`; failures are treated as non-fatal (documented intent).
########################################################################################################################

from __future__ import annotations

import json
import logging
import os
from dataclasses import dataclass, field
from enum import Enum
from pathlib import Path
from typing import Any, Callable, Dict, Generic, Iterable, List, Optional, Tuple, TypeVar

## CACHING CLASSES #######################################################################

__CACHING_CLASSES___________________________________________ = ""


V = TypeVar("V")


class CachePolicy(str, Enum):
    """
    An enum that selects the cache write behavior (JSON-serializable as strings).

    Values:
        `"read_only"`: Never mutates the cache; no network writes.
        `"write_miss_only"`: Adds new keys on cache misses only (default-safe).
        `"overwrite"`: May replace existing keys.
    """

    READ_ONLY = "read_only"
    WRITE_MISS_ONLY = "write_miss_only"
    OVERWRITE = "overwrite"

    @classmethod
    def from_value(cls, value: str) -> "CachePolicy":
        """Parses a member by value, raising a uniform error on failure."""
        try:
            return cls(value)
        except ValueError as e:
            raise ValueError(f"'{value}' is not a valid value for '{cls.__name__}'") from e

    @classmethod
    def from_name(cls, name: str) -> "CachePolicy":
        """Parses a member by name, raising a uniform error on failure."""
        try:
            return cls[name]
        except KeyError as e:
            raise ValueError(f"'{name}' is not a valid name for '{cls.__name__}'") from e

    @classmethod
    def names(cls) -> List[str]:
        """Returns the list of member names."""
        return list(cls.__members__.keys())

    @classmethod
    def values(cls) -> List[str]:
        """Returns the list of member values."""
        return [m.value for m in cls]


@dataclass
class SectionedCache(Generic[V]):
    """
    A sectioned on-disk JSON cache.

    A `section` is a top-level JSON key (e.g., `"en"`, `"fr"`, `"de_def"`). Each section maps `str` keys to `V`
    values. The API selects entries by `(section, key)`. Values must be JSON-serializable.

    Args:
        path: The cache file path.
        section_names: The exact set of section names to maintain (declared contract).
        policy: The write policy (`CachePolicy`).
        persist_every: Saves every N successful `set(...)` calls when `N > 0`.
        coerce_value: An optional converter applied to raw JSON values during `load(...)`.

    Raises:
        ValueError: If the on-disk JSON is not an object of section objects.
    """

    path: Path
    section_names: Tuple[str, ...]
    policy: CachePolicy = field(default_factory=lambda: CachePolicy.WRITE_MISS_ONLY)
    persist_every: int = 0
    coerce_value: Optional[Callable[[Any], V]] = None

    _store: Dict[str, Dict[str, V]] = field(default_factory=dict, init=False, repr=False)
    _since_save: int = field(default=0, init=False, repr=False)

    ### LIFECYCLE ##########################################

    @classmethod
    def load(
        cls,
        path: Path,
        section_names: Iterable[str],
        *,
        policy: CachePolicy = CachePolicy.WRITE_MISS_ONLY,
        persist_every: int = 0,
        coerce_value: Optional[Callable[[Any], V]] = None,
    ) -> "SectionedCache[V]":
        """
        Loads the cache from `path`, returning a ready-to-use instance.

        The method tolerates unreadable or malformed files by creating an empty cache with the declared sections.

        Args:
            path: The cache file path.
            section_names: The iterable of section names to enforce (contract).
            policy: The write policy applied by default (`CachePolicy`).
            persist_every: The frequency for periodic saves (0 disables auto-save).
            coerce_value: The converter from raw JSON to `V` for values.

        Returns:
            A `SectionedCache` instance populated from disk or empty when missing/invalid.
        """
        obj: Dict[str, Any] = {}
        if path.exists() and path.stat().st_size > 0:
            try:
                obj = json.loads(path.read_text(encoding="utf-8"))
            except Exception as e:
                logging.warning("Failed to read the cache '%s': %s", path, e)

        cache = cls(
            path=path,
            section_names=tuple(section_names),
            policy=policy,
            persist_every=persist_every,
            coerce_value=coerce_value,
        )
        cache._store = cache._coerce_and_validate(obj)
        if not path.exists():
            cache.save()  # create the file eagerly to establish the sections
        return cache

    def save(self) -> None:
        """
        Saves the cache atomically to disk.

        Writes to a temporary file and swaps it into place via `os.replace`. Attempts `fsync`, but treats failures
        as non-fatal to avoid introducing runtime errors on certain filesystems.
        """
        payload = self._to_json()
        self._atomic_write_json(self.path, payload)
        self._since_save = 0

    ### QUERIES ############################################

    def get_sections(self) -> List[str]:
        """
        Returns the list of known section names.

        The method returns both declared and discovered sections (order not guaranteed).
        """
        return list(self._store.keys())

    def has(self, section: str, key: str) -> bool:
        """
        Reports whether the `section/key` exists in the cache (including negative-cached empties).
        """
        bucket = self._store.get(section)
        return bucket is not None and key in bucket

    def get(self, section: str, key: str) -> Optional[V]:
        """
        Selects the cached value for `section/key`, returning `None` on a cache miss.
        """
        bucket = self._store.get(section)
        if bucket is None:
            return None
        return bucket.get(key)

    ### MUTATIONS ##########################################

    def set(self, section: str, key: str, value: V, *, policy: Optional[CachePolicy] = None) -> bool:
        """
        Sets the `section/key` to `value` in memory while respecting the effective write policy.

        Args:
            section: The section name.
            key: The entry key.
            value: The JSON-serializable value to store.
            policy: The write policy to use for this call (defaults to the cache `policy`).

        Returns:
            `True` if the in-memory value changed, otherwise `False`.

        Raises:
            KeyError: If `section` is not part of `section_names`.
        """
        eff = policy or self.policy
        if eff == CachePolicy.READ_ONLY:
            return False

        bucket = self._require_section(section)
        if eff == CachePolicy.WRITE_MISS_ONLY and key in bucket:
            return False

        changed = (key not in bucket) or (bucket[key] != value)
        if not changed and eff != CachePolicy.OVERWRITE:
            return False

        bucket[key] = value
        self._since_save += 1
        if self.persist_every > 0 and self._since_save >= self.persist_every:
            self.save()
        return True

    def set_many(
        self,
        section: str,
        items: Iterable[Tuple[str, V]],
        *,
        policy: Optional[CachePolicy] = None,
    ) -> int:
        """
        Sets multiple `(key, value)` pairs in a single section.

        Args:
            section: The section name.
            items: The iterable of `(key, value)` pairs.
            policy: The effective write policy (defaults to the cache `policy`).

        Returns:
            The count of in-memory changes applied.
        """
        count = 0
        for k, v in items:
            if self.set(section, k, v, policy=policy):
                count += 1
        return count

    def delete(self, section: str, key: str) -> bool:
        """
        Deletes a `section/key` entry when present.

        Args:
            section: The section name.
            key: The entry key.

        Returns:
            `True` if the entry was removed, otherwise `False`.

        Raises:
            KeyError: If `section` is not part of `section_names`.
        """
        bucket = self._require_section(section)
        if key in bucket:
            del bucket[key]
            self._since_save += 1
            if self.persist_every > 0 and self._since_save >= self.persist_every:
                self.save()
            return True
        return False

    def clear_section(self, section: str) -> None:
        """
        Clears all entries in a declared `section`.

        Args:
            section: The section name to clear.

        Raises:
            KeyError: If `section` is not part of `section_names`.
        """
        bucket = self._require_section(section)
        bucket.clear()
        self._since_save += 1
        if self.persist_every > 0 and self._since_save >= self.persist_every:
            self.save()

    ### INTERNALS ##########################################

    def _require_section(self, section: str) -> Dict[str, V]:
        """
        Selects the mutable bucket for `section`, creating it if missing, and validating the section contract.
        """
        if section not in self.section_names:
            raise KeyError(f"'{section}' is not a valid name for '{self.__class__.__name__}.section_names'")
        if section not in self._store:
            self._store[section] = {}
        return self._store[section]

    def _coerce_and_validate(self, obj: Dict[str, Any]) -> Dict[str, Dict[str, V]]:
        """
        Coerces and validates the parsed JSON object into the internal store structure.
        """
        out: Dict[str, Dict[str, V]] = {name: {} for name in self.section_names}
        if not isinstance(obj, dict):
            return out

        # Preserve unknown sections when they are dict-like; this is non-destructive and future-proof
        for sec, raw_map in obj.items():
            if not isinstance(raw_map, dict):
                continue
            bucket: Dict[str, V] = {}
            for k, raw_v in raw_map.items():
                try:
                    key = str(k)
                except Exception:
                    continue
                v = self.coerce_value(raw_v) if self.coerce_value else raw_v
                bucket[key] = v
            out[sec] = bucket

        # Ensure all the declared sections exist
        for name in self.section_names:
            out.setdefault(name, {})
        return out

    def _to_json(self) -> Dict[str, Dict[str, V]]:
        """
        Serializes the in-memory store to a JSON-serializable object.
        """
        return {sec: dict(bucket) for sec, bucket in self._store.items()}

    @staticmethod
    def _atomic_write_json(path: Path, payload: Dict[str, Any]) -> None:
        """
        Saves `payload` to `path` atomically using `os.replace`, attempting `fsync` (best-effort).
        """
        path.parent.mkdir(parents=True, exist_ok=True)
        temp_path = path.with_suffix(path.suffix + ".tmp")
        with temp_path.open("w", encoding="utf-8") as fh:
            json.dump(payload, fh, ensure_ascii=False)
            try:
                fh.flush()
                os.fsync(fh.fileno())
            except Exception:
                # Treat the `fsync` failures as non-fatal
                pass
        os.replace(temp_path, path)


## CONVENIENCE FUNCTIONS #################################################################


def load_sectioned_cache(
    path: Path,
    section_names: Iterable[str],
    *,
    policy: CachePolicy = CachePolicy.WRITE_MISS_ONLY,
    persist_every: int = 0,
    coerce_value: Optional[Callable[[Any], V]] = None,
) -> SectionedCache[V]:
    """
    Loads a `SectionedCache` from disk or creates an empty one with the declared sections.

    Args:
        path: The cache file path.
        section_names: The iterable of section names to enforce (contract).
        policy: The default write policy for the cache.
        persist_every: The frequency for periodic saves (0 disables auto-save).
        coerce_value: The converter from raw JSON to `V` for values.

    Returns:
        A ready-to-use `SectionedCache`.
    """
    return SectionedCache[V].load(
        path,
        section_names,
        policy=policy,
        persist_every=persist_every,
        coerce_value=coerce_value,
    )


def save_sectioned_cache(cache: SectionedCache[Any]) -> None:
    """Saves a `SectionedCache` atomically to its configured path."""
    cache.save()
