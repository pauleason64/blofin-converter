package com.peason.databasetables;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class Trade {

            @JsonProperty("leverage")
            @JsonIgnore
            private String leverage;
            @JsonProperty("maker")
            @JsonIgnore
            private boolean maker;
            @JsonProperty("postxid")
            @JsonIgnore
            private String postxid;

            private long id;
            private long profileid;
            private long sourceid;
            private String tradekey;

            @JsonProperty("margin")
            private String margin;

            @JsonProperty("cost")
            private String cost;

            @JsonProperty("fee")
            private String fee;

            @JsonProperty("type")
            private String tradetype;

            @JsonProperty("pair")
            private String pair;

            @JsonProperty("vol")
            private long volume;

            @JsonProperty("tradeid")
            private int tradeId;

            @JsonProperty("price")
            private String price;

            @JsonProperty("ordertxid")
            private String ordertxid;

            @JsonProperty("time")
            private Date tradedt;

            @JsonProperty("ordertype")
            private String otype;

            @JsonProperty("misc")
            private String misc;

            private String ledgerids;

            private String resultKey;

            public long getId() {
            return id;
            }

            public void setId(long id) {
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
            return tradetype;
            }

            public String getPair() {
            return pair;
            }

            public long getVol() {
            return volume;
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

            public String getPostxid() {
            return postxid;
            }

            public String getOrdertype() {
            return otype;
            }

            public String getMisc() {
            return misc;
            }

            public void setResultKey(String resultKey) {
            this.resultKey = resultKey;
            }

            public String getResultKey() {
            return resultKey;
            }

            public String getTradeKey() {
            return tradekey;
            }

            public void setTradeKey(String tradeKey) {
            this.tradekey = tradeKey;
            }

            public long getProfileid() {return profileid;}

            public void setProfileid(long profileid) {this.profileid = profileid;}

            public long getSourceid() {return sourceid;}

            public void setSourceid(long sourceid) {this.sourceid = sourceid;}

            public String getTradekey() {
            return tradekey;
            }

            public void setTradekey(String tradekey) {
            this.tradekey = tradekey;
            }

            public void setMargin(String margin) {
            this.margin = margin;
            }

            public void setCost(String cost) {
            this.cost = cost;
            }

            public void setFee(String fee) {
            this.fee = fee;
            }

            public String getTradetype() {
            return tradetype;
            }

            public void setTradetype(String tradetype) {
            this.tradetype = tradetype;
            }

            public void setPair(String pair) {
            this.pair = pair;
            }

            public long getVolume() {
            return volume;
            }

            public void setVolume(long volume) {
            this.volume = volume;
            }

            public void setTradeId(int tradeId) {
            this.tradeId = tradeId;
            }

            public void setPrice(String price) {
            this.price = price;
            }

            public void setOrdertxid(String ordertxid) {
            this.ordertxid = ordertxid;
            }

            public Date getTradedt() {
            return tradedt;
            }

            @JsonProperty("time")
            public void setTime(Double time) {
                this.tradedt=new Date(time.longValue()*1000L);
            }


            public void setTradedt(Timestamp tradedt) {
                this.tradedt=new Date(tradedt.getTime());
            }

            public String getOtype() {
            return otype;
            }

            public void setOtype(String otype) {
            this.otype = otype;
            }

            public void setMisc(String misc) {
            this.misc = misc;
            }

            public Object[] getFieldsForInsertQuery(){
            return new Object[]{getProfileid(),getSourceid(),getTradeKey(),getType(),getTradedt(),getPair(),
            getOrdertype(),getVol(),getPrice(),getFee(),getCost(),
            getMargin(),getOrdertxid(),getTradeId(),getLedgerids()};
            }

            public void setLedgerIds(ArrayList<String> ledgeridArray) {
                if (ledgeridArray==null | ledgeridArray.isEmpty()) {
                    this.ledgerids="";
                    return;
                }
                this.ledgerids= ledgeridArray.stream().collect(
                        Collectors.joining(","));
            }

            @JsonProperty("ledgers")
            public String getLedgerids() {
                return ledgerids;
            }

            @Override
                public String toString() {
            return
            "Ledger{" +"id = '" + getId() + '\'' +
            ",key = '" + getResultKey() + '\'' +
            ",type = '" + getType() + '\'' +
            ",pair = '" + getPair() + '\'' +
            ",time = '" + getTradedt() + '\'' +
            ",ordertype = '" + getOrdertype() +
            ",vol = '" + getVol() + '\'' +
            ",price = '" + getPrice() + '\'' +
            ",fee = '" + getFee() + '\'' +
            ",cost = '" + getCost() + '\'' +
            ",margin = '" + getMargin() + '\'' +
            ",leverage = '" + getLeverage() + '\'' +
            ",txid = '" + getOrdertxid() + '\'' +
            ",tradeid = '" + getTradeId() + '\'' +
            ",ledgerids = '" + getLedgerids() + '\'' +
            ",misc = '" + getMisc() + '\'' +
            "}";
            }

        }
