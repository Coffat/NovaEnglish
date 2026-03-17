package com.languagecenter.ui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.languagecenter.dao.AttendanceDAO;
import com.languagecenter.dao.CourseClassDAO;
import com.languagecenter.dao.EnrollmentDAO;
import com.languagecenter.dao.StudentDAO;
import com.languagecenter.entity.Attendance;
import com.languagecenter.entity.CourseClass;
import com.languagecenter.entity.Enrollment;
import com.languagecenter.entity.Student;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

public class TakeClassAttendanceDialog extends JDialog {

    private JComboBox<CourseClass> cbClass;
    private DatePicker dpDate;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnSave;
    
    private CourseClassDAO classDAO = CourseClassDAO.getInstance();
    private EnrollmentDAO enrollmentDAO = EnrollmentDAO.getInstance();
    private AttendanceDAO attendanceDAO = AttendanceDAO.getInstance();
    
    private Runnable onSaveSuccess;

    public TakeClassAttendanceDialog(JFrame parent, Runnable onSaveSuccess) {
        this(parent, null, onSaveSuccess);
    }

    public TakeClassAttendanceDialog(JFrame parent, CourseClass preSelectedClass, Runnable onSaveSuccess) {
        super(parent, preSelectedClass != null ? "Record Attendance: " + preSelectedClass.getClassName() : "Record Class Attendance", true);
        this.onSaveSuccess = onSaveSuccess;
        
        setSize(900, 700);
        setLocationRelativeTo(parent);
        
        initUI();
        loadClasses();
        
        if (preSelectedClass != null) {
            for (int i = 0; i < cbClass.getItemCount(); i++) {
                if (cbClass.getItemAt(i).getId() == preSelectedClass.getId()) {
                    cbClass.setSelectedIndex(i);
                    break;
                }
            }
            cbClass.setEnabled(false);
            loadStudents();
        }
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0xF8FAFC));
        
        // Header Panel
        JPanel headerPanel = new JPanel(new MigLayout("insets 20", "[][grow][]", "[]"));
        headerPanel.setOpaque(false);
        
        JLabel lblClass = new JLabel("Select Class:");
        cbClass = new JComboBox<>();
        cbClass.putClientProperty(FlatClientProperties.STYLE, "focusedBorderColor: #6366F1");
        cbClass.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CourseClass) {
                    setText(((CourseClass) value).getClassName());
                }
                return this;
            }
        });
        
        JLabel lblDate = new JLabel("Select Date:");
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("yyyy-MM-dd");
        dpDate = new DatePicker(dateSettings);
        dpDate.setDate(LocalDate.now());
        dpDate.addDateChangeListener(e -> loadStudents());
        
        headerPanel.add(lblClass);
        headerPanel.add(cbClass, "growx, w 200!");
        headerPanel.add(lblDate, "gapleft 20");
        headerPanel.add(dpDate, "w 180!");

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table Panel
        String[] columns = {"Student ID", "Student Name", "Status"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only status is editable
            }
        };
        
        table = new JTable(model);
        table.setRowHeight(40);
        table.getTableHeader().setPreferredSize(new Dimension(table.getWidth(), 40));
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));
        
        // Custom combo box for Status column
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Present", "Absent", "Late"});
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(cbStatus));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        footerPanel.setOpaque(false);
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());
        
        btnSave = new JButton("Save Attendance");
        btnSave.setBackground(new Color(0x6366F1));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> saveAttendance());
        
        footerPanel.add(btnCancel);
        footerPanel.add(btnSave);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Auto reload when changing selections
        cbClass.addActionListener(e -> {
            if (cbClass.getSelectedItem() != null) loadStudents();
        });
    }

    private void loadClasses() {
        cbClass.removeAllItems();
        List<CourseClass> classes = classDAO.getAllClasses();
        for (CourseClass c : classes) {
            if ("On-going".equals(c.getStatus())) {
                cbClass.addItem(c);
            }
        }
        if (cbClass.getItemCount() == 0) {
            for (CourseClass c : classes) {
                cbClass.addItem(c);
            }
        }
    }

    private void loadStudents() {
        CourseClass selectedClass = (CourseClass) cbClass.getSelectedItem();
        if (selectedClass == null) return;

        LocalDate attendanceDate = dpDate.getDate();
        if (attendanceDate == null) return;

        model.setRowCount(0);
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByClassId(selectedClass.getId());
        
        // Fetch existing attendances for this class and date
        List<Attendance> existingAttendances = attendanceDAO.getAttendancesByClassAndDate(selectedClass.getId(), attendanceDate);

        for (Enrollment e : enrollments) {
            String enrollStatus = e.getStatus();
            if (enrollStatus != null && (enrollStatus.equalsIgnoreCase("Cancelled") || enrollStatus.equalsIgnoreCase("Dropped") || enrollStatus.equalsIgnoreCase("Error"))) {
                continue; // Skip inactive enrollments
            }
            
            Student student = e.getStudent();
            if (student != null) {
                String status = "Present"; // Default
                // Check if there is already an attendance record for this student on this date
                for (Attendance a : existingAttendances) {
                    if (a.getStudent() != null && a.getStudent().getId() == student.getId()) {
                        status = a.getStatus();
                        break;
                    }
                }
                model.addRow(new Object[]{student.getId(), student.getFullName(), status});
            }
        }
        
        if (enrollments.isEmpty()) {
            // Optional: feedback for no students
        }
    }

    private void saveAttendance() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        CourseClass selectedClass = (CourseClass) cbClass.getSelectedItem();
        if (selectedClass == null) {
            JOptionPane.showMessageDialog(this, "Please select a class.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate attendanceDate = dpDate.getDate();
        if (attendanceDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid date.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int rowCount = model.getRowCount();
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(this, "No students to record attendance for.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Fetch existing to overwrite if necessary
        List<Attendance> existingAttendances = attendanceDAO.getAttendancesByClassAndDate(selectedClass.getId(), attendanceDate);
        StudentDAO studentDAO = StudentDAO.getInstance();

        try {
            for (int i = 0; i < rowCount; i++) {
                int studentId = (int) model.getValueAt(i, 0);
                String status = (String) model.getValueAt(i, 2);
    
                Student student = studentDAO.getStudentById(studentId);
                if (student != null) {
                    Attendance attendance = null;
                    // Check if exists
                    for (Attendance a : existingAttendances) {
                         if (a.getStudent() != null && a.getStudent().getId() == studentId) {
                             attendance = a;
                             break;
                         }
                    }

                    if (attendance == null) {
                        attendance = new Attendance();
                        attendance.setStudent(student);
                        attendance.setCourseClass(selectedClass);
                        attendance.setAttendanceDate(attendanceDate);
                        attendance.setStatus(status);
                        attendanceDAO.addAttendance(attendance);
                    } else {
                        attendance.setStatus(status);
                        attendanceDAO.updateAttendance(attendance);
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Attendance recorded and updated successfully for " + rowCount + " students.", "Success", JOptionPane.INFORMATION_MESSAGE);
            if (onSaveSuccess != null) {
                onSaveSuccess.run();
            }
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving attendance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
