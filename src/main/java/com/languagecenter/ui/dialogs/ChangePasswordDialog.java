package com.languagecenter.ui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.languagecenter.dao.UserAccountDAO;
import com.languagecenter.entity.UserAccount;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordDialog extends JDialog {

    private UserAccount userAccount;
    private UserAccountDAO userAccountDAO;

    private JPasswordField txtCurrentPassword;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;

    public ChangePasswordDialog(JFrame parent, UserAccount userAccount) {
        super(parent, "Change Password", true);
        this.userAccount = userAccount;
        this.userAccountDAO = new UserAccountDAO();

        initComponents();
        setSize(400, 350);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new MigLayout("wrap 1, fillx, insets 20", "[fill]", "[]15[]5[]15[]5[]15[]5[]20[]"));
        mainPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Change Password");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 18));

        txtCurrentPassword = new JPasswordField(20);
        txtCurrentPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Current Password");
        txtCurrentPassword.putClientProperty(FlatClientProperties.STYLE_CLASS, "showRevealButton");
        
        txtNewPassword = new JPasswordField(20);
        txtNewPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "New Password");
        txtNewPassword.putClientProperty(FlatClientProperties.STYLE_CLASS, "showRevealButton");
        
        txtConfirmPassword = new JPasswordField(20);
        txtConfirmPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Confirm New Password");
        txtConfirmPassword.putClientProperty(FlatClientProperties.STYLE_CLASS, "showRevealButton");

        JButton btnSave = new JButton("Save Password");
        btnSave.setBackground(new Color(0x6366F1));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Inter", Font.BOLD, 14));
        btnSave.addActionListener(e -> onChangePassword());

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Inter", Font.PLAIN, 14));
        btnCancel.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new MigLayout("insets 0, fillx", "[grow][grow]", "[]"));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnCancel, "growx");
        buttonPanel.add(btnSave, "growx");

        mainPanel.add(lblTitle, "center");
        mainPanel.add(new JLabel("Current Password:"));
        mainPanel.add(txtCurrentPassword, "h 40!");
        mainPanel.add(new JLabel("New Password:"));
        mainPanel.add(txtNewPassword, "h 40!");
        mainPanel.add(new JLabel("Confirm Password:"));
        mainPanel.add(txtConfirmPassword, "h 40!");
        mainPanel.add(buttonPanel, "growx");

        setContentPane(mainPanel);
    }

    private void onChangePassword() {
        String current = new String(txtCurrentPassword.getPassword());
        String newPass = new String(txtNewPassword.getPassword());
        String confirm = new String(txtConfirmPassword.getPassword());

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!current.equals(userAccount.getPassword())) {
            JOptionPane.showMessageDialog(this, "Incorrect current password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        userAccount.setPassword(newPass);
        try {
            userAccountDAO.updateUserAccount(userAccount);
            JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating password: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
