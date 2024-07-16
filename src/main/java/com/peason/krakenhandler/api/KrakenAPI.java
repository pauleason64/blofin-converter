package com.peason.krakenhandler.api;

import com.peason.databasetables.APIFEEDS;
import com.peason.services.ServersAndTablesRepository;
import com.peason.krakenhandler.FrontEnd;
import com.peason.krakenhandler.data.KrakenParser;
import com.peason.krakenhandler.data.KrakenData;
import com.peason.krakenhandler.data.TradesHistoryResult;
import com.peason.krakenhandler.data.LedgerHistoryResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;

import java.net.URL;

import java.sql.SQLException;
import java.util.Base64;

import javax.crypto.Mac;

import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import java.io.BufferedReader;

import java.io.InputStreamReader;

import java.security.MessageDigest;
import java.util.List;

@Service
@ComponentScan(basePackages = {"com.peason.*"})
@DependsOn({"krakenData","krakenParser"})
public class KrakenAPI extends Thread {


    private volatile boolean running=false;
    public static int LedgerOffset = 0;
    public static int TradesOffset = 0;

    @Autowired
    @Qualifier("serversAndTablesRepository")
    private ServersAndTablesRepository serversAndTablesRepository;

    @Autowired
    @Qualifier("krakenData")
    private KrakenData kd;

    @Autowired
    @Qualifier("krakenParser")
    private  KrakenParser krakenParser;

    //removed autowiring due to circlular dep

    private  FrontEnd fend=null;

    public KrakenAPI(){}

    public void init(FrontEnd fe) {
        this.fend=fe;
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

    public String getLedgersAndTrades(String endPoint,long start, long end, String type, long ofs) {
        StringBuilder bld=new StringBuilder();
        bld.append(start > 0 ? "start="+String.valueOf(start)+"&" : "");
        bld.append(end > 0 ? "end="+String.valueOf(end)+"&" : "");
        bld.append(type !="" ? "type="+type+"&" :"");
        bld.append(ofs >0 ? "ofs="+String.valueOf(ofs) +"&": "");
        if (endPoint.contains("Trades")) bld.append("ledgers=true");
        String inputParameters= bld.toString();
        if (inputParameters.endsWith("&")) inputParameters=inputParameters.substring(0,inputParameters.length()-1);
        APIFEEDS apifeed;
        try {
            apifeed = serversAndTablesRepository.getAPIDataForSource("Kraken");
            return   QueryPrivateEndpoint(endPoint,inputParameters,
                    apifeed.getApiKey(), apifeed.getApiPk());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        public String QueryPrivateEndpoint(String endPointName,
                                           String inputParameters,
                                           String apiPublicKey,
                                           String apiPrivateKey) {

        String responseJson = "";
        String errMsg;

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

            if (responseJson.substring(0,30).contains("Rate limit")) {
                errMsg = "Api rate limit reached - try again later";
                } else { errMsg="";
            }

            switch (endPointName) {
                case "Ledgers":
                    if (errMsg.equals("")) {
                        LedgerHistoryResult lresult = krakenParser.parseLedgers(responseJson);
                        kd.addLedgers(lresult.getResult().ledgers);
                        kd.setAvailableLedgerCount(lresult.getResult().getCount());
                        kd.setFetchedLedgerOffset(kd.getFetchedLedgerOffset() + lresult.getResult().ledgers.size());
                    }
                    fend.refreshLedgerTable(errMsg);
                    return errMsg;
              //      List<String>assets= krakenData.getUniqueLedgerAssets();
                default:
                    if (errMsg.equals("")) {
                        TradesHistoryResult tresult = krakenParser.parseTradeHistory(responseJson);
                        kd.addTrades(tresult.getResult().getTrades());
                        kd.setAvailableTradeCount(tresult.getResult().getCount());
                        kd.setFetchedTradeOffset(kd.getFetchedTradeOffset() + tresult.getResult().trades.size());
                    }
                    fend.refreshTradesTable(errMsg);
                    return errMsg;
              //      List<String> pairs=krakenData.getUniqueTradePairs();
            }

        } catch (Exception e) {
            System.out.println(e);
            return e.getMessage();
        }
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
