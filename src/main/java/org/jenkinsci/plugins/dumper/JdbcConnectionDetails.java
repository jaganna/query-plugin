package org.jenkinsci.plugins.dumper;

import com.google.common.base.Objects;

class JdbcConnectionDetails {
    private final String jdbcDriver;
    private final String jdbcUrl;
    private final String jdbcUser;
    private final String jdbcPassword;

    public JdbcConnectionDetails(String jdbcDriver, String jdbcUrl, String jdbcUser, String jdbcPassword) {
        this.jdbcDriver = jdbcDriver;
        this.jdbcUrl = jdbcUrl;
        this.jdbcUser = jdbcUser;
        this.jdbcPassword = jdbcPassword;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("jdbcDriver", jdbcDriver)
                .add("jdbcUrl", jdbcUrl)
                .add("jdbcUser", jdbcUser)
                .add("jdbcPassword", jdbcPassword)
                .toString();
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getJdbcUser() {
        return jdbcUser;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }
}
