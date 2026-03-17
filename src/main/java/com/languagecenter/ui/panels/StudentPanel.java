package com.languagecenter.ui.panels;

import com.languagecenter.dao.StudentDAO;
import com.languagecenter.entity.Student;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentPanel extends JPanel implements RefreshablePanel {

    private TableRowSorter<DefaultTableModel> sorter;
    private StudentDAO studentDAO = StudentDAO.getInstance();
    private DefaultTableModel model;

    private com.languagecenter.ui.MainFrame mainFrame;

    public StudentPanel(com.languagecenter.ui.MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initUI();
    }

    public void bindSearchField(JTextField searchField) {
        Timer timer = new Timer(300, e -> {
            String text = searchField.getText().trim();
            if (text.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                String[] words = text.split("\\s+");
                java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();
                for (String word : words) {
                    filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(word)));
                }
                sorter.setRowFilter(RowFilter.andFilter(filters));
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

        // Outer layout already has correct gap
        // Title was removed because it's in MainFrame Header

        String[] columns = { "ID", "Full Name", "Date of Birth", "Gender", "Phone", "Email", "Address", "Reg Date",
                "Status", "Actions" };

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
        table.getColumnModel().getColumn(8).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(9).setCellRenderer(new ActionRenderer());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.getColumnModel().getColumnIndexAtX(e.getX());
                int row = table.rowAtPoint(e.getPoint());

                if (row < table.getRowCount() && row >= 0) {
                    // Double click check
                    if (e.getClickCount() == 2) {
                        int modelRow = table.convertRowIndexToModel(row);
                        int studentId = (int) model.getValueAt(modelRow, 0);
                        Student student = studentDAO.getStudentById(studentId);
                        if (student != null) {
                            new com.languagecenter.ui.dialogs.StudentProfileDialog(
                                SwingUtilities.getWindowAncestor(StudentPanel.this), student, mainFrame, (updatedStudent) -> {
                                    loadData();
                                }).setVisible(true);
                        }
                        return;
                    }

                    if (column >= 0 && table.getColumnName(column).equals("Actions")) {
                        int modelRow = table.convertRowIndexToModel(row);
                        int studentId = (int) model.getValueAt(modelRow, 0);

                        Rectangle cellRect = table.getCellRect(row, column, false);
                        int clickX = e.getX() - cellRect.x;

                        if (clickX < cellRect.width / 2) {
                            // Edit
                            Student student = studentDAO.getStudentById(studentId);
                            if (student != null) {
                                if (mainFrame != null) {
                                    mainFrame.openStudentSidePanel(student, (updatedStudent) -> {
                                        loadData();
                                        mainFrame.toggleSidePanel(false);
                                        JOptionPane.showMessageDialog(mainFrame, "Student updated successfully!");
                                    });
                                }
                            }
                        } else {
                            // Delete
                            int confirm = JOptionPane.showConfirmDialog(StudentPanel.this,
                                    "Are you sure you want to delete this student?", "Confirm Delete",
                                    JOptionPane.YES_NO_OPTION);
                            if (confirm == JOptionPane.YES_OPTION) {
                                studentDAO.deleteStudent(studentId);
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

        // Container with Elevation (Card Look)
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(new Color(0xFFFFFF)); // White Elevated card
        tableContainer.putClientProperty(FlatClientProperties.STYLE, "arc: 25");
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE2E8F0), 1, true),
                new EmptyBorder(10, 20, 10, 20) // adjusted breathing room
        ));

        tableContainer.add(scrollPane, BorderLayout.CENTER);

        // Action Panel Add Student
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);


        JButton btnAdd = new JButton("Add Student");
        btnAdd.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.openStudentSidePanel(null, (newStudent) -> {
                    loadData();
                    mainFrame.toggleSidePanel(false);
                    JOptionPane.showMessageDialog(mainFrame, "Student added successfully!");
                });
            }
        });

        actionPanel.add(btnAdd);

        tableContainer.add(actionPanel, BorderLayout.SOUTH);

        add(tableContainer, BorderLayout.CENTER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refresh();
            }
        });
    }

    @Override
    public void refresh() {
        loadData();
    }

    private void loadData() {
        if (model == null)
            return;
        model.setRowCount(0);
        List<Student> students = studentDAO.getAllStudents();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Student s : students) {
            // Check if New Student (no enrollments)
            String status = s.getStatus();
            if ("Active".equalsIgnoreCase(status) && (s.getEnrollments() == null || s.getEnrollments().isEmpty())) {
                status = "New";
            }
            
            Object[] row = new Object[] {
                    s.getId(),
                    s.getFullName(),
                    s.getDateOfBirth() != null ? s.getDateOfBirth().format(formatter) : "",
                    s.getGender(),
                    s.getPhone(),
                    s.getEmail(),
                    s.getAddress(),
                    s.getRegistrationDate() != null ? s.getRegistrationDate().format(formatter) : "",
                    status,
                    ""
            };
            model.addRow(row);
        }
    }

    private void setupTableStyling(JTable table) {
        // Overall Style using FlatLaf
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xF1F5F9)); // subtle color
        table.setIntercellSpacing(new Dimension(0, 0));
        table.putClientProperty("Table.alternateRowColor", new Color(0xF8FAFC)); // Alternating subtly

        // Row Styling
        table.setRowHeight(50);
        table.setSelectionBackground(new Color(0xE0E7FF)); // Light Indigo
        table.setSelectionForeground(new Color(0x4338CA));

        // Header Design
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setFont(new Font("Inter", Font.BOLD, 14));
        header.setBackground(new Color(0xF1F5F9)); // #F1F5F9
        header.setForeground(new Color(0x475569)); // #475569

        // Remove Sub-separators for FlatLaf
        header.putClientProperty(FlatClientProperties.STYLE, "separatorColor: #E2E8F0; bottomSeparatorColor: #E2E8F0");

        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
    }

    class StatusBadgeRenderer extends DefaultTableCellRenderer {
        private JPanel container;
        private JPanel pill;
        private JLabel badge;

        public StatusBadgeRenderer() {
            container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
            container.setOpaque(true);
            
            badge = new JLabel();
            badge.setFont(new Font("Inter", Font.BOLD, 11));
            badge.setBorder(new EmptyBorder(5, 12, 5, 12));
            badge.setOpaque(false);

            pill = new JPanel(new BorderLayout());
            pill.add(badge);
            pill.setOpaque(true);
            pill.putClientProperty(FlatClientProperties.STYLE, "arc: 999");
            
            container.add(pill);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            container.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

            if (value != null) {
                pill.setVisible(true);
                String status = value.toString();
                badge.setText(status);

                if (status.equalsIgnoreCase("Active")) {
                    badge.setForeground(new Color(0x15803D)); // Dark Green Text
                    pill.setBackground(new Color(0xDCFCE7)); // Light Green Bg
                } else if (status.equalsIgnoreCase("New")) {
                    badge.setForeground(new Color(0x6366F1)); // Indigo Text
                    pill.setBackground(new Color(0xEEF2FF)); // Light Indigo Bg
                } else {
                    badge.setForeground(new Color(0xB91C1C)); // Dark Red Text
                    pill.setBackground(new Color(0xFEE2E2)); // Light Red Bg
                }
            } else {
                pill.setVisible(false);
            }
            return container;
        }
    }

    // Actions Renderer Inner Class
    class ActionRenderer extends JPanel implements TableCellRenderer {
        private JLabel lblEdit;
        private JLabel lblDelete;

        public ActionRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

            try {
                lblEdit = new JLabel(new FlatSVGIcon("icons/edit.svg", 16, 16));
                lblDelete = new JLabel(new FlatSVGIcon("icons/delete.svg", 16, 16));
            } catch (Exception e) {
                lblEdit = new JLabel("E");
                lblDelete = new JLabel("D");
            }

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
