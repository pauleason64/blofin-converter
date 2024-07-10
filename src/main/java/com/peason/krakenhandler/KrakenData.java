package com.peason.krakenhandler;

import java.util.*;

public class KrakenData {

    static KrakenData instance;
    //These counts are used during the recursive calls to the apis. They dont necessarily relate to the total available
    //e.g. On first call we will fetch all of them, say 2000 records and keep calling 50 at a time
    //on subsequent update calls we will only fetch the new ones based on dates.
    //ultimately the records will be saved to the DB so we wont use this long term
    static long availableLedgerCount=0 ;
    static long availableTradeCount=0;
    static long fetchedLedgerOffset=0;
    static long fetchedTradeOffset=0;

    //store all trades and ledgers by time field to avoid sorting requirements on frontend
    static Comparator tradeDateComparator = new Comparator<TradesHistoryResult.Trade>() {

            public int compare(TradesHistoryResult.Trade t1, TradesHistoryResult.Trade t2) {
                return (t1.getTime().compareTo(t2.getTime()));
            }
        };

    static      Comparator ledgerDateComparator = new Comparator<LedgerHistoryResult.Ledger>() {

            public int compare(LedgerHistoryResult.Ledger l1, LedgerHistoryResult.Ledger l2) {
                return (l1.getTime().compareTo(l2.getTime()));
            }
        };

  private KrakenData() {}

  public static synchronized KrakenData getInstance() {
    if (null==instance) instance=new KrakenData();
    return instance;
  }

  private TreeMap<String, TradesHistoryResult.Trade> allTrades =
                new TreeMap<String, TradesHistoryResult.Trade>((tradeDateComparator));


  private TreeMap<String, LedgerHistoryResult.Ledger> allLedgers =
                new TreeMap<String, LedgerHistoryResult.Ledger>((ledgerDateComparator));

    public  TreeMap<String, TradesHistoryResult.Trade> getAllTrades () {
            return allTrades;     }

    public void addLedgers(HashMap < String, LedgerHistoryResult.Ledger > ledgers){
            allLedgers.putAll(ledgers);     }

    public void addTrades(HashMap<String,TradesHistoryResult.Trade> trades) {
        allTrades.putAll(trades);}

}
