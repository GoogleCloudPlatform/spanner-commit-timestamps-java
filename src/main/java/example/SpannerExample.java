// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package example;

import java.sql.*;

public class SpannerExample {
  static final String JDBC_DRIVER = "com.google.cloud.spanner.jdbc.JdbcDriver";
  static final String DB_URL_TMPL =
      "jdbc:cloudspanner:/projects/%s/instances/%s/databases/%s;credentials=%s";

  public static void main(String[] args) throws Exception {
    String DB_URL =
        String.format(
            DB_URL_TMPL,
            System.getenv("PROJECT_ID"),
            System.getenv("INSTANCE_ID"),
            System.getenv("DB_NAME"),
            System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));

    Class.forName(JDBC_DRIVER);

    System.out.println("Connecting to Cloud Spanner...");

    try (Connection conn = DriverManager.getConnection(DB_URL)) {
      conn.setAutoCommit(false);
      doPrepare(conn);
      doInsert(conn);
      doUpdate(conn);
      doDelete(conn);
    }

    System.out.println(System.lineSeparator() + "Goodbye!");
  }

  private static void doPrepare(Connection conn) throws Exception {
    //   Delete all existing data from
    // the two tables.
    System.out.println("Prepare...");
    try (Statement stmt = conn.createStatement()) {
      stmt.executeUpdate("delete from msgs_history where true");
      stmt.executeUpdate("delete from msgs where true");
      conn.commit();
    }
  }

  private static void doInsert(Connection conn) throws Exception {

    try (Statement stmt = conn.createStatement()) {
      // [START cloud_spanner_commit_timestamp_example_insert]
      //   The insert should be split into 2 steps: insert to the history
      // and insert to the fact table

      System.out.println("Insert...");
      // insert to history
      stmt.executeUpdate(
          "insert into msgs_history (id, previous_msg, ts)"
              + " values (1, Null, PENDING_COMMIT_TIMESTAMP())");
      // insert to real table
      stmt.executeUpdate("insert into msgs (id, msg)" + " values (1, 'a real msg')");
      conn.commit();
      // [END cloud_spanner_commit_timestamp_example_insert]
    }
  }

  private static void doUpdate(Connection conn) throws Exception {
    try (Statement stmt = conn.createStatement()) {
      // [START cloud_spanner_commit_timestamp_example_update]
      //   The update should be split into 2 steps: insert to the history
      // and update the fact table

      System.out.println("Update...");
      // insert  to history
      stmt.executeUpdate(
          "insert msgs_history (id, previous_msg, ts)"
              + " values  ((select id from msgs where id =1),"
              + " (select msg from msgs where id = 1), PENDING_COMMIT_TIMESTAMP())");
      // update to fact table
      stmt.executeUpdate("update msgs set msg = 'new message' where id = 1");
      conn.commit();
      // [END cloud_spanner_commit_timestamp_example_update]
    }
  }

  private static void doDelete(Connection conn) throws Exception {
    try (Statement stmt = conn.createStatement()) {
      // [START cloud_spanner_commit_timestamp_example_delete]
      //   The delete should be split into 2 steps: insert to the history
      // and delete to the fact table

      System.out.println("Delete...");
      // insert  to history
      stmt.executeUpdate(
          "insert msgs_history (id, previous_msg, ts)"
              + " values  ((select id from msgs where id =1),"
              + " (select msg from msgs where id = 1), PENDING_COMMIT_TIMESTAMP()) ");
      // delete to real table
      stmt.executeUpdate("delete from msgs where id = 1");
      conn.commit();
      // [END cloud_spanner_commit_timestamp_example_delete]
    }
  }
}
