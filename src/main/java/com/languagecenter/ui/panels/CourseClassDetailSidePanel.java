package com.languagecenter.ui.panels;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.languagecenter.dao.CourseDAO;
import com.languagecenter.dao.TeacherDAO;
import com.languagecenter.entity.Course;
import com.languagecenter.entity.CourseClass;
import com.languagecenter.entity.Teacher;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.util.List;
import java.util.function.Consumer;

public class CourseClassDetailSidePanel extends JPanel {

    private final Color cardBg = new Color(0xFFFFFF);
    private final Color borderColor = new Color(0xE2E8F0);
    private final Color accentColor = new Color(0x6366F1);

    private Runnable onCloseCallback;
    private Consumer<CourseClass> onSaveCallback;
    private CourseClass currentClass;

    private JTextField tfClassName;
    private JComboBox<CourseComboItem> cbCourse;
    private JComboBox<TeacherComboItem> cbTeacher;
    private DatePicker dpStartDate;
    private JComboBox<String> cbSchedulePattern;
    private JTextField tfStartTime;
    private JTextField tfEndTime;
    private JTextField tfEndDate;
    private JSpinner spMaxStudent;
    private JComboBox<String> cbStatus;

    public CourseClassDetailSidePanel(Runnable onCloseCallback) {
        this.onCloseCallback = onCloseCallback;
        initUI();
    }

    public void setClass(CourseClass courseClass, Consumer<CourseClass> onSaveCallback) {
        this.currentClass = courseClass;
        this.onSaveCallback = onSaveCallback;

        refreshComboBoxes();

        if (courseClass != null) {
            tfClassName.setText(courseClass.getClassName());
            dpStartDate.setDate(courseClass.getStartDate());
            cbSchedulePattern.setSelectedItem(courseClass.getSchedulePattern() != null ? courseClass.getSchedulePattern() : "2-4-6");
            tfStartTime.setText(courseClass.getStartTime() != null ? courseClass.getStartTime().toString() : "08:00");
            tfEndTime.setText(courseClass.getEndTime() != null ? courseClass.getEndTime().toString() : "10:00");
            tfEndDate.setText(courseClass.getEndDate() != null ? courseClass.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
            spMaxStudent.setValue(courseClass.getMaxStudent());
            cbStatus.setSelectedItem(courseClass.getStatus());

            if (courseClass.getCourse() != null) {
                for (int i = 0; i < cbCourse.getItemCount(); i++) {
                    if (cbCourse.getItemAt(i).course.getId() == courseClass.getCourse().getId()) {
                        cbCourse.setSelectedIndex(i);
                        break;
                    }
                }
            }

            if (courseClass.getTeacher() != null) {
                for (int i = 0; i < cbTeacher.getItemCount(); i++) {
                    if (cbTeacher.getItemAt(i).teacher.getId() == courseClass.getTeacher().getId()) {
                        cbTeacher.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } else {
            tfClassName.setText("");
            dpStartDate.setDate(null);
            cbSchedulePattern.setSelectedIndex(0);
            tfStartTime.setText("08:00");
            tfEndTime.setText("10:00");
            tfEndDate.setText("");
            spMaxStudent.setValue(20);
            cbStatus.setSelectedIndex(0);
            if (cbCourse.getItemCount() > 0)
                cbCourse.setSelectedIndex(0);
            if (cbTeacher.getItemCount() > 0)
                cbTeacher.setSelectedIndex(0);
        }
        updateEndDate();
    }

    private void refreshComboBoxes() {
        cbCourse.removeAllItems();
        List<Course> courses = CourseDAO.getInstance().getAllCourses();
        for (Course c : courses) {
            cbCourse.addItem(new CourseComboItem(c));
        }

        cbTeacher.removeAllItems();
        List<Teacher> teachers = TeacherDAO.getInstance().getAllTeachers();
        for (Teacher t : teachers) {
            cbTeacher.addItem(new TeacherComboItem(t));
        }
    }

    private void onSave() {
        if (onSaveCallback != null) {
            if (currentClass == null) {
                currentClass = new CourseClass();
            }

            String name = tfClassName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Class Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentClass.setClassName(name);
            CourseComboItem selectedCourse = (CourseComboItem) cbCourse.getSelectedItem();
            if (selectedCourse != null) {
                currentClass.setCourse(selectedCourse.course);
            }

            TeacherComboItem selectedTeacher = (TeacherComboItem) cbTeacher.getSelectedItem();
            if (selectedTeacher != null) {
                currentClass.setTeacher(selectedTeacher.teacher);
            }

            currentClass.setMaxStudent((Integer) spMaxStudent.getValue());
            currentClass.setStatus((String) cbStatus.getSelectedItem());
            currentClass.setSchedulePattern((String) cbSchedulePattern.getSelectedItem());

            try {
                currentClass.setStartTime(java.time.LocalTime.parse(tfStartTime.getText().trim()));
                currentClass.setEndTime(java.time.LocalTime.parse(tfEndTime.getText().trim()));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid Time format. Use HH:mm.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentClass.setStartDate(dpStartDate.getDate());
            
            // Re-calculate end date before saving to be sure
            updateEndDate();
            String endDateText = tfEndDate.getText();
            if (!endDateText.isEmpty()) {
                currentClass.setEndDate(LocalDate.parse(endDateText, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }

            onSaveCallback.accept(currentClass);
        }
    }

    private void updateEndDate() {
        LocalDate start = dpStartDate.getDate();
        CourseComboItem item = (CourseComboItem) cbCourse.getSelectedItem();
        String pattern = (String) cbSchedulePattern.getSelectedItem();

        if (start != null && item != null && pattern != null) {
            int duration = item.course.getDuration(); // Duration is total sessions
            if (duration <= 0) {
                tfEndDate.setText("");
                return;
            }

            LocalDate end = start;
            int sessionsCount = 0;
            
            // Pattern rules
            // 2-4-6: Monday, Wednesday, Friday
            // 3-5-7: Tuesday, Thursday, Saturday
            
            while (sessionsCount < duration) {
                DayOfWeek dow = end.getDayOfWeek();
                boolean isSessionDay = false;
                if (pattern.equals("2-4-6")) {
                    if (dow == DayOfWeek.MONDAY || dow == DayOfWeek.WEDNESDAY || dow == DayOfWeek.FRIDAY) {
                        isSessionDay = true;
                    }
                } else if (pattern.equals("3-5-7")) {
                    if (dow == DayOfWeek.TUESDAY || dow == DayOfWeek.THURSDAY || dow == DayOfWeek.SATURDAY) {
                        isSessionDay = true;
                    }
                }
                
                if (isSessionDay) {
                    sessionsCount++;
                    if (sessionsCount == duration) break;
                }
                end = end.plusDays(1);
            }
            tfEndDate.setText(end.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            tfEndDate.setText("");
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

        JLabel lblTitle = new JLabel("Class Details");
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

        add(headerPanel, "growx, gapbottom 20");

        // Form fields
        tfClassName = createTextField("e.g. ENG-Basic-01");
        spMaxStudent = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));
        spMaxStudent.setPreferredSize(new Dimension(-1, 40));

        add(createLabel("Class Name"));
        add(tfClassName, "growx, gapbottom 10");

        add(createLabel("Course"));
        cbCourse = new JComboBox<>();
        styleComboBox(cbCourse);
        cbCourse.addActionListener(e -> updateEndDate());
        add(cbCourse, "growx, gapbottom 10");

        add(createLabel("Teacher"));
        cbTeacher = new JComboBox<>();
        styleComboBox(cbTeacher);
        add(cbTeacher, "growx, gapbottom 10");

        add(createLabel("Start Date"));
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra("dd/MM/yyyy");
        dpStartDate = new DatePicker(settings);
        dpStartDate.addDateChangeListener(e -> updateEndDate());
        add(dpStartDate, "growx, gapbottom 10");

        add(createLabel("Schedule Pattern"));
        cbSchedulePattern = new JComboBox<>(new String[] { "2-4-6", "3-5-7" });
        cbSchedulePattern.putClientProperty(FlatClientProperties.STYLE, "focusColor: #6366F1; background: #F8FAFC");
        cbSchedulePattern.setPreferredSize(new Dimension(-1, 40));
        cbSchedulePattern.addActionListener(e -> updateEndDate());
        add(cbSchedulePattern, "growx, gapbottom 10");

        JPanel timePanel = new JPanel(new MigLayout("insets 0, fillx", "[grow][grow]", "[][]"));
        timePanel.setOpaque(false);
        
        tfStartTime = createTextField("08:00");
        tfEndTime = createTextField("10:00");
        
        timePanel.add(createLabel("Start Time (HH:mm)"), "cell 0 0");
        timePanel.add(createLabel("End Time (HH:mm)"), "cell 1 0");
        timePanel.add(tfStartTime, "cell 0 1, growx");
        timePanel.add(tfEndTime, "cell 1 1, growx");
        
        add(timePanel, "growx, gapbottom 10");

        add(createLabel("End Date (Calculated)"));
        tfEndDate = createTextField("");
        tfEndDate.setEditable(false);
        tfEndDate.setBackground(new Color(0xF1F5F9));
        add(tfEndDate, "growx, gapbottom 10");

        add(createLabel("Max Students"));
        add(spMaxStudent, "growx, gapbottom 10");

        add(createLabel("Status"));
        cbStatus = new JComboBox<>(new String[] { "Opening", "On-going", "Completed", "Cancelled" });
        styleComboBox(cbStatus);
        add(cbStatus, "growx, gapbottom 20");

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
        tf.setPreferredSize(new Dimension(-1, 40));
        return tf;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.putClientProperty(FlatClientProperties.STYLE,
                "focusColor: #6366F1; background: #F8FAFC");
        cb.setPreferredSize(new Dimension(-1, 40));
    }

    private static class CourseComboItem {
        Course course;

        public CourseComboItem(Course course) {
            this.course = course;
        }

        @Override
        public String toString() {
            return course.getName();
        }
    }

    private static class TeacherComboItem {
        Teacher teacher;

        public TeacherComboItem(Teacher teacher) {
            this.teacher = teacher;
        }

        @Override
        public String toString() {
            return teacher.getFullName();
        }
    }
}
