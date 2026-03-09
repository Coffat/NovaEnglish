package com.languagecenter.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.languagecenter.dao.UserAccountDAO;
import com.languagecenter.entity.UserAccount;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
        setTitle("Language Center - Login");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);

        // Main container with background
        JPanel mainPanel = new JPanel(new MigLayout("wrap, fillx, insets 40 40 40 40", "[fill, 350]", "[]30[]15[]30[]"));
        mainPanel.setBackground(Color.WHITE);

        // App Logo or Icon (Optional, using a label for now)
        JLabel lblIcon = new JLabel("L O G I N");
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setFont(new Font("Inter", Font.BOLD, 16));
        lblIcon.setForeground(new Color(0x6366F1)); 

        JLabel lblTitle = new JLabel("Welcome Back!");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 28));
        lblTitle.setForeground(new Color(0x1E293B));

        JLabel lblSubtitle = new JLabel("Please enter your details to sign in");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Inter", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(0x64748B));

        JPanel headerPanel = new JPanel(new MigLayout("wrap, fillx, insets 0", "[fill]", "[]5[]5[]"));
        headerPanel.setOpaque(false);
        headerPanel.add(lblIcon);
        headerPanel.add(lblTitle);
        headerPanel.add(lblSubtitle);

        // Username Field
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Inter", Font.BOLD, 13));
        lblUser.setForeground(new Color(0x334155));

        txtUsername = new JTextField();
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        txtUsername.putClientProperty(FlatClientProperties.STYLE, "focusedBorderColor: #6366F1; borderColor: #CBD5E1; background: #F8FAFC; margin: 5, 15, 5, 15");
        txtUsername.setPreferredSize(new Dimension(-1, 45));

        // Password Field
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Inter", Font.BOLD, 13));
        lblPass.setForeground(new Color(0x334155));

        txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "••••••••");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "focusedBorderColor: #6366F1; borderColor: #CBD5E1; background: #F8FAFC; margin: 5, 15, 5, 15");
        txtPassword.putClientProperty(FlatClientProperties.STYLE_CLASS, "showRevealButton");
        txtPassword.setPreferredSize(new Dimension(-1, 45));

        // Login Button
        btnLogin = new JButton("Sign In");
        btnLogin.setBackground(new Color(0x6366F1));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Inter", Font.BOLD, 15));
        btnLogin.putClientProperty(FlatClientProperties.STYLE, "margin: 8, 20, 8, 20");
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(-1, 45));
        
        // Add action listener to button
        btnLogin.addActionListener(e -> attemptLogin());

        // Allow pressing ENTER to login from fields
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    attemptLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKeyAdapter);
        txtPassword.addKeyListener(enterKeyAdapter);

        // Also set as default button for the root pane
        getRootPane().setDefaultButton(btnLogin);

        // Assembly
        mainPanel.add(headerPanel);
        
        JPanel formPanel = new JPanel(new MigLayout("wrap, fillx, insets 0", "[fill]", "[]5[]15[]5[]"));
        formPanel.setOpaque(false);
        formPanel.add(lblUser);
        formPanel.add(txtUsername);
        formPanel.add(lblPass);
        formPanel.add(txtPassword);
        
        mainPanel.add(formPanel);
        mainPanel.add(btnLogin);

        setContentPane(mainPanel);
    }

    private void attemptLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Login Failed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserAccount user = userAccountDAO.verifyCredentials(username, password);
        if (user != null) {
            this.dispose();
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
