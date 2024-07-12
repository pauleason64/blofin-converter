package com.peason.krakenhandler;

import com.peason.blofin2koinly.TableUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FrontEnd extends JFrame {
    KrakenAPI krakenAPI=null;
    KrakenData krakenData =null;
    private JTextArea statusTextArea;
    private static FrontEnd instance;
    public boolean isRunning=false;
    JPanel topPanel ;
    JPanel centrePanel ;
    JPanel bottomPanel;
    JPanel welcomeScreen;
    JPanel tradesPanel;
    JPanel ledgerPanel;
    JTextField textField;
    JTextArea txtArea;

    private DefaultTableModel ledgerTable;
    private DefaultTableModel tradesTable;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FrontEnd app = new FrontEnd();
            app.setVisible(true);
        });
    }

    public static synchronized FrontEnd getInstance() {
        if (instance == null) {
            instance = new FrontEnd();
        }
        return instance;
    }

    private FrontEnd() {
        setTitle("Status Application");
        setLayout(new BorderLayout());
        setSize(new Dimension(1200, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create an instance of BackgroundThread
        krakenAPI = new KrakenAPI(this);
        krakenData= KrakenData.getInstance();

        // Create components
        statusTextArea = new JTextArea();
        statusTextArea.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(statusTextArea);
        topPanel = configureTopPanel();
        centrePanel = configureCenterPanel();
        bottomPanel = configureBottomPanel();
        ledgerPanel=configureLedgerTablePanel();
        tradesPanel= configureTradeTablePanel();
        centrePanel.add(tradesPanel);
        this.add(topPanel,BorderLayout.NORTH);
        this.add(centrePanel,BorderLayout.CENTER);
        this.add(bottomPanel,BorderLayout.SOUTH);

        //pack();
        setLocationRelativeTo(null);
        krakenAPI.start();
        isRunning=true;
    }

    public void refreshLedgerTable() {
        //update table with new records
        SwingUtilities.invokeLater(() -> {
            textField.setText(String.format("Ledgers: Available %d, Fetched %d, Trades: Available %d, Fetched %d ",
                    krakenData.availableLedgerCount, krakenData.fetchedLedgerOffset,
                    krakenData.availableTradeCount, krakenData.fetchedTradeOffset
            ));
            ledgerTable.setRowCount(0);
            for (Component comp : centrePanel.getComponents()) {
                if( comp instanceof JPanel) centrePanel.remove(comp);
            }
                for (LedgerHistoryResult.Ledger record : krakenData.ledgerList) {
                    ledgerTable.addRow(new Object[]{
                            record.getRefid(), record.getType(), record.getSubtype(),
                            record.getAsset(), record.getTime(), record.getAmount(),
                            record.getFee(), record.getBalance()});

                }
            ledgerPanel.setVisible(true);
            centrePanel.add(ledgerPanel);
            centrePanel.updateUI();
            });
    }

    public void refreshTradesTable() {
        //update table with new records
        SwingUtilities.invokeLater(() -> {
            textField.setText(String.format("Ledgers: Available %d, Fetched %d, Trades: Available %d, Fetched %d ",
                    krakenData.availableLedgerCount, krakenData.fetchedLedgerOffset,
                    krakenData.availableTradeCount, krakenData.fetchedTradeOffset
            ));
            for (Component comp : centrePanel.getComponents()) {
                if (comp instanceof JPanel) centrePanel.remove(comp);
            }
            //clear out table without deleting headers
            tradesTable.setRowCount(0);
            for (TradesHistoryResult.Trade record : krakenData.tradeList) {
                tradesTable.addRow(new Object[]{
                        record.getType(), record.getPair(), record.getOrdertype(),
                        record.getTime(), record.getVol(), record.getPrice(),
                        record.getFee(), record.getCost(), record.getMargin(),
                        record.getOrdertxid(), record.getTradeId(), "+"});

                }
            tradesPanel.setVisible(true);
            centrePanel.add(tradesPanel);
            centrePanel.updateUI();
        });
    }

    private void writeToLogFile(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("log.txt", true))) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = now.format(formatter);
            writer.println(timestamp + " - " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JPanel configureTopPanel() {
        JPanel panel=new JPanel();
        panel.setLayout(new BorderLayout());
        textField = new JTextField(BorderLayout.CENTER);
        textField.setText("Welcome..");
        panel.add(textField);
        return panel;
    }

    public JPanel configureCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(50, 50, 50));
        return panel;
    }

    public JPanel configureBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        JButton showLedgerButton = new JButton("Show Ledgers");
        JButton showTradesButton = new JButton("Show Trades");
        JButton ledgerButton = new JButton("Fetch Ledgers");
        JButton tradesButton = new JButton("Fetch Trades");
        // Set action listener for the button to start, pause or stop the background process
        ledgerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                krakenAPI.getLedgersAndTrades("Ledgers",0,0,"all",krakenData.fetchedLedgerOffset);
            }
        });
        tradesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                krakenAPI.getLedgersAndTrades("TradesHistory",0,0,"all",krakenData.fetchedTradeOffset);
            }
        });
        showLedgerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshLedgerTable();
            }
        });
        showTradesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTradesTable();
            }
        });
        panel.add(ledgerButton,FlowLayout.LEFT);
        panel.add(tradesButton,FlowLayout.LEFT);
        panel.add(showLedgerButton,FlowLayout.RIGHT);
        panel.add(showTradesButton,FlowLayout.RIGHT);
        return panel;
    }

    public JPanel configureLedgerTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        ledgerTable = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing for all columns except the first (delete button)
                return column != 0;
            }
        };
        JTable ledgerView = new JTable(ledgerTable);
        ledgerView.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(ledgerView);
        scrollPane.setVisible(true);
        panel.add(scrollPane, BorderLayout.CENTER);
        for (int i = 0; i < LedgerHistoryResult.ColHeadings.length ; i++) {
            ledgerTable.addColumn(LedgerHistoryResult.ColHeadings[i].trim());
        }
        TableUtils.setJTableColumnsWidth(ledgerView, 1160, LedgerHistoryResult.ColWidthPercent);
        return panel;
    }

    public JPanel configureTradeTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        tradesTable = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing for all columns except the first (delete button)
                return column != 0;
            }
        };
        //scrollPane.setSize(new Dimension(1184,frame.getHeight()-80));
        JTable tradesView = new JTable(tradesTable);
        tradesView.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(tradesView);
        scrollPane.setVisible(true);
        panel.add(scrollPane, BorderLayout.CENTER);
        for (int i = 0; i < TradesHistoryResult.ColHeadings.length ; i++) {
            tradesTable.addColumn(TradesHistoryResult.ColHeadings[i].trim());
        }
        TableUtils.setJTableColumnsWidth(tradesView, 1160, TradesHistoryResult.ColWidthPercent);
        return panel;
    }

    public JPanel getWelcomeScreen() {
        //todo: add menu etc
         welcomeScreen=new JPanel(new FlowLayout());
         txtArea=new JTextArea();
         txtArea.setBounds(this.getBounds());
        txtArea.setText(" This is a framework for downloading API data from multiple APIs across ,multiple crypto exchanges" +
               "\n Scope in no particular order is:" +
                "\n Kraken API - download ledger and trade data" +
                "\n Bitget API - download ledger and trade data" +
                "\n Solana wallet transaction data and balances including meme coins" +
                "\n Bitget API - for historic transactions " +
                "\n Binance API - for historic transactions " +
                "\n Display transactions in front-end" +
                "\n Display net balances across all accounts and exchanges" +
                "\n Track cost of purchase and cost of disposal to calculate net profit/losses" +
                "\n create audit logs for YE tax returns to support CGT submissions"
        );
        txtArea.setVisible(true);
        welcomeScreen.add(txtArea);
        welcomeScreen.setVisible(true);
        return welcomeScreen;
    }

}




