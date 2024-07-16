package com.peason.databasetables;

import java.util.Date;

public class FIATRATES {
    Date rateDt;
    String baseCurr;
    String toCurr;
    double exrate;

    public Date getRateDt() {
        return rateDt;
    }

    public void setRateDt(Date rateDt) {
        this.rateDt = rateDt;
    }

    public String getBaseCurr() {
        return baseCurr;
    }

    public void setBaseCurr(String baseCurr) {
        this.baseCurr = baseCurr;
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
