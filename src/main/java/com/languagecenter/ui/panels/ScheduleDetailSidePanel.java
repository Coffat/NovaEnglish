package com.languagecenter.ui.panels;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.languagecenter.dao.CourseClassDAO;
import com.languagecenter.entity.CourseClass;
import com.languagecenter.entity.Schedule;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class ScheduleDetailSidePanel extends JPanel {

    private final Color cardBg = new Color(0xFFFFFF);
    private final Color borderColor = new Color(0xE2E8F0);
    private final Color accentColor = new Color(0x6366F1);

    private Runnable onCloseCallback;
    private Consumer<Schedule> onSaveCallback;
    private Schedule currentSchedule;

    private JComboBox<CourseClass> cbCourseClass;
    private JTextField tfDate;
    private JTextField tfStartTime;
    private JTextField tfEndTime;
    private JTextField tfRoomId;

    private CourseClassDAO classDAO = CourseClassDAO.getInstance();

    public ScheduleDetailSidePanel(Runnable onCloseCallback) {
        this.onCloseCallback = onCloseCallback;
        initUI();
    }

    public void setSchedule(Schedule schedule, Consumer<Schedule> onSaveCallback) {
        this.currentSchedule = schedule;
        this.onSaveCallback = onSaveCallback;

        loadComboBoxData();

        if (schedule != null) {
            if (schedule.getCourseClass() != null) {
                for (int i = 0; i < cbCourseClass.getItemCount(); i++) {
                    if (cbCourseClass.getItemAt(i).getId() == schedule.getCourseClass().getId()) {
                        cbCourseClass.setSelectedIndex(i);
                        break;
                    }
                }
            }
            tfDate.setText(schedule.getScheduleDate() != null ? schedule.getScheduleDate().toString() : LocalDate.now().toString());
            tfStartTime.setText(schedule.getStartTime() != null ? schedule.getStartTime().toString() : "08:00");
            tfEndTime.setText(schedule.getEndTime() != null ? schedule.getEndTime().toString() : "10:00");
            tfRoomId.setText(schedule.getRoomId() != null ? schedule.getRoomId().toString() : "");
        } else {
            if (cbCourseClass.getItemCount() > 0) cbCourseClass.setSelectedIndex(0);
            tfDate.setText(LocalDate.now().toString());
            tfStartTime.setText("08:00");
            tfEndTime.setText("10:00");
            tfRoomId.setText("");
        }
    }

    private void loadComboBoxData() {
        cbCourseClass.removeAllItems();
        List<CourseClass> classes = classDAO.getAllClasses();
        for (CourseClass c : classes) {
            cbCourseClass.addItem(c);
        }
    }

    private void onSave() {
        if (onSaveCallback != null) {
            if (currentSchedule == null) {
                currentSchedule = new Schedule();
            }

            CourseClass selectedClass = (CourseClass) cbCourseClass.getSelectedItem();
            if (selectedClass == null) {
                JOptionPane.showMessageDialog(this, "Please select a Class.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                currentSchedule.setScheduleDate(LocalDate.parse(tfDate.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid Date format. Use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                currentSchedule.setStartTime(LocalTime.parse(tfStartTime.getText().trim()));
                currentSchedule.setEndTime(LocalTime.parse(tfEndTime.getText().trim()));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid Time format. Use HH:mm.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String roomText = tfRoomId.getText().trim();
                currentSchedule.setRoomId(roomText.isEmpty() ? null : Integer.parseInt(roomText));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid Room ID. Must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentSchedule.setCourseClass(selectedClass);
            onSaveCallback.accept(currentSchedule);
        }
    }

    private void initUI() {
        setLayout(new MigLayout("wrap 1, fillx, insets 25", "[grow]", ""));
        setBackground(cardBg);
        putClientProperty(FlatClientProperties.STYLE, "arc: 0");
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, borderColor));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Schedule Details");
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
        cbCourseClass = new JComboBox<>();
        cbCourseClass.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CourseClass) {
                    setText(((CourseClass) value).getClassName());
                }
                return this;
            }
        });
        styleComboBox(cbCourseClass);

        tfDate = new JTextField();
        styleTextField(tfDate, "yyyy-MM-dd");

        tfStartTime = new JTextField();
        styleTextField(tfStartTime, "HH:mm");

        tfEndTime = new JTextField();
        styleTextField(tfEndTime, "HH:mm");

        tfRoomId = new JTextField();
        styleTextField(tfRoomId, "e.g. 101");

        add(createLabel("Class"));
        add(cbCourseClass, "growx, gapbottom 15");

        add(createLabel("Date (yyyy-MM-dd)"));
        add(tfDate, "growx, gapbottom 15");

        add(createLabel("Start Time (HH:mm)"));
        add(tfStartTime, "growx, gapbottom 15");

        add(createLabel("End Time (HH:mm)"));
        add(tfEndTime, "growx, gapbottom 15");

        add(createLabel("Room ID"));
        add(tfRoomId, "growx, gapbottom 30");

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
        tf.putClientProperty(FlatClientProperties.STYLE, "focusedBorderColor: #6366F1; borderColor: #CBD5E1; background: #F8FAFC; margin: 5, 10, 5, 10");
        tf.setPreferredSize(new Dimension(-1, 44));
        tf.setMinimumSize(new Dimension(10, 44));
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.putClientProperty(FlatClientProperties.STYLE, "focusedBorderColor: #6366F1; background: #F8FAFC; borderColor: #CBD5E1");
        cb.setPreferredSize(new Dimension(-1, 40));
        cb.setMinimumSize(new Dimension(10, 40));
    }
}
