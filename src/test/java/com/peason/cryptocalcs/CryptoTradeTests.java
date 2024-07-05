package com.peason.cryptocalcs;

import com.peason.cryptocalcs.datatypes.FiatTrade;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class CryptoTradeTests {


    @Test
    void AddTrades() {
        CryptoTradeManager cm = new CryptoTradeManager(); //init trades

        cm.addTrade(initTrade("2024-03-31","D","SOL",25,0));
        assert(cm.getTradesByCurrency().size()==1);
        cm.addTrade(initTrade("2024-04-07","B","SOL-USDT",10,160));
        assert(cm.getTradesByCurrency().size()==2);
        cm.addTrade(initTrade("2024-04-08","B","SOL-GBP",20,120));  //need to convert to usdt later
        assert(cm.getTradesByCurrency().size()==3);
        cm.addTrade(initTrade("2024-04-08","S","SOL-USDT",5,150));  //need to convert to usdt later
        cm.addTrade(initTrade("2024-04-20","W","SOL",12,0));
        assert(cm.getTradesByCurrency().size()==3);
        assert(cm.getTradesByDate().size()==4);
        //sol count should be 28
        //add tests for realised pnl
        System.out.println (cm.getTradesByCurrency().toString());
        System.out.println (cm.getTradesByDate().toString());

    }

    private FiatTrade initTrade(String dt, String type, String pair, double coins, double exrate) {
        LocalDate ld=LocalDate.parse(dt);
        FiatTrade fiatTrade =new FiatTrade();
        fiatTrade.setTradeDate(ld);
        fiatTrade.setCurrencyPair(pair);
        fiatTrade.setExchangeRate(exrate);
        fiatTrade.setSourceCurrencyCoinAmount(coins);
        fiatTrade.setDestinationCurrencyCoinAmount(coins*exrate);
        fiatTrade.setTradeType(type);
        return fiatTrade;
    }

}
