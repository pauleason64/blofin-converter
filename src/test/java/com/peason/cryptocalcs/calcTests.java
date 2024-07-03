package com.peason.cryptocalcs;

import com.peason.cryptocalcs.datatypes.CryptoCurrency;
import com.peason.cryptocalcs.datatypes.Trade;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

public class calcTests {


    @Test
    void AddTrades() {
        CryptoTradeManager cm = new CryptoTradeManager(); //init trades

        cm.addTrade(initTrade(new Date(),"B","SOL-USDT",10,160));
        assert(cm.getCryptos().size()==2);
        cm.addTrade(initTrade(new Date(),"B","SOL-GBP",20,120));  //need to convert to usdt later
        assert(cm.getCryptos().size()==3);
        cm.addTrade(initTrade(new Date(),"S","SOL-USDT",5,150));  //need to convert to usdt later
        assert(cm.getCryptos().size()==3);
        assert(cm.getTrades().size()==3);
        //add tests for realised pnl

    }

    private Trade initTrade(Date dt,String type, String pair, double coins, double exrate) {
        Trade trade=new Trade();
        trade.setTradeDate(dt);
        trade.setCurrencyPair(pair);
        trade.setExchangeRate(exrate);
        trade.setSourceCurrencyCoinAmount(coins);
        trade.setDestinationCurrencyCoinAmount(coins*exrate);
        trade.setTradeType(type);
        return trade;
    }

}
