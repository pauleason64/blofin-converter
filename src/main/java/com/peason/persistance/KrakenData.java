package com.peason.persistance;

import com.peason.databasetables.Ledger;
import com.peason.databasetables.Trade;
import com.peason.services.KrakenDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@Repository
public class KrakenData {

    @Autowired
    KrakenDAO krakenDAO;

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    //These counts are used during the recursive calls to the apis. They dont necessarily relate to the total available
    //e.g. On first call we will fetch all of them, say 2000 records and keep calling 50 at a time
    //on subsequent update calls we will only fetch the new ones based on dates.
    //ultimately the records will be saved to the DB so we wont use this long term
    private long availableLedgerCount=0 ;
    private long availableTradeCount=0;
    private long fetchedLedgerOffset=0;
    private long fetchedTradeOffset=0;


    public KrakenData() {}

    public List<Trade> tradeList = new ArrayList<>();
    public List<Ledger> ledgerList = new ArrayList<>();

    public List<String> getUniqueLedgerAssets() {
    if (ledgerList==null) return new ArrayList<String>();
    List<String> assets = ledgerList.stream().filter(distinctByKey(l->l.getAsset()))
              .map(l->l.getAsset()).toList();
    return assets;
  }

 public List<String> getUniqueTradePairs() {
    if (tradeList==null) return new ArrayList<String>();
    List<String>  pairs = tradeList.stream().filter(distinctByKey(l->l.getPair()))
            .map(l->l.getPair()).toList();
    return pairs;
}


public void addLedgers(HashMap < String, Ledger> ledgers) {
    for (Map.Entry<String, Ledger> ledger : ledgers.entrySet()) {
        //changing the key to the date
        ledger.getValue().setLedgerKey(ledger.getKey());
        ledgerList.add(ledger.getValue());
    }
    //sort newest first
    ledgerList.sort((a, b) -> b.getTradedt().compareTo(a.getTradedt()));
}


public void addTrades(HashMap <String, Trade> trades ) {
        for (Map.Entry<String,Trade> trade : trades.entrySet()) {
            trade.getValue().setTradeKey(trade.getKey());
            tradeList.add(trade.getValue());
        }
    //sort newest first
    tradeList.sort((a, b)->b.getTradedt().compareTo(a.getTradedt()));
    }

    public void addTrades(ArrayList <Trade> trades ) {
        tradeList.addAll(trades);
        //sort newest first
        tradeList.sort((a, b)->b.getTradedt().compareTo(a.getTradedt()));
    }

    public void refreshLedgersFromDB() {
       ledgerList=krakenDAO.refreshLedgersFromDB();
    }
    public void refreshTradesFromDB() {
        tradeList=krakenDAO.refreshTradesFromDB();
    }
    public long getAvailableLedgerCount() {
        return availableLedgerCount;
    }
    public void setAvailableLedgerCount(long availableLedgerCount) {this.availableLedgerCount = availableLedgerCount;}
    public long getAvailableTradeCount() {
        return availableTradeCount;
    }
  public void setAvailableTradeCount(long availableTradeCount) { this.availableTradeCount = availableTradeCount;}
    public long getFetchedLedgerOffset() {
        return fetchedLedgerOffset;
    }
    public void setFetchedLedgerOffset(long fetchedLedgerOffset) {this.fetchedLedgerOffset = fetchedLedgerOffset;}
    public long getFetchedTradeOffset() {
        return fetchedTradeOffset;
    }
    public void setFetchedTradeOffset(long fetchedTradeOffset) {
        this.fetchedTradeOffset = fetchedTradeOffset;}

    public List<Trade> getTradeList() {
        return tradeList;
    }

    public List<Ledger> getLedgerList() {
        return ledgerList;
    }
}
