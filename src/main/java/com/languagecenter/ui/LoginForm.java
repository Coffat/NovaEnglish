package com.languagecenter.ui;

import com.languagecenter.dao.UserAccountDAO;
import com.languagecenter.entity.UserAccount;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private UserAccountDAO userAccountDAO;

    public LoginForm() {
        userAccountDAO = new UserAccountDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("Language Center");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Use MigLayout: center everything horizontally, spacing between elements
        setLayout(new MigLayout("wrap 1, insets 30, fillx, center", "[center]", "[]20[][][][]"));

        JLabel lblTitle = new JLabel("LANGUAGE CENTER LOGIN");
        lblTitle.putClientProperty("FlatLaf.styleClass", "h1"); // FlatLaf styling
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 22f));

        txtUsername = new JTextField();
        txtUsername.putClientProperty("JTextField.placeholderText", "Username");

        txtPassword = new JPasswordField();
        txtPassword.putClientProperty("JTextField.placeholderText", "Password");

        btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> attemptLogin());

        add(lblTitle, "wrap");
        add(txtUsername, "growx, h 40!");
        add(txtPassword, "growx, h 40!");
        add(btnLogin, "growx, h 45!, top, gaptop 10");
    }

    private void attemptLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserAccount user = userAccountDAO.verifyCredentials(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login successful! Welcome " + user.getUsername(), "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new MainFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
