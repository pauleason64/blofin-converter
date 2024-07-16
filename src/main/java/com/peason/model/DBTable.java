package com.peason.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;


public class DBTable {

    private ArrayList<TableColumn> columnList ;
    private ArrayList<Integer> columnDataTypes;
    @Expose
    private String tableName;
    private String selectStatement;
    private String insertStatement;
    private String updateStatement;
    private String deleteStatement;
    private String schema;

    public DBTable(String tableName, String schema) {

        this.tableName = tableName;
        this.schema = schema;
        columnList = new ArrayList<>();
        columnDataTypes=new ArrayList<>();
    }

    @Deprecated //local testing only
    public void addTableColumn(String columnName,int dataType, int dataLength,boolean isIdentifier,boolean isNullable) {

        TableColumn tableColumn=new TableColumn(columnName,dataType,dataLength,isIdentifier,isNullable);
        columnList.add(tableColumn);
        columnDataTypes.add(dataType);
    }

    public void addTableColumn(TableColumn tableColumn) {

        columnList.add(tableColumn);
    }

    public ArrayList<TableColumn> getTableColumnList() {
        return columnList;
    }

    public String getTableName() {
        return tableName;
    }

    public String getSelectStatement() {
        if (selectStatement == null) {
            selectStatement = buildSelectStatement();
        }
        return selectStatement;
    }

    public String buildSelectStatement() {
        StringBuilder sb = new StringBuilder("select ");
        for (int i=0; i< columnList.size(); i++) {
            sb.append(columnList.get(i).getName());
            if (i<columnList.size()-1) sb.append(",");
        }
        sb.append(" from ").append(schema).append(".").append(tableName);
        return sb.toString();
    }

    public String getInsertStatement() {
        if (insertStatement == null) {
            insertStatement = buildInsertStatement();
        }
        return insertStatement;
    }

    public String buildInsertStatement() {
        StringBuilder sb = new StringBuilder("insert into ")
            .append(schema).append(".").append(tableName).append("(");
            for (int i=0; i< columnList.size(); i++) {
                if (!columnList.get(i).isIdentifier) {
                    sb.append(columnList.get(i).getName());
                    if (i<columnList.size()-1) sb.append(","); }
            }
            sb.append(") values (");
            for (int i=0; i< columnList.size(); i++) {
                if (!columnList.get(i).isIdentifier)  {
                    sb.append(":").append(columnList.get(i).getKey());
                    if (i<columnList.size()-1) sb.append(","); }
            }
            sb.append(")");
        System.out.println(sb.toString());
        return sb.toString();
    }

    public String getDeleteStatement() {
        if (deleteStatement == null) {
            deleteStatement = buildDeleteStatement();
        }
        return deleteStatement;
    }

    public String buildDeleteStatement() {
        StringBuilder sb = new StringBuilder("delete from ");
        sb.append(schema).append(".").append(tableName).append(" where ");
        for (int i=0; i< columnList.size(); i++) {
            if (!columnList.get(i).isIdentifier) {
                sb.append(columnList.get(i).getKey()).append("= :").append(columnList.get(i).getKey());
                if (i<columnList.size()-1)  {
                    sb.append(" and ");
                }
            }
        }
        System.out.println(sb.toString());
        return sb.toString();
    }

    public String getUpdateStatement() {
        if (updateStatement == null) {
            updateStatement = buildUpdateStatement();
        }
        return updateStatement;
    }

    public String buildUpdateStatement() {
        StringBuilder sb = new StringBuilder("update ");
        StringBuilder where=new StringBuilder(" where ");
        sb.append(schema).append(".").append(tableName).append(" set ");
        for (int i=0; i< columnList.size(); i++) {
            if (!columnList.get(i).isIdentifier) {
                sb.append(columnList.get(i).getKey()).append("= :").append(columnList.get(i).getKey());
                //sb.append(columnList.get(i).getName()).append("=? ");
                where.append(columnList.get(i).getKey()).append("= :w_").append(columnList.get(i).getKey());
                //where.append(columnList.get(i).getName()).append("= ?");
                if (i<columnList.size()-1)  {
                    sb.append(",");
                    where.append(" and ");
                }
            }
        }
        sb.append(where);
        where=null;
        System.out.println(sb.toString());
        return sb.toString();
    }

    public ArrayList<Integer> getColumnDataTypes() {
        return this.columnDataTypes;
    }

    public ArrayList<TableColumn> getColumnList() {
        return columnList;
    }

    @Override
    public String toString() {
        return this.tableName;
    }
}

