package com.blofinconv;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    public static String KOINLY="Koinly";
    public static String BLOFIN="Underl";
    public static String OTHER="Other";
    public static String LOADERR = "le";
    public static String SAVEERR = "se";

    public static String[] TRADECOLS = {"Koinly Date","Pair","Side","Amount","Total",	"Fee Amount",	"Fee Currency",	"Order ID",	"Trade ID"};

    public static class FileAndData {
        ArrayList<String > srcRows=new ArrayList<>();
        ArrayList<String > outRows=new ArrayList<>();
        boolean isMultipart;
        String fileType="";
        String lastFilename="";
    }

    public FileAndData loadFile(String fileName) {
        FileAndData fad=new FileAndData();
        ArrayList<String> rows=new ArrayList();
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
        fad.srcRows=rows;
        switch (rows.get(0).substring(0,6)) {
            case "Underl":
                fad.fileType =BLOFIN;
                break;
            case "Koinly":
                fad.fileType = KOINLY;
                break;
            default:
                fad.fileType = OTHER;
                break;
        }

        return fad;
    }

    public String writeFile(FileAndData fad,String saveFileName) {

        List<String> outrows;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outfileName))) {

                for (String row : fad.outRows) {
                    writer.write(row+System.lineSeparator());
                    }
                    System.out.println("File saved: " + outfileName);
                    return "file "+outfileName+"  created.";
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

    public  List<String> convertTradeFile(FileAndData fad,boolean appendOutrows)  {

        String[] TRADEROW ; // Initialize TRADES array
        String[] SOURCE=new String[11];
        String ROWDATA = "";
        List<String> outRows = new ArrayList<>();
        if (appendOutrows) outRows=fad.outRows;

        try {
            int rownum=0 ; //discard 1st row
            String feeCurr="";
            String fillCurr="";
            TRADEROW = new String[9];

            for (rownum=1; rownum <fad.srcRows.size(); rownum++) {
                ROWDATA = fad.srcRows.get(rownum) + " ";
                SOURCE = ROWDATA.split(",");
                fillCurr = SOURCE[6].split(" ")[1];
                feeCurr = SOURCE[7].split(" ")[1];
                float usdtEquiv = 0f;
                float feeAmount = Float.valueOf(SOURCE[7].split(" ")[0]);
                if (fillCurr.equals("USDT")) {
                    usdtEquiv = Float.valueOf(SOURCE[6].split(" ")[0]);
                } else {
                    float fillPrice = Float.valueOf(SOURCE[3].split(" ")[0]);
                    float coins = Float.valueOf(SOURCE[6].split(" ")[0]);
                    usdtEquiv = (coins * fillPrice) + feeAmount;
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
                outRows.add(String.join(",", TRADEROW));
                }
            } catch (Exception e) {
                //do nothing;
            }
        fad.isMultipart =true;
        return outRows;
    }


    public String fileNameWithoutExt(String fn ) {
        int idx=fn.lastIndexOf(".");
        return fn.substring(0,idx);
    }


}
