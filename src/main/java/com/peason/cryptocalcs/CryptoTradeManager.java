package com.peason.cryptocalcs;
import java.util.*;
import com.peason.cryptocalcs.datatypes.*;

public class CryptoTradeManager  {

     List trades ;
     Map cryptos;
     Map realisedPLs;

        public CryptoTradeManager() {
            trades = new ArrayList<Trade>();
            cryptos = new HashMap<String, CryptoCurrency>();
            realisedPLs = new HashMap<>();
        }

        public void addTrade(Trade trade) {

            //todo: this WILL LIKELY FAIL once three trades for same coin are done. we need to ensure when we
            //fetch the LATEST trade by date and update that. Hashmaps are not FIFO so wrong trade could be returned
            CryptoCurrency newCryptoEntry;
            CryptoCurrency cryptoCurrency;
            CryptoCurrency existingCryptoEntry;
            double runningTotal=0;

            trades.add(trade);
            String sourceCurrency = trade.getCurrencyPair().split("-")[0];
            String destCurrency = trade.getCurrencyPair().split("-")[1];
            switch (trade.getTradeType()) {
                case "B":
                    if (!cryptos.containsKey(sourceCurrency)) {
                        cryptoCurrency = new CryptoCurrency();
                        cryptoCurrency.setCurrency(sourceCurrency);
                        cryptoCurrency.setCoinsAmount(trade.getSourceCurrencyCoinAmount());
                        cryptoCurrency.setAverageRate(trade.getExchangeRate());
                        cryptoCurrency.setTradeDate(trade.getTradeDate());
                        cryptos.put(sourceCurrency, cryptoCurrency);
                    } else {
                        existingCryptoEntry = (CryptoCurrency) cryptos.get(sourceCurrency);
                        newCryptoEntry = new CryptoCurrency();
                        newCryptoEntry.setCurrency(sourceCurrency);
                        newCryptoEntry.setTradeDate(trade.getTradeDate());
                         runningTotal = Double.valueOf(existingCryptoEntry.getCoinsAmount() * existingCryptoEntry.getAverageRate());
                        runningTotal += Double.valueOf((trade.getExchangeRate() * trade.getSourceCurrencyCoinAmount()));
                        newCryptoEntry.setCoinsAmount(existingCryptoEntry.getCoinsAmount() + trade.getSourceCurrencyCoinAmount());
                        newCryptoEntry.setAverageRate(runningTotal / newCryptoEntry.getCoinsAmount());
                        cryptos.put(sourceCurrency, newCryptoEntry);
                    }
                    if (!cryptos.containsKey(destCurrency)) {
                        //hmmm we have a trade to buy source currency but no destination currency to buy with
                        //must be missing trade or fiat deposit
                        cryptoCurrency = new CryptoCurrency();
                        cryptoCurrency.setCurrency(destCurrency);
                        cryptoCurrency.setCoinsAmount(trade.getSourceCurrencyCoinAmount() * trade.getExchangeRate());
                        cryptoCurrency.setAverageRate(0);  //na for destCurrency
                        cryptoCurrency.setTradeDate(trade.getTradeDate());
                        cryptos.put(destCurrency, cryptoCurrency);
                    } else {
                        existingCryptoEntry = (CryptoCurrency) cryptos.get(destCurrency);
                        newCryptoEntry = new CryptoCurrency();
                        newCryptoEntry.setCurrency(destCurrency);
                        newCryptoEntry.setTradeDate(trade.getTradeDate());
                         runningTotal = Double.valueOf(existingCryptoEntry.getCoinsAmount() * existingCryptoEntry.getAverageRate());
                        runningTotal -= Double.valueOf((trade.getExchangeRate() * trade.getSourceCurrencyCoinAmount()));
                        newCryptoEntry.setCoinsAmount(existingCryptoEntry.getCoinsAmount() - trade.getSourceCurrencyCoinAmount());
                        newCryptoEntry.setAverageRate(existingCryptoEntry.getAverageRate()); //rate doesnt change when we sell
                        cryptos.put(destCurrency, newCryptoEntry);
                    }
                    break;
                case "S":
                //todo
                    //for now assume no errors - ie currency of source coins must exist
                    //however dest coins may not eg buy sol for usd but sell sol for gbp
                    existingCryptoEntry = (CryptoCurrency) cryptos.get(sourceCurrency);
                    newCryptoEntry = new CryptoCurrency();
                    newCryptoEntry.setCurrency(sourceCurrency);
                    newCryptoEntry.setTradeDate(trade.getTradeDate());
                     runningTotal = Double.valueOf(existingCryptoEntry.getCoinsAmount() * existingCryptoEntry.getAverageRate());
                    runningTotal -= Double.valueOf((trade.getExchangeRate() * trade.getSourceCurrencyCoinAmount()));
                    newCryptoEntry.setCoinsAmount(existingCryptoEntry.getCoinsAmount() - trade.getSourceCurrencyCoinAmount());
                    newCryptoEntry.setAverageRate(runningTotal / newCryptoEntry.getCoinsAmount());
                    cryptos.put(sourceCurrency, newCryptoEntry);

                    if (!cryptos.containsKey(destCurrency)) {
                        CryptoCurrency cryptoCurrency = new CryptoCurrency();
                        cryptoCurrency.setCurrency(destCurrency);
                        cryptoCurrency.setCoinsAmount(trade.getSourceCurrencyCoinAmount() * trade.getExchangeRate());
                        cryptoCurrency.setAverageRate(0);  //na for destCurrency
                        cryptoCurrency.setTradeDate(trade.getTradeDate());
                        cryptos.put(destCurrency, cryptoCurrency);
                    } else {
                        existingCryptoEntry = (CryptoCurrency) cryptos.get(destCurrency);
                         newCryptoEntry = new CryptoCurrency();
                        newCryptoEntry.setCurrency(destCurrency);
                        newCryptoEntry.setTradeDate(trade.getTradeDate());
                         runningTotal = Double.valueOf(existingCryptoEntry.getCoinsAmount() * existingCryptoEntry.getAverageRate());
                        runningTotal += Double.valueOf((trade.getExchangeRate() * trade.getSourceCurrencyCoinAmount()));
                        newCryptoEntry.setCoinsAmount(existingCryptoEntry.getCoinsAmount() + trade.getSourceCurrencyCoinAmount());
                        newCryptoEntry.setAverageRate(runningTotal / newCryptoEntry.getCoinsAmount());
                        cryptos.put(destCurrency, newCryptoEntry);
                    }
                    break;
                case "D" : //deposit
                case "W" : //withdrawal
                break;
            }
        }
//                Map> TRADES = new HashMap<>();
//
//                // Create and add sample TRADE objects
//                TRADE t1 = new TRADE(new Date(), "USD", 100.);
//                addTradeToMap(TRADES, t1);
//
//                TRADE t2 = new TRADE(new Date(), "EUR", 200.);
//                addTradeToMap(TRADES, t2);
//
//                // Example to retrieve the most recent Trade object for a given currency
//                String currencyKey = "USD";
//                Optional mostRecentTradeForCurrency =
//                        getMostRecentTradeForCurrency(TRADES.get(currencyKey));
//
//                if (mostRecentTradeForCurrency.isPresent()) {
//                    System.out.println("Most recent USD Trade: " + mostRecentTradeForCurrency.get().getTradeDate());
//                } else {
//                    System.out.println("No trades found for USD");
//                }
//            }
//
//            private static void addTradeToMap(Map> map, TRADE t) {
//                map.computeIfAbsent(t.getTradeCurrency(), k -> new ArrayList<>()).add(t);
//            }
//
//            private static Optional getMostRecentTradeForCurrency(List trades) {
//                return trades.stream()
//                        .max(Comparator.comparing(TRADE::getTradeDate));
//            }
//        }

    public void saveToDatabase() {

    }

    public  List getTrades() {
        return trades;
    }

    public  void setTrades(List trades) {
        this.trades = trades;
    }

    public  Map getCryptos() {
        return cryptos;
    }

    public  void setCryptos(Map cryptos) {
        this.cryptos = cryptos;
    }

    public  Map getRealisedPLs() {
        return realisedPLs;
    }

    public  void setRealisedPLs(Map realisedPLs) {
       this.realisedPLs = realisedPLs;
    }
}
