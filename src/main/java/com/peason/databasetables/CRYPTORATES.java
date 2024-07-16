package com.peason.databasetables;

import java.util.Date;

public class CRYPTORATES {
    Date rateDt;
    String fromCurr;
    String toCurr;
    double exrate;

    public Date getRateDt() {
        return rateDt;
    }

    public void setRateDt(Date rateDt) {
        this.rateDt = rateDt;
    }

    public String getFromCurr() {
        return fromCurr;
    }

    public void setFromCurr(String fromCurr) {
        this.fromCurr = fromCurr;
    }

    public String getToCurr() {
        return toCurr;
    }

    public void setToCurr(String toCurr) {
        this.toCurr = toCurr;
    }

    public double getExrate() {
        return exrate;
    }

    public void setExrate(double exrate) {
        this.exrate = exrate;
    }
}
