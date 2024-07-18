package com.peason.krakenhandler;

import com.peason.blofin2koinly.TableUtils;
import com.peason.databasetables.USERPROFILE;
import com.peason.krakenhandler.api.KrakenAPI;
import com.peason.krakenhandler.data.*;
import com.peason.model.AppConfig;
import com.peason.persistance.KrakenData;
import com.peason.persistance.ServersAndTablesRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;

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

@SpringBootApplication   (exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
@ComponentScan(basePackages = {"com.peason.*"})
public class FrontEnd extends JFrame implements ApplicationContextAware, InitializingBean {

    private JTextArea statusTextArea;
    private static FrontEnd instance;
    public boolean isRunning = false;
    JPanel topPanel;
    static JPanel centrePanel;
    JPanel bottomPanel;
    JPanel welcomeScreen;
    JPanel tradesPanel;
    static JPanel ledgerPanel;
    static JTextField textField;
    JTextArea txtArea;
    private ApplicationContext context;

    KrakenAPI krakenAPI ;
    ServersAndTablesRepository serversAndTablesRepository;
    AppConfig appConfig;
    USERPROFILE userprofile;

    @Autowired
    KrakenData krakenData ;

    private static DefaultTableModel ledgerTable;
    private DefaultTableModel tradesTable;

    public void init() {
        updateUI();
    }

    public void processLogin() {
        //dummy for now
        //dao
    }
    public void updateUI() {
        setTitle("Status Application");
        setLayout(new BorderLayout());
        setSize(new Dimension(1200, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create components
        statusTextArea = new JTextArea();
        statusTextArea.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(statusTextArea);
        topPanel = configureTopPanel();
        centrePanel = configureCenterPanel();
        bottomPanel = configureBottomPanel();
        ledgerPanel = configureLedgerTablePanel();
        tradesPanel = configureTradeTablePanel();
        centrePanel.add(tradesPanel);
        this.add(topPanel, BorderLayout.NORTH);
        this.add(centrePanel, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);

        //pack();
        setLocationRelativeTo(null);
        krakenAPI.start();
        isRunning = true;
        this.setVisible(true);
    }

    public  void refreshLedgerTable(String errMsg) {
        //update table with new records
        SwingUtilities.invokeLater(() -> {
            if (!errMsg.equals("")) {
                textField.setText(String.format("** Ledgers: Available %d, Fetched %d, Trades: Available %d, Fetched %d ** ERROR:%s",
                        krakenData.getAvailableLedgerCount(), krakenData.getFetchedLedgerOffset(),
                        krakenData.getAvailableTradeCount(), krakenData.getFetchedTradeOffset(), errMsg
                ));
                return;
            }
            textField.setText(String.format("Ledgers: Available %d, Fetched %d, Trades: Available %d, Fetched %d ",
                    krakenData.getAvailableLedgerCount(), krakenData.getFetchedLedgerOffset(),
                    krakenData.getAvailableTradeCount(), krakenData.getFetchedTradeOffset()
            ));
            ledgerTable.setRowCount(0);
            for (Component comp : centrePanel.getComponents()) {
                if (comp instanceof JPanel) centrePanel.remove(comp);
            }
            for (Ledger row : krakenData.ledgerList) {
                ledgerTable.addRow(new Object[]{
                        row.getRefid(), row.getType(), row.getSubtype(),
                        row.getAsset(), row.getTime(), row.getAmount(),
                        row.getFee(), row.getBalance()});

            }
            ledgerPanel.setVisible(true);
            centrePanel.add(ledgerPanel);
            centrePanel.updateUI();
        });
    }

    public void refreshTradesTable(String errMsg) {
        //update table with new records
        SwingUtilities.invokeLater(() -> {
            if (!errMsg.equals("")) {
                textField.setText(String.format("** Ledgers: Available %d, Fetched %d, Trades: Available %d, Fetched %d ** ERROR:%s",
                        krakenData.getAvailableLedgerCount(), krakenData.getFetchedLedgerOffset(),
                        krakenData.getAvailableTradeCount(), krakenData.getFetchedTradeOffset(), errMsg
                ));
                return;
            }
            textField.setText(String.format("Ledgers: Available %d, Fetched %d, Trades: Available %d, Fetched %d ",
                    krakenData.getAvailableLedgerCount(), krakenData.getFetchedLedgerOffset(),
                    krakenData.getAvailableTradeCount(), krakenData.getFetchedTradeOffset()
            ));
            for (Component comp : centrePanel.getComponents()) {
                if (comp instanceof JPanel) centrePanel.remove(comp);
            }
            //clear out table without deleting headers
            tradesTable.setRowCount(0);
            for (Trade row : krakenData.tradeList) {
                tradesTable.addRow(new Object[]{
                        row.getType(), row.getPair(), row.getOrdertype(),
                        row.getTime(), row.getVol(), row.getPrice(),
                        row.getFee(), row.getCost(), row.getMargin(),
                        row.getOrdertxid(), row.getTradeId(), row.getLedgerIdsAsString()});

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
        JPanel panel = new JPanel();
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
        JButton allledgerButton = new JButton("All Ledgers");
        JButton ledgerButton = new JButton("Update Ledgers");
        JButton showTradesButton = new JButton("Show Trades");
        JButton alltradesButton = new JButton("All Trades");
        JButton tradesButton = new JButton("Update Trades");
        // Set action listener for the button to start, pause or stop the background process
        ledgerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = krakenAPI.getLedgersAndTrades("Ledgers", 0, 0, "all", krakenData.getFetchedLedgerOffset());
                if (!message.equals("")) textField.setText(message);
            }
        });
        allledgerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField.setText("fetching all ledgers...");
                while (krakenData.getAvailableLedgerCount() == 0 || krakenData.getAvailableLedgerCount() > krakenData.getFetchedLedgerOffset()) {
                    String message = krakenAPI.getLedgersAndTrades("Ledgers", 0, 0, "all", krakenData.getFetchedLedgerOffset());
                    if (!message.equals("")) {
                        textField.setText(message);
                        return;
                    }//end calls
                }
                //once all fetched, reset counters
                krakenData.setAvailableLedgerCount(0);
                krakenData.setFetchedTradeOffset(0);
            }
        });
        tradesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message =krakenAPI.getLedgersAndTrades("TradesHistory", 0, 0, "all", krakenData.getFetchedTradeOffset());
                if (!message.equals("")) textField.setText(message);
            }
        });

        alltradesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField.setText("fetching all trades...");
                while (krakenData.getAvailableTradeCount() == 0 || krakenData.getAvailableTradeCount() > krakenData.getFetchedTradeOffset()) {
                    String message = krakenAPI.getLedgersAndTrades("TradesHistory", 0, 0, "all", krakenData.getFetchedTradeOffset());
                    if (!message.equals("")) {
                        textField.setText(message);
                        return;
                    } //end calls
                }
                //once all fetched, reset counters
                krakenData.setAvailableTradeCount(0);
                krakenData.setFetchedTradeOffset(0);
            }
        });

        showLedgerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //todo: implement paging or date ranges later
                krakenData.refreshLedgersFromDB();
                refreshLedgerTable("");
            }
        });
        showTradesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                krakenData.refreshTradesFromDB();
                refreshTradesTable("");
            }
        });
        panel.add(ledgerButton, FlowLayout.LEFT);
        panel.add(showLedgerButton, FlowLayout.LEFT);
        panel.add(allledgerButton, FlowLayout.CENTER);
        panel.add(tradesButton, FlowLayout.CENTER);
        panel.add(alltradesButton, FlowLayout.RIGHT);
        panel.add(showTradesButton, FlowLayout.RIGHT);
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
        for (int i = 0; i < LedgerHistoryResult.ColHeadings.length; i++) {
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
        for (int i = 0; i < TradesHistoryResult.ColHeadings.length; i++) {
            tradesTable.addColumn(TradesHistoryResult.ColHeadings[i].trim());
        }
        TableUtils.setJTableColumnsWidth(tradesView, 1160, TradesHistoryResult.ColWidthPercent);
        return panel;
    }

    public JPanel getWelcomeScreen() {
        //todo: add menu etc
        welcomeScreen = new JPanel(new FlowLayout());
        txtArea = new JTextArea();
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

    public static void main(String[] args) {
        var ctx = new SpringApplicationBuilder(FrontEnd.class)
                .headless(false).web(WebApplicationType.NONE).run(args);

        EventQueue.invokeLater(() -> {

            var ex = ctx.getBean(FrontEnd.class);
            ex.setVisible(true);
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        krakenAPI=context.getBean(KrakenAPI.class);
        serversAndTablesRepository = context.getBean(ServersAndTablesRepository.class);
        appConfig=context.getBean(AppConfig.class);
        //todo:will move later - need to add login screen
        userprofile=serversAndTablesRepository.getProfileData(appConfig.getProfileUserName(),null,null);
        krakenAPI.init(this);
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        context = ctx;
    }
}




