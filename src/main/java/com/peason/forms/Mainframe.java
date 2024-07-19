package com.peason.forms;

import com.peason.databasetables.SOURCES;
import com.peason.databasetables.USERPROFILE;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
@ComponentScan(basePackages = {"com.peason.*"})
public class Mainframe extends JFrame implements ApplicationContextAware {

    static ApplicationContext applicationContext;
    static USERPROFILE currentUser;
    static SOURCES currentSource;
   // static List<SOURCES> sourcesList = new ArrayList<SOURCES>();
    JPanel welcomeScreen;
    private JLabel usernameLabel;
    private Mainframe home;
    public JPanel formsPanel;
    public JMenu homeMenu,sourcesMenu,profileMenu;

    @Autowired
    @Qualifier("settings")
    Settingsframe settingsframe;
    @Autowired
    @Qualifier("viewer")
    Viewer viewer;

    public Mainframe() {};

    public void init() {
        home =this;
        setTitle("Crypto Trades - Manage your own taxes or not as the case may be.");
        setSize(new Dimension(1250, 720));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel menuPanel = new JPanel();
        formsPanel = new JPanel(new BorderLayout());
        usernameLabel = new JLabel("Username: ");

// Create the menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create the main menu
        homeMenu = new JMenu("Menu");
        menuBar.add(homeMenu);
        sourcesMenu = new JMenu("Sources");
        menuBar.add(sourcesMenu);
        profileMenu = new JMenu("Profile");
        menuBar.add(sourcesMenu);

        // Create menu items
        JMenuItem viewerItem = new JMenuItem("viewer");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem settingsItem = new JMenuItem("Settings");
        JMenuItem option4Item = new JMenuItem("Option 4");

        // Add action listeners for menu items
        settingsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShowSettings(); }
        });

        viewerItem.addActionListener (new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShowViewer(); }
        });
        // Add similar action listeners for other items

        // Add menu items to the menu
        homeMenu.add(viewerItem);
        profileMenu.add(settingsItem);
        profileMenu.addSeparator();
        profileMenu.add(logoutItem);
        sourcesMenu.setToolTipText("Sources appear here when added via settings");
        // Add the menu bar to the top panel
        topPanel.add(menuBar, BorderLayout.WEST);
        topPanel.add(usernameLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
//        formsPanel.add(settingsframe);
        add(formsPanel, BorderLayout.CENTER);
        ShowSettings();
        this.setVisible(true);
    }

    public void AddSourcesItems(USERPROFILE userprofile) {
            List<SOURCES> sources = userprofile.getSources();

            for (int i=0; i<sources.size(); i++) {
                JMenuItem item = new JMenuItem(sources.get(i).getSourceName());
                int finalI = i;
                item.addActionListener (new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Mainframe.currentSource=sources.get(finalI);
                        ShowViewer(); }
                });
            }
    }


    public void ShowSettings() {
       // removeOldScreens(formsPanel);
       // this.revalidate();
       // this.repaint();
        formsPanel.add(settingsframe.show(home));
        formsPanel.setVisible(true);
    }

    public void ShowViewer() {
        removeOldScreens(formsPanel);
        formsPanel.add(viewer.show(home));
        formsPanel.setVisible(true);
        home.repaint();
    }

    private void removeOldScreens(JPanel main) {
        for (Component comp : main.getComponents()) {
             main.remove(comp);
        }
    }

    public void setUsername(String username) {
        usernameLabel.setText("Username: " + username);
    }

    public JPanel getWelcomeScreen()  {
        //todo: add menu etc
        JPanel welcomeScreen = new JPanel(new FlowLayout());
        welcomeScreen.setBounds(home.formsPanel.getBounds());
        JTextArea txtArea = new JTextArea();
        txtArea.setBounds(welcomeScreen.getBounds());
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

    public void ConfigureMenu(USERPROFILE userprofile) {
        this.usernameLabel.setText(userprofile.getUserName());

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Mainframe ui = new Mainframe();
            ui.setVisible(true);
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }
}

