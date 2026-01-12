#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the connectivity utilities for databases.
########################################################################################################################

from __future__ import annotations

from sqlalchemy import event
from sqlalchemy.pool import StaticPool

from nconnect.db import *
from nutil.struct.util import to_frame
from nutil.test.unittest import Test

__DB_TEST_CASES___________________________________________________________________________ = ""


### DB CONNECTOR ###########################################


class TestDB(Test):

    TABLE = "t"

    @classmethod
    def setUpClass(cls) -> None:
        # In-memory DB shared across connections (StaticPool) → avoids Windows file locking issues
        cls._engine = db.create_engine(
            "sqlite+pysqlite:///:memory:",
            connect_args={"check_same_thread": False},
            poolclass=StaticPool,
        )

    @classmethod
    def tearDownClass(cls) -> None:
        cls._engine.dispose()

    def setUp(self) -> None:
        self._reset_table(rows=5)

    def _reset_table(self, *, rows: int = 5) -> None:
        df = self._make_df(rows)
        create_table(
            self._engine,
            df,
            self.TABLE,
            replace=True,
            chunk_size=None,
            index=False,
            schema=None,
        )

    @staticmethod
    def _make_df(n: int) -> pd.DataFrame:
        return to_frame([[f"k{i:03d}", float(i)] for i in range(n)], names=["A", "B"])

    @staticmethod
    def _sort_df(df: pd.DataFrame) -> pd.DataFrame:
        return df.sort_values(["A"]).reset_index(drop=True)

    def test_builders(self) -> None:
        df = to_frame([["x", 1.0], ["y", 2.0], ["z", 3.0]], names=["A", "B"])
        self.assert_equals(
            str(get_col_types(df)),
            str({0: db.Integer(), "A": db.String(length=8000), "B": db.Float(asdecimal=True)}),
        )

        self.assert_equals(
            build_select_table_where_query("name", filtering_row={"A": 1}, schema="dbo"),
            'SELECT * FROM "dbo"."name" WHERE "A"=1;',
        )
        self.assert_equals(
            build_delete_table_query("name", filtering_row={"A": 1}, schema="dbo"),
            'DELETE FROM "dbo"."name" WHERE "A"=1;',
        )
        self.assert_equals(
            build_insert_table_query("name", ["A"], {"A": 1}, schema="dbo"),
            'INSERT INTO "dbo"."name" ("A") VALUES (1);',
        )
        self.assert_equals(
            build_update_table_query("name", ["A"], {"A": 1}, schema="dbo"),
            'UPDATE "dbo"."name" SET "A"=1 WHERE "A"=1;',
        )

    def test_create_and_select(self) -> None:
        df = self._make_df(7)
        create_table(
            self._engine,
            df,
            self.TABLE,
            replace=True,
            chunk_size=None,
            index=False,
            schema=None,
        )
        got = select_table(self._engine, self.TABLE, chunk_size=None, index=False, schema=None, verbose=False)
        self.assert_equals(self._sort_df(got).to_dict(orient="list"), self._sort_df(df).to_dict(orient="list"))

    def test_select_table_where(self) -> None:
        got = select_table_where(
            self._engine,
            self.TABLE,
            chunk_size=None,
            cols=["A", "B"],
            filtering_cols=["A"],
            filtering_row={"A": "k002"},
            index=False,
            is_mssql=False,
            schema=None,
            verbose=False,
        )
        self.assert_equals(len(got), 1)
        self.assert_equals(got.iloc[0]["A"], "k002")
        self.assert_equals(float(got.iloc[0]["B"]), 2.0)

    def test_insert_and_bulk_insert(self) -> None:
        df_new = to_frame([["k999", 999.0], ["k998", 998.0]], names=["A", "B"])

        n1 = insert_table(
            self._engine,
            df_new,
            self.TABLE,
            index=False,
            is_mssql=False,
            schema=None,
            test=True,
            verbose=False,
        )
        self.assert_equals(n1, 2)

        # SQLite DB-API often rejects multi-statement execute; keep chunk_size=1
        df_more = to_frame([[f"k{i:03d}", float(i)] for i in range(1000, 1010)], names=["A", "B"])
        n2 = bulk_insert_table(
            self._engine,
            df_more,
            self.TABLE,
            chunk_size=1,
            index=False,
            is_mssql=False,
            schema=None,
            test=True,
            verbose=False,
        )
        self.assert_equals(n2, len(df_more))

        got = select_table(self._engine, self.TABLE, chunk_size=None, index=False, schema=None, verbose=False)
        self.assert_equals(len(got), 5 + len(df_new) + len(df_more))

    def test_update_and_bulk_update(self) -> None:
        df_update = to_frame([["k001", 101.0], ["k003", 103.0]], names=["A", "B"])

        n1 = update_table(
            self._engine,
            df_update,
            self.TABLE,
            filtering_cols=["A"],
            index=False,
            is_mssql=False,
            schema=None,
            test=True,
            verbose=False,
        )
        self.assert_equals(n1, 2)

        got = select_table_where(
            self._engine,
            self.TABLE,
            chunk_size=None,
            cols=["A", "B"],
            filtering_cols=["A"],
            filtering_row={"A": ["k001", "k003"]},
            index=False,
            is_mssql=False,
            schema=None,
            verbose=False,
        )
        got = self._sort_df(got)
        self.assert_equals(got["A"].tolist(), ["k001", "k003"])
        self.assert_equals([float(x) for x in got["B"].tolist()], [101.0, 103.0])

        # SQLite: chunk_size=1 to avoid multi-statement execute
        df_bulk = to_frame([[f"k{i:03d}", float(i) + 1000.0] for i in range(5)], names=["A", "B"])
        n2 = bulk_update_table(
            self._engine,
            df_bulk,
            self.TABLE,
            chunk_size=1,
            filtering_cols=["A"],
            index=False,
            is_mssql=False,
            schema=None,
            test=True,
            verbose=False,
        )
        self.assert_equals(n2, len(df_bulk))

    def test_delete_and_bulk_delete(self) -> None:
        df_del = to_frame([["k000"], ["k004"]], names=["A"])
        n1 = delete_table(
            self._engine,
            df_del,
            self.TABLE,
            filtering_cols=["A"],
            index=False,
            is_mssql=False,
            schema=None,
            test=True,
            verbose=False,
        )
        self.assert_equals(n1, 2)

        got = select_table(self._engine, self.TABLE, chunk_size=None, index=False, schema=None, verbose=False)
        self.assert_equals(set(got["A"].tolist()), {"k001", "k002", "k003"})

        # SQLite: chunk_size=1 to avoid multi-statement execute
        df_bulk_del = to_frame([["k001"], ["k003"]], names=["A"])
        n2 = bulk_delete_table(
            self._engine,
            df_bulk_del,
            self.TABLE,
            chunk_size=1,
            filtering_cols=["A"],
            index=False,
            is_mssql=False,
            schema=None,
            test=True,
            verbose=False,
        )
        self.assert_equals(n2, 2)

        got2 = select_table(self._engine, self.TABLE, chunk_size=None, index=False, schema=None, verbose=False)
        self.assert_equals(set(got2["A"].tolist()), {"k002"})

    def test_upsert(self) -> None:
        df = to_frame([["k002", 222.0], ["k777", 777.0]], names=["A", "B"])
        n = upsert_table(
            self._engine,
            df,
            self.TABLE,
            filtering_cols=["A"],
            index=False,
            is_mssql=False,
            schema=None,
            test=True,
            verbose=False,
        )
        self.assert_equals(n, 2)

        got = select_table_where(
            self._engine,
            self.TABLE,
            chunk_size=None,
            cols=["A", "B"],
            filtering_cols=["A"],
            filtering_row={"A": ["k002", "k777"]},
            index=False,
            is_mssql=False,
            schema=None,
            verbose=False,
        )
        got = self._sort_df(got)
        self.assert_equals(got["A"].tolist(), ["k002", "k777"])
        self.assert_equals([float(x) for x in got["B"].tolist()], [222.0, 777.0])

    def test_migrate_sqlite_to_sqlite(self) -> None:
        src = self._engine
        dst = db.create_engine(
            "sqlite+pysqlite:///:memory:",
            connect_args={"check_same_thread": False},
            poolclass=StaticPool,
        )
        try:
            n = migrate(
                src,
                dst,
                [self.TABLE],
                chunk_size=2,
                create=True,
                drop=True,
                fill=True,
                is_mssql_from=False,
                is_mssql_to=False,
                schema=None,
                upsert=False,
                test=True,
                verbose=False,
            )
            self.assert_equals(n, 5)

            df_src = select_table(src, self.TABLE, chunk_size=None, schema=None, verbose=False)
            df_dst = select_table(dst, self.TABLE, chunk_size=None, schema=None, verbose=False)
            self.assert_equals(
                self._sort_df(df_dst).to_dict(orient="list"),
                self._sort_df(df_src).to_dict(orient="list"),
            )
        finally:
            dst.dispose()

    def test_efficiency_single_connection_delete_table(self) -> None:
        self._reset_table(rows=60)

        counter: Dict[str, int] = {"checkout": 0}

        def _on_checkout(dbapi_connection: Any, connection_record: Any, connection_proxy: Any) -> None:
            counter["checkout"] += 1

        event.listen(self._engine.pool, "checkout", _on_checkout)
        try:
            df_del = to_frame([[f"k{i:03d}"] for i in range(60)], names=["A"])
            n = delete_table(
                self._engine,
                df_del,
                self.TABLE,
                filtering_cols=["A"],
                index=False,
                is_mssql=False,
                schema=None,
                test=False,
                verbose=False,
            )
            self.assert_equals(n, 60)

            # If delete_table opens a new transaction/connection per row, this will blow up
            self.assert_true(counter["checkout"] <= 10)
        finally:
            event.remove(self._engine.pool, "checkout", _on_checkout)

    def test_efficiency_single_connection_update_table(self) -> None:
        self._reset_table(rows=60)

        counter: Dict[str, int] = {"checkout": 0}

        def _on_checkout(dbapi_connection: Any, connection_record: Any, connection_proxy: Any) -> None:
            counter["checkout"] += 1

        event.listen(self._engine.pool, "checkout", _on_checkout)
        try:
            df_upd = to_frame([[f"k{i:03d}", float(i) + 10000.0] for i in range(60)], names=["A", "B"])
            n = update_table(
                self._engine,
                df_upd,
                self.TABLE,
                filtering_cols=["A"],
                index=False,
                is_mssql=False,
                schema=None,
                test=False,
                verbose=False,
            )
            self.assert_equals(n, 60)
            self.assert_true(counter["checkout"] <= 10)
        finally:
            event.remove(self._engine.pool, "checkout", _on_checkout)
