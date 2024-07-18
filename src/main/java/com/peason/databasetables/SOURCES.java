package com.peason.databasetables;

import java.util.Date;

public class SOURCES {
    //fetch from DB
    int profileID;
    int sourceTypeID;
    String sourceName;
    String apiKey;
    String apiPk;
    String status;
    Date fromDate;
    Date toDate;
    long tradeCount;
    long ledgerCount;
    boolean autorefresh;
    Date nextRefreshDate;

    public int getProfileID() {return profileID;}

    public void setProfileID(int profileID) {this.profileID = profileID;}

    public int getSourceTypeID() {return sourceTypeID;}

    public void setSourceTypeID(int sourceTypeID) {this.sourceTypeID = sourceTypeID;}

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiPk() {
        return apiPk;
    }

    public void setApiPk(String apiPk) {
        this.apiPk = apiPk;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public long getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(long tradeCount) {
        this.tradeCount = tradeCount;
    }

    public long getLedgerCount() {
        return ledgerCount;
    }

    public void setLedgerCount(long ledgerCount) {
        this.ledgerCount = ledgerCount;
    }

    public boolean isAutorefresh() {
        return autorefresh;
    }

    public void setAutorefresh(boolean autorefresh) {
        this.autorefresh = autorefresh;
    }

    public Date getNextRefreshDate() {
        return nextRefreshDate;
    }

    public void setNextRefreshDate(Date nextRefreshDate) {
        this.nextRefreshDate = nextRefreshDate;
    }


}
