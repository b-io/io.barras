#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the connectivity utilities for databases.
########################################################################################################################

from __future__ import annotations

import unittest

from nconnect.db import *
from nutil.io.logging import configure_logging
from nutil.struct.util import to_frame
from nutil.test.unittest import Test

__DB_TEST_CASES___________________________________________________________________________ = ""


### DB CONNECTOR ###########################################


class TestDB(Test):

    def test(self):
        df = to_frame([["x", 1.0], ["y", 2.0], ["z", 3.0]], names=["A", "B"])
        self.assert_equals(
            str(get_col_types(df)),
            str({0: db.Integer(), "A": db.String(length=8000), "B": db.Float(asdecimal=True)}),
        )

        self.assert_equals(
            create_select_table_where_query("name", filtering_row={"A": 1}),
            'SELECT * FROM "dbo"."name" WHERE "A"=1;',
        )
        self.assert_equals(
            create_delete_table_query("name", filtering_row={"A": 1}),
            'DELETE FROM "dbo"."name" WHERE "A"=1;',
        )
        self.assert_equals(
            create_insert_table_query("name", ["A"], {"A": 1}),
            'INSERT INTO "dbo"."name" ("A") VALUES (1);',
        )
        self.assert_equals(
            create_update_table_query("name", ["A"], {"A": 1}),
            'UPDATE "dbo"."name" SET "A"=1 WHERE "A"=1;',
        )


__DB_TEST_RUNNERS_________________________________________________________________________ = ""


### MAIN ###################################################


def main() -> None:
    """Tests the DB utilities."""
    configure_logging(level=logging.DEBUG)
    unittest.main()


if __name__ == "__main__":
    main()
