package com.blofinconv;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Convert {

    public ArrayList<String> loadFile(String fileName) {
        ArrayList<String> rows=new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String rowText = reader.readLine();
            while (rowText != null) {
                rows.add(rowText);
                rowText = reader.readLine();
            }
        }catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            return null;
        }
        return rows;
    }

    public  String convert(String fileName, boolean overwrite) {

        String outfileName = fileNameWithoutExt(fileName).concat("converted.csv");
        File file = new File(outfileName);
        if (file.exists() ==true) {
            if ( overwrite == false) {
                return "Destination file " + outfileName + " already exists. remove file , or rerun with overwrite set to true"; }
            else {
                file.delete();
                }
            }
    List<String> outrows;
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outfileName))) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String HDR = reader.readLine(); // Read the first line
            if (HDR != null) {
                if (HDR.startsWith("Underlying")) {
                  outrows=  handleTradeFile(reader);
                } else {
                    handleTransactionFile(reader, HDR,outfileName);
                    return "Deposit and withdrawal file handling not yet implemented";
                }
            for (String row : outrows) {
                writer.write(row+System.lineSeparator());
                }
                System.out.println("File saved: " + outfileName);
                return "file "+outfileName+"  created.";
            }
        }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            return null;
        }
    //never gets here
        return null;
    }

    public  void handleTransactionFile(BufferedReader reader, String ROWDATA, String outfileName) {
        /* add implementation */
        String[] TRANSACTIONS = new String[9]; // Initialize TRANSACTIONS array
        System.out.println("Handle the remaining columns");
        return;
    }

    public  List<String> handleTradeFile(BufferedReader reader)  {
        String[] TRADECOLS = {"Koinly Date","Pair","Side","Amount","Total",	"Fee Amount",	"Fee Currency",	"Order ID",	"Trade ID"};
        String[] TRADEROW = new String[9]; // Initialize TRADES array
        String[] SOURCE=new String[11];
        String ROWDATA;
        List<String> outrowData = new ArrayList<>();
//        Asset	        Order Time	        Side	Avg Fill	Price	Filled	    Total	        Fee	            Order Options	Status
//        GUMMY/USDT	05/16/2024 02:05:05	Buy 	0.1 USDT	Market	1.77 GUMMY	0.177951 USDT	0.00177 GUMMY	GTC	Filled
//        Koinly Date	        Pair	    Side	Amount	Total	Fee Amount	    Fee Currency	Order ID	Trade ID
//        2024-05-16 02:05:05	GUMMY-USDT	Buy	    1.77	0.17795	0.00177	        GUMMY
        try {
            ROWDATA = reader.readLine();
            outrowData.add(String.join("," ,TRADECOLS));
            String feeCurr="";
            String fillCurr="";
            TRADEROW = new String[9];

            while (ROWDATA != null) {

                SOURCE = ROWDATA.split(",");
                fillCurr = SOURCE[6].split(" ")[1];
                feeCurr = SOURCE[7].split(" ")[1];
                float usdtEquiv=0f;
                float feeAmount = Float.valueOf(SOURCE[7].split(" ")[0]);
                if (fillCurr.equals("USDT")) {
                    usdtEquiv = Float.valueOf(SOURCE[6].split(" ")[0]); }
                else {
                    float fillPrice = Float.valueOf(SOURCE[3].split(" ")[0]);
                    float coins = Float.valueOf(SOURCE[6].split(" ")[0]);
                    usdtEquiv= (coins * fillPrice)+feeAmount;
                }
                TRADEROW[0] = SOURCE[1];
                TRADEROW[1] = SOURCE[0].replace("/", "-");
                TRADEROW[2] = SOURCE[2];
                TRADEROW[3] = SOURCE[5].split(" ")[0];
                TRADEROW[4] = String.valueOf(usdtEquiv);
                TRADEROW[5] = String.valueOf(feeAmount);
                TRADEROW[6] = feeCurr;
                TRADEROW[7] = "";
                TRADEROW[8] = "";
                outrowData.add(String.join(",", TRADEROW));
                ROWDATA = reader.readLine();
                }
            } catch (Exception e) {
                //do nothing;
            }
        return outrowData;
    }

    public String fileNameWithoutExt(String fn ) {
        int idx=fn.lastIndexOf(".");
        return fn.substring(0,idx);
    }


}
