package com.peason.krakenhandler.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class Trade {

        private String id;
        private String tradeKey;
        private long profileid;
        private long sourceid;

        @JsonProperty("leverage")
        private String leverage;

        @JsonProperty("margin")
        private String margin;

        @JsonProperty("cost")
        private String cost;

        @JsonProperty("fee")
        private String fee;

        @JsonProperty("maker")
        private boolean maker;

        @JsonProperty("type")
        private String type;

        @JsonProperty("pair")
        private String pair;

        @JsonProperty("vol")
        private String vol;

        @JsonProperty("trade_id")
        private int tradeId;

        @JsonProperty("price")
        private String price;

        @JsonProperty("ordertxid")
        private String ordertxid;

        @JsonProperty("time")
        private Date time;

        @JsonProperty("postxid")
        private String postxid;

        @JsonProperty("ordertype")
        private String ordertype;

        @JsonProperty("misc")
        private String misc;

        @JsonProperty("ledgers")
        private ArrayList<String> ledgerIds;

        private String resultKey;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("posstatus")
        @JsonIgnore
        String posstatus;

        public String getLeverage() {
            return leverage;
        }

        public String getMargin() {
            return margin;
        }

        public String getCost() {
            return cost;
        }

        public String getFee() {
            return fee;
        }

        public boolean isMaker() {
            return maker;
        }

        public String getType() {
            return type;
        }

        public String getPair() {
            return pair;
        }

        public String getVol() {
            return vol;
        }

        public int getTradeId() {
            return tradeId;
        }

        public String getPrice() {
            return price;
        }

        public String getOrdertxid() {
            return ordertxid;
        }

        public Date getTime() {
            return time;
        }

        public String getPostxid() {
            return postxid;
        }

        public String getOrdertype() {
            return ordertype;
        }

        public String getMisc() {
            return misc;
        }

        public void setTime(Double time) {
            this.time = new Date(time.longValue() * 1000L);
        }

        public ArrayList<String> getLedgerIds() {
            return ledgerIds;
        }

        public void setResultKey(String resultKey) {
            this.resultKey = resultKey;
        }

        public String getResultKey() {
            return resultKey;
        }

        public String getTradeKey() {
            return tradeKey;
        }

        public void setTradeKey(String tradeKey) {
            this.tradeKey = tradeKey;
        }

        public long getProfileid() {return profileid;}

        public void setProfileid(long profileid) {this.profileid = profileid;}

        public long getSourceid() {return sourceid;}

        public void setSourceid(long sourceid) {this.sourceid = sourceid;}

        public Object[] getFieldsForInsertQuery(){
            return new Object[]{getProfileid(),getSourceid(),getTradeKey(),getType(),getTime(),getPair(),
                    getOrdertype(),getVol(),getPrice(),getFee(),getCost(),
                    getMargin(),getOrdertxid(),getTradeId(),getLedgerIdsAsString()};
        }

    public void setLedgerIds(ArrayList<String> ledgerIds) {
        this.ledgerIds = ledgerIds!=null ? ledgerIds : new ArrayList<String>();
    }

    public String getLedgerIdsAsString() {
            if (ledgerIds==null|| ledgerIds.isEmpty()) return "";
            return ledgerIds.stream().collect(
                Collectors.joining(","));
    }

    @Override
        public String toString() {
            return
                    "Ledger{" +"id = '" + getId() + '\'' +
                            ",key = '" + getResultKey() + '\'' +
                            ",type = '" + getType() + '\'' +
                            ",pair = '" + getPair() + '\'' +
                            ",time = '" + getTime() + '\'' +
                            ",ordertype = '" + getOrdertype() +
                            ",vol = '" + getVol() + '\'' +
                            ",price = '" + getPrice() + '\'' +
                            ",fee = '" + getFee() + '\'' +
                            ",cost = '" + getCost() + '\'' +
                            ",margin = '" + getMargin() + '\'' +
                            ",leverage = '" + getLeverage() + '\'' +
                            ",txid = '" + getOrdertxid() + '\'' +
                            ",tradeid = '" + getTradeId() + '\'' +
                            ",ledgerids = '" + getLedgerIdsAsString() + '\'' +
                            ",misc = '" + getMisc() + '\'' +
                            "}";
        }

}
