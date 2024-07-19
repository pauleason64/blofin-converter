package com.peason.persistance;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.peason.databasetables.SOURCES;
import com.peason.databasetables.USERPROFILE;
import com.peason.model.*;
import com.peason.services.DAO;
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

    DAO dao ;
    DBServer dbServer;
    Map<String, DBServer> serverMap ;
    List<SOURCES> sources =new ArrayList<>();
    List<USERPROFILE> users= new ArrayList<>();

    @PostConstruct
    public void init() {
        dao = context.getBean(DAO.class);
        dbServer=context.getBean(DBServer.class);

    }
    public static final String emptyColumns = "[]";

    public String getTableNames() {
        List<DBTable> tables = dbServer.getAvailableTables();
        return null == tables ? "[]" : gsonHideNonExposed.toJson(tables);
    }

    public ArrayList<TableColumn> getTableColumnArray( String tableName) {
        ArrayList<TableColumn> columns = null;
        DBTable table = dbServer.getTable(tableName);
        if (table != null) {
            columns = table.getTableColumnList();
        }
        return columns == null ? new ArrayList<>() : columns;
    }

    public String getTableColumns( String tableName) {
        ArrayList<TableColumn> columns = getTableColumnArray( tableName);
        return columns == null ? emptyColumns : gsonHideNonExposed.toJson(columns);
    }


    @PostConstruct
    public void setup() {
        dao.loadTableData();
    }

   public TableRows getTableData( String tableName) throws SQLException {
      //  JSONArray jsonArray=dao.getTableData(serverName,tableName);
        TableRows tr=new TableRows();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            tr.addtableRow(jsonArray.getJSONObject(i));
//        }
        HashMap<String,HashMap<String,Object>> rows;
        return tr;
    }

    public String insertRow(JSONObject json, String tableName) throws SQLException {
        return dao.insertTableData(json,  tableName);
    }

    public String deleteRow(JSONObject json, String serverName, String tableName) throws SQLException {
        return dao.deleteTableData(json, serverName, tableName);
    }

    public String updateRow(JSONArray json, String tableName) throws Exception {
        return dao.updateTableData(json,  tableName);
    }

    public SOURCES getAPIDataForSource (String sourceName) throws SQLException {
        if (sources.size() == 0) {
            sources = dao.getSourcesData();
        }
        for (int i = 0; i < sources.size(); i++) {
            SOURCES source = (SOURCES) sources.get(i);
            if (source.getSourceName().equalsIgnoreCase(sourceName)) return source;
        }
        return null;
    }

   public USERPROFILE getProfileData(String userName,String password) {
       if (users.size() == 0) {
           users = dao.getProfileData();
       }
       //expand to full check later
       for (int i = 0; i < users.size(); i++) {
           if (users.get(i).getUserName().equalsIgnoreCase(userName)) {
               //check pwd and email later
               return users.get(i);
           }
       }
       USERPROFILE errorProfile = new USERPROFILE();
       errorProfile.setErrMsg("unknown username or password");
       return errorProfile;
   }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context=applicationContext;
    }

    public static void main(String[] args) {
        ServersAndTablesRepository server=new ServersAndTablesRepository();
        server.setup();
    }

}
