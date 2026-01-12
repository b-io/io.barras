#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the tooling utilities (style config loader, style checker, and style fixer).
########################################################################################################################

from __future__ import annotations

import re
from pathlib import Path
from typing import List

from ntool.style.common import load_yaml_config, StyleRule
from ntool.style.style_checker import scan_file
from ntool.style.style_fixer import (
    fix_hash_banner_length,
    fix_inline_comment_lowercase,
    fix_line_comment_capitalized,
    fix_line_comment_trailing_period,
    fix_underscore_banner_length,
)

__STYLE_TEST_CASES________________________________________________________________________ = ""


__COMMON_STYLE_TEST_CASES___________________________________ = ""


def test_load_yaml_config_merges_excludes_and_prunes(tmp_path: Path) -> None:
    yaml_text = """
include: [ "**/*.py" ]
exclude: [ ".venv/**", "dist/**" ]
rules:
  - id: "no-tabs"
    description: "Tabs are forbidden."
    pattern: "\\\\t"
    include: [ "**/*.py" ]
    exclude: [ ]
    flags: [ ]
    severity: "error"
  - id: "case-insensitive-foo"
    description: "Flags are compiled."
    pattern: "foo"
    include: [ "**/*.py" ]
    exclude: [ ]
    flags: [ "IGNORECASE" ]
    severity: "warning"
"""
    cfg_path = _write(tmp_path, "STYLE.yml", yaml_text)

    # Pass a custom default exclude list so the merge behavior is deterministic in this test
    cfg = load_yaml_config(cfg_path, exclude=["build/**"])

    assert cfg.include == ["**/*.py"]
    assert cfg.exclude[:3] == ["build/**", ".venv/**", "dist/**"]
    assert ".venv" in cfg.prune_names

    assert [r.id for r in cfg.rules] == ["no-tabs", "case-insensitive-foo"]
    assert (cfg.rules[1].pattern.flags & re.IGNORECASE) != 0
    assert cfg.rules[1].flags == ["IGNORECASE"]


__STYLE_CHECKER_TEST_CASES__________________________________ = ""


def test_scan_file_line_rule_reports_line_number(tmp_path: Path) -> None:
    path = _write(tmp_path, "a.py", "ok\nbad \nend\n")

    rules: List[StyleRule] = [
        _rule("trailing-whitespace", r"[ \t]+$"),
    ]
    violations = scan_file(path, rules)

    assert len(violations) == 1
    rule, line_no, line_text = violations[0]
    assert rule.id == "trailing-whitespace"
    assert line_no == 2
    assert line_text == "bad "


def test_scan_file_multiline_rule_reports_last_line(tmp_path: Path) -> None:
    path = _write(tmp_path, "a.py", "BEGIN\nmiddle\nBAD\nend\n")

    rules: List[StyleRule] = [
        _rule("block-bad", r"(?s)BEGIN\n.*BAD"),
    ]
    violations = scan_file(path, rules)

    assert len(violations) == 1
    rule, line_no, line_text = violations[0]
    assert rule.id == "block-bad"
    assert line_no == 3
    assert line_text == "BAD"


__STYLE_FIXER_TEST_CASES____________________________________ = ""


def test_fix_hash_banner_length_pads_to_target_length() -> None:
    rule = _rule("hash-banner-length", r".*")

    line = "## Quickstart ####\n"
    fixed, changed = fix_hash_banner_length(line, rule)

    assert changed is True
    assert fixed.startswith("## Quickstart ")
    assert len(fixed.rstrip("\n")) == 90


def test_fix_hash_banner_length_trims_only_hash_overflow() -> None:
    rule = _rule("hash-banner-length", r".*")

    # Intentionally too long, with only trailing '#' overflow beyond the target (60)
    line = "### Title " + ("#" * 80) + "\n"
    fixed, changed = fix_hash_banner_length(line, rule)

    assert changed is True
    assert fixed.endswith("\n")
    assert len(fixed.rstrip("\n")) == 60


def test_fix_underscore_banner_length_normalizes_name_and_snaps_length() -> None:
    rule = _rule("underscore-banner-length", r".*")

    line = '__some__name______________________________ = ""\n'
    fixed, changed = fix_underscore_banner_length(line, rule)

    assert changed is True
    assert fixed.endswith("\n")
    assert fixed.rstrip("\n").endswith(' = ""')
    assert fixed.startswith("__SOME_NAME")
    assert len(fixed.rstrip("\n")) in (35, 65, 95, 125)


def test_fix_line_comment_capitalized() -> None:
    rule = _rule("line-comment-capitalized", r".*")

    fixed, changed = fix_line_comment_capitalized("# hello world\n", rule)
    assert changed is True
    assert fixed == "# Hello world\n"


def test_fix_inline_comment_lowercase() -> None:
    rule = _rule("inline-comment-lowercase", r".*")

    fixed, changed = fix_inline_comment_lowercase("x = 1  # Hello\n", rule)
    assert changed is True
    assert fixed == "x = 1  # hello\n"


def test_fix_line_comment_trailing_period() -> None:
    rule = _rule("line-comment-trailing-period", r".*")

    fixed, changed = fix_line_comment_trailing_period("# Hello.\n", rule)
    assert changed is True
    assert fixed == "# Hello\n"


#### HELPERS ###############################################


def _rule(rule_id: str, pattern: str) -> StyleRule:
    return StyleRule(
        id=rule_id,
        description="test",
        pattern=re.compile(pattern),
        include=["**/*"],
        exclude=[],
        flags=[],
        severity="warning",
    )


def _write(tmp_path: Path, rel: str, text: str) -> Path:
    path = tmp_path / rel
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text, encoding="utf-8")
    return path
