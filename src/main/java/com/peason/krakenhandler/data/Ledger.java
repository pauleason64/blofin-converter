package com.peason.krakenhandler.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Ledger {

        static final DateTimeFormatter dtfm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        private long id;
        private String ledgerKey;
        private long sourceid;
        private long profileid;

        @JsonProperty("refid")
        private String refid;

        @JsonProperty("time")
        private LocalDateTime time;

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

        public LocalDateTime getTime() {
            return time;
        }

//        public LocalDateTime getTimeAsLocalDate() {
//            return LocalDateTime.parse (time,dtfm);
//        }

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

        public void setTime(String time) {
            this.time=LocalDateTime.parse(time);
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

    public Object[] getFieldsForInsertQuery(){
            return new Object[]{getProfileid(),getSourceid(),getRefid(),getTime(),getAsset(),
            getType(),getSubtype(),getAmount(),getFee(),getBalance()};

        }

        @Override
        public String toString(){
            return  "id = '"+ getId() + '\'' +
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
