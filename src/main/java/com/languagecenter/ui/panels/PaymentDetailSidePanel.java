package com.languagecenter.ui.panels;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.languagecenter.dao.EnrollmentDAO;
import com.languagecenter.dao.StudentDAO;
import com.languagecenter.entity.Enrollment;
import com.languagecenter.entity.Payment;
import com.languagecenter.entity.Student;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class PaymentDetailSidePanel extends JPanel {

    private final Color cardBg = new Color(0xFFFFFF);
    private final Color borderColor = new Color(0xE2E8F0);
    private final Color accentColor = new Color(0x6366F1);

    private Runnable onCloseCallback;
    private Consumer<Payment> onSaveCallback;
    private Payment currentPayment;

    private JComboBox<Student> cbStudent;
    private JComboBox<Enrollment> cbEnrollment;
    private JTextField tfAmount;
    private JTextField tfDate;
    private JComboBox<String> cbMethod;
    private JComboBox<String> cbStatus;

    private StudentDAO studentDAO = StudentDAO.getInstance();
    private EnrollmentDAO enrollmentDAO = EnrollmentDAO.getInstance();

    public PaymentDetailSidePanel(Runnable onCloseCallback) {
        this.onCloseCallback = onCloseCallback;
        initUI();
    }

    public void setPayment(Payment payment, Consumer<Payment> onSaveCallback) {
        this.currentPayment = payment;
        this.onSaveCallback = onSaveCallback;

        loadComboBoxData();

        if (payment != null) {
            if (payment.getStudent() != null) {
                for (int i = 0; i < cbStudent.getItemCount(); i++) {
                    if (cbStudent.getItemAt(i).getId() == payment.getStudent().getId()) {
                        cbStudent.setSelectedIndex(i);
                        break;
                    }
                }
            }
            if (payment.getEnrollment() != null) {
                for (int i = 0; i < cbEnrollment.getItemCount(); i++) {
                    if (cbEnrollment.getItemAt(i).getId() == payment.getEnrollment().getId()) {
                        cbEnrollment.setSelectedIndex(i);
                        break;
                    }
                }
            }
            tfAmount.setText(payment.getAmount() != null ? payment.getAmount().toString() : "");
            tfDate.setText(payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : LocalDate.now().toString());
            cbMethod.setSelectedItem(payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "Cash");
            cbStatus.setSelectedItem(payment.getStatus() != null ? payment.getStatus() : "Completed");
        } else {
            if (cbStudent.getItemCount() > 0) cbStudent.setSelectedIndex(0);
            if (cbEnrollment.getItemCount() > 0) cbEnrollment.setSelectedIndex(0);
            tfAmount.setText("");
            tfDate.setText(LocalDate.now().toString());
            cbMethod.setSelectedIndex(0);
            cbStatus.setSelectedIndex(0);
        }
    }

    private void loadComboBoxData() {
        cbStudent.removeAllItems();
        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            cbStudent.addItem(s);
        }

        cbEnrollment.removeAllItems();
        List<Enrollment> enrollments = enrollmentDAO.getAllEnrollments();
        for (Enrollment e : enrollments) {
            cbEnrollment.addItem(e);
        }
    }

    private void onSave() {
        if (onSaveCallback != null) {
            if (currentPayment == null) {
                currentPayment = new Payment();
            }

            Student selectedStudent = (Student) cbStudent.getSelectedItem();
            Enrollment selectedEnrollment = (Enrollment) cbEnrollment.getSelectedItem();

            if (selectedStudent == null) {
                JOptionPane.showMessageDialog(this, "Please select a Student.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String amountStr = tfAmount.getText().trim();
                currentPayment.setAmount(amountStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(amountStr));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid Amount. Must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String dateText = tfDate.getText().trim();
                currentPayment.setPaymentDate(LocalDate.parse(dateText, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid Date format. Use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentPayment.setStudent(selectedStudent);
            currentPayment.setEnrollment(selectedEnrollment);
            currentPayment.setPaymentMethod((String) cbMethod.getSelectedItem());
            currentPayment.setStatus((String) cbStatus.getSelectedItem());

            onSaveCallback.accept(currentPayment);
        }
    }

    private void initUI() {
        setLayout(new MigLayout("wrap 1, fillx, insets 25", "[grow]", "[][][grow][]"));
        setBackground(cardBg);
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, borderColor));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Payment Details");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0x1E293B));

        JButton btnClose = new JButton("✕");
        btnClose.setFont(new Font("Inter", Font.BOLD, 18));
        btnClose.setForeground(new Color(0x94A3B8));
        try {
            java.net.URL url = getClass().getResource("/icons/close.svg");
            if (url != null) {
                btnClose.setIcon(new FlatSVGIcon("icons/close.svg", 16, 16));
                btnClose.setText("");
            }
        } catch (Exception e) {}
        btnClose.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> {
            if (onCloseCallback != null) {
                onCloseCallback.run();
            }
        });

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnClose, BorderLayout.EAST);

        add(headerPanel, "growx, gapbottom 30");

        // Form fields
        cbStudent = new JComboBox<>();
        cbStudent.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student) {
                    setText(((Student) value).getFullName());
                }
                return this;
            }
        });
        styleComboBox(cbStudent);

        cbEnrollment = new JComboBox<>();
        cbEnrollment.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Enrollment) {
                    Enrollment e = (Enrollment) value;
                    String className = e.getCourseClass() != null ? e.getCourseClass().getClassName() : "Unknown";
                    setText("ID: " + e.getId() + " - " + className);
                }
                return this;
            }
        });
        styleComboBox(cbEnrollment);

        tfAmount = new JTextField();
        styleTextField(tfAmount, "e.g. 500.00");

        tfDate = new JTextField();
        styleTextField(tfDate, "yyyy-MM-dd");

        cbMethod = new JComboBox<>(new String[]{"Cash", "Credit Card", "Bank Transfer"});
        styleComboBox(cbMethod);

        cbStatus = new JComboBox<>(new String[]{"Pending", "Completed", "Failed", "Refunded"});
        styleComboBox(cbStatus);

        add(createLabel("Student"));
        add(cbStudent, "growx, gapbottom 15");

        add(createLabel("Enrollment"));
        add(cbEnrollment, "growx, gapbottom 15");

        add(createLabel("Amount"));
        add(tfAmount, "growx, gapbottom 15");

        add(createLabel("Payment Date (yyyy-MM-dd)"));
        add(tfDate, "growx, gapbottom 15");

        add(createLabel("Payment Method"));
        add(cbMethod, "growx, gapbottom 15");

        add(createLabel("Status"));
        add(cbStatus, "growx, gapbottom 30");

        add(new JLabel(), "growy, pushy");

        // Action Buttons
        JButton btnSave = new JButton("Save Changes");
        btnSave.setBackground(accentColor);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Inter", Font.BOLD, 14));
        btnSave.putClientProperty(FlatClientProperties.STYLE, "margin: 12, 20, 12, 20");
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> onSave());

        add(btnSave, "growx");
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", Font.PLAIN, 12));
        label.setForeground(new Color(0x475569));
        return label;
    }

    private void styleTextField(JTextField tf, String placeholder) {
        tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        tf.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
        tf.putClientProperty(FlatClientProperties.STYLE, "focusedBorderColor: #6366F1; background: #F8FAFC; margin: 5, 10, 5, 10");
        tf.setPreferredSize(new Dimension(-1, 44));
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.putClientProperty(FlatClientProperties.STYLE, "focusedBorderColor: #6366F1; background: #F8FAFC");
        cb.setPreferredSize(new Dimension(-1, 40));
    }
}
