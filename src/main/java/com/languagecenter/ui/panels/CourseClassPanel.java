package com.languagecenter.ui.panels;

import com.languagecenter.dao.CourseClassDAO;
import com.languagecenter.entity.CourseClass;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CourseClassPanel extends JPanel {

    private TableRowSorter<DefaultTableModel> sorter;
    private CourseClassDAO classDAO = CourseClassDAO.getInstance();
    private DefaultTableModel model;

    private com.languagecenter.ui.MainFrame mainFrame;

    public CourseClassPanel(com.languagecenter.ui.MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initUI();
    }

    public void bindSearchField(JTextField searchField) {
        Timer timer = new Timer(300, e -> {
            String text = searchField.getText();
            if (text.trim().isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
            }
        });
        timer.setRepeats(false);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                timer.restart();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                timer.restart();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                timer.restart();
            }
        });
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setOpaque(false);

        String[] columns = { "ID", "Class Name", "Course", "Teacher", "Start Date", "End Date", "Capacity", "Status",
                "Actions" };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        loadData();

        JTable table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        setupTableStyling(table);

        // Setup Custom Renderers
        table.getColumnModel().getColumn(7).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(8).setCellRenderer(new ActionRenderer());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / table.getRowHeight();

                if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
                    if (table.getColumnName(column).equals("Actions")) {
                        int modelRow = table.convertRowIndexToModel(row);
                        int classId = (int) model.getValueAt(modelRow, 0);

                        Rectangle cellRect = table.getCellRect(row, column, false);
                        int clickX = e.getX() - cellRect.x;

                        if (clickX < cellRect.width / 4) {
                            // Students / Enrollments
                            CourseClass courseClass = classDAO.getClassById(classId);
                            if (courseClass != null) {
                                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(CourseClassPanel.this);
                                com.languagecenter.ui.dialogs.ClassEnrollmentsDialog dialog = new com.languagecenter.ui.dialogs.ClassEnrollmentsDialog(parentFrame, courseClass);
                                dialog.setVisible(true);
                            }
                        } else if (clickX < 2 * cellRect.width / 4) {
                            // Attendance
                            CourseClass courseClass = classDAO.getClassById(classId);
                            if (courseClass != null) {
                                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(CourseClassPanel.this);
                                com.languagecenter.ui.dialogs.TakeClassAttendanceDialog dialog = new com.languagecenter.ui.dialogs.TakeClassAttendanceDialog(parentFrame, courseClass, () -> {
                                    // Nothing specific to refresh in CourseClassPanel for attendance, maybe just a success message
                                });
                                dialog.setVisible(true);
                            }
                        } else if (clickX < 3 * cellRect.width / 4) {
                            // Edit
                            CourseClass courseClass = classDAO.getClassById(classId);
                            if (courseClass != null) {
                                if (mainFrame != null) {
                                    mainFrame.openClassSidePanel(courseClass, (updatedClass) -> {
                                        classDAO.updateClass(updatedClass);
                                        loadData();
                                        mainFrame.toggleSidePanel(false);
                                        JOptionPane.showMessageDialog(mainFrame, "Class updated successfully!");
                                    });
                                }
                            }
                        } else {
                            // Delete
                            int confirm = JOptionPane.showConfirmDialog(CourseClassPanel.this,
                                    "Are you sure you want to delete this class?", "Confirm Delete",
                                    JOptionPane.YES_NO_OPTION);
                            if (confirm == JOptionPane.YES_OPTION) {
                                classDAO.deleteClass(classId);
                                loadData();
                            }
                        }
                    }
                }
            }
        });

        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int column = table.getColumnModel().getColumnIndexAtX(e.getX());
                if (column >= 0 && table.getColumnName(column).equals("Actions")) {
                    table.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(new Color(0xFFFFFF));
        tableContainer.putClientProperty(FlatClientProperties.STYLE, "arc: 25");
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE2E8F0), 1, true),
                new EmptyBorder(10, 20, 10, 20)));

        tableContainer.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);

        JButton btnRefresh = new JButton("Refresh / Load");
        btnRefresh.addActionListener(e -> {
            loadData();
            JOptionPane.showMessageDialog(this, "Data refreshed!");
        });

        JButton btnAdd = new JButton("Add Class");
        btnAdd.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.openClassSidePanel(null, (newClass) -> {
                    classDAO.addClass(newClass);
                    loadData();
                    mainFrame.toggleSidePanel(false);
                    JOptionPane.showMessageDialog(mainFrame, "Class added successfully!");
                });
            }
        });

        actionPanel.add(btnRefresh);
        actionPanel.add(btnAdd);

        tableContainer.add(actionPanel, BorderLayout.SOUTH);
        add(tableContainer, BorderLayout.CENTER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadData();
            }
        });
    }

    private void loadData() {
        if (model == null)
            return;
        model.setRowCount(0);
        List<CourseClass> classes = classDAO.getAllClasses();
        for (CourseClass c : classes) {
            Object[] row = new Object[] {
                    c.getId(),
                    c.getClassName(),
                    c.getCourse() != null ? c.getCourse().getName() : "N/A",
                    c.getTeacher() != null ? c.getTeacher().getFullName() : "N/A",
                    c.getStartDate() != null ? c.getStartDate().toString() : "",
                    c.getEndDate() != null ? c.getEndDate().toString() : "",
                    c.getMaxStudent(),
                    c.getStatus(),
                    ""
            };
            model.addRow(row);
        }
    }

    private void setupTableStyling(JTable table) {
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xF1F5F9));
        table.setIntercellSpacing(new Dimension(0, 0));
        table.putClientProperty("Table.alternateRowColor", new Color(0xF8FAFC));

        table.setRowHeight(50);
        table.setSelectionBackground(new Color(0xE0E7FF));
        table.setSelectionForeground(new Color(0x4338CA));

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setFont(new Font("Inter", Font.BOLD, 14));
        header.setBackground(new Color(0xF1F5F9));
        header.setForeground(new Color(0x475569));
        header.putClientProperty(FlatClientProperties.STYLE, "separatorColor: #E2E8F0; bottomSeparatorColor: #E2E8F0");

        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
    }

    class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
            container.setOpaque(true);
            container.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

            if (value != null) {
                JLabel badge = new JLabel(value.toString());
                badge.setFont(new Font("Inter", Font.BOLD, 11));
                badge.setBorder(new EmptyBorder(5, 12, 5, 12));
                badge.setOpaque(false);

                JPanel pill = new JPanel(new BorderLayout());
                pill.add(badge);
                pill.setOpaque(false);
                pill.putClientProperty(FlatClientProperties.STYLE, "arc: 999");

                if (value.toString().equalsIgnoreCase("On-going") || value.toString().equalsIgnoreCase("In Progress")
                        || value.toString().equalsIgnoreCase("Active")) {
                    badge.setForeground(new Color(0x15803D)); // Dark Green
                    pill.setBackground(new Color(0xDCFCE7)); // Light Green
                } else if (value.toString().equalsIgnoreCase("Opening") || value.toString().equalsIgnoreCase("Upcoming")
                        || value.toString().equalsIgnoreCase("Oncoming")) {
                    badge.setForeground(new Color(0xB45309)); // Dark Amber
                    pill.setBackground(new Color(0xFEF3C7)); // Light Amber
                } else if (value.toString().equalsIgnoreCase("Completed")) {
                    badge.setForeground(new Color(0x1D4ED8)); // Dark Blue
                    pill.setBackground(new Color(0xDBEAFE)); // Light Blue
                } else {
                    // Cancelled or Inactive
                    badge.setForeground(new Color(0xB91C1C)); // Dark Red
                    pill.setBackground(new Color(0xFEE2E2)); // Light Red
                }
                pill.setOpaque(true);

                container.add(pill);
            }
            return container;
        }
    }

    class ActionRenderer extends JPanel implements TableCellRenderer {
        private JLabel lblStudents;
        private JLabel lblAttendance;
        private JLabel lblEdit;
        private JLabel lblDelete;

        public ActionRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

            try {
                lblStudents = new JLabel(new FlatSVGIcon("icons/students.svg", 16, 16));
                lblAttendance = new JLabel(new FlatSVGIcon("icons/attendance.svg", 16, 16));
                lblEdit = new JLabel(new FlatSVGIcon("icons/edit.svg", 16, 16));
                lblDelete = new JLabel(new FlatSVGIcon("icons/delete.svg", 16, 16));
            } catch (Exception e) {
                lblStudents = new JLabel("S");
                lblAttendance = new JLabel("A");
                lblEdit = new JLabel("E");
                lblDelete = new JLabel("D");
            }
            lblStudents.setToolTipText("View Enrollments");
            lblAttendance.setToolTipText("Take Attendance");
            lblEdit.setToolTipText("Edit Class");
            lblDelete.setToolTipText("Delete Class");

            add(lblStudents);
            add(lblAttendance);
            add(lblEdit);
            add(lblDelete);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }
}
