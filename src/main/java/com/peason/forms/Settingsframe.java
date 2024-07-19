package com.peason.forms;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

import com.peason.databasetables.USERPROFILE;
import com.peason.model.DBServer;
import com.peason.persistance.ServersAndTablesRepository;
import com.sun.tools.javac.Main;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import com.peason.services.DAO;

//todo: add cardpanel so we can add/edit new feeds for this user
@Component("settings")
public class Settingsframe extends JPanel implements ApplicationContextAware {
    private JTextField emailField, usernameField, loginUsernameField;
    private JPasswordField passwordField, loginPasswordField;
    private Mainframe mainFrame;
    static ApplicationContext applicationContext;

    DAO dao;
    DBServer dbServer;
    ServersAndTablesRepository serversAndTablesRepository;

    public Settingsframe() {
        //create instance for reference only
    }

    public void show(Mainframe mainFrame) {
            this.mainFrame = mainFrame;
            dao=applicationContext.getBean(DAO.class);
            dbServer=applicationContext.getBean(DBServer.class);
            serversAndTablesRepository=applicationContext.getBean(ServersAndTablesRepository.class);
            setLayout(new GridLayout(5, 2));

            add(new JLabel("Username:"));
            usernameField = new JTextField();
            add(usernameField);

            add(new JLabel("Password:"));
            passwordField = new JPasswordField();
            add(passwordField);

            add(new JLabel("Email:"));
            emailField = new JTextField();
            add(emailField);

            JButton createButton = new JButton("Create New Profile");
            createButton.addActionListener(e -> createUser());
            add(createButton);

            JButton existingUserButton = new JButton("login");
          //  JButton existingUserButton = new JButton("Existing User");
            existingUserButton.addActionListener(e -> mockLogin());
            //existingUserButton.addActionListener(e -> showLoginDialog(mainFrame));
            add(existingUserButton);
            this.setVisible(true);
            mainFrame.formsPanel.add(this);
            mainFrame.invalidate();
        }

    private void createUser() {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try (Connection conn = dbServer.getDataSource().getConnection()) {
            String sql = "INSERT INTO users (email, username, password) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setString(2, username);
                pstmt.setString(3, hashedPassword);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "User created successfully!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating user.");
        }
    }

    private void showLoginDialog(Mainframe main) {
        JDialog loginDialog = new JDialog(main, "Login", true);
        loginDialog.setSize(300, 200);
        loginDialog.setLayout(new GridLayout(3, 2));
        loginDialog.setLocationRelativeTo(this);

        loginDialog.add(new JLabel("Username:"));
        loginUsernameField = new JTextField();
        loginDialog.add(loginUsernameField);

        loginDialog.add(new JLabel("Password:"));
        loginPasswordField = new JPasswordField();
        loginDialog.add(loginPasswordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> loginUser(main,loginUsernameField.getText(),new String(loginPasswordField.getPassword())));
        loginDialog.add(loginButton);

        loginDialog.setVisible(true);
    }

    private void loginUser(Mainframe mainframe,String username,String password) {
        String hashed=BCrypt.hashpw(password, BCrypt.gensalt());
        USERPROFILE userprofile=  serversAndTablesRepository.getProfileData(username,hashed);
        String storedHash = userprofile.getPassword();
        if (BCrypt.checkpw(password, storedHash)) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            mainframe.setUsername(username);
            Mainframe.currentUser=userprofile;
            mainframe.ShowMainForm(); // Close the settings frame
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials.");
        }
    }

    public void mockLogin() {
        loginUser(mainFrame,"peason","peason");
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext=context;
    }
}
