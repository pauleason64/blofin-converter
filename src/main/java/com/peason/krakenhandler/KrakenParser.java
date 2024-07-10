package com.peason.krakenhandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public class KrakenParser {

        public KrakenParser() {}

       public TradesHistoryResult parseTradeHistory(String response) {

           ObjectMapper objectMapper = new ObjectMapper();
           TradesHistoryResult tradesHistoryResult=null;
            try {
                // Create ObjectMapper instance

                tradesHistoryResult=   objectMapper.readValue(response, TradesHistoryResult.class);

            } catch (Exception e) {
                e.printStackTrace();
            }
           return tradesHistoryResult;
        }

    public LedgerHistoryResult parseLedgers(String response) {

        ObjectMapper objectMapper = new ObjectMapper();
        LedgerHistoryResult ledgerResult=null;
        try {
            // Create ObjectMapper instance

            ledgerResult= objectMapper.readValue(response, LedgerHistoryResult.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ledgerResult;
    }
}
