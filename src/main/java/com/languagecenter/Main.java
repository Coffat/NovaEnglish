package com.languagecenter;

import com.formdev.flatlaf.FlatLightLaf;
import com.languagecenter.ui.LoginForm;
import com.languagecenter.util.HibernateUtil;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();

        // Cấu hình Hibernate chạy ngầm (nếu muốn preload)
        new Thread(() -> {
            try {
                HibernateUtil.getSessionFactory();
                System.out.println("Hibernate Engine initialized successfully.");
            } catch (Exception ex) {
                System.err.println("Hibernate Configuration Error: " + ex.getMessage());
            }
        }).start();

        // Khởi động UI trên EventDispatchThread
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}
