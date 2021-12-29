package com.scraperservice.app.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class ConfigRunDAO {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public ConfigRunDAO(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int getConfigIdByHash(String hash) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT config_id FROM run WHERE hash='?'", hash);
        if(sqlRowSet.next())
            return sqlRowSet.getInt("config_id");
        return -1;
    }
}
