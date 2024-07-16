package com.peason.model;

import com.google.gson.annotations.Expose;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TableRows {

    private ArrayList<TableColumn> columns;
    @Expose
    private ArrayList<String> rows=new ArrayList<>();

    public void addtableRow(JSONObject row) {
        rows.add(row.toString());
    }

    public List<String> getRows() {
        return rows;
    }
}
