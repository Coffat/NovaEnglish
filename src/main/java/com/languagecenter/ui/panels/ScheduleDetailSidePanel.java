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
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

public class ScheduleDetailSidePanel extends JPanel {

    private final Color cardBg = new Color(0xFFFFFF);
    private final Color borderColor = new Color(0xE2E8F0);
    private final Color accentColor = new Color(0x6366F1);

    private Runnable onCloseCallback;
    private Consumer<Schedule> onSaveCallback;
    private Schedule currentSchedule;

    private JComboBox<CourseClass> cbCourseClass;
    private JComboBox<String> cbPattern;
    private JTextField tfRoomId;
    private DatePicker dpSessionDate;
    private JTextField tfStartTime;
    private JTextField tfEndTime;
    private JRadioButton rbSingleSession;
    private JRadioButton rbBatchGenerate;
    private JTextArea taSchedulePreview;
    private JLabel lblClassInfo;

    // Mode-specific components for toggling
    private JLabel lblPattern;
    private JLabel lblClassInfoDynamic;
    private JLabel lblPreview;
    private JScrollPane spPreview;
    private JLabel lblSingleSessionHeader;
    private JPanel sessionFields;

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
                setComboBoxSelectedClass(schedule.getCourseClass());
            }
            tfRoomId.setText(schedule.getRoomId() != null ? schedule.getRoomId().toString() : "");
            dpSessionDate.setDate(schedule.getScheduleDate());
            tfStartTime.setText(schedule.getStartTime() != null ? schedule.getStartTime().toString() : "");
            tfEndTime.setText(schedule.getEndTime() != null ? schedule.getEndTime().toString() : "");
            rbSingleSession.setSelected(true);
        } else {
            tfRoomId.setText("");
            dpSessionDate.setDate(LocalDate.now());
            tfStartTime.setText("");
            tfEndTime.setText("");
            
            rbBatchGenerate.setSelected(true);
        }
        toggleModeFields();
        updateClassInfo();
    }

    private void setComboBoxSelectedClass(CourseClass target) {
        for (int i = 0; i < cbCourseClass.getItemCount(); i++) {
            if (cbCourseClass.getItemAt(i).getId() == target.getId()) {
                cbCourseClass.setSelectedIndex(i);
                break;
            }
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
            CourseClass selectedClass = (CourseClass) cbCourseClass.getSelectedItem();
            if (selectedClass == null) {
                JOptionPane.showMessageDialog(this, "Please select a Class.");
                return;
            }

            Integer roomId;
            try {
                String roomText = tfRoomId.getText().trim();
                roomId = roomText.isEmpty() ? null : Integer.parseInt(roomText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Room ID.");
                return;
            }

            String newPattern = (String) cbPattern.getSelectedItem();
            
            if (rbBatchGenerate.isSelected()) {
                saveEntireClass(selectedClass, newPattern, roomId);
            } else {
                if (currentSchedule == null) {
                    currentSchedule = new Schedule();
                }
                saveSingleSession(selectedClass, roomId);
            }
        }
    }

    private void saveSingleSession(CourseClass selectedClass, Integer roomId) {
        try {
            currentSchedule.setCourseClass(selectedClass);
            currentSchedule.setRoomId(roomId);
            currentSchedule.setScheduleDate(dpSessionDate.getDate());
            
            String startTimeStr = tfStartTime.getText().trim();
            String endTimeStr = tfEndTime.getText().trim();
            
            if (!startTimeStr.isEmpty()) currentSchedule.setStartTime(LocalTime.parse(startTimeStr));
            if (!endTimeStr.isEmpty()) currentSchedule.setEndTime(LocalTime.parse(endTimeStr));
            
            onSaveCallback.accept(currentSchedule);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid time format (HH:mm) or date.");
        }
    }

    private void saveEntireClass(CourseClass selectedClass, String newPattern, Integer roomId) {
        String msg = "Confirm generating/updating the entire schedule for class " + selectedClass.getClassName() + " with pattern " + newPattern + "?\n(Existing schedules will be deleted)";
        int confirm = JOptionPane.showConfirmDialog(this, msg, "Confirm", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                selectedClass.setSchedulePattern(newPattern);
                classDAO.updateClass(selectedClass);
                
                List<Schedule> newSchedules = generateSchedules(selectedClass, roomId);
                if (newSchedules.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No sessions were created.");
                    return;
                }

                com.languagecenter.dao.ScheduleDAO.getInstance().replaceSchedulesForClass(selectedClass.getId(), newSchedules);
                JOptionPane.showMessageDialog(this, "Updated " + newSchedules.size() + " new sessions.");
                onSaveCallback.accept(null);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private List<Schedule> generateSchedules(CourseClass cls, Integer roomId) {
        List<Schedule> list = new ArrayList<>();
        LocalDate start = cls.getStartDate();
        LocalDate end = cls.getEndDate();
        String pattern = cls.getSchedulePattern();
        
        if (start == null || end == null || pattern == null) return list;
        
        LocalDate current = start;
        while (!current.isAfter(end)) {
            DayOfWeek dow = current.getDayOfWeek();
            boolean isDay = false;
            if (pattern.equals("2-4-6")) {
                if (dow == DayOfWeek.MONDAY || dow == DayOfWeek.WEDNESDAY || dow == DayOfWeek.FRIDAY) isDay = true;
            } else if (pattern.equals("3-5-7")) {
                if (dow == DayOfWeek.TUESDAY || dow == DayOfWeek.THURSDAY || dow == DayOfWeek.SATURDAY) isDay = true;
            }
            
            if (isDay) {
                Schedule s = new Schedule();
                s.setCourseClass(cls);
                s.setScheduleDate(current);
                s.setStartTime(cls.getStartTime());
                s.setEndTime(cls.getEndTime());
                s.setRoomId(roomId);
                list.add(s);
            }
            current = current.plusDays(1);
        }
        return list;
    }

    private void updateClassInfo() {
        CourseClass selected = (CourseClass) cbCourseClass.getSelectedItem();
        if (selected != null) {
            String currentPattern = (String) cbPattern.getSelectedItem();
            String info = String.format("<html><b>Pattern:</b> %s<br><b>Time:</b> %s - %s<br><b>Range:</b> %s to %s</html>",
                currentPattern,
                selected.getStartTime() != null ? selected.getStartTime() : "N/A",
                selected.getEndTime() != null ? selected.getEndTime() : "N/A",
                selected.getStartDate() != null ? selected.getStartDate() : "N/A",
                selected.getEndDate() != null ? selected.getEndDate() : "N/A");
            lblClassInfo.setText(info);
            
            // Override the objects pattern for preview
            String oldPattern = selected.getSchedulePattern();
            selected.setSchedulePattern(currentPattern);
            List<Schedule> preview = generateSchedules(selected, null);
            selected.setSchedulePattern(oldPattern); // restore it
            StringBuilder sb = new StringBuilder("Preview Dates:\n");
            for (int i = 0; i < Math.min(preview.size(), 10); i++) {
                sb.append("- ").append(preview.get(i).getScheduleDate()).append("\n");
            }
            if (preview.size() > 10) sb.append("... and ").append(preview.size() - 10).append(" more sessions.");
            taSchedulePreview.setText(sb.toString());
        } else {
            lblClassInfo.setText("Select a class to see info.");
            taSchedulePreview.setText("");
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
        cbCourseClass.addActionListener(e -> {
            CourseClass selectedClass = (CourseClass) cbCourseClass.getSelectedItem();
            if (selectedClass != null) {
                cbPattern.setSelectedItem(selectedClass.getSchedulePattern() != null ? selectedClass.getSchedulePattern() : "2-4-6");
            }
            updateClassInfo();
        });

        cbPattern = new JComboBox<>(new String[] { "2-4-6", "3-5-7" });
        styleComboBox(cbPattern);
        cbPattern.addActionListener(e -> updateClassInfo());

        lblClassInfo = new JLabel();
        lblClassInfo.setFont(new Font("Inter", Font.PLAIN, 12));
        lblClassInfo.setForeground(new Color(0x475569));
        
        tfRoomId = new JTextField();
        styleTextField(tfRoomId, "Room ID");

        DatePickerSettings ds = new DatePickerSettings();
        ds.setFormatForDatesCommonEra("dd/MM/yyyy");
        dpSessionDate = new DatePicker(ds);
        dpSessionDate.setBackground(cardBg);
        dpSessionDate.getComponentDateTextField().putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);

        tfStartTime = new JTextField();
        styleTextField(tfStartTime, "HH:mm");
        tfEndTime = new JTextField();
        styleTextField(tfEndTime, "HH:mm");

        rbSingleSession = new JRadioButton("Single Session (One-off)");
        rbBatchGenerate = new JRadioButton("Batch Generate (Whole Course)");
        rbSingleSession.setOpaque(false);
        rbBatchGenerate.setOpaque(false);
        rbSingleSession.setFont(new Font("Inter", Font.BOLD, 13));
        rbBatchGenerate.setFont(new Font("Inter", Font.BOLD, 13));
        
        rbSingleSession.addActionListener(e -> toggleModeFields());
        rbBatchGenerate.addActionListener(e -> toggleModeFields());

        ButtonGroup group = new ButtonGroup();
        group.add(rbSingleSession);
        group.add(rbBatchGenerate);

        add(createLabel("Operation Mode"));
        add(rbSingleSession, "growx");
        add(rbBatchGenerate, "growx, gapbottom 15");

        taSchedulePreview = new JTextArea(6, 20);
        taSchedulePreview.setEditable(false);
        taSchedulePreview.setFont(new Font("Monospaced", Font.PLAIN, 11));
        taSchedulePreview.setBackground(new Color(0xF8FAFC));
        taSchedulePreview.setBorder(BorderFactory.createLineBorder(new Color(0xE2E8F0)));
        spPreview = new JScrollPane(taSchedulePreview);
        spPreview.setBorder(BorderFactory.createEmptyBorder());
        spPreview.putClientProperty(FlatClientProperties.STYLE, "hidemode 3");

        add(createLabel("Course Class"));
        add(cbCourseClass, "growx, gapbottom 10");

        lblPattern = createLabel("Schedule Pattern");
        lblPattern.putClientProperty(FlatClientProperties.STYLE, "hidemode 3");
        add(lblPattern);
        
        cbPattern.putClientProperty(FlatClientProperties.STYLE, "focusColor: #6366F1; background: #F8FAFC; hidemode 3");
        add(cbPattern, "growx, gapbottom 10");

        lblClassInfoDynamic = lblClassInfo;
        lblClassInfoDynamic.putClientProperty(FlatClientProperties.STYLE, "hidemode 3");
        add(lblClassInfoDynamic, "growx, gapbottom 15");

        lblSingleSessionHeader = createLabel("Single Session Settings:");
        lblSingleSessionHeader.putClientProperty(FlatClientProperties.STYLE, "hidemode 3");
        add(lblSingleSessionHeader);
        
        sessionFields = new JPanel(new MigLayout("insets 0, gap 5, fillx", "[grow][grow][grow]"));
        sessionFields.setOpaque(false);
        sessionFields.putClientProperty(FlatClientProperties.STYLE, "hidemode 3");
        sessionFields.add(createLabel("Date"), "wrap");
        sessionFields.add(dpSessionDate, "growx, wrap");
        sessionFields.add(createLabel("Start Time"), "split 2");
        sessionFields.add(createLabel("End Time"), "wrap");
        sessionFields.add(tfStartTime, "growx, split 2");
        sessionFields.add(tfEndTime, "growx, wrap");
        add(sessionFields, "growx, gapbottom 15");

        add(createLabel("Room ID"));
        add(tfRoomId, "growx, gapbottom 20");

        lblPreview = createLabel("Schedule Preview (Full Course)");
        lblPreview.putClientProperty(FlatClientProperties.STYLE, "hidemode 3");
        add(lblPreview);
        add(spPreview, "growx, gapbottom 30");

        add(new JLabel(), "growy, pushy");

        // Action Buttons
        JButton btnSave = new JButton("Save Changes");
        btnSave.setBackground(accentColor);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Inter", Font.BOLD, 14));
        btnSave.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> onSave());

        add(btnSave, "growx");
    }

    private void toggleModeFields() {
        boolean isSingle = rbSingleSession.isSelected();
        
        // Single session components
        lblSingleSessionHeader.setVisible(isSingle);
        sessionFields.setVisible(isSingle);
        
        // Batch generate components
        lblPattern.setVisible(!isSingle);
        cbPattern.setVisible(!isSingle);
        lblClassInfoDynamic.setVisible(!isSingle);
        lblPreview.setVisible(!isSingle);
        spPreview.setVisible(!isSingle);
        
        revalidate();
        repaint();
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
        tf.putClientProperty(FlatClientProperties.STYLE, "focusColor: #6366F1; background: #F8FAFC; margin: 5, 10, 5, 10");
        tf.setPreferredSize(new Dimension(-1, 44));
        tf.setMinimumSize(new Dimension(10, 44));
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.putClientProperty(FlatClientProperties.STYLE, "focusColor: #6366F1; background: #F8FAFC");
        cb.setPreferredSize(new Dimension(-1, 40));
        cb.setMinimumSize(new Dimension(10, 40));
    }
}
