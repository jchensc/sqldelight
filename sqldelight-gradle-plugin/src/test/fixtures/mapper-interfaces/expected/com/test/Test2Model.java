package com.test;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.sample.Test1Model;
import com.squareup.sqldelight.RowMapper;
import com.squareup.sqldelight.SqlDelightQuery;
import com.squareup.sqldelight.internal.TableSet;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

public interface Test2Model {
  String TABLE_NAME = "test2";

  String _ID = "_id";

  String CREATE_TABLE = ""
      + "CREATE TABLE test2 (\n"
      + "  _id INTEGER PRIMARY KEY AUTOINCREMENT\n"
      + ")";

  @Nullable
  Long _id();

  interface Join_tablesModel<T1 extends Test2Model, T2 extends Test1Model> {
    @NonNull
    T1 test2();

    @NonNull
    T2 test1();
  }

  interface Join_tablesCreator<T1 extends Test2Model, T2 extends Test1Model, T extends Join_tablesModel<T1, T2>> {
    T create(@NonNull T1 test2, @NonNull T2 test1);
  }

  final class Join_tablesMapper<T1 extends Test2Model, T2 extends Test1Model, T extends Join_tablesModel<T1, T2>> implements RowMapper<T> {
    private final Join_tablesCreator<T1, T2, T> creator;

    private final Factory<T1> test2ModelFactory;

    private final Test1Model.Factory<T2> test1ModelFactory;

    public Join_tablesMapper(Join_tablesCreator<T1, T2, T> creator, Factory<T1> test2ModelFactory,
        Test1Model.Factory<T2> test1ModelFactory) {
      this.creator = creator;
      this.test2ModelFactory = test2ModelFactory;
      this.test1ModelFactory = test1ModelFactory;
    }

    @Override
    @NonNull
    public T map(@NonNull Cursor cursor) {
      return creator.create(
          test2ModelFactory.creator.create(
              cursor.isNull(0) ? null : cursor.getLong(0)
          ),
          test1ModelFactory.creator.create(
              cursor.isNull(1) ? null : cursor.getLong(1),
              cursor.isNull(2) ? null : test1ModelFactory.dateAdapter.decode(cursor.getString(2))
          )
      );
    }
  }

  interface Creator<T extends Test2Model> {
    T create(@Nullable Long _id);
  }

  final class Mapper<T extends Test2Model> implements RowMapper<T> {
    private final Factory<T> test2ModelFactory;

    public Mapper(Factory<T> test2ModelFactory) {
      this.test2ModelFactory = test2ModelFactory;
    }

    @Override
    public T map(@NonNull Cursor cursor) {
      return test2ModelFactory.creator.create(
          cursor.isNull(0) ? null : cursor.getLong(0)
      );
    }
  }

  final class Factory<T extends Test2Model> {
    public final Creator<T> creator;

    public Factory(Creator<T> creator) {
      this.creator = creator;
    }

    public SqlDelightQuery join_tables() {
      return new SqlDelightQuery(""
          + "SELECT *\n"
          + "FROM test2\n"
          + "JOIN test1",
          new TableSet("test2", "test1"));
    }

    public <T2 extends Test1Model, R extends Join_tablesModel<T, T2>> Join_tablesMapper<T, T2, R> join_tablesMapper(
        Join_tablesCreator<T, T2, R> creator, Test1Model.Factory<T2> test1ModelFactory) {
      return new Join_tablesMapper<T, T2, R>(creator, this, test1ModelFactory);
    }
  }
}
