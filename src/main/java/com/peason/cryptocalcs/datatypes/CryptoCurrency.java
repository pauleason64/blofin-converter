package com.peason.cryptocalcs.datatypes;

import java.util.Date;

public class CryptoCurrency  {

        String currency;
        double coinsAmount;
        double averageRate;
        Date tradeDate;

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


        public Date getTradeDate() {
                return tradeDate;
        }


        public void setTradeDate(Date tradeDate) {
                this.tradeDate = tradeDate;
        }


}
