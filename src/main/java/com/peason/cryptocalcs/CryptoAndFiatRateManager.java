package com.peason.cryptocalcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class CryptoAndFiatRateManager {

    //this initial version will load a csv file of xrates which has been obtained from a public api
    //api integration isnt free or if free only allows a few calls

    static final DateTimeFormatter dtfm = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static String separator = FileSystems.getDefault().getSeparator();
    static String defaultFilePath = new File(".").getAbsolutePath();
    static String arg="";
    static Logger logger = Logger.getLogger(CryptoAndFiatRateManager.class.getName());
    TreeMap<LocalDate,HashMap<String,HashMap<String,Double>>> fiatRates =new TreeMap<>();//Date,base currency for date, list of foreign currencies and rates
    TreeMap<LocalDate,HashMap<String,Double>> cryptoRates = new TreeMap<>();
    private static CryptoAndFiatRateManager instance;
    LocalDate dateEOD;

    private CryptoAndFiatRateManager(){}

    public static synchronized CryptoAndFiatRateManager getInstance() {
        if (instance == null) {
            instance = new CryptoAndFiatRateManager();
        }
        return instance;
    }
    /*
    allRates will contain a map of dates. Each date will contain at least one map of rates .
    the map of rates object will contain one or more rate object
    So on a given date we could have 2 base currencies, Say GBP and EUR. Each one of those might contain a rate to convert to say USDT and CHF;

    cryptorate will contain a map of dates. Each date will contain at least on map of rates
     */

    public class fiatRate {
        String baseCurrency;
        Map<String,Double> baseRates= new HashMap<>();
    }

    public TreeMap<LocalDate,HashMap<String,HashMap<String,Double>>> getFiatRates() {
        return fiatRates;
    }

    public TreeMap<LocalDate, HashMap<String, Double>> getCryptoRates() {
        return cryptoRates;
    }

    public void loadFiles() {
        String fiatFNameWithPath= defaultFilePath + separator +"exrates.csv";
        logger.info("loading rates from :" + fiatFNameWithPath );

        try (BufferedReader reader = new BufferedReader(new FileReader(fiatFNameWithPath))) {
            //for now assume no duplicates , each row has one date for each converted currency
            //eg GBP to USD then GBP to EUR
            String rowText = reader.readLine();
            if (rowText.startsWith("Date")) rowText = reader.readLine();  //header so re-read
            while (rowText != null) {
                processFiatRecord(rowText);
                rowText = reader.readLine();
            }
        }catch (IOException e) {
            logger.warning("Error reading the fiat file: " + e.getMessage());
            return ;
        }
        logger.info("fiat currency file loaded");
        String cryptoFNameWithPath= defaultFilePath + separator +"cryptorates.csv";

        try (BufferedReader reader = new BufferedReader(new FileReader(cryptoFNameWithPath))) {
            String rowText = reader.readLine();
            if (rowText.startsWith("Date")) rowText = reader.readLine();  //header so re-read
            while (rowText != null) {
                processCryptoRecord(rowText);
                rowText = reader.readLine();
            }
        }catch (IOException e) {
            logger.warning("Error reading the crypto file: " + e.getMessage());
            return ;
        }
        logger.info("crypto currency file loaded");

    }

    public void processFiatRecord(String rowText) {
        String[] columns;
        LocalDate dt;
        String baseCurrency;
        String convCurrency;
        double exrate;

        columns = rowText.split(",");
        dt = LocalDate.parse(columns[0],dtfm);
        baseCurrency = columns[1];
        convCurrency = columns[2];
        exrate = Double.valueOf(columns[3]);
        HashMap<String, HashMap<String, Double>> ratesForDay;
        HashMap<String, Double> ratesForBaseCurrency;

        if (fiatRates.containsKey(dt)) {
            //we already have a rate for the date, so fetch the Rates map for the given baseCurrency
            ratesForDay = (HashMap<String, HashMap<String, Double>>) fiatRates.get(dt);
            if (ratesForDay.containsKey(baseCurrency)) {
                ratesForBaseCurrency = ratesForDay.get(baseCurrency);
                ratesForBaseCurrency.put(convCurrency, exrate);
            } else {
                ratesForBaseCurrency = new HashMap<>();
                ratesForBaseCurrency.put(convCurrency, exrate);
                ratesForDay.put(baseCurrency,ratesForBaseCurrency);
            }
        } else {
            //new keu
            ratesForBaseCurrency = new HashMap<>();
            ratesForBaseCurrency.put(convCurrency, exrate);
            ratesForDay = new HashMap<>();
            ratesForDay.put(baseCurrency, ratesForBaseCurrency);
            fiatRates.put(dt, ratesForDay);
        }
    }

    public void processCryptoRecord(String rowText) {
        String[] columns;
        LocalDate dt;
        String sourceCurrency;
        String convCurrency;
        String pair;
        double exrate;

        columns = rowText.split(",");
        dt = LocalDate.parse( columns[0],dtfm);
        sourceCurrency = columns[1];
        convCurrency = columns[2];
        exrate = Double.valueOf(columns[3]);
        pair=sourceCurrency.concat("-").concat(convCurrency);
        HashMap<String,  Double> ratesForDay;

        if (cryptoRates.containsKey(dt)) {
            //we already have a rate for the date, so fetch the Rates map for the given baseCurrency
            ratesForDay = (HashMap<String, Double>) cryptoRates.get(dt);
            ratesForDay.put(pair,exrate);
            cryptoRates.put(dt,ratesForDay);  //??needed?
        } else {
            ratesForDay = new HashMap<>();
            ratesForDay.put(pair, exrate);
            cryptoRates.put(dt, ratesForDay);
        }
    }

    public static void main(String[] args) {
        if (args.length>0) {
            arg = args[1];
            if (arg.startsWith("-dc")) {
                defaultFilePath =arg.substring(2);
            } else
            {
                logger.severe("Unexpected command line argument");
                return;
            }
          }
        CryptoAndFiatRateManager fc = CryptoAndFiatRateManager.getInstance();
        logger.info(String.valueOf(fc.getFiatRates().size()));
        logger.info(String.valueOf(fc.getCryptoRates().size()));

    }

    public double getCryptoRateForDate(String date, String pair ) {

        LocalDate date1=LocalDate.parse(date,dtfm);
        logger.info(getCryptoRates().headMap(date1).entrySet().toString());
        double xrate=0;
        //find the nearest date
        LocalDate date2=getCryptoRates().headMap(date1, false).lastKey();
        HashMap<String, Double> ratesForDay=getCryptoRates().get(date2);
        logger.info(ratesForDay.toString());

        return xrate;

    }

    public double getFiatRateForDate(String date, String pair ) {
        LocalDate date1 = LocalDate.parse(date, dtfm);
        logger.info(getFiatRates().headMap(date1,true).lastKey().toString());
        double xrate = 0;
        //find the nearest date
        LocalDate date2 = getFiatRates().headMap(date1,true).lastKey();
        HashMap<String,HashMap<String, Double>> ratesForDay = getFiatRates().get(date2);
        logger.info(String.valueOf(ratesForDay));

        return xrate;
    }
}
