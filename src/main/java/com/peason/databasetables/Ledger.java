package com.peason.databasetables;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.beans.BeanProperty;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class Ledger {

        private long id;
        private String ledgerKey;
        private long sourceid;
        private long profileid;

        @JsonProperty("refid")
        private String refid;

        private Date tradedt;

        @JsonProperty("type")
        private String tradetype;

        @JsonProperty("subtype")
        private String subtype;

        @JsonProperty("aclass")
        private String aclass;

        @JsonProperty("asset")
        private String asset;

        @JsonProperty("amount")
        private BigDecimal cost;

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

        public Date getTradedt() {
            return tradedt;
        }
        public String getTradetype() {
            return tradetype;
        }

        public void setTradetype(String type) {
            this.tradetype = type;
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

        public BigDecimal getCost() {
            return this.cost;
        }

        public void setCost(String amount) {
            this.cost = new BigDecimal(amount);
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

        @JsonProperty("time")
        public void setTime(Double time) {
            this.tradedt=new Date(time.longValue()*1000L);
        }

        public void setTradedt(Timestamp tradedt) {
        this.tradedt=new Date(tradedt.getTime());
        }


    public String getLedgerKey() {
            return ledgerKey;
        }

        public void setLedgerKey(String ledgerKey) {
            this.ledgerKey = ledgerKey;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getSourceid() {
            return sourceid;
    }

    public void setSourceid(long sourceid) {
        this.sourceid = sourceid;
    }

    public long getProfileid() {
        return profileid;
    }

    public void setProfileid(long profileid) {
        this.profileid = profileid;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }


    public Object[] getFieldsForInsertQuery(){
            return new Object[]{getProfileid(),getSourceid(),getRefid(),getLedgerKey(),getTradedt(),getAsset(),
            getTradetype(),getSubtype(),getCost(),getFee(),getBalance()};
        }

        @Override
        public String toString(){
            return  "id = '"+ getId() + '\'' +
                    "key = '" + getLedgerKey() + '\'' +
                    "type = '" + getTradetype() + '\'' +
                    ",subtype = '" + getSubtype() + '\'' +
                    ",refid = '" + getRefid() + '\'' +
                    ",asset = '" + getAsset() + '\'' +
                    ",time = '" + getTradedt() +
                    ",amount = '" + getCost() + '\'' +
                    ",fee = '" + getFee() + '\'' +
                    ",bal = '" + getBalance() + '\'' +
                    "}";
        }
}
