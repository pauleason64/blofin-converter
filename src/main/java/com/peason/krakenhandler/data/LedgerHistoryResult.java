package com.peason.krakenhandler.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

public class LedgerHistoryResult extends KrakenResult {

    public static String[] ColHeadings="Refid,Trade Type,subType,Asset,Date,Cost,Fee,Balance".split(",");
    public static int[] ColWidthPercent = {15,10,8,8,22,15,10,10};

    public LedgerHistoryResult() {}

    @JsonProperty("result")
    Result result;

    public Result getResult() {
        return result;
    }

//    public void setResults(Result result) {
//        this.result = result;
//    }

    @Override
    public String toString(){
        return
                "Result{" +
                        ",error = '" + getErrors() + '\'' +
//                        ",count = '" + getCount() +
                        ",ledgers = '" + getResult().ledgers + '\'' +
                        "}";
    }

   public static class Result {

        @JsonProperty("count")
        private int count;

        @JsonProperty("ledger")
        public  HashMap<String, Ledger> ledgers =new HashMap<>();

        public void setCount(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

}
