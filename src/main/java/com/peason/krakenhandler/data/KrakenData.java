package com.peason.krakenhandler.data;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class KrakenData {

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

   // static KrakenData instance;
    //These counts are used during the recursive calls to the apis. They dont necessarily relate to the total available
    //e.g. On first call we will fetch all of them, say 2000 records and keep calling 50 at a time
    //on subsequent update calls we will only fetch the new ones based on dates.
    //ultimately the records will be saved to the DB so we wont use this long term
    private long availableLedgerCount=0 ;
    private long availableTradeCount=0;
    private long fetchedLedgerOffset=0;
    private long fetchedTradeOffset=0;


    public KrakenData() {}

    public List<TradesHistoryResult.Trade> tradeList = new ArrayList<>();
    public List<LedgerHistoryResult.Ledger> ledgerList = new ArrayList<>();

//    public static synchronized KrakenData getInstance() {
//    if (null==instance) instance=new KrakenData();
//    return instance;
//  }

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


public void addLedgers(HashMap < String, LedgerHistoryResult.Ledger> ledgers) {
    for (Map.Entry<String, LedgerHistoryResult.Ledger> ledger : ledgers.entrySet()) {
        //changing the key to the date
        ledger.getValue().setLedgerKey(ledger.getKey());
        ledgerList.add(ledger.getValue());
    }
    //sort newest first
    ledgerList.sort((a, b) -> b.getTime().compareTo(a.getTime()));
}


public void addTrades(HashMap <String, TradesHistoryResult.Trade> trades ) {
        for (Map.Entry<String,TradesHistoryResult.Trade> trade : trades.entrySet()) {
            trade.getValue().setTradeKey(trade.getKey());
            tradeList.add(trade.getValue());
        }
    //sort newest first
    tradeList.sort((a, b)->b.getTime().compareTo(a.getTime()));
    }

    public long getAvailableLedgerCount() {
        return availableLedgerCount;
    }

    public void setAvailableLedgerCount(long availableLedgerCount) {
        this.availableLedgerCount = availableLedgerCount;
    }

    public long getAvailableTradeCount() {
        return availableTradeCount;
    }

    public void setAvailableTradeCount(long availableTradeCount) {
        this.availableTradeCount = availableTradeCount;
    }

    public long getFetchedLedgerOffset() {
        return fetchedLedgerOffset;
    }

    public void setFetchedLedgerOffset(long fetchedLedgerOffset) {
        this.fetchedLedgerOffset = fetchedLedgerOffset;
    }

    public long getFetchedTradeOffset() {
        return fetchedTradeOffset;
    }

    public void setFetchedTradeOffset(long fetchedTradeOffset) {
        this.fetchedTradeOffset = fetchedTradeOffset;
    }
}
