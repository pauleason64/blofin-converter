package com.peason.krakenhandler;

import java.io.DataOutputStream;

import java.io.UnsupportedEncodingException;
import java.net.URL;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;

import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import java.io.BufferedReader;

import java.io.InputStreamReader;

import java.security.MessageDigest;


public class KrakenAPI extends Thread {

    private KrakenData krakenData;
    private volatile boolean running=false;
    static KrakenParser krakenParser= new KrakenParser();
    public static FrontEnd fend;
    public static int LedgerOffset = 0;
    public static int TradesOffset = 0;

    public KrakenAPI(FrontEnd fe){
        fend =fe;
        krakenData = KrakenData.getInstance();
    }

    public void run() {
        //wait for frontend to start then do nothing until api method is called
        while (fend.isRunning == false) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void getLedgersAndTrades(String endPoint,long start, long end, String type, long ofs) {
        StringBuilder bld=new StringBuilder();
        bld.append(start > 0 ? "start="+String.valueOf(start)+"&" : "");
        bld.append(end > 0 ? "end="+String.valueOf(end)+"&" : "");
        bld.append(type !="" ? "type="+type+"&" :"");
        bld.append(ofs >0 ? "ofs="+String.valueOf(ofs) +"&": "");
        if (endPoint.contains("Trades")) bld.append("ledgers=true");
        String inputParameters= bld.toString();
        if (inputParameters.endsWith("&")) inputParameters=inputParameters.substring(0,inputParameters.length()-1);
//        try {
//            inputParameters= URLEncoder.encode(inputParameters, StandardCharsets.UTF_8.toString());
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException(e);
//        }

         QueryPrivateEndpoint(endPoint,inputParameters,
                "ueeiVqWa6U/ce20f8c6tIeQQH6LUmRRPV49wQDn6+CQ9MtBxdOMEtjjw",
                "LkgL89b57xtg3gg5T4zLkt3ztFgneZ0L14lIUUX5O+c59KzwhP9BiuC+yQnfi0xE3xo5KGR1pX2RO4NJqxKfSw==");
        //System.out.println(responseJson);
    }

        public void QueryPrivateEndpoint(String endPointName,
                                           String inputParameters,
                                           String apiPublicKey,
                                           String apiPrivateKey) {

        String responseJson = "";

        String baseDomain = "https://api.kraken.com";
        String privatePath = "/0/private/";
        String apiEndpointFullURL = baseDomain + privatePath + endPointName;
        String nonce = String.valueOf(System.currentTimeMillis());
        String apiPostBodyData = "nonce=" + nonce + "&" + inputParameters;
        String signature = CreateAuthenticationSignature(apiPrivateKey,
                privatePath,
                endPointName,
                nonce,
                apiPostBodyData);

        try {
            HttpsURLConnection httpConnection = null;
            URL apiUrl = new URL(apiEndpointFullURL);
            httpConnection = (HttpsURLConnection) apiUrl.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("API-Key", apiPublicKey);
            httpConnection.setRequestProperty("API-Sign", signature);
            httpConnection.setDoOutput(true);
            DataOutputStream os = new DataOutputStream(httpConnection.getOutputStream());
            os.writeBytes(apiPostBodyData);
            os.flush();
            os.close();
            BufferedReader br = null;

            br = new BufferedReader(new InputStreamReader((httpConnection.getInputStream())));
            String line;
            while ((line = br.readLine()) != null) {
                responseJson += line;
            }


            switch (endPointName) {
                case "Ledgers":
                    LedgerHistoryResult lresult= krakenParser.parseLedgers(responseJson);
                    krakenData.addLedgers(lresult.getResult().ledgers);
                    krakenData.availableLedgerCount= lresult.getResult().getCount();
                    krakenData.fetchedLedgerOffset+= lresult.getResult().ledgers.size();
                    fend.refreshLedgerTable();
                    break;
                case "TradesHistory":
                    TradesHistoryResult tresult= krakenParser.parseTradeHistory(responseJson);
                    krakenData.addTrades(tresult.getResult().trades);
                    krakenData.availableTradeCount= tresult.getResult().getCount();
                    krakenData.fetchedTradeOffset+= tresult.getResult().trades.size();
                    fend.refreshTradesTable();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
       // return responseJson;
    }


    public static String CreateAuthenticationSignature(String apiPrivateKey,
                                                    String apiPath,
                                                    String endPointName,
                                                    String nonce,
                                                    String apiPostBodyData) {

        try {

// GET 256 HASH

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((nonce + apiPostBodyData).getBytes());
            byte[] sha256Hash = md.digest();

// GET 512 HASH

            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(Base64.getDecoder().decode(apiPrivateKey.getBytes()), "HmacSHA512"));
            mac.update((apiPath + endPointName).getBytes());

// CREATE API SIGNATURE

            String signature = new String(Base64.getEncoder().encodeToString(mac.doFinal(sha256Hash)));
            return signature;

        } catch (Exception e) {

            e.printStackTrace();
            return "";
        }
    }

    public boolean isRunning(){
        return running;
    }

    public void pause(){
        running=false;
    }

    public void doresume(){
        running=true;
    }

    public class Request {

    }
}
