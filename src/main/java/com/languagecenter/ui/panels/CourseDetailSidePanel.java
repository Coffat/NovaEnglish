package com.languagecenter.ui.panels;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.languagecenter.entity.Course;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.function.Consumer;

public class CourseDetailSidePanel extends JPanel {

    private final Color cardBg = new Color(0xFFFFFF);
    private final Color borderColor = new Color(0xE2E8F0);
    private final Color accentColor = new Color(0x6366F1);

    private Runnable onCloseCallback;
    private Consumer<Course> onSaveCallback;
    private Course currentCourse;

    private JTextField tfName;
    private JTextField tfDescription;
    private JTextField tfLevel;
    private JSpinner spDuration;
    private JTextField tfFee;
    private JComboBox<String> cbStatus;

    public CourseDetailSidePanel(Runnable onCloseCallback) {
        this.onCloseCallback = onCloseCallback;
        initUI();
    }

    public void setCourse(Course course, Consumer<Course> onSaveCallback) {
        this.currentCourse = course;
        this.onSaveCallback = onSaveCallback;

        if (course != null) {
            tfName.setText(course.getName());
            tfDescription.setText(course.getDescription());
            tfLevel.setText(course.getLevel());
            spDuration.setValue(course.getDuration());
            tfFee.setText(course.getFee() != null ? course.getFee().toString() : "");
            cbStatus.setSelectedItem(course.getStatus());
        } else {
            tfName.setText("");
            tfDescription.setText("");
            tfLevel.setText("");
            spDuration.setValue(1);
            tfFee.setText("");
            cbStatus.setSelectedIndex(0);
        }
    }

    private void onSave() {
        if (onSaveCallback != null) {
            if (currentCourse == null) {
                currentCourse = new Course();
            }

            String name = tfName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentCourse.setName(name);
            currentCourse.setDescription(tfDescription.getText().trim());
            currentCourse.setLevel(tfLevel.getText().trim());
            currentCourse.setDuration((Integer) spDuration.getValue());
            currentCourse.setStatus((String) cbStatus.getSelectedItem());

            try {
                String feeText = tfFee.getText().trim();
                currentCourse.setFee(feeText.isEmpty() ? BigDecimal.ZERO : new BigDecimal(feeText));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid Fee value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            onSaveCallback.accept(currentCourse);
        }
    }

    private void initUI() {
        setLayout(new MigLayout("wrap 1, fillx, insets 25", "[grow]", "[][][grow][]"));
        setBackground(cardBg);
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, borderColor));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Course Details");
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
        tfName = createTextField("e.g. Basic English");
        tfDescription = createTextField("Description");
        tfLevel = createTextField("e.g. Beginner");
        spDuration = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        tfFee = createTextField("e.g. 5.000.000");

        add(createLabel("Course Name"));
        add(tfName, "growx, gapbottom 15");

        add(createLabel("Description"));
        add(tfDescription, "growx, gapbottom 15");

        add(createLabel("Level"));
        add(tfLevel, "growx, gapbottom 15");

        add(createLabel("Duration (Weeks)"));
        add(spDuration, "growx, gapbottom 15");

        add(createLabel("Fee"));
        add(tfFee, "growx, gapbottom 15");

        add(createLabel("Status"));
        cbStatus = new JComboBox<>(new String[] { "Active", "Inactive" });
        cbStatus.putClientProperty(FlatClientProperties.STYLE,
                "focusColor: #6366F1; background: #F8FAFC");
        cbStatus.setPreferredSize(new Dimension(-1, 40));
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

    private JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        tf.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
        tf.putClientProperty(FlatClientProperties.STYLE,
                "focusColor: #6366F1; background: #F8FAFC; margin: 5, 10, 5, 10");
        tf.setPreferredSize(new Dimension(-1, 44));
        return tf;
    }
}
