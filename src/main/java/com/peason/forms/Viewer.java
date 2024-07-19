package com.peason.forms;

import com.peason.blofin2koinly.TableUtils;
import com.peason.databasetables.*;
import com.peason.krakenhandler.api.KrakenAPI;
import com.peason.krakenhandler.data.*;
import com.peason.model.AppConfig;
import com.peason.persistance.KrakenData;
import com.peason.persistance.ServersAndTablesRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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

@org.springframework.stereotype.Component("viewer")
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
//@ComponentScan(basePackages = {"com.peason.*"})
public class Viewer extends JPanel implements ApplicationContextAware, InitializingBean {

    private SOURCES source;
    private USERPROFILE userprofile;
    private JTextArea statusTextArea;
    public boolean isRunning = false;
    JPanel topPanel;
    static JPanel centrePanel;
    JPanel bottomPanel;
    JPanel tradesPanel;
    static JPanel ledgerPanel;
    static JTextField textField;
    private ApplicationContext context;
    private Mainframe mainframe;

    @Autowired
    KrakenAPI krakenAPI ;
    @Autowired
    ServersAndTablesRepository serversAndTablesRepository;
    @Autowired
    AppConfig appConfig;

    @Autowired
    KrakenData krakenData ;

    private static DefaultTableModel ledgerTable;
    private DefaultTableModel tradesTable;

   // @PostConstruct
    public void init() {
        setLayout(new BorderLayout());
//        setSize(mainFrame.formsPanel.getBounds().getSize());
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
        krakenAPI.start();
        isRunning = true;
    }

    public JPanel show(Mainframe mainFrame) {
        this.mainframe=mainFrame;
        this.userprofile=Mainframe.currentUser;
        this.source=Mainframe.currentSource;
        this.setBounds(mainFrame.formsPanel.getBounds());
        mainFrame.setTitle("Crypto Trades Application");
        return this;
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
            for (Ledger row : krakenData.getLedgerList()) {
                ledgerTable.addRow(new Object[]{
                        row.getRefid(), row.getTradetype(), row.getSubtype(),
                        row.getAsset(), row.getTradedt(), row.getCost(),
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
            for (Trade row : krakenData.getTradeList()) {
                tradesTable.addRow(new Object[]{
                        row.getType(), row.getPair(), row.getOrdertype(),
                        row.getTradedt(), row.getVol(), row.getPrice(),
                        row.getFee(), row.getCost(), row.getMargin(),
                        row.getOrdertxid(), row.getTradeId(), row.getLedgerids()});

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
        JPanel panel = new JPanel(new GridLayout(1,6));
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
        panel.add(showLedgerButton);
        panel.add(ledgerButton);
        panel.add(allledgerButton);
        panel.add(alltradesButton);
        panel.add(tradesButton);
        panel.add(showTradesButton);
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

    public static void main(String[] args) {
        var ctx = new SpringApplicationBuilder(Viewer.class)
                .headless(false).web(WebApplicationType.NONE).run(args);

        EventQueue.invokeLater(() -> {

            var ex = ctx.getBean(Viewer.class);
            ex.setVisible(true);
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        krakenAPI=context.getBean(KrakenAPI.class);
        serversAndTablesRepository = context.getBean(ServersAndTablesRepository.class);
        appConfig=context.getBean(AppConfig.class);
        //todo:will move later - need to add login screen
        krakenAPI.init(this);
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        context = ctx;
        init();
    }
}




