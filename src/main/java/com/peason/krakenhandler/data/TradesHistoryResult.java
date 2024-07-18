package com.peason.krakenhandler.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.peason.databasetables.Trade;


import java.util.HashMap;

public class TradesHistoryResult  extends KrakenResult {

	public static String[] ColHeadings = "T-Type,Pair,O-Type,Date,Vol,Price,Fee,Cost,Margin,txid,tradeid,ledgers".split(",");
	public static int[] ColWidthPercent = {5, 8, 5, 10, 10, 10, 8, 10, 8, 11, 11,4};

	public TradesHistoryResult() {
	}

	@JsonProperty("result")
	Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return
				"Result{" +
						",error = '" + getErrors() + '\'' +
//						",count = '" + getCount() +
						",results = '" + getResult() + '\'' +
						"}";
	}

	public static class Result {

		@JsonProperty("count")
		private int count;

		@JsonProperty("trades")
		public HashMap<String, Trade> trades;

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public HashMap<String, Trade> getTrades() {
			return trades;
		}

		@Override
		public String toString() {
			return
					"Result{" +
//							",error = '" + error + '\'' +
							",count = '" + count +
							",trades = '" + trades + '\'' +
							"}";
		}
	}

}

