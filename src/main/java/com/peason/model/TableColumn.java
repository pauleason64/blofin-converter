package com.peason.model;

import com.google.gson.annotations.Expose;

public class TableColumn {


    @Expose String name;
    @Expose String key;
    @Expose int dataType;
    @Expose int dataLength;
    @Expose boolean isIdentifier;
    @Expose boolean isNullable;
    @Expose boolean editable=true;
    @Expose boolean filterable=true;
    @Expose boolean sortable=true;
    @Expose boolean resizable=true;
    @Expose String setWrap="wrap";
    String rowId;



    public TableColumn ( String name,int dataType, int dataLength,boolean isIdentifier,boolean isNullable ) {
        this.name =name.toLowerCase();
        this.key=name.toLowerCase();
        this.dataType=dataType;
        this.dataLength=dataLength;
        this.isIdentifier=isIdentifier;
        this.editable = !isIdentifier;
        this.isNullable=isNullable;
    }

    @Override
    public String toString() {
        return new StringBuilder("Column MetaData: ").append(name).append(",").append(dataType).append(",").append(dataLength).toString();
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public int getDataType() {
        return dataType;
    }

    public int getDataLength() {
        return dataLength;
    }

    public boolean isIdentifier() {
        return isIdentifier;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public boolean isEditable() { return editable; }

    public boolean isFilterable() { return filterable; }

    public boolean isSortable() { return sortable; }

    public boolean isResizable() { return resizable; }

    public String getRowId() { return rowId; }

    public void setRowId(String rowId) { this.rowId = rowId; }
}
