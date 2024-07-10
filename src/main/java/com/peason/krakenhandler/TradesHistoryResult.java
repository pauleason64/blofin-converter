package com.peason.krakenhandler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TradesHistoryResult  extends KrakenResult {

	public static String[] ColHeadings = "T-Type,Pair,O-Type,Date,Vol,Price,Fee,Cost,Margin,txid,tradeid,ledgers".split(",");
	public static int[] ColWidthPercent = {5, 8, 5, 10, 10, 10, 8, 10, 8, 11, 11,4};

	public TradesHistoryResult() {
	}

	@JsonProperty("result")
	Result result;

	public Result getResults() {
		return result;
	}

	public void setResults(Result result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return
				"Result{" +
						",error = '" + getErrors() + '\'' +
						",count = '" + getCount() +
						",results = '" + getResults() + '\'' +
						"}";
	}

	static class Result {
		@JsonProperty("error")
		@JsonIgnore
		private List<Object> error;

		@JsonProperty("count")
		private int count;

		@JsonProperty("trades")
		public HashMap<String, Trade> trades;

		public List<Object> getError() {
			return error;
		}

		public int getCount() {
			return count;
		}

		@Override
		public String toString() {
			return
					"Result{" +
							",error = '" + error + '\'' +
							",count = '" + count +
							",trades = '" + trades + '\'' +
							"}";
		}
	}

	static class Trade {

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

		@Override
		public String toString() {
			return
					"Ledger{" +
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
							",ledgerids = '" + getLedgerIds().toString() + '\'' +
							",misc = '" + getMisc() + '\'' +
							"}";
		}
	}
}

