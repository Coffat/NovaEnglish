package com.languagecenter.ui.panels;

import com.languagecenter.dao.ReportDAO;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.internal.chartpart.Chart;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Rectangle;
import java.awt.Dimension;

public class ReportPanel extends JPanel {

    private ReportDAO reportDAO = ReportDAO.getInstance();

    // UI Components
    private JLabel lblTotalStudents;
    private JLabel lblTotalTeachers;
    private JLabel lblTotalCourses;
    private JLabel lblActiveClasses;
    private JLabel lblTotalRevenue;

    private JPanel chartsContainer;
    
    // Internal class for scrollable content that tracks viewport width
    private class ScrollablePanel extends JPanel implements Scrollable {
        public ScrollablePanel(LayoutManager layout) {
            super(layout);
        }
        @Override
        public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) { return 16; }
        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) { return 80; }
        @Override
        public boolean getScrollableTracksViewportWidth() { return true; }
        @Override
        public boolean getScrollableTracksViewportHeight() { return false; }
    }

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

        // Main Content in a single scrollable container
        ScrollablePanel mainScrollContent = new ScrollablePanel(new MigLayout("wrap 1, insets 0, gap 20, fillx", "[grow, fill]"));
        mainScrollContent.setOpaque(false);

        // Cards Panel - Responsive flow wrap
        JPanel cardsContainer = new JPanel(new MigLayout("wrap, insets 0, gap 20, fillx", "[grow, fill]"));
        cardsContainer.setOpaque(false);

        // Initialize Labels
        lblTotalStudents = createValueLabel();
        lblTotalTeachers = createValueLabel();
        lblTotalCourses = createValueLabel();
        lblActiveClasses = createValueLabel();
        lblTotalRevenue = createValueLabel();

        // Add Cards with responsive width constraints
        String cardConstraints = "growx, width 200:300:, split 3";
        cardsContainer.add(createCard("Total Students", lblTotalStudents, new Color(0xEFF6FF), new Color(0x3B82F6), "👥"), cardConstraints);
        cardsContainer.add(createCard("Total Teachers", lblTotalTeachers, new Color(0xF5F3FF), new Color(0x8B5CF6), "👨‍🏫"), cardConstraints);
        cardsContainer.add(createCard("Total Courses", lblTotalCourses, new Color(0xECFCCB), new Color(0x65A30D), "📚"), "growx, width 200:300:, wrap");
        
        cardsContainer.add(createCard("Active Classes", lblActiveClasses, new Color(0xFEF2F2), new Color(0xEF4444), "🏫"), "growx, width 200:300:, split 2");
        cardsContainer.add(createCard("Total Revenue (VNĐ)", lblTotalRevenue, new Color(0xDCFCE7), new Color(0x22C55E), "💰"), "growx, width 200:300:, wrap");

        mainScrollContent.add(cardsContainer, "growx, wrap");

        // Charts Panel - Responsive flow wrap
        chartsContainer = new JPanel(new MigLayout("wrap, insets 0, gap 20, fillx", "[grow, fill]"));
        chartsContainer.setOpaque(false);
        mainScrollContent.add(chartsContainer, "growx");

        // Wrap everything in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(mainScrollContent);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);

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
            java.util.List<Object[]> enrollmentStatus;
            java.util.List<Object[]> registrationTrend;
            java.util.List<Object[]> teacherLoad;
            java.util.List<Object[]> classStatus;

            @Override
            protected Void doInBackground() {
                students = reportDAO.getTotalStudents();
                teachers = reportDAO.getTotalTeachers();
                courses = reportDAO.getTotalCourses();
                activeClasses = reportDAO.getActiveClasses();
                revenue = reportDAO.getTotalRevenue();
                studentsByCourse = reportDAO.getStudentsByCourse();
                revenueByMonth = reportDAO.getRevenueByMonth();
                enrollmentStatus = reportDAO.getEnrollmentStatusDistribution();
                registrationTrend = reportDAO.getStudentRegistrationTrend();
                teacherLoad = reportDAO.getTeacherLoad();
                classStatus = reportDAO.getClassStatusDistribution();
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
                updateCharts(studentsByCourse, revenueByMonth, enrollmentStatus, registrationTrend, teacherLoad, classStatus);
            }
        };
        worker.execute();
    }

    private void updateCharts(
            java.util.List<Object[]> studentsByCourse, 
            java.util.List<Object[]> revenueByMonth,
            java.util.List<Object[]> enrollmentStatus,
            java.util.List<Object[]> registrationTrend,
            java.util.List<Object[]> teacherLoad,
            java.util.List<Object[]> classStatus) {
        
        chartsContainer.removeAll();
        
        try {
            String chartConstraints = "growx, width 300:450:, split 2";
            
            // 1. Pie Chart: Students by Course
            chartsContainer.add(createXChartPanel(createPieChart("Students by Course", studentsByCourse)), chartConstraints);

            // 2. Bar Chart: Revenue by Month
            chartsContainer.add(createXChartPanel(createBarChart("Revenue Trend (VNĐ)", "Month", "Revenue", revenueByMonth, "#3B82F6")), "growx, width 300:450:, wrap");

            // 3. Bar Chart: Student Registration Trend
            chartsContainer.add(createXChartPanel(createBarChart("New Student Registrations", "Month", "Count", registrationTrend, "#F59E0B")), chartConstraints);

            // 4. Bar Chart: Teacher Workload
            chartsContainer.add(createXChartPanel(createBarChart("Classes Per Teacher", "Teacher", "Classes", teacherLoad, "#8B5CF6")), "growx, width 300:450:, wrap");

            // 5. Pie Chart: Enrollment Status Distribution
            chartsContainer.add(createXChartPanel(createPieChart("Enrollment Status", enrollmentStatus)), chartConstraints);

            // 6. Pie Chart: Class Status Distribution
            chartsContainer.add(createXChartPanel(createPieChart("Class Status Distribution", classStatus)), "growx, width 300:450:, wrap");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        chartsContainer.revalidate();
        chartsContainer.repaint();
    }

    private JPanel createXChartPanel(Chart<?, ?> chart) {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        XChartPanel panel = new XChartPanel(chart);
        panel.putClientProperty(FlatClientProperties.STYLE, "arc: 40");
        panel.setBorder(BorderFactory.createCompoundBorder(
                new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(1, 1, 1, 1), new Color(0xE2E8F0), 1, 40),
                new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    private PieChart createPieChart(String title, java.util.List<Object[]> data) {
        PieChart chart = new PieChartBuilder().width(300).height(250).title(title).build();
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setPlotBorderVisible(false);
        
        boolean hasData = false;
        for (Object[] row : data) {
            if (row[0] != null && row[1] != null) {
                chart.addSeries(row[0].toString(), ((Number) row[1]).intValue());
                hasData = true;
            }
        }
        if (!hasData) chart.addSeries("No Data", 1);
        return chart;
    }

    private CategoryChart createBarChart(String title, String xAxis, String yAxis, java.util.List<Object[]> data, String colorHex) {
        CategoryChart chart = new CategoryChartBuilder().width(300).height(250).title(title)
                .xAxisTitle(xAxis).yAxisTitle(yAxis).build();
        
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setPlotGridLinesVisible(false);
        chart.getStyler().setPlotBorderVisible(false);
        chart.getStyler().setSeriesColors(new Color[] { Color.decode(colorHex) });

        java.util.List<String> xData = new java.util.ArrayList<>();
        java.util.List<Number> yData = new java.util.ArrayList<>();
        for (Object[] row : data) {
            if (row[0] != null && row[1] != null) {
                xData.add(row[0].toString());
                yData.add((Number) row[1]);
            }
        }
        
        if (xData.isEmpty()) {
            xData.add("No Data");
            yData.add(0);
        }
        
        chart.addSeries("Data", xData, yData);
        return chart;
    }
}
