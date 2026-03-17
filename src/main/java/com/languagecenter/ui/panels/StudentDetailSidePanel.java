package com.languagecenter.ui.panels;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.languagecenter.entity.Student;
import com.languagecenter.entity.Course;
import com.languagecenter.dao.CourseDAO;
import com.languagecenter.dao.StudentDAO;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.List;
import java.util.function.Consumer;

public class StudentDetailSidePanel extends JPanel {

    private final Color cardBg = new Color(0xFFFFFF);
    private final Color borderColor = new Color(0xE2E8F0);
    private final Color accentColor = new Color(0x6366F1);

    private Runnable onCloseCallback;
    private Consumer<Student> onSaveCallback;
    private Student currentStudent;

    private JTextField tfFullName;
    private DatePicker dpDob;
    private JTextField tfEmail;
    private JTextField tfPhone;
    
    private JComboBox<String> cbStatus;
    
    private JPanel courseCheckboxPanel;

    public StudentDetailSidePanel(Runnable onCloseCallback) {
        this.onCloseCallback = onCloseCallback;
        initUI();
    }

    public void setStudent(Student student, Consumer<Student> onSaveCallback) {
        this.currentStudent = student;
        this.onSaveCallback = onSaveCallback;

        if (student != null) {
            tfFullName.setText(student.getFullName());
            dpDob.setDate(student.getDateOfBirth());
            tfEmail.setText(student.getEmail());
            tfPhone.setText(student.getPhone());
            cbStatus.setSelectedItem(student.getStatus());
            
            // Mark registered courses
            java.util.Set<Integer> studentCourseIds = new java.util.HashSet<>();
            if (student.getEnrollments() != null) {
                for (com.languagecenter.entity.Enrollment e : student.getEnrollments()) {
                    if (e.getCourseClass() != null && e.getCourseClass().getCourse() != null) {
                        studentCourseIds.add(e.getCourseClass().getCourse().getId());
                    }
                }
            }
            
            if (courseCheckboxPanel != null) {
                for (Component comp : courseCheckboxPanel.getComponents()) {
                    if (comp instanceof JCheckBox cb) {
                        com.languagecenter.entity.Course course = (com.languagecenter.entity.Course) cb.getClientProperty("courseObject");
                        if (course != null) {
                            cb.setSelected(studentCourseIds.contains(course.getId()));
                        }
                    }
                }
            }
        } else {
            tfFullName.setText("");
            dpDob.setDate(null);
            tfEmail.setText("");
            tfPhone.setText("");
            cbStatus.setSelectedIndex(0);
            
            // Clear all checkboxes
            if (courseCheckboxPanel != null) {
                for (Component comp : courseCheckboxPanel.getComponents()) {
                    if (comp instanceof JCheckBox cb) {
                        cb.setSelected(false);
                    }
                }
            }
        }
    }

    private void onSave() {
        if (onSaveCallback != null) {
            if (currentStudent == null) {
                currentStudent = new Student();
            }

            String name = tfFullName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String phone = tfPhone.getText().trim();
            if (!phone.isEmpty() && !phone.matches("^0\\d{9}$")) {
                JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits and start with 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentStudent.setFullName(name);
            currentStudent.setEmail(tfEmail.getText().trim());
            currentStudent.setPhone(phone);
            currentStudent.setStatus((String) cbStatus.getSelectedItem());
            currentStudent.setDateOfBirth(dpDob.getDate());

            List<Course> selectedCourses = new java.util.ArrayList<>();
            if (courseCheckboxPanel != null) {
                for (Component comp : courseCheckboxPanel.getComponents()) {
                    if (comp instanceof JCheckBox) {
                        JCheckBox cb = (JCheckBox) comp;
                        if (cb.isSelected()) {
                            selectedCourses.add((Course) cb.getClientProperty("courseObject"));
                        }
                    }
                }
            }

            try {
                if (currentStudent.getId() == 0) {
                    StudentDAO.getInstance().saveStudentWithPayments(currentStudent, selectedCourses);
                } else {
                    StudentDAO.getInstance().updateStudent(currentStudent);
                    
                    // If student becomes Inactive, remove from all classes
                    if ("Inactive".equalsIgnoreCase(currentStudent.getStatus())) {
                        com.languagecenter.dao.EnrollmentDAO.getInstance().deleteEnrollmentsByStudentId(currentStudent.getId());
                    }
                }
                
                if (onSaveCallback != null) {
                    onSaveCallback.accept(currentStudent);
                }
                
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
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

        JLabel lblTitle = new JLabel("Student Details");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0x1E293B));

        JButton btnClose = new JButton("✕");
        btnClose.setFont(new Font("Inter", Font.BOLD, 18));
        btnClose.setForeground(new Color(0x94A3B8));
        // Fallback to text if icon fails
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

        // Form fields (vertical)
        tfFullName = createTextField("e.g. Samuel");
        tfEmail = createTextField("student@example.com");
        tfPhone = createTextField("09xxxxxxxx");
        
        // Setup Phone filter
        ((AbstractDocument) tfPhone.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String result = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                if (result.length() <= 10 && result.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        dateSettings.setAllowKeyboardEditing(true);
        dpDob = new DatePicker(dateSettings);
        dpDob.setBackground(cardBg);
        JTextField dobTf = dpDob.getComponentDateTextField();
        dobTf.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
        dobTf.putClientProperty(FlatClientProperties.STYLE,
                "focusColor: #6366F1; background: #F8FAFC; margin: 5, 10, 5, 10");
        
        JButton dobBtn = dpDob.getComponentToggleCalendarButton();
        dobBtn.putClientProperty(FlatClientProperties.STYLE,
                "background: #6366F1; foreground: #FFFFFF; margin: 5, 10, 5, 10");

        add(createLabel("Full Name"));
        add(tfFullName, "growx, gapbottom 15");

        add(createLabel("Date of Birth (dd/MM/yyyy)"));
        add(dpDob, "growx, h 44!, gapbottom 15");

        add(createLabel("Email Address"));
        add(tfEmail, "growx, gapbottom 15");

        add(createLabel("Phone Number"));
        add(tfPhone, "growx, gapbottom 15");

        add(createLabel("Status"));
        cbStatus = new JComboBox<>(new String[] { "Active", "Inactive" });
        cbStatus.putClientProperty(FlatClientProperties.STYLE,
                "focusColor: #6366F1; background: #F8FAFC");
        cbStatus.setPreferredSize(new Dimension(-1, 40));
        add(cbStatus, "growx, gapbottom 15");

        add(createLabel("Select Courses"));
        
        courseCheckboxPanel = new JPanel();
        courseCheckboxPanel.setLayout(new BoxLayout(courseCheckboxPanel, BoxLayout.Y_AXIS));
        courseCheckboxPanel.setBackground(new Color(0xF8FAFC));
        courseCheckboxPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        List<Course> courses = CourseDAO.getInstance().getAllCourses();
        for (Course c : courses) {
            JCheckBox cb = new JCheckBox(c.getName() + " - $" + c.getFee());
            cb.putClientProperty("courseObject", c);
            cb.setBackground(new Color(0xF8FAFC));
            cb.setFont(new Font("Inter", Font.PLAIN, 13));
            cb.setCursor(new Cursor(Cursor.HAND_CURSOR));
            courseCheckboxPanel.add(cb);
        }

        JScrollPane scrollPane = new JScrollPane(courseCheckboxPanel);
        scrollPane.setPreferredSize(new Dimension(-1, 120));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xCBD5E1)));
        add(scrollPane, "growx, gapbottom 30");

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
        tf.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
        tf.putClientProperty(FlatClientProperties.STYLE,
                "focusColor: #6366F1; background: #F8FAFC; margin: 5, 10, 5, 10");
        tf.setPreferredSize(new Dimension(-1, 44));
        return tf;
    }
}
