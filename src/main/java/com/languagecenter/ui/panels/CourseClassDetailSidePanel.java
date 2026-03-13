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
import java.time.format.DateTimeParseException;
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
    private JTextField tfStartDate;
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
            tfStartDate.setText(courseClass.getStartDate() != null ? courseClass.getStartDate().toString() : "");
            tfEndDate.setText(courseClass.getEndDate() != null ? courseClass.getEndDate().toString() : "");
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
            tfStartDate.setText("");
            tfEndDate.setText("");
            spMaxStudent.setValue(20);
            cbStatus.setSelectedIndex(0);
            if (cbCourse.getItemCount() > 0)
                cbCourse.setSelectedIndex(0);
            if (cbTeacher.getItemCount() > 0)
                cbTeacher.setSelectedIndex(0);
        }
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

            try {
                String startDateText = tfStartDate.getText().trim();
                currentClass.setStartDate(startDateText.isEmpty() ? null : LocalDate.parse(startDateText));

                String endDateText = tfEndDate.getText().trim();
                currentClass.setEndDate(endDateText.isEmpty() ? null : LocalDate.parse(endDateText));
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid Date format. Use yyyy-MM-dd.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            onSaveCallback.accept(currentClass);
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

        add(headerPanel, "growx, gapbottom 20");

        // Form fields
        tfClassName = createTextField("e.g. ENG-Basic-01");
        cbCourse = new JComboBox<>();
        styleComboBox(cbCourse);
        cbTeacher = new JComboBox<>();
        styleComboBox(cbTeacher);
        tfStartDate = createTextField("yyyy-MM-dd");
        tfEndDate = createTextField("yyyy-MM-dd");
        spMaxStudent = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));
        spMaxStudent.setPreferredSize(new Dimension(-1, 40));

        add(createLabel("Class Name"));
        add(tfClassName, "growx, gapbottom 10");

        add(createLabel("Course"));
        add(cbCourse, "growx, gapbottom 10");

        add(createLabel("Teacher"));
        add(cbTeacher, "growx, gapbottom 10");

        add(createLabel("Start Date"));
        add(tfStartDate, "growx, gapbottom 10");

        add(createLabel("End Date"));
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
        tf.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; focusedBorderColor: #6366F1; borderColor: #CBD5E1; background: #F8FAFC; margin: 5, 10, 5, 10");
        tf.setPreferredSize(new Dimension(-1, 40));
        return tf;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; focusedBorderColor: #6366F1; background: #F8FAFC; borderColor: #CBD5E1");
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
