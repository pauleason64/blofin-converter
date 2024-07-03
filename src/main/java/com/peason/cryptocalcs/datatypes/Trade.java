package com.peason.cryptocalcs.datatypes;

import java.util.Date;

public class Trade {
/* example

We bought 10 SOL for a cost of 1600 USDT, which was calculated
by multiplying the coin amount by the exchange rate

Therefore we need to add an entry to:
1) SOl - to increment the number of coins held and calculate the new average exchange rate
2) USDT - to decrease the amount of USDT held

For now lets ignore fees.
 */
     String currencyPair;
     Date tradeDate;
     double sourceCurrencyCoinAmount;
     double destinationCurrencyCoinAmount;
     double exchangeRate;
     double realisedPL;
     String tradeType; // "B" or "S"

    public String getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(String currencyPair) {
        this.currencyPair = currencyPair;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public double getDestinationCurrencyCoinAmount() {
        return destinationCurrencyCoinAmount;
    }

    public void setDestinationCurrencyCoinAmount(double destinationCurrencyCoinAmount) {
        this.destinationCurrencyCoinAmount = destinationCurrencyCoinAmount;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public double getSourceCurrencyCoinAmount() {
        return sourceCurrencyCoinAmount;
    }

    public void setSourceCurrencyCoinAmount(double sourceCurrencyCoinAmount) {
        this.sourceCurrencyCoinAmount = sourceCurrencyCoinAmount;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public double getRealisedPL() {
        return realisedPL;
    }

    public void setRealisedPL(double realisedPL) {
        this.realisedPL = realisedPL;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

}
