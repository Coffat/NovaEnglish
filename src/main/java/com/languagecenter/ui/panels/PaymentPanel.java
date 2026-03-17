package com.languagecenter.ui.panels;

import com.languagecenter.dao.PaymentDAO;
import com.languagecenter.entity.Payment;
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

public class PaymentPanel extends JPanel {

    private TableRowSorter<DefaultTableModel> sorter;
    private PaymentDAO paymentDAO = PaymentDAO.getInstance();
    private DefaultTableModel model;

    private com.languagecenter.ui.MainFrame mainFrame;

    public PaymentPanel(com.languagecenter.ui.MainFrame mainFrame) {
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
            public void insertUpdate(javax.swing.event.DocumentEvent e) { timer.restart(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { timer.restart(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { timer.restart(); }
        });
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setOpaque(false);

        String[] columns = {"ID", "Student", "Enrollment ID", "Amount", "Date", "Method", "Status", "Actions"};

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
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new ActionRenderer());

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / table.getRowHeight();

                if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
                    if (table.getColumnName(column).equals("Actions")) {
                        int modelRow = table.convertRowIndexToModel(row);
                        int paymentId = (int) model.getValueAt(modelRow, 0);

                        Rectangle cellRect = table.getCellRect(row, column, false);
                        int clickX = e.getX() - cellRect.x;

                        if (clickX < cellRect.width / 2) {
                            // Edit
                            Payment payment = paymentDAO.getPaymentById(paymentId);
                            if (payment != null && mainFrame != null) {
                                mainFrame.openPaymentSidePanel(payment, (updatedPayment) -> {
                                    paymentDAO.updatePayment(updatedPayment);
                                    loadData();
                                    mainFrame.toggleSidePanel(false);
                                    JOptionPane.showMessageDialog(mainFrame, "Payment updated successfully!");
                                });
                            }
                        } else {
                            // Delete
                            int confirm = JOptionPane.showConfirmDialog(PaymentPanel.this,
                                    "Are you sure you want to delete this payment?", "Confirm Delete",
                                    JOptionPane.YES_NO_OPTION);
                            if (confirm == JOptionPane.YES_OPTION) {
                                paymentDAO.deletePayment(paymentId);
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
                new EmptyBorder(10, 20, 10, 20)
        ));

        tableContainer.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);


        JButton btnAdd = new JButton("Add Payment");
        btnAdd.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.openPaymentSidePanel(null, (newPayment) -> {
                    paymentDAO.addPayment(newPayment);
                    loadData();
                    mainFrame.toggleSidePanel(false);
                    JOptionPane.showMessageDialog(mainFrame, "Payment added successfully!");
                });
            }
        });

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
        if (model == null) return;
        model.setRowCount(0);
        List<Payment> payments = paymentDAO.getAllPayments();
        for (Payment p : payments) {
            String studentName = p.getStudent() != null ? p.getStudent().getFullName() : "N/A";
            String enrollmentIdStr = p.getEnrollment() != null ? String.valueOf(p.getEnrollment().getId()) : "N/A";
            Object[] row = new Object[]{
                    p.getId(),
                    studentName,
                    enrollmentIdStr,
                    p.getAmount() != null ? p.getAmount() : "0",
                    p.getPaymentDate() != null ? p.getPaymentDate().toString() : "",
                    p.getPaymentMethod() != null ? p.getPaymentMethod() : "",
                    p.getStatus() != null ? p.getStatus() : "",
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
        private final com.languagecenter.strategy.StatusStrategy strategy = new com.languagecenter.strategy.PaymentStatusStrategy();
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
                com.languagecenter.strategy.StatusStyle style = strategy.getStyle(status);

                badge.setText(status);
                badge.setForeground(style.getForeground());
                pill.setBackground(style.getBackground());
            } else {
                pill.setVisible(false);
            }
            return container;
        }
    }

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
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }
}
