package com.peason.krakenhandler.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peason.databasetables.LedgerHistoryResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
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
