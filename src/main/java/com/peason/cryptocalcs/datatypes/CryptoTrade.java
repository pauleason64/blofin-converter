package com.peason.cryptocalcs.datatypes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CryptoTrade {

        static final DateTimeFormatter dtfm = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String currency;
        double coinsAmount;
        double averageRate;
        LocalDate tradeDate;

        public String getCurrency() {
                return currency;
        }

        public void setCurrency(String currency) {
                this.currency = currency;
        }

        public double getCoinsAmount() {
                return coinsAmount;
        }

        public void setCoinsAmount(double coinsAmount) {
                this.coinsAmount = coinsAmount;
        }

        public double getAverageRate() {
                return averageRate;
        }

        public void setAverageRate(double averageRate) {
                this.averageRate = averageRate;
        }


        public String getTradeDate() {
                return tradeDate.toString();
        }

        public void setTradeDate(LocalDate tradeDate) {
                this.tradeDate = tradeDate;
        }

        public void setTradeDate(String tradeDate) {
                this.tradeDate = LocalDate.parse(tradeDate,dtfm);

        }

        @Override
        public String toString() {
                return new String("Date:" + getTradeDate() +",Curr:" + getCurrency() + ",Coins:" + getCoinsAmount() + ",Exrate:" + getAverageRate());
        }

}
