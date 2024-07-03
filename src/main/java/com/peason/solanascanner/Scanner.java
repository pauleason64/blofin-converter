package com.peason.solanascanner;

import com.peason.blofin2koinly.B2Klogging;
import com.peason.blofin2koinly.FileConverter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.logging.*;

import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.programs.MemoProgram;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;

import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.*;
import org.p2p.solanaj.rpc.types.TokenResultObjects.*;
import org.p2p.solanaj.rpc.types.config.Commitment;
import org.p2p.solanaj.token.TokenManager;

public class Scanner {

    static Logger logger = Logger.getLogger(Scanner.class.getName());
    static String defaultLogFilePath ="";
    static String separator = FileSystems.getDefault().getSeparator();
    static String solWallet = "2ktMA72qAhqCnrhQiq2EBDcQ1NE29dVETeeEEB1MPCrY";

    private void run() throws Exception {
       final RpcClient client = new RpcClient("https://api.mainnet-beta.solana.com");
       final TokenManager tokenManager = new TokenManager(client);
        TokenAmountInfo tokenAccountBalance = client.getApi().getTokenAccountBalance(PublicKey.valueOf(
                "6kPm5NPXHcw4Mah25XgHcEufqJQwEVwyp5K7U5hw53ci"));
        logger.info("tooker balance is:" + tokenAccountBalance.toString());
     //   long balance = client.getApi().getBalance(PublicKey.valueOf("2ktMA72qAhqCnrhQiq2EBDcQ1NE29dVETeeEEB1MPCrY"));
     //   logger.info(String.format("Balance = %d", balance));
       //  static final PublicKey USDC_TOKEN_MINT = new PublicKey("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");
       //  static final long LAMPORTS_PER_SOL = 1000000000L;
    }

    private boolean processArgs(String[] args) {
        String arg;
        for (int i=0; i<args.length; i++) {
            arg = args[i];
            if (arg.startsWith("-lp")) {
                defaultLogFilePath = arg.substring(3);
                logger.info("default logfile path from args:" + defaultLogFilePath);
                continue;
            }
        }
        return  true;
    }

    private void configureLogging() {

        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("b2klogging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
        logger.setLevel(Level.INFO);
        logger.addHandler(new ConsoleHandler());
        //logger.addHandler(new StreamHandler());
        try {
            //FileHandler file name with max size and number of log files limit
            String path = defaultLogFilePath.equals("") ?
                    FileSystems.getDefault().getPath("").toAbsolutePath().toString() + separator :
                    defaultLogFilePath + separator;
            Handler fileHandler = new java.util.logging.FileHandler(path + "b2klogger.log", 2000, 5);
            fileHandler.setFormatter(new B2Klogging.Formatter());
            //setting custom filter for FileHandler
            fileHandler.setFilter(new B2Klogging.Filter());
            logger.addHandler(fileHandler);
            logger.log(Level.CONFIG, "Reading and setting logging config");
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner= new Scanner();
        if (args.length==0  || scanner.processArgs(args)) {
            //config passed
            scanner.configureLogging();
            logger.info("Starting application..");
            scanner.run();
        } else {
            logger.info("Closing application..");
        }
    }
}