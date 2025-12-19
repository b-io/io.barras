#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide formatting utilities for HTML.
########################################################################################################################

from __future__ import annotations

from typing import Container, List, Optional

from bs4 import NavigableString, Tag

__HTML_PROCESSORS_________________________________________________________________________ = ""


### HTML TAGS ##############################################


def find_tags(
    container: Tag,
    from_delimiters: Optional[Container[str]] = None,
    to_delimiters: Optional[Container[str]] = None,
    tag_names: Optional[Container[str]] = None,
    tag_classes: Optional[Container[str]] = None,
) -> List[Tag]:
    """
    Returns the tags in document order that fall between the optional text delimiters.

    Behavior:
        • Iterates all descendants of `container`.
        • Starts collecting once any `from_delimiters` substring appears in a text node
          (or from the start when `from_delimiters` is `None`).
        • Stops collecting once any `to_delimiters` substring appears in a text node.
        • Filters tags by `tag_names` (HTML names) and/or `tag_classes` (CSS classes).

    Args:
        container: The root tag whose descendants are inspected.
        from_delimiters: Optional substrings that mark the start of the collection range.
        to_delimiters: Optional substrings that mark the end of the collection range.
        tag_names: Optional set of tag names to keep (e.g., `{"a", "span"}`).
        tag_classes: Optional set of CSS classes to keep.

    Returns:
        The matching tags in document order between the delimiters.
    """
    tags: List[Tag] = []
    in_range: bool = not from_delimiters
    for tag in container.descendants:
        if isinstance(tag, NavigableString):
            if not in_range and (not from_delimiters or any(d in tag for d in from_delimiters)):
                # Once `from_delimiters` is reached, start collecting matching tags
                in_range = True
            elif in_range and to_delimiters and any(d in tag for d in to_delimiters):
                # Once `to_delimiters` is reached, stop collecting matching tags
                in_range = False
        elif (
            in_range
            and isinstance(tag, Tag)
            and (not tag_names or tag.name in tag_names)
            and (not tag_classes or any(c in tag_classes for c in get_tag_classes(tag)))
        ):
            tags.append(tag)
    return tags


def tags_to_values(tags: List[Tag]) -> List[str]:
    """Returns the stripped text content (`.get_text(strip=True)`) for each tag."""
    return [tag.get_text(strip=True) for tag in tags]


def get_tag_classes(container: Tag) -> List[str]:
    """Returns the `"class"` attribute of the tag as a list of strings (empty list when absent)."""
    classes = container.get("class", [])
    if isinstance(classes, str):
        return [classes]
    return [str(c) for c in classes]
