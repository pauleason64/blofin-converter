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

    static public class Ledger{

        private String ledgerKey;

        @JsonProperty("refid")
        private String refid;

        @JsonProperty("time")
        private Date time;

        @JsonProperty("type")
        private String type;

        @JsonProperty("subtype")
        private String subtype;

        @JsonProperty("aclass")
        private String aclass;

        @JsonProperty("asset")
        private String asset;

        @JsonProperty("amount")
        private BigDecimal amount;

        @JsonProperty("fee")
        private BigDecimal fee;

        @JsonProperty("balance")
        private BigDecimal balance;

        @JsonProperty("posstatus")
        @JsonIgnore
        String posstatus;

        public String getRefid() {
            return refid;
        }

        public void setRefid(String refid) {
            this.refid = refid;
        }

        public Date getTime() {
            return time;
        }


        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSubtype() {
            return subtype;
        }

        public void setSubtype(String subtype) {
            this.subtype = subtype;
        }

        public String getAclass() {
            return aclass;
        }

        public void setAclass(String aclass) {
            this.aclass = aclass;
        }

        public String getAsset() {
            return asset;
        }

        public void setAsset(String asset) {
            this.asset = asset;
        }

        public BigDecimal getAmount() {
            return this.amount;
        }

        public void setAmount(String amount) {
            this.amount = new BigDecimal(amount);
        }

        public BigDecimal getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = new BigDecimal(fee);
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = new BigDecimal(balance);
        }

        public void setTime(Double time) {
            this.time=new Date(time.longValue()*1000L);
        }

        public String getLedgerKey() {
            return ledgerKey;
        }

        public void setLedgerKey(String ledgerKey) {
            this.ledgerKey = ledgerKey;
        }

        @Override
        public String toString(){
            return
                    "key = '" + getLedgerKey() + '\'' +
                    "type = '" + getType() + '\'' +
                    ",subtype = '" + getSubtype() + '\'' +
                    ",refid = '" + getRefid() + '\'' +
                    ",asset = '" + getAsset() + '\'' +
                    ",time = '" + getTime() +
                    ",amount = '" + getAmount() + '\'' +
                    ",fee = '" + getFee() + '\'' +
                    ",bal = '" + getBalance() + '\'' +
                    "}";
        }
    }
}
