package org.jenkinsci.plugins.dumper;

import org.junit.Test;

public class DatabaseTest {

    private static final JdbcConnectionDetails JDBC_DETAILS = new JdbcConnectionDetails(
            "com.mysql.jdbc.Driver",
            "jdbc:mysql://localhost:3306/test",
            "root",
            "");
    public static final String TEST_SQL_QUERY = "select * from tab";

    @Test
    public void shouldConnectToDb() {
        new Database(JDBC_DETAILS).queryFor(TEST_SQL_QUERY, System.out);
    }
}
