package com.peason.services;

import com.peason.databasetables.SOURCES;
import com.peason.databasetables.USERPROFILE;
import com.peason.krakenhandler.data.Ledger;
import com.peason.model.*;
import com.peason.utils.DateUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.sql.*;
import java.util.*;

@Component
public class DAO implements ApplicationContextAware {

    public JdbcTemplate jdbcTemplate;
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    public List<Map<String,Object>> sources;
    public static final String DBNAME = "crypto_trans";

    private static final String TS_CONVERT="TO_TIMESTAMP('xxx', 'YYYY-MM-DD HH24:MI:SS.FF'";


    AppConfig appConfig;

    @Autowired
    @Qualifier(DBNAME)
    DBServer dbServer;

    static ApplicationContext context;

    @PostConstruct
    public void init() {
        appConfig=context.getBean(AppConfig.class);
        jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dbServer.getDataSource());
        dbServer.setJdbcTemplate(jdbcTemplate);
    }

    public boolean loadTableData() {

        try {
            boolean connectionError = false;
                if (dbServer.isEnabled()) {
                    List<String> foundTables = new ArrayList<String>();
                   // JdbcTemplate j = dbServer.getJdbcTemplate();
                    Connection conn = dbServer.getDataSource().getConnection();
                    //validate tables are accessible
                    if (conn != null) {
                        String schemaName = conn.getSchema();
                        System.out.println("Loading table data for application db ".concat(dbServer.getFriendlyName()));
                        DatabaseMetaData databaseMetaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
                        ResultSet resultSet = databaseMetaData.getTables(conn.getCatalog(), "", null, new String[]{"TABLE"});
                      //  ResultSet resultSet = databaseMetaData.getTables(null, schemaName, null, new String[]{"TABLE"});
                        while (resultSet.next()) {
                            foundTables.add(resultSet.getString("TABLE_NAME").toLowerCase());
                        }
                        System.out.println("Removing inaccessible tables if any. ");
                        Iterator<DBTable> itr = dbServer.getAvailableTables().iterator();
                        while (itr.hasNext()) {
                            DBTable dbTable = itr.next();
                            String tableName = dbTable.getTableName().toLowerCase();
                            if (foundTables.contains(tableName)) {
                                ResultSet columns = databaseMetaData.getColumns(null,
                                        dbServer.getDataSource().getConnection().getSchema(), tableName, null);
                                while (columns.next()) {
                                    TableColumn tableColumn = new TableColumn(
                                            columns.getString("COLUMN_NAME"),
                                            columns.getInt("DATA_TYPE"),
                                            Integer.parseInt(columns.getString("COLUMN_SIZE")),
                                            columns.getBoolean("IS_AUTOINCREMENT"),
                                            columns.getBoolean("IS_NULLABLE")
                                    );
                                    dbTable.addTableColumn(tableColumn);
                                }
                            } else {
                                System.out.println("Removing ".concat(tableName));
                                itr.remove();
                            }
                        }
                    } else {
                        System.out.println("unable to load table data for " + conn.getSchema());
                    }
                }
                System.out.println("Column metadata loaded");
            } catch (SQLException e) {
                System.out.println(e.toString());
                return false;
        }
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public JSONArray getTableDataAsJson(String serverName, String tableName) throws SQLException {

        Connection conn = dbServer.getJdbcTemplate().getDataSource().getConnection();
        DBTable table = dbServer.getTable(tableName);
        String select=table.getSelectStatement();
        try {
            JSONArray jsonArray = dbServer.getJdbcTemplate()
                    .query(select, (ResultSet rs) -> {
                        return ResultsetMapper.convert(rs);
                    });
            return jsonArray;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    public List<SOURCES> getSourcesData() {
        String sql =dbServer.getTable("sources").getSelectStatement();
        List<SOURCES> sources = jdbcTemplate.query(sql,
                new BeanPropertyRowMapper(SOURCES.class));
        return sources;

    }

    public List<USERPROFILE> getProfileData() {
        String sql =dbServer.getTable("profiles").getSelectStatement();
        List<com.peason.databasetables.USERPROFILE> users = jdbcTemplate.query(sql,
                new BeanPropertyRowMapper(com.peason.databasetables.USERPROFILE.class));
        return users;
    }

    public ResultSet getTableData( String tableName) throws SQLException {
        ResultSet rs1=null;
        try {
        Class clazz= Class.forName("com.peason.databasetables."+tableName.toUpperCase());
        DBTable table = dbServer.getTable(tableName);
        String select=table.getSelectStatement();
        rs1=dbServer.getJdbcTemplate().query(select, (rs) -> rs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return rs1;
    }

//    public  HashMap<String,HashMap<String,Object>> convert(ResultSet resultSet, Class A) throws SQLException {
//
//        HashMap<String,Object> row;
//        HashMap<String,HashMap<String,Object>> rows = new HashMap<>();
//        ResultSetMetaData rsmd=resultSet.getMetaData();
//        int columns = rsmd.getColumnCount();
//        String sourceName="";
//        while (resultSet.next()) {
//            row = new HashMap<String,Object>();
//            for (int i = 1; i <= columns; i++) {
//                String colName=rsmd.getColumnLabel(i).toLowerCase();
//                row.put(colName, resultSet.getObject(i));
//                if (colName.equals("sourcename")) {
//                    sourceName=resultSet.getString(i);
//                }
//            }
//            rows.put(sourceName,row);
//        }
//        return rows;
//
//    }

    private MapSqlParameterSource createInsertOrDeleteParameters(JSONObject json, ArrayList<TableColumn> columnList, String action) {
        MapSqlParameterSource namedParameters  =new MapSqlParameterSource();
        int dataType=0;
        for (int i=0; i<columnList.size(); i++) {
            dataType=columnList.get(i).getDataType();
            System.out.println(json);
            if (columnList.get(i).isIdentifier() && action.equalsIgnoreCase("I")) continue;
         //   System.out.println(json.get(columnList.get(i).getKey()));
            try {
                if (dataType!=93) {
                    System.out.println(columnList.get(i).getKey());
                    namedParameters.addValue(columnList.get(i).getKey(), json.get(columnList.get(i).getKey()));
                } else {
                    String param=(String)json.get(columnList.get(i).getKey());
                    if (action.equalsIgnoreCase("I")) param=DateUtil.SQLTimestampStringToDB2FormatString(param);
                    namedParameters.addValue(columnList.get(i).getKey(), Timestamp.valueOf((param)));
                    //"before" value must already be valid format //
                }
            } catch (Exception e) {
                System.out.println("error with paramater:".concat(columnList.get(i).getKey()).concat("=").concat((String)json.get(columnList.get(i).getKey())));
                //todo: handle
            }

        }
        return namedParameters;
    }

    private MapSqlParameterSource createUpdateParameters( JSONArray jsonArr, ArrayList<TableColumn> columnList) throws Exception {
        MapSqlParameterSource namedParameters  =new MapSqlParameterSource();
        int dataType=0;
        JSONObject before =jsonArr.getJSONObject(0);
        JSONObject after=jsonArr.getJSONObject(1);
        for (int i=0; i<columnList.size(); i++) {
            System.out.println("b4"+before.get(columnList.get(i).getKey()));
            System.out.println(">>"+after.get(columnList.get(i).getKey()));
            dataType=columnList.get(i).getDataType();
            if (dataType!=93) {
                namedParameters.addValue(columnList.get(i).getKey(), (String)after.get(columnList.get(i).getKey()));
                namedParameters.addValue("w_"+columnList.get(i).getKey(), before.get(columnList.get(i).getKey()));
            } else {
                String param=(String)after.get(columnList.get(i).getKey());
                namedParameters.addValue(columnList.get(i).getKey(), Timestamp.valueOf(DateUtil.SQLTimestampStringToDB2FormatString(param)));
                namedParameters.addValue("w_"+columnList.get(i).getKey(), Timestamp.valueOf((String)before.get(columnList.get(i).getKey())));
            }
        }
        return namedParameters;

    }

    public String insertTableData(Object src,String tableName) throws SQLException {

        //strip the result piece from the json
        HashMap<String, Ledger> ledgers=(HashMap<String, Ledger>)src;

        //WIP - at the moment this does not support returning the id of the inserted row
        DBTable table = dbServer.getTable(tableName);
        ArrayList<TableColumn> columnList = table.getTableColumnList();
        for (Map.Entry<String, Ledger> ledger : ledgers.entrySet()) {

        }
        Object[] arr=ledgers.entrySet().toArray();
        Object[] keys=ledgers.keySet().toArray();

        MapSqlParameterSource namedParameters = createInsertOrDeleteParameters(new JSONObject("ledger"),columnList,"I");

        try {
            int affected=namedParameterJdbcTemplate.update(table.getInsertStatement(),namedParameters);
            System.out.println("affected rows inserted: {}"+String.valueOf(affected));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "-1::".concat(e.getMessage());
        }
        //later to be row id + ::
        return "1::";
    }

    public String deleteTableData(JSONObject json,String serverName, String tableName) throws SQLException {
     //   NamedParameterJdbcTemplate namedParameterJdbcTemplate=server.getNamedParameterJdbcTemplate();
        DBTable table = dbServer.getTable(tableName);
        ArrayList<TableColumn> columnList = table.getTableColumnList();
        //now the column list return from the front-end is usually in a different order to the column metadata
        //so we need to extract the values from json object into object arrays
        MapSqlParameterSource namedParameters = createInsertOrDeleteParameters(json,columnList,"D");

        try {
            int affected=namedParameterJdbcTemplate.update(table.getDeleteStatement(),namedParameters);
            System.out.println("affected rows deleted: {}"+String.valueOf(affected));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "false::".concat(e.getMessage());

        }
        return "true::";
    }

    public String updateTableData(JSONArray json, String tableName) throws Exception {

      //  NamedParameterJdbcTemplate namedParameterJdbcTemplate=server.getNamedParameterJdbcTemplate();
        DBTable table = dbServer.getTable(tableName);
        ArrayList<TableColumn> columnList = table.getTableColumnList();
        MapSqlParameterSource namedParameters = createUpdateParameters(json,columnList);

        try {
            int affected=namedParameterJdbcTemplate.update(table.getUpdateStatement(),namedParameters);
            System.out.println("affected rows updated: {}"+String.valueOf(affected));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "false::".concat(e.getMessage());

        }
        return "true::";
    }

    public List<Map<String,Object>> getSources() {
        if (sources.size()==0) {
            sources=getSources();
        }
        return sources;
    }
}
