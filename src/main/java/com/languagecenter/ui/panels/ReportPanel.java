package com.languagecenter.ui.panels;

import com.languagecenter.dao.ReportDAO;
import com.formdev.flatlaf.FlatClientProperties;

import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ReportPanel extends JPanel {

    private ReportDAO reportDAO = ReportDAO.getInstance();

    // UI Components
    private JLabel lblTotalStudents;
    private JLabel lblTotalTeachers;
    private JLabel lblTotalCourses;
    private JLabel lblActiveClasses;
    private JLabel lblTotalRevenue;

    private JPanel chartsContainer;

    public ReportPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Center Reports & Statistics");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 28));
        lblTitle.setForeground(new Color(0x1E293B));
        

        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);

        // Cards Panel using Grid layout
        JPanel cardsContainer = new JPanel(new GridLayout(2, 3, 20, 20));
        cardsContainer.setOpaque(false);

        // Initialize Labels
        lblTotalStudents = createValueLabel();
        lblTotalTeachers = createValueLabel();
        lblTotalCourses = createValueLabel();
        lblActiveClasses = createValueLabel();
        lblTotalRevenue = createValueLabel();

        // Add Cards
        cardsContainer.add(createCard("Total Students", lblTotalStudents, new Color(0xEFF6FF), new Color(0x3B82F6), "👥"));
        cardsContainer.add(createCard("Total Teachers", lblTotalTeachers, new Color(0xF5F3FF), new Color(0x8B5CF6), "👨‍🏫"));
        cardsContainer.add(createCard("Total Courses", lblTotalCourses, new Color(0xECFCCB), new Color(0x65A30D), "📚"));
        cardsContainer.add(createCard("Active Classes", lblActiveClasses, new Color(0xFEF2F2), new Color(0xEF4444), "🏫"));
        cardsContainer.add(createCard("Total Revenue (VNĐ)", lblTotalRevenue, new Color(0xDCFCE7), new Color(0x22C55E), "💰"));

        // Wrapper to keep cards at top
        JPanel centerWrapper = new JPanel(new BorderLayout(0, 20));
        centerWrapper.setOpaque(false);
        centerWrapper.add(cardsContainer, BorderLayout.NORTH);

        // Charts Panel
        chartsContainer = new JPanel(new GridLayout(1, 2, 20, 20));
        chartsContainer.setOpaque(false);
        centerWrapper.add(chartsContainer, BorderLayout.CENTER);

        add(centerWrapper, BorderLayout.CENTER);

        // Load Initial Data
        refreshData();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshData();
            }
        });
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("Inter", Font.BOLD, 36));
        label.setForeground(new Color(0x0F172A));
        return label;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color bgPillColor, Color iconColor, String iconText) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 40");
        card.setBorder(BorderFactory.createCompoundBorder(
                new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(1, 1, 1, 1), new Color(0xE2E8F0), 1, 40),
                new EmptyBorder(20, 25, 20, 25)
        ));

        // Top part with Title and Icon
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 14));
        lblTitle.setForeground(new Color(0x64748B));

        // Build simple text icon as placeholder for SVG
        JLabel lblIcon = new JLabel(iconText, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        lblIcon.setForeground(iconColor);
        
        JPanel iconPill = new JPanel(new BorderLayout());
        iconPill.setBackground(bgPillColor);
        iconPill.putClientProperty(FlatClientProperties.STYLE, "arc: 999");
        iconPill.setBorder(new EmptyBorder(10, 10, 10, 10));
        iconPill.add(lblIcon);

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(iconPill, BorderLayout.EAST);

        // Add components to card
        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.SOUTH);

        return card;
    }

    private void refreshData() {
        // Run in SwingWorker to prevent UI blocking
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            long students, teachers, courses, activeClasses;
            java.math.BigDecimal revenue;
            java.util.List<Object[]> studentsByCourse;
            java.util.List<Object[]> revenueByMonth;

            @Override
            protected Void doInBackground() {
                students = reportDAO.getTotalStudents();
                teachers = reportDAO.getTotalTeachers();
                courses = reportDAO.getTotalCourses();
                activeClasses = reportDAO.getActiveClasses();
                revenue = reportDAO.getTotalRevenue();
                studentsByCourse = reportDAO.getStudentsByCourse();
                revenueByMonth = reportDAO.getRevenueByMonth();
                return null;
            }

            @Override
            protected void done() {
                lblTotalStudents.setText(String.valueOf(students));
                lblTotalTeachers.setText(String.valueOf(teachers));
                lblTotalCourses.setText(String.valueOf(courses));
                lblActiveClasses.setText(String.valueOf(activeClasses));
                if (revenue != null) {
                    lblTotalRevenue.setText(com.languagecenter.util.CurrencyUtil.formatVND(revenue));
                } else {
                    lblTotalRevenue.setText(com.languagecenter.util.CurrencyUtil.formatVND(java.math.BigDecimal.ZERO));
                }
                updateCharts(studentsByCourse, revenueByMonth);
            }
        };
        worker.execute();
    }

    private void updateCharts(java.util.List<Object[]> studentsByCourse, java.util.List<Object[]> revenueByMonth) {
        chartsContainer.removeAll();

        // 1. Pie Chart for Students by Course
        PieChart pieChart = new PieChartBuilder().width(400).height(300).title("Students by Course").build();
        pieChart.getStyler().setLegendVisible(true);
        pieChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        pieChart.getStyler().setChartBackgroundColor(Color.WHITE);
        pieChart.getStyler().setPlotBorderVisible(false);

        boolean hasPieData = false;
        for (Object[] row : studentsByCourse) {
            if (row[0] != null && row[1] != null) {
                pieChart.addSeries(row[0].toString(), ((Number) row[1]).intValue());
                hasPieData = true;
            }
        }
        if (!hasPieData) {
            pieChart.addSeries("No Data", 1);
        }

        XChartPanel<PieChart> piePanel = new XChartPanel<>(pieChart);
        piePanel.putClientProperty(FlatClientProperties.STYLE, "arc: 40");
        piePanel.setBorder(BorderFactory.createCompoundBorder(
                new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(1, 1, 1, 1), new Color(0xE2E8F0), 1, 40),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // 2. Bar Chart for Revenue by Month
        CategoryChart barChart = new CategoryChartBuilder().width(400).height(300).title("Revenue by Month (VNĐ)").xAxisTitle("Month").yAxisTitle("Revenue").build();
        barChart.getStyler().setLegendVisible(false);
        barChart.getStyler().setChartBackgroundColor(Color.WHITE);
        barChart.getStyler().setPlotGridLinesVisible(false);
        barChart.getStyler().setPlotBorderVisible(false);

        java.util.List<String> xData = new java.util.ArrayList<>();
        java.util.List<Number> yData = new java.util.ArrayList<>();
        for (Object[] row : revenueByMonth) {
            if (row[0] != null && row[1] != null) {
                xData.add(row[0].toString());
                yData.add((Number) row[1]);
            }
        }
        
        if (xData.isEmpty()) {
            xData.add("No Data");
            yData.add(0);
        }
        
        barChart.addSeries("Revenue", xData, yData);

        XChartPanel<CategoryChart> barPanel = new XChartPanel<>(barChart);
        barPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 40");
        barPanel.setBorder(BorderFactory.createCompoundBorder(
                new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(1, 1, 1, 1), new Color(0xE2E8F0), 1, 40),
                new EmptyBorder(10, 10, 10, 10)
        ));

        chartsContainer.add(piePanel);
        chartsContainer.add(barPanel);
        
        chartsContainer.revalidate();
        chartsContainer.repaint();
    }
}
