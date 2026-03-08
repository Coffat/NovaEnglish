package com.languagecenter.ui.panels;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.languagecenter.dao.CourseClassDAO;
import com.languagecenter.dao.StudentDAO;
import com.languagecenter.entity.CourseClass;
import com.languagecenter.entity.Student;
import com.languagecenter.entity.Enrollment;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class EnrollmentDetailSidePanel extends JPanel {

    private final Color cardBg = new Color(0xFFFFFF);
    private final Color borderColor = new Color(0xE2E8F0);
    private final Color accentColor = new Color(0x6366F1);

    private Runnable onCloseCallback;
    private Consumer<Enrollment> onSaveCallback;
    private Enrollment currentEnrollment;

    private JComboBox<Student> cbStudent;
    private JComboBox<CourseClass> cbCourseClass;
    private JTextField tfDate;
    private JComboBox<String> cbStatus;
    private JTextField tfResult;

    private StudentDAO studentDAO = new StudentDAO();
    private CourseClassDAO classDAO = new CourseClassDAO();

    public EnrollmentDetailSidePanel(Runnable onCloseCallback) {
        this.onCloseCallback = onCloseCallback;
        initUI();
    }

    public void setEnrollment(Enrollment enrollment, Consumer<Enrollment> onSaveCallback) {
        this.currentEnrollment = enrollment;
        this.onSaveCallback = onSaveCallback;

        loadComboBoxData();

        if (enrollment != null) {
            // Find and set student
            if (enrollment.getStudent() != null) {
                for (int i = 0; i < cbStudent.getItemCount(); i++) {
                    if (cbStudent.getItemAt(i).getId() == enrollment.getStudent().getId()) {
                        cbStudent.setSelectedIndex(i);
                        break;
                    }
                }
            }
            // Find and set class
            if (enrollment.getCourseClass() != null) {
                for (int i = 0; i < cbCourseClass.getItemCount(); i++) {
                    if (cbCourseClass.getItemAt(i).getId() == enrollment.getCourseClass().getId()) {
                        cbCourseClass.setSelectedIndex(i);
                        break;
                    }
                }
            }
            tfDate.setText(enrollment.getEnrollmentDate() != null ? enrollment.getEnrollmentDate().toString() : LocalDate.now().toString());
            cbStatus.setSelectedItem(enrollment.getStatus() != null ? enrollment.getStatus() : "Pending");
            tfResult.setText(enrollment.getResult() != null ? enrollment.getResult().toString() : "");
        } else {
            if (cbStudent.getItemCount() > 0) cbStudent.setSelectedIndex(0);
            if (cbCourseClass.getItemCount() > 0) cbCourseClass.setSelectedIndex(0);
            tfDate.setText(LocalDate.now().toString());
            cbStatus.setSelectedIndex(0);
            tfResult.setText("");
        }
    }

    private void loadComboBoxData() {
        cbStudent.removeAllItems();
        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            cbStudent.addItem(s);
        }

        cbCourseClass.removeAllItems();
        List<CourseClass> classes = classDAO.getAllClasses();
        for (CourseClass c : classes) {
            cbCourseClass.addItem(c);
        }
    }

    private void onSave() {
        if (onSaveCallback != null) {
            if (currentEnrollment == null) {
                currentEnrollment = new Enrollment();
            }

            Student selectedStudent = (Student) cbStudent.getSelectedItem();
            CourseClass selectedClass = (CourseClass) cbCourseClass.getSelectedItem();

            if (selectedStudent == null) {
                JOptionPane.showMessageDialog(this, "Please select a Student.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (selectedClass == null) {
                JOptionPane.showMessageDialog(this, "Please select a Class.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String dateText = tfDate.getText().trim();
                currentEnrollment.setEnrollmentDate(LocalDate.parse(dateText, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid Date format. Use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String resultText = tfResult.getText().trim();
                currentEnrollment.setResult(resultText.isEmpty() ? null : Float.parseFloat(resultText));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid Result. Must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentEnrollment.setStudent(selectedStudent);
            currentEnrollment.setCourseClass(selectedClass);
            currentEnrollment.setStatus((String) cbStatus.getSelectedItem());

            onSaveCallback.accept(currentEnrollment);
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

        JLabel lblTitle = new JLabel("Enrollment Details");
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
        tfDate.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "yyyy-MM-dd");
        tfDate.putClientProperty(FlatClientProperties.STYLE, "focusedBorderColor: #6366F1; borderColor: #CBD5E1; background: #F8FAFC; margin: 5, 10, 5, 10");
        tfDate.setPreferredSize(new Dimension(-1, 44));
        
        cbStatus = new JComboBox<>(new String[]{"Pending", "Confirmed", "Cancelled"});
        styleComboBox(cbStatus);
        
        tfResult = new JTextField();
        tfResult.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. 8.5");
        tfResult.putClientProperty(FlatClientProperties.STYLE, "focusedBorderColor: #6366F1; borderColor: #CBD5E1; background: #F8FAFC; margin: 5, 10, 5, 10");
        tfResult.setPreferredSize(new Dimension(-1, 44));

        add(createLabel("Student"));
        add(cbStudent, "growx, gapbottom 15");

        add(createLabel("Class"));
        add(cbCourseClass, "growx, gapbottom 15");

        add(createLabel("Enrollment Date (yyyy-MM-dd)"));
        add(tfDate, "growx, gapbottom 15");
        
        add(createLabel("Status"));
        add(cbStatus, "growx, gapbottom 15");
        
        add(createLabel("Result (Score)"));
        add(tfResult, "growx, gapbottom 30");

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

    private void styleComboBox(JComboBox<?> cb) {
        cb.putClientProperty(FlatClientProperties.STYLE, "focusedBorderColor: #6366F1; background: #F8FAFC; borderColor: #CBD5E1");
        cb.setPreferredSize(new Dimension(-1, 40));
    }
}
