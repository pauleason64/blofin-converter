package com.peason.blofin2koinly;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileHandler {

    static Logger logger = Logger.getLogger(FileConverter.class.getName());

    public static String KOINLY="Koinly";
    public static String BLOFIN="Blofin";
    public static String OTHER="Currently unsupported";
    public static String LOADERR = "le";
    public static String SAVEERR = "se";
    public static String[] TRADECOLS = {"Koinly Date","Pair","Side","Amount","Total",	"Fee Amount",	"Fee Currency",	"Order ID",	"Trade ID"};

    public static class FileAndData {
        ArrayList<String > existingSrcRows =new ArrayList<>();
        ArrayList<String > newSrcRows =new ArrayList<>();
        ArrayList<String > outRows=new ArrayList<>();
        boolean isMultipart;
        String fileType="";
        String lastFilename="";
        boolean isDirty = false; //is file is converted then we need to save it
    }

    public FileAndData loadFile(String fileName) {
        return loadFile(fileName,new FileAndData());
    }

    public FileAndData loadFile(String fileName,FileAndData fad) {
        logger.info("loading file " + fileName);
        ArrayList<String> rows=new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String rowText = reader.readLine();
            while (rowText != null) {
                rows.add(rowText);
                rowText = reader.readLine();
            }
        }catch (IOException e) {
            logger.warning("Error reading the file: " + e.getMessage());
            return null;
        }
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
        //assume header row is always present for now , so lets remove it
        rows.remove(0);
        //now merge back onto previous rows if any
        //fad.existingSrcRows.addAll(rows);
        fad.newSrcRows=rows;
        logger.info("Read "+ String.valueOf(rows.size()) + " rows for filetype:" +
                fad.fileType);
        return fad;
    }

    public boolean writeFile(FileAndData fad,String outfileName) {

        List<String> outrows;
        logger.info("writing file " + outfileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outfileName))) {
                writer.write(String.join(",",TRADECOLS)+System.lineSeparator());
                for (String row : fad.outRows) {
                    writer.write(row+System.lineSeparator());
                    }
                    logger.info ("File saved: " + outfileName);
                    logger.info ("Records saved: " + fad.outRows.size());
                    return true;

        } catch (IOException e) {
            logger.warning("Error writing the file: " + e.getMessage());
            return false;
        }
    }

    public  void handleTransactionFile(BufferedReader reader, String ROWDATA, String outfileName) {
        /* add implementation */
        String[] TRANSACTIONS = new String[9]; // Initialize TRANSACTIONS array
        logger.info("Handle the remaining columns");
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

            for (rownum=0; rownum <fad.newSrcRows.size(); rownum++) {
                ROWDATA = fad.newSrcRows.get(rownum);

                if (fad.fileType.equals(KOINLY)) {
                    outRows.add(ROWDATA);
                } else {
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
                        logger.info("Row:"+ String.valueOf(rownum+1)+" USDT amount set to "+String.valueOf(usdtEquiv));
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
