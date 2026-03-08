package com.languagecenter.ui.dialogs;

import com.languagecenter.entity.Student;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StudentDialog extends JDialog {
    private Student student;
    private boolean isEditMode;
    private Student resultStudent;

    private JTextField txtFullName;
    private JTextField txtDob;
    private JComboBox<String> cbGender;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextArea txtAddress;
    private JComboBox<String> cbStatus;

    private JLabel lblTitle;

    public StudentDialog(Window parent, Student student) {
        super(parent, "Student Dialog", ModalityType.APPLICATION_MODAL);
        this.student = student;
        this.isEditMode = (student != null);
        initUI();
        if (isEditMode) {
            fillData();
        }
    }

    private void initUI() {
        // Mac full window content to remove default title bar gracefully
        getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
        setSize(600, 700);
        setLocationRelativeTo(getParent());

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainContainer.setBackground(new Color(0x1E293B)); // Floating sheet background
        setContentPane(mainContainer);

        // Header Section
        JPanel headerPanel = new JPanel(new MigLayout("insets 0, gap 10", "[][grow]", "[][]"));

        JLabel lblIcon = new JLabel();
        try {
            lblIcon.setIcon(new FlatSVGIcon("icons/user_add.svg", 48, 48));
        } catch (Exception e) {
            // fallback if icon not found
        }

        lblTitle = new JLabel(isEditMode ? "Edit Student" : "Student Information");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0xF8FAFC)); // Slate 50 (almost white)

        JLabel lblSubtitle = new JLabel("Fill in the details below to save student data.");
        lblSubtitle.setForeground(new Color(0x94A3B8)); // Slate 400

        headerPanel.add(lblIcon, "spany 2, aligny top");
        headerPanel.add(lblTitle, "wrap");
        headerPanel.add(lblSubtitle, "wrap");

        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.add(headerPanel, BorderLayout.CENTER);
        JSeparator separator = new JSeparator();
        separator.setBorder(new EmptyBorder(10, 0, 0, 0));
        headerWrapper.add(separator, BorderLayout.SOUTH);

        mainContainer.add(headerWrapper, BorderLayout.NORTH);

        // Body Section
        JPanel bodyPanel = new JPanel(new MigLayout("wrap 2, insets 20 30 20 30, gap 20 15", "[grow][grow]"));

        txtFullName = createTextField();
        txtDob = createTextField();

        cbGender = new JComboBox<>(new String[] { "Male", "Female", "Other" });
        styleComponent(cbGender);

        txtPhone = createTextField();
        txtEmail = createTextField();

        txtAddress = new JTextArea(3, 20);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        txtAddress.putClientProperty("JComponent.outlineWidth", 1);
        styleComponent(txtAddress); // applies outline and min height, though JTextArea handles height via rows
        txtAddress.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "...");
        JScrollPane addressScroll = new JScrollPane(txtAddress);

        cbStatus = new JComboBox<>(new String[] { "Active", "Inactive" });
        styleComponent(cbStatus);

        // Add labels and fields to MigLayout
        bodyPanel.add(createLabel("Full Name"));
        bodyPanel.add(createLabel("Date of Birth (yyyy-MM-dd)"), "wrap");

        bodyPanel.add(txtFullName, "growx");
        bodyPanel.add(txtDob, "growx, wrap");

        bodyPanel.add(createLabel("Gender"));
        bodyPanel.add(createLabel("Phone"), "wrap");

        bodyPanel.add(cbGender, "growx");
        bodyPanel.add(txtPhone, "growx, wrap");

        bodyPanel.add(createLabel("Email"), "span 2, wrap");
        bodyPanel.add(txtEmail, "span 2, growx, wrap");

        bodyPanel.add(createLabel("Address"), "span 2, wrap");
        bodyPanel.add(addressScroll, "span 2, growx, wrap");

        bodyPanel.add(createLabel("Status"), "span 2, wrap");
        bodyPanel.add(cbStatus, "span 2, growx, wrap");

        mainContainer.add(bodyPanel, BorderLayout.CENTER);

        // Footer Section
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setForeground(new Color(0x94A3B8));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.putClientProperty(FlatClientProperties.STYLE,
                "hoverBackground: #334155; margin: 10, 24, 10, 24");
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = new JButton("Save");
        btnSave.setBackground(new Color(0x4F46E5)); // Solid gradient-like color #4F46E5
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Inter", Font.BOLD, 14));
        btnSave.putClientProperty(FlatClientProperties.STYLE,
                "margin: 10, 24, 10, 24");
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> onSave());

        footerPanel.add(btnCancel);
        footerPanel.add(btnSave);

        mainContainer.add(footerPanel, BorderLayout.SOUTH);
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        styleComponent(tf);
        tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "...");
        return tf;
    }

    private void styleComponent(JComponent comp) {
        comp.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; borderColor: #475569; focusedBorderColor: #818CF8");
        if (!(comp instanceof JTextArea)) {
            comp.setPreferredSize(new Dimension(-1, 38));
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", Font.BOLD, 12));
        label.setForeground(new Color(0x94A3B8)); // Gray/Slate 400
        return label;
    }

    private void fillData() {
        if (student == null)
            return;

        txtFullName.setText(student.getFullName());
        if (student.getDateOfBirth() != null) {
            txtDob.setText(student.getDateOfBirth().toString());
        }
        cbGender.setSelectedItem(student.getGender());
        txtPhone.setText(student.getPhone());
        txtEmail.setText(student.getEmail());
        txtAddress.setText(student.getAddress());
        cbStatus.setSelectedItem(student.getStatus());
    }

    private void onSave() {
        if (student == null) {
            student = new Student();
            student.setRegistrationDate(LocalDate.now()); // Default reg date for new
        }
        student.setFullName(txtFullName.getText().trim());
        try {
            String dobText = txtDob.getText().trim();
            if (!dobText.isEmpty()) {
                student.setDateOfBirth(LocalDate.parse(dobText, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        } catch (Exception ex) {
            System.err.println("Invalid date format. Use yyyy-MM-dd");
        }
        student.setGender((String) cbGender.getSelectedItem());
        student.setPhone(txtPhone.getText().trim());
        student.setEmail(txtEmail.getText().trim());
        student.setAddress(txtAddress.getText().trim());
        student.setStatus((String) cbStatus.getSelectedItem());

        resultStudent = student;
        dispose();
    }

    public Student getStudentData() {
        return resultStudent;
    }
}
