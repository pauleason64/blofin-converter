package com.peason.cryptocalcs;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.peason.cryptocalcs.datatypes.*;

public class CryptoTradeManager  {

     static final DateTimeFormatter dtfm = DateTimeFormatter.ofPattern("yyyy-MM-dd");

     Map tradesByCurrency;
     Map tradesByDate;
     Map realisedPLs;

     public enum StatusCode {
         Valid(0),
         MissingRate(90),
         MissingCost(91),
         Error(99);

         public int value = 0;

         private StatusCode(int value) {
             this.value = value;
             }
         }

        public CryptoTradeManager() {
            tradesByCurrency = new HashMap<String, CryptoTrade>();
            tradesByDate = new HashMap<LocalDate, CryptoTrade>();
            realisedPLs = new HashMap<>();
        }

        public void addTrade(FiatTrade fiatTrade) {

            //todo: this WILL LIKELY FAIL once three trades for same coin are done. we need to ensure when we
            //fetch the LATEST trade by date and update that. Hashmaps are not FIFO so wrong trade could be returned
            //for a deposit the trade destCurrency will be set to null
            CryptoTrade newCryptoEntry;
            CryptoTrade cryptoTrade;
            CryptoTrade existingCryptoEntry;
            double runningTotal=0;
            String sourceCurrency;
            String destCurrency;
            switch (fiatTrade.getTradeType()) {
                case "B":
                    sourceCurrency = fiatTrade.getCurrencyPair().split("-")[0];
                    destCurrency = fiatTrade.getCurrencyPair().split("-")[1];
                    if (!tradesByCurrency.containsKey(sourceCurrency)) {
                        cryptoTrade = new CryptoTrade();
                        cryptoTrade.setCurrency(sourceCurrency);
                        cryptoTrade.setCoinsAmount(fiatTrade.getSourceCurrencyCoinAmount());
                        cryptoTrade.setAverageRate(fiatTrade.getExchangeRate());
                        cryptoTrade.setTradeDate(fiatTrade.getTradeDate());
                        tradesByCurrency.put(sourceCurrency, cryptoTrade);
                        tradesByDate.put(fiatTrade.getTradeDate(), cryptoTrade);
                    } else {
                        existingCryptoEntry = (CryptoTrade) tradesByCurrency.get(sourceCurrency);
                        newCryptoEntry = new CryptoTrade();
                        newCryptoEntry.setCurrency(sourceCurrency);
                        newCryptoEntry.setTradeDate(fiatTrade.getTradeDate());
                         runningTotal = Double.valueOf(existingCryptoEntry.getCoinsAmount() * existingCryptoEntry.getAverageRate());
                        runningTotal += Double.valueOf((fiatTrade.getExchangeRate() * fiatTrade.getSourceCurrencyCoinAmount()));
                        newCryptoEntry.setCoinsAmount(existingCryptoEntry.getCoinsAmount() + fiatTrade.getSourceCurrencyCoinAmount());
                        newCryptoEntry.setAverageRate(runningTotal / newCryptoEntry.getCoinsAmount());
                        tradesByCurrency.put(sourceCurrency, newCryptoEntry);
                        tradesByDate.put(fiatTrade.getTradeDate(),newCryptoEntry);
                    }
                    if (!tradesByCurrency.containsKey(destCurrency)) {
                        //hmmm we have a trade to buy source currency but no destination currency to buy with
                        //must be missing trade or fiat deposit
                        cryptoTrade = new CryptoTrade();
                        cryptoTrade.setCurrency(destCurrency);
                        cryptoTrade.setCoinsAmount(fiatTrade.getSourceCurrencyCoinAmount() * fiatTrade.getExchangeRate());
                        cryptoTrade.setAverageRate(0);  //na for destCurrency
                        cryptoTrade.setTradeDate(fiatTrade.getTradeDate());
                        tradesByCurrency.put(destCurrency, cryptoTrade);
                        tradesByDate.put(fiatTrade.getTradeDate(), cryptoTrade);
                    } else {
                        existingCryptoEntry = (CryptoTrade) tradesByCurrency.get(destCurrency);
                        newCryptoEntry = new CryptoTrade();
                        newCryptoEntry.setCurrency(destCurrency);
                        newCryptoEntry.setTradeDate(fiatTrade.getTradeDate());
                         runningTotal = Double.valueOf(existingCryptoEntry.getCoinsAmount() * existingCryptoEntry.getAverageRate());
                        runningTotal -= Double.valueOf((fiatTrade.getExchangeRate() * fiatTrade.getSourceCurrencyCoinAmount()));
                        newCryptoEntry.setCoinsAmount(existingCryptoEntry.getCoinsAmount() - fiatTrade.getSourceCurrencyCoinAmount());
                        newCryptoEntry.setAverageRate(existingCryptoEntry.getAverageRate()); //rate doesnt change when we sell
                        tradesByCurrency.put(destCurrency, newCryptoEntry);
                        tradesByDate.put(fiatTrade.getTradeDate(),newCryptoEntry);
                    }
                    break;
                case "S":
                //todo
                    //for now assume no errors - ie currency of source coins must exist
                    //however dest coins may not eg buy sol for usd but sell sol for gbp
                    sourceCurrency = fiatTrade.getCurrencyPair().split("-")[0];
                    destCurrency = fiatTrade.getCurrencyPair().split("-")[1];
                    existingCryptoEntry = (CryptoTrade) tradesByCurrency.get(sourceCurrency);
                    newCryptoEntry = new CryptoTrade();
                    newCryptoEntry.setCurrency(sourceCurrency);
                    newCryptoEntry.setTradeDate(fiatTrade.getTradeDate());
                     runningTotal = Double.valueOf(existingCryptoEntry.getCoinsAmount() * existingCryptoEntry.getAverageRate());
                    runningTotal -= Double.valueOf((fiatTrade.getExchangeRate() * fiatTrade.getSourceCurrencyCoinAmount()));
                    newCryptoEntry.setCoinsAmount(existingCryptoEntry.getCoinsAmount() - fiatTrade.getSourceCurrencyCoinAmount());
                    newCryptoEntry.setAverageRate(runningTotal / newCryptoEntry.getCoinsAmount());
                    tradesByCurrency.put(sourceCurrency, newCryptoEntry);
                    tradesByDate.put(fiatTrade.getTradeDate(),newCryptoEntry);

                    if (!tradesByCurrency.containsKey(destCurrency)) {
                        cryptoTrade = new CryptoTrade();
                        cryptoTrade.setCurrency(destCurrency);
                        cryptoTrade.setCoinsAmount(fiatTrade.getSourceCurrencyCoinAmount() * fiatTrade.getExchangeRate());
                        cryptoTrade.setAverageRate(0);  //na for destCurrency
                        cryptoTrade.setTradeDate(fiatTrade.getTradeDate());
                        tradesByCurrency.put(destCurrency, cryptoTrade);
                        tradesByDate.put(fiatTrade.getTradeDate(), cryptoTrade);

                    } else {
                        existingCryptoEntry = (CryptoTrade) tradesByCurrency.get(destCurrency);
                         newCryptoEntry = new CryptoTrade();
                        newCryptoEntry.setCurrency(destCurrency);
                        newCryptoEntry.setTradeDate(fiatTrade.getTradeDate());
                         runningTotal = Double.valueOf(existingCryptoEntry.getCoinsAmount() * existingCryptoEntry.getAverageRate());
                        runningTotal += Double.valueOf((fiatTrade.getExchangeRate() * fiatTrade.getSourceCurrencyCoinAmount()));
                        newCryptoEntry.setCoinsAmount(existingCryptoEntry.getCoinsAmount() + fiatTrade.getSourceCurrencyCoinAmount());
                        newCryptoEntry.setAverageRate(runningTotal / newCryptoEntry.getCoinsAmount());
                        tradesByCurrency.put(destCurrency, newCryptoEntry);
                        tradesByDate.put(fiatTrade.getTradeDate(),newCryptoEntry);
                    }
                    break;
                case "D" : //deposit
                    //for deposits, we just need to update the currency amount, no calculations are performed
                    //later - A LOT LATER - when we can support transfers from other exchanges then we think about that
                    sourceCurrency = fiatTrade.getCurrencyPair();
                    if (!tradesByCurrency.containsKey(sourceCurrency)) {
                        cryptoTrade = new CryptoTrade();
                        cryptoTrade.setCurrency(sourceCurrency);
                        cryptoTrade.setCoinsAmount(fiatTrade.getSourceCurrencyCoinAmount());
                        cryptoTrade.setAverageRate(0);
                        cryptoTrade.setTradeDate(fiatTrade.getTradeDate());
                        tradesByCurrency.put(sourceCurrency, cryptoTrade);
                        tradesByDate.put(fiatTrade.getTradeDate(), cryptoTrade);
                        fiatTrade.setStatus(StatusCode.MissingCost);

                    } else {
                        existingCryptoEntry = (CryptoTrade) tradesByCurrency.get(sourceCurrency);
                        newCryptoEntry = new CryptoTrade();
                        newCryptoEntry.setCurrency(sourceCurrency);
                        newCryptoEntry.setTradeDate(fiatTrade.getTradeDate());
                        runningTotal = Double.valueOf(existingCryptoEntry.getCoinsAmount() * existingCryptoEntry.getAverageRate());
                        runningTotal += Double.valueOf((fiatTrade.getExchangeRate() * fiatTrade.getSourceCurrencyCoinAmount()));
                        newCryptoEntry.setCoinsAmount(existingCryptoEntry.getCoinsAmount() + fiatTrade.getSourceCurrencyCoinAmount());
                        newCryptoEntry.setAverageRate(runningTotal / newCryptoEntry.getCoinsAmount());
                        tradesByCurrency.put(sourceCurrency, newCryptoEntry);
                        tradesByDate.put(fiatTrade.getTradeDate(), newCryptoEntry);
                        fiatTrade.setStatus(StatusCode.MissingCost);
                    }
                    break;
                case "W" : //withdrawal
                    sourceCurrency = fiatTrade.getCurrencyPair();
                    existingCryptoEntry = (CryptoTrade) tradesByCurrency.get(sourceCurrency);
                    newCryptoEntry = new CryptoTrade();
                    newCryptoEntry.setCurrency(sourceCurrency);
                    newCryptoEntry.setTradeDate(fiatTrade.getTradeDate());
                    runningTotal = Double.valueOf((existingCryptoEntry.getCoinsAmount() - fiatTrade.getSourceCurrencyCoinAmount()) * existingCryptoEntry.getAverageRate());
                    newCryptoEntry.setCoinsAmount(existingCryptoEntry.getCoinsAmount() - fiatTrade.getSourceCurrencyCoinAmount());
                    newCryptoEntry.setAverageRate(existingCryptoEntry.getAverageRate());
                    tradesByCurrency.put(sourceCurrency, newCryptoEntry);
                    tradesByDate.put(fiatTrade.getTradeDate(), newCryptoEntry);
                break;
            }

        }
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

    public  Map getTradesByCurrency() {
        return tradesByCurrency;
    }

    public  void setTradesByCurrency(Map tradesByCurrency) {
        this.tradesByCurrency = tradesByCurrency;
    }

    public Map getTradesByDate() {
        return tradesByDate;
    }

    public void setTradesByDate(Map tradesByDate) {
        this.tradesByDate = tradesByDate;
    }

    public  Map getRealisedPLs() {
        return realisedPLs;
    }

    public  void setRealisedPLs(Map realisedPLs) {
       this.realisedPLs = realisedPLs;
    }
}
