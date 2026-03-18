package com.languagecenter.ui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.languagecenter.dao.EnrollmentDAO;
import com.languagecenter.dao.StudentDAO;
import com.languagecenter.entity.CourseClass;
import com.languagecenter.entity.Enrollment;
import com.languagecenter.entity.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

public class ClassEnrollmentsDialog extends JDialog {

    private CourseClass courseClass;
    private EnrollmentDAO enrollmentDAO = EnrollmentDAO.getInstance();
    private StudentDAO studentDAO = StudentDAO.getInstance();

    private DefaultTableModel model;
    private JTable table;
    private JComboBox<Student> cbAddStudent;
    private JButton btnSave;
    private boolean isUpdating = false;

    public ClassEnrollmentsDialog(JFrame parent, CourseClass courseClass) {
        super(parent, "Enrollments: " + courseClass.getClassName(), true);
        this.courseClass = courseClass;

        setSize(800, 600);
        setLocationRelativeTo(parent);

        initUI();
        loadEnrollments();
        loadAvailableStudents();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0xF8FAFC));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Students in " + courseClass.getClassName());
        lblTitle.setFont(new Font("Inter", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0x1E293B));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // Add Student Area
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addPanel.setOpaque(false);
        cbAddStudent = new JComboBox<>();
        cbAddStudent.setPreferredSize(new Dimension(200, 40));
        cbAddStudent.putClientProperty(FlatClientProperties.STYLE, "focusedBorderColor: #6366F1");
        cbAddStudent.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student) {
                    setText(((Student) value).getFullName());
                }
                return this;
            }
        });

        JButton btnAdd = new JButton("Add to Class");
        btnAdd.setBackground(new Color(0x6366F1));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Inter", Font.BOLD, 14));
        btnAdd.putClientProperty(FlatClientProperties.STYLE, "margin: 8, 15, 8, 15");
        btnAdd.addActionListener(e -> addStudentToClass());

        addPanel.add(new JLabel("Select Student:"));
        addPanel.add(cbAddStudent);
        addPanel.add(btnAdd);

        headerPanel.add(addPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Student Name", "Enrollment Date", "Result", "Status", "Action"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.getTableHeader().setPreferredSize(new Dimension(table.getWidth(), 40));
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));
        table.setGridColor(new Color(0xE2E8F0));
        table.setShowVerticalLines(false);
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());

                if (row < table.getRowCount() && row >= 0 && column == 5) {
                    int enrollmentId = (int) model.getValueAt(table.convertRowIndexToModel(row), 0);
                    int confirm = JOptionPane.showConfirmDialog(ClassEnrollmentsDialog.this,
                            "Are you sure you want to remove this student from the class?", "Confirm Remove",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            enrollmentDAO.deleteEnrollment(enrollmentId);
                            loadEnrollments();
                            loadAvailableStudents();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(ClassEnrollmentsDialog.this, 
                                "Failed to remove student from class: " + ex.getMessage(), 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        // Set up Status editor
        TableColumn statusColumn = table.getColumnModel().getColumn(4);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Enrolled", "Ongoing", "Completed", "Dropped", "Failed"});
        statusColumn.setCellEditor(new DefaultCellEditor(statusCombo));

        // Add Listener for edits
        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (!isUpdating && e.getType() == TableModelEvent.UPDATE) {
                    btnSave.setEnabled(true);
                    btnSave.setBackground(new Color(0x6366F1));
                }
            }
        });

        table.addMouseMotionListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int column = table.getColumnModel().getColumnIndexAtX(e.getX());
                if (column == 5) {
                    table.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        footerPanel.setOpaque(false);

        btnSave = new JButton("Save Changes");
        btnSave.setEnabled(false);
        btnSave.setBackground(new Color(0xCBD5E1)); // Gray when disabled
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Inter", Font.BOLD, 14));
        btnSave.putClientProperty(FlatClientProperties.STYLE, "margin: 8, 20, 8, 20");
        btnSave.addActionListener(e -> saveAllChanges());

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        
        footerPanel.add(btnSave);
        footerPanel.add(btnClose);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void saveAllChanges() {
        try {
            for (int i = 0; i < model.getRowCount(); i++) {
                int enrollmentId = (int) model.getValueAt(i, 0);
                Object resultObj = model.getValueAt(i, 3);
                String status = model.getValueAt(i, 4).toString();

                Enrollment enrollment = enrollmentDAO.getEnrollmentById(enrollmentId);
                if (enrollment != null) {
                    if (resultObj != null && !resultObj.toString().trim().isEmpty()) {
                        enrollment.setResult(Float.parseFloat(resultObj.toString()));
                    }
                    enrollment.setStatus(status);
                    enrollmentDAO.updateEnrollment(enrollment);
                }
            }
            JOptionPane.showMessageDialog(this, "All changes saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            btnSave.setEnabled(false);
            btnSave.setBackground(new Color(0xCBD5E1));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving changes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadEnrollments() {
        isUpdating = true;
        model.setRowCount(0);
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByClassId(courseClass.getId());
        for (Enrollment e : enrollments) {
            Student student = e.getStudent();
            if (student != null) {
                model.addRow(new Object[]{
                        e.getId(),
                        student.getFullName(),
                        e.getEnrollmentDate() != null ? e.getEnrollmentDate().toString() : "",
                        e.getResult(),
                        e.getStatus(),
                        "❌ Remove"
                });
            }
        }
        isUpdating = false;
        if (btnSave != null) {
            btnSave.setEnabled(false);
            btnSave.setBackground(new Color(0xCBD5E1));
        }
    }

    private void loadAvailableStudents() {
        cbAddStudent.removeAllItems();
        List<Student> allStudents = studentDAO.getAllStudents();
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByClassId(courseClass.getId());

        for (Student s : allStudents) {
            boolean isEnrolled = enrollments.stream().anyMatch(e -> e.getStudent() != null && e.getStudent().getId() == s.getId());
            if (!isEnrolled) {
                cbAddStudent.addItem(s);
            }
        }
    }

    private void addStudentToClass() {
        Student selectedStudent = (Student) cbAddStudent.getSelectedItem();
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this, "Please select a student to add.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setStudent(selectedStudent);
        newEnrollment.setCourseClass(courseClass);
        newEnrollment.setEnrollmentDate(LocalDate.now());
        newEnrollment.setStatus("Enrolled");
        newEnrollment.setResult(0.0f);

        try {
            enrollmentDAO.enrollStudent(newEnrollment);
            JOptionPane.showMessageDialog(this, "Student added to class successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadEnrollments();
            loadAvailableStudents();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Eligibility Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
