package com.languagecenter.ui.panels;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.languagecenter.entity.Teacher;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.Consumer;

public class TeacherDetailSidePanel extends JPanel {

    private final Color cardBg = new Color(0xFFFFFF);
    private final Color borderColor = new Color(0xE2E8F0);
    private final Color accentColor = new Color(0x6366F1);

    private Runnable onCloseCallback;
    private Consumer<Teacher> onSaveCallback;
    private Teacher currentTeacher;

    private JTextField tfFullName;
    private JTextField tfSpecialty;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JTextField tfHireDate;
    private JComboBox<String> cbStatus;

    public TeacherDetailSidePanel(Runnable onCloseCallback) {
        this.onCloseCallback = onCloseCallback;
        initUI();
    }

    public void setTeacher(Teacher teacher, Consumer<Teacher> onSaveCallback) {
        this.currentTeacher = teacher;
        this.onSaveCallback = onSaveCallback;

        if (teacher != null) {
            tfFullName.setText(teacher.getFullName());
            tfHireDate.setText(teacher.getHireDate() != null ? teacher.getHireDate().toString() : "");
            tfEmail.setText(teacher.getEmail());
            tfPhone.setText(teacher.getPhone());
            tfSpecialty.setText(teacher.getSpecialty());
            cbStatus.setSelectedItem(teacher.getStatus());
        } else {
            tfFullName.setText("");
            tfHireDate.setText("");
            tfEmail.setText("");
            tfPhone.setText("");
            tfSpecialty.setText("");
            cbStatus.setSelectedIndex(0);
        }
    }

    private void onSave() {
        if (onSaveCallback != null) {
            if (currentTeacher == null) {
                currentTeacher = new Teacher();
            }

            String name = tfFullName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentTeacher.setFullName(name);
            currentTeacher.setEmail(tfEmail.getText().trim());
            currentTeacher.setPhone(tfPhone.getText().trim());
            currentTeacher.setSpecialty(tfSpecialty.getText().trim());
            currentTeacher.setStatus((String) cbStatus.getSelectedItem());

            try {
                String dateText = tfHireDate.getText().trim();
                if (!dateText.isEmpty()) {
                    currentTeacher.setHireDate(LocalDate.parse(dateText));
                }
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid Date format. Use yyyy-MM-dd.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            onSaveCallback.accept(currentTeacher);
        }
    }

    private void initUI() {
        setLayout(new MigLayout("wrap 1, fillx, insets 25", "[grow]", "[][][grow][]"));
        setBackground(cardBg);
        putClientProperty(FlatClientProperties.STYLE, "arc: 0");
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, borderColor));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Teacher Details");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0x1E293B));

        JButton btnClose = new JButton("✕");
        btnClose.setFont(new Font("Inter", Font.BOLD, 18));
        btnClose.setForeground(new Color(0x94A3B8));
        try {
            java.net.URL url = getClass().getResource("/icons/delete.svg");
            if (url != null) {
                btnClose.setIcon(new FlatSVGIcon("icons/delete.svg", 16, 16));
                btnClose.setText("");
            }
        } catch (Exception e) {
        }

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
        tfFullName = createTextField("e.g. Samuel");
        tfSpecialty = createTextField("e.g. English");
        tfEmail = createTextField("teacher@example.com");
        tfPhone = createTextField("+1 234 567 890");
        tfHireDate = createTextField("2024-01-01");

        add(createLabel("Full Name"));
        add(tfFullName, "growx, gapbottom 15");

        add(createLabel("Specialty"));
        add(tfSpecialty, "growx, gapbottom 15");

        add(createLabel("Hire Date (yyyy-MM-dd)"));
        add(tfHireDate, "growx, gapbottom 15");

        add(createLabel("Email Address"));
        add(tfEmail, "growx, gapbottom 15");

        add(createLabel("Phone Number"));
        add(tfPhone, "growx, gapbottom 15");

        add(createLabel("Status"));
        cbStatus = new JComboBox<>(new String[] { "Active", "Inactive" });
        cbStatus.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; focusedBorderColor: #6366F1; background: #F8FAFC; borderColor: #CBD5E1");
        cbStatus.setPreferredSize(new Dimension(-1, 40));
        add(cbStatus, "growx, gapbottom 30");

        // Bottom push
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

    private JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        tf.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; focusedBorderColor: #6366F1; borderColor: #CBD5E1; background: #F8FAFC; margin: 5, 10, 5, 10");
        tf.setPreferredSize(new Dimension(-1, 44));
        return tf;
    }
}
