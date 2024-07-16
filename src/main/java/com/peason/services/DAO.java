package com.peason.services;

import com.peason.databasetables.APIFEEDS;
import com.peason.model.*;
import com.peason.utils.DateUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
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

    private JdbcTemplate jdbcTemplate;
    private Map<String, DBServer> dataSources;
    private List<Map<String,Object>> apiFeeds;
    public static final String DBNAME = "crypto_trans";

    private static final String TS_CONVERT="TO_TIMESTAMP('xxx', 'YYYY-MM-DD HH24:MI:SS.FF'";

    //@Autowired
    AppConfig appConfig; //=springClasses.getAppConfig();

    @Autowired
    @Qualifier(DBNAME)
    DBServer cryptoDS;

    static ApplicationContext context;

    ServersAndTablesRepository serversAndTablesRepository; //= springClasses.getServersAndTablesRepository();

    @PostConstruct
    public void init() {
        appConfig=context.getBean(AppConfig.class);
        dataSources = new HashMap<>();
        jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(cryptoDS.getDataSource());
        cryptoDS.setJdbcTemplate(jdbcTemplate);
        if (cryptoDS.isEnabled()) dataSources.put(DBNAME, cryptoDS);
//        try {
//            apiFeeds=serversAndTablesRepository.getAPIData();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    }

    public boolean loadTableData() {

        try {
            boolean connectionError = false;
            for (DBServer DBServer : dataSources.values()) {
                if (DBServer.isEnabled()) {
                    List<String> foundTables = new ArrayList<String>();
                    JdbcTemplate j = DBServer.getJdbcTemplate();
                    Connection conn = j.getDataSource().getConnection();
                    //validate tables are accessible
                    if (conn != null) {
                        String schemaName = conn.getSchema();
                        System.out.println("Loading table data for application db ".concat(DBServer.getFriendlyName()));
                        DatabaseMetaData databaseMetaData = j.getDataSource().getConnection().getMetaData();
                        ResultSet resultSet = databaseMetaData.getTables(conn.getCatalog(), "", null, new String[]{"TABLE"});
                      //  ResultSet resultSet = databaseMetaData.getTables(null, schemaName, null, new String[]{"TABLE"});
                        while (resultSet.next()) {
                            foundTables.add(resultSet.getString("TABLE_NAME").toLowerCase());
                        }
                        System.out.println("Removing inaccessible tables if any. ");
                        Iterator<DBTable> itr = DBServer.getAvailableTables().iterator();
                        while (itr.hasNext()) {
                            DBTable dbTable = itr.next();
                            String tableName = dbTable.getTableName().toLowerCase();
                            if (foundTables.contains(tableName)) {
                                ResultSet columns = databaseMetaData.getColumns(null,
                                        DBServer.getDataSource().getConnection().getSchema(), tableName, null);
                                while (columns.next()) {
                                    TableColumn tableColumn = new TableColumn(
                                            columns.getString("COLUMN_NAME"),
                                            columns.getInt("DATA_TYPE"),
                                            Integer.parseInt(columns.getString("COLUMN_SIZE")),
                                            Boolean.parseBoolean(columns.getString("IS_AUTOINCREMENT")),
                                            Boolean.parseBoolean(columns.getString("IS_NULLABLE"))
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
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
        return true;
    }

    public Map<String, DBServer> getDataSources() {
        return dataSources;
    }

    public DBServer getDataSource(String serverName) {
        return dataSources.get(serverName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public JSONArray getTableDataAsJson(String serverName, String tableName) throws SQLException {

        DBServer server = dataSources.get(serverName);
        Connection conn = server.getJdbcTemplate().getDataSource().getConnection();
        DBTable table = server.getTable(tableName);
        String select=table.getSelectStatement();
        try {
            JSONArray jsonArray = server.getJdbcTemplate()
                    .query(select, (ResultSet rs) -> {
                        return ResultsetMapper.convert(rs);
                    });
            return jsonArray;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<APIFEEDS> getAPIData() {
        String sql =dataSources.get(DBNAME).getTable("apifeeds").getSelectStatement();
        List<APIFEEDS> customers = jdbcTemplate.query(sql,
                new BeanPropertyRowMapper(APIFEEDS.class));
        return customers;
//        BeanPropertyRowMapper bm= new BeanPropertyRowMapper(APIFEEDS.class);
//        List<Map<String,Object>> res=jdbcTemplate.queryForList(sql,bm);
//        return res;

//        return (APIFEEDS) jdbcTemplate.queryForObject(
//                sql,
//                new BeanPropertyRowMapper(APIFEEDS.class));
    }

    public ResultSet getTableData(String serverName, String tableName) throws SQLException {
        ResultSet rs1=null;
        try {
        Class clazz= Class.forName("com.peason.databasetables."+tableName.toUpperCase());
        DBServer server = dataSources.get(serverName);
//        Connection conn = server.getJdbcTemplate().getDataSource().getConnection();
        DBTable table = server.getTable(tableName);
        String select=table.getSelectStatement();
        rs1=server.getJdbcTemplate().query(select, (rs) -> rs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return rs1;
    }

    public  HashMap<String,HashMap<String,Object>> convert(ResultSet resultSet, Class A) throws SQLException {

        HashMap<String,Object> row;
        HashMap<String,HashMap<String,Object>> rows = new HashMap<>();
        ResultSetMetaData rsmd=resultSet.getMetaData();
        int columns = rsmd.getColumnCount();
        String sourceName="";
        while (resultSet.next()) {
            row = new HashMap<String,Object>();
            for (int i = 1; i <= columns; i++) {
                String colName=rsmd.getColumnLabel(i).toLowerCase();
                row.put(colName, resultSet.getObject(i));
                if (colName.equals("sourcename")) {
                    sourceName=resultSet.getString(i);
                }
            }
            rows.put(sourceName,row);
        }
        return rows;

//        ResultSetMetaData rmsd = resultSet.getMetaData();
//        while (resultSet.next()) {
//            for (int i = 1; i <= rmsd.getColumnCount(); i++) {
//                Field f = null;
//                try {
//                    f = A.getDeclaredField(rmsd.getColumnName(i));
//                    f.setAccessible(true);
//                    f.set(A, rmsd.getColumnName(i));
//                } catch (NoSuchFieldException | IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//                BeanWrapper wrapper = new BeanWrapperImpl(A);
//                wrapper.setPropertyValues(map);
//                Object instance = wrapper.getWrappedInstance();
//                return (T)instance;
//            }
//                BeanWrapper wrapper = new BeanWrapperImpl(A);
//                Map map = new HashMap();
//                wrapper.setPropertyValues(map);
//                A  = (T) wrapper.getWrappedInstance();
//                return A;
//            }
//    public <T> T jsonToMapLocation(String json, Class<T> c) {
    }

    private MapSqlParameterSource createInsertOrDeleteParameters(JSONObject json, ArrayList<TableColumn> columnList, String action) {
        MapSqlParameterSource namedParameters  =new MapSqlParameterSource();
        int dataType=0;
        for (int i=0; i<columnList.size(); i++) {
            System.out.println(json.get(columnList.get(i).getKey()));
            dataType=columnList.get(i).getDataType();
            try {
                if (dataType!=93) {
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

    public String insertTableData(JSONObject json,String serverName, String tableName) throws SQLException {

        //WIP - at the moment this does not support returning the id of the inserted row
        DBServer server = dataSources.get(serverName);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate=server.getNamedParameterJdbcTemplate();
        DBTable table = server.getTable(tableName);
        ArrayList<TableColumn> columnList = table.getTableColumnList();

        MapSqlParameterSource namedParameters = createInsertOrDeleteParameters(json,columnList,"I");

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
        DBServer server = dataSources.get(serverName);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate=server.getNamedParameterJdbcTemplate();
        DBTable table = server.getTable(tableName);
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

    public String updateTableData(JSONArray json,String serverName, String tableName) throws Exception {
        DBServer server = dataSources.get(serverName);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate=server.getNamedParameterJdbcTemplate();
        DBTable table = server.getTable(tableName);
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

    public List<Map<String,Object>> getApiFeeds() {
        if (apiFeeds.size()==0) {
            apiFeeds=getApiFeeds();
        }
        return apiFeeds;
    }
}
