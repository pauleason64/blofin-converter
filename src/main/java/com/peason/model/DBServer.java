package com.peason.model;

import com.google.gson.annotations.Expose;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.*;

public class DBServer {

    DataSource dataSource;
    @Expose
    String friendlyName;
    @Expose
    String shortName;
    String schema;

    boolean enabled;
    //temp storage - read from properties
    JdbcTemplate jdbcTemplate;
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    List<DBTable> availableTables = new ArrayList<>();

    //manual invocation only for testing
    @Deprecated
    public DBServer(String shortName, String friendlyName, String schema) {
        this.shortName = shortName;
        this.friendlyName = friendlyName;
        this.schema = schema;
    }

    public DBServer(DataSource dataSource) {
        super();
        this.dataSource = dataSource;
    }

    public boolean isAllowedTable(String table)  {
        return availableTables.contains(table);
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public void setAvailableTables(List<String> tableList) {

        this.availableTables = new ArrayList<>();
        tableList.forEach(table -> availableTables.add(new DBTable(table,this.schema)));
    }

    public List<DBTable> getAvailableTables() {
        return availableTables;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate=new NamedParameterJdbcTemplate(this.jdbcTemplate);
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public DBTable getTable(String tableName) {
        return availableTables.stream().filter(tn -> tn.getTableName().equalsIgnoreCase(tableName)).findFirst().get();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }

    public String tableProps() {
        return new StringBuffer("FN:").append(getFriendlyName()).append(",Tables:").append(getAvailableTables()).toString();
    }
}
