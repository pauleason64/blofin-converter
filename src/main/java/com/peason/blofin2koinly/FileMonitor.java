package com.peason.blofin2koinly;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
        import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FileMonitor {

    /*
    This class may be used in future to monitor for files arriving in source directory and then processing automatically
     */
    // Modify these variables according to your MySQL configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/database_name";
    private static final String DB_USER = "username";
    private static final String DB_PASSWORD = "password";

    public static void main(String[] args) throws IOException {
        File folderToMonitor = new File("path_to_folder");

        // Create and start FileWatcher on a separate thread
        Thread watcherThread = new Thread(new FileWatcher(folderToMonitor.toPath(), "*.csv"));
        watcherThread.start();
    }
}

class Parser {
    private int recordsCount;
    private double sumOfValuesInThirdColumn;

    public synchronized void resetStatistics() {
        recordsCount = 0;
        sumOfValuesInThirdColumn = 0;
    }

    public synchronized void processRecord(String[] record) {
        recordsCount++;

        if (record.length >=3) {
            double valueInThirdColumn= Double.parseDouble(record[2]);
            sumOfValuesInThirdColumn += valueInThirdColumn;
        }
    }

    public synchronized int getRecordsCount() {
        return recordsCount;
    }

    public synchronized double getSumOfValues() {
        return sumOfValuesInThirdColumn;
    }
}

class FileWatcher implements Runnable {

    private final WatchService watchService;

    public FileWatcher(Path dir, String filePattern) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Override
    public void run() {
        try {
            WatchKey key;

            while ((key = watchService.take()) != null) {
                for (WatchEvent event : key.pollEvents()) {
                    Path filePath = ((Path) event.context()).toAbsolutePath();

                    if (filePath.toString().endsWith(".csv")) {
                        parseAndSaveCSV(filePath.toFile());
                        writeAuditLog(filePath.getFileName().toString());
                    }
                }

                key.reset();
            }
        } catch (InterruptedException | IOException | SQLException e ) {
            e.printStackTrace();

        }

    }

    private void parseAndSaveCSV(File csvFile) throws IOException, SQLException {

        Parser parser= new Parser();

        try(FileReader fileReader=new FileReader(csvFile);
            BufferedReader bufferedReader=new BufferedReader(fileReader)){

            parser.resetStatistics();

            String line ;

            while((line=bufferedReader.readLine())!=null){
                String[] record=line.split(",");
                parser.processRecord(record);
            }

            System.out.println("Total records: " + parser.getRecordsCount());
            System.out.println("Sum of values in third column: " + parser.getSumOfValues());

        }catch(IOException e){
            throw e ;
        }finally{

        }
    }

    private void writeAuditLog(String fileName) throws SQLException {

//        try(Connection conn= DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD)){
//
//            int recordsRead=getParser().getRecordsCount();
//            double totalSum=getParser().getSumOfValues();
//
//            String sqlQuery="INSERT INTO LOGS(file_processed_time,num_records_read,total_sum)"
//                    +"VALUES(?,?,?)";
//
//            PreparedStatement statement=conn.prepareStatement(sqlQuery);
//
//            statement.setTimestamp(1,new Timestamp(System.currentTimeMillis()));
//            statement.setInt(2,getParser().getRecordsCount());
//            statement.setDouble(3,getParser().getSumOfValues());
//
//            statement.executeUpdate();
//
//        }catch(SQLException e){
//
//            throw e ;
//
//
//        }}
//...getter method for accessing the shared instance of Parser

    }
}

