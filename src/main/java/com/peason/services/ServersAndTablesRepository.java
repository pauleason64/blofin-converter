package com.peason.services;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.peason.databasetables.APIFEEDS;
import com.peason.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeansException;

import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("serversAndTablesRepository")
public class ServersAndTablesRepository implements Serializable, ApplicationContextAware {

    private static final Gson gsonHideNonExposed  = new GsonBuilder()
      .excludeFieldsWithoutExposeAnnotation().create();
    private static final long serialVersionUID = -2247265272093512013L;
    static ApplicationContext context;

    public ServersAndTablesRepository() {}

//    @Autowired
    private AppConfig appConfig;//= springClasses.getAppConfig();

    //@Autowired
    DAO dao ;//= springClasses.getDao();

    DBServer dbServer;
    Map<String, DBServer> serverMap ;
    List<APIFEEDS> apiFeeds =new ArrayList<>();


    @PostConstruct
    public void init() {
        dao = context.getBean(DAO.class);
        appConfig= context.getBean(AppConfig.class);

    }
    public static final String emptyColumns = "[]";

    public String getServerNames() {

        List<DBServer> servers = new ArrayList<>();
        for (DBServer server : serverMap.values()) {
            servers.add(server);
        }
        return gsonHideNonExposed.toJson(servers);
    }

    public String getTableNames(String serverName) {
        DBServer server = serverMap.get(serverName);
        if (null == server) return "";
        List<DBTable> tables = server.getAvailableTables();
        return null == tables ? "[]" : gsonHideNonExposed.toJson(tables);
    }

    public ArrayList<TableColumn> getTableColumnArray(String serverName, String tableName) {

        ArrayList<TableColumn> columns = null;
        DBServer server = serverMap.get(serverName);
        DBTable table = server.getTable(tableName);
        if (table != null) {
            columns = table.getTableColumnList();
        }
        return columns == null ? new ArrayList<>() : columns;
    }

    public String getTableColumns(String serverName, String tableName) {

        ArrayList<TableColumn> columns = getTableColumnArray(serverName, tableName);
        return columns == null ? emptyColumns : gsonHideNonExposed.toJson(columns);
    }


    @PostConstruct
    public void setup() {
        if (!dao.loadTableData()) {
//            useDummyTables();
        } else {
            //do something!
            serverMap = dao != null ? dao.getDataSources() : new HashMap<>();
            System.out.println(serverMap);
        }
    }


    public TableRows getTableData(String serverName, String tableName) throws SQLException {
      //  JSONArray jsonArray=dao.getTableData(serverName,tableName);
        TableRows tr=new TableRows();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            tr.addtableRow(jsonArray.getJSONObject(i));
//        }
        HashMap<String,HashMap<String,Object>> rows;
        return tr;
    }

    public String insertRow(JSONObject json,String serverName, String tableName) throws SQLException {


        return dao.insertTableData(json, serverName, tableName);

    }

    public String deleteRow(JSONObject json, String serverName, String tableName) throws SQLException {


        return dao.deleteTableData(json, serverName, tableName);

    }

    public String updateRow(JSONArray json,String serverName, String tableName) throws Exception {


        return dao.updateTableData(json, serverName, tableName);

    }

    public Map<String, DBServer> getServerMap() {
        return serverMap;
    }

    public static void main(String[] args) {
        ServersAndTablesRepository server=new ServersAndTablesRepository();
        server.setup();
    }

    public APIFEEDS getAPIDataForSource (String sourceName) throws SQLException {
        if (apiFeeds.size() == 0) {
            apiFeeds = dao.getAPIData();
        }
        for (int i = 0; i < apiFeeds.size(); i++) {
            APIFEEDS feed = (APIFEEDS) apiFeeds.get(i);
            if (feed.getSourceName().equalsIgnoreCase(sourceName)) return feed;
        }
        return null;
    }

    public List<APIFEEDS> getAPIData () throws SQLException {
        //look to make this a mapper class later
        if (apiFeeds.size() == 0) {
            apiFeeds = dao.getAPIData();
        }
        return apiFeeds;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context=applicationContext;
    }
}
