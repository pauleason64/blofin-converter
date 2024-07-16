package com.peason.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

@Component
public class ResultsetMapper {

    //covert resultset to json object

    public static JSONArray convert(ResultSet resultSet) throws SQLException,
            JSONException {
        JSONArray jsonArray = new JSONArray();
        ResultSetMetaData rsmd=resultSet.getMetaData();
        int columns = rsmd.getColumnCount();
        while (resultSet.next()) {
            JSONObject obj = new JSONObject();

            for (int i = 1; i <= columns; i++) {
                obj.put(rsmd.getColumnLabel(i).toLowerCase(), resultSet.getObject(i));
            }
            jsonArray.put(obj);
        }
        return jsonArray;
    }
}
