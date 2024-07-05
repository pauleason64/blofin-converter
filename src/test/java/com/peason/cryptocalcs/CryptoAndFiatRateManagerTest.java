package com.peason.cryptocalcs;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class CryptoAndFiatRateManagerTest {

    static CryptoAndFiatRateManager fc;

    @BeforeAll
    static void init() {
        fc = CryptoAndFiatRateManager.getInstance();
    }

    @Test
    void testFiatRates() {
        String row1 = "2024-04-02,USD,GBP,1.1622";
        String row2 = "2024-04-05,USD,EUR,1.18654";
        String row3 = "2024-04-05,GBP,EUR,0.925";
        String row4 = "2024-04-06,USD,GBP,1.16589405";
        String row5 = "2024-04-06,USD,EUR,1.188888";
        fc.processFiatRecord(row1);
        LocalDate ld=LocalDate.parse(row1.split(",")[0],CryptoAndFiatRateManager.dtfm);
        assert fc.getFiatRates().containsKey(ld);
        fc.processFiatRecord(row2);
        fc.processFiatRecord(row3);
        fc.processFiatRecord(row4);
        fc.processFiatRecord(row5);
        System.out.println(fc.getFiatRates());
        //get nearest rate for SOL
        System.out.println(fc.getFiatRateForDate("2024-04-06","GBP-USD"));
        assert fc.getFiatRateForDate("2024-04-02","GBP-USD")==1.1622;  //exact match
        assert fc.getFiatRateForDate("2024-04-08","GBP-USD")==1.16589405; //nearest match
    }

    @Test
    void testCryptoRates() {
        String row1 = "2024-03-31,BTC,USDT,71333";
        String row2 = "2024-03-31,SOL,USDT,202.87";
        String row3 = "2024-04-05,BTC,USDT,71200";
        String row4 = "2024-04-05,SOL,USDT,181.1";
        fc.processCryptoRecord(row1);
        assert fc.getCryptoRates().containsKey(row1.split(",")[0]);
        fc.processCryptoRecord(row2);
        fc.processCryptoRecord(row3);
        fc.processCryptoRecord(row4);
        System.out.println(fc.getCryptoRates());
        System.out.println(fc.getCryptoRateForDate("2024-04-09","SOL"));
    }
}
