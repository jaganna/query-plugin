package org.jenkinsci.plugins.dumper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static java.lang.String.valueOf;

class Database {

    private static final String SEPARATOR = "|";
    private static final String SPACE = " ";

    private final DataSource dataSource;

    Database(JdbcConnectionDetails jdbcConnectionDetails) {
        dataSource = dataSource(jdbcConnectionDetails);
        checkConnection();
    }

    private void checkConnection() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void queryFor(String sql, PrintStream output){


        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        if (!results.isEmpty()) {
            output.println(createRowString(results.get(0).keySet()));
        }

        for (Map<String, Object> result : results) {
            output.println(createRowString(asStringList(result.values())));
        }
    }

    private static Set<String> asStringList(Collection<Object> objects) {
        Set<String> result = new LinkedHashSet<>(objects.size());
        for (Object object : objects) {
            result.add(valueOf(object));
        }
        return result;
    }

    private String createRowString(Set<String> values) {
        StringBuilder header = new StringBuilder();
        header.append(SEPARATOR);
        for (String key : values) {
            header.append(SEPARATOR);
            header.append(SPACE).append(key).append(SPACE);
        }
        header.append(SEPARATOR).append(SEPARATOR);
        return header.toString();
    }

    public DataSource dataSource(JdbcConnectionDetails jdbcConnectionDetails)
    {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(jdbcConnectionDetails.getJdbcDriver());
        dataSource.setUrl(jdbcConnectionDetails.getJdbcUrl());
        dataSource.setUsername(jdbcConnectionDetails.getJdbcUser());
        dataSource.setPassword(jdbcConnectionDetails.getJdbcPassword());
        return dataSource;
    }
}
