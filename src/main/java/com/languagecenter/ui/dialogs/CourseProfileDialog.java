package com.languagecenter.ui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.languagecenter.entity.Course;
import com.languagecenter.entity.CourseClass;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class CourseProfileDialog extends JDialog {

    private final Course course;
    private final com.languagecenter.ui.MainFrame mainFrame;
    private final java.util.function.Consumer<Course> onSaveCallback;

    public CourseProfileDialog(Window parent, Course course, com.languagecenter.ui.MainFrame mainFrame, java.util.function.Consumer<Course> onSaveCallback) {
        super(parent, "Course Details", ModalityType.MODELESS);
        this.course = course;
        this.mainFrame = mainFrame;
        this.onSaveCallback = onSaveCallback;
        initUI();

        // Auto-close when clicking outside
        this.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {}
            @Override
            public void windowLostFocus(java.awt.event.WindowEvent e) {
                dispose();
            }
        });
    }

    private void initUI() {
        setUndecorated(true);
        setSize(950, 650);
        setLocationRelativeTo(getParent());
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        JPanel mainPanel = new JPanel(new MigLayout("insets 0, gap 0, fill", "[grow]", "[180!][grow]"));
        mainPanel.setBackground(new Color(0xF1F5F9));
        mainPanel.setBorder(new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(0,0,0,0), new Color(0xF1F5F9), 0, 30));

        // --- Header Section with Gradient ---
        JPanel headerPanel = new JPanel(new MigLayout("insets 20 30 20 30, fill", "[][grow][]", "[]")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0x10B981), getWidth(), getHeight(), new Color(0x059669));
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight() + 30, 30, 30));
                g2.dispose();
            }
        };

        // Course Icon
        JLabel lblIcon = new JLabel();
        try {
            FlatSVGIcon courseIcon = new FlatSVGIcon("icons/courses.svg", 100, 100);
            courseIcon.setColorFilter(new FlatSVGIcon.ColorFilter(c -> Color.WHITE));
            lblIcon.setIcon(courseIcon);
        } catch (Exception e) {}
        
        // Course Basic Info
        JPanel basicInfo = new JPanel(new MigLayout("insets 0, gap 0, fillx", "[grow]", "[][]"));
        basicInfo.setOpaque(false);
        
        JLabel lblName = new JLabel(course.getName());
        lblName.setFont(new Font("Inter", Font.BOLD, 22));
        lblName.setForeground(Color.WHITE);
        
        JLabel lblLevel = new JLabel("Level: " + course.getLevel());
        lblLevel.setFont(new Font("Inter", Font.PLAIN, 14));
        lblLevel.setForeground(new Color(0xD1FAE5));
        
        basicInfo.add(lblName, "wrap");
        basicInfo.add(lblLevel, "wrap");

        // Header Action Buttons
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerActions.setOpaque(false);

        JButton btnEdit = new JButton(new FlatSVGIcon("icons/edit.svg", 18, 18));
        btnEdit.setToolTipText("Edit Course");
        btnEdit.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.addActionListener(e -> {
            dispose();
            if (mainFrame != null) {
                mainFrame.openCourseSidePanel(course, updatedCourse -> {
                    if (onSaveCallback != null) onSaveCallback.accept(updatedCourse);
                });
            }
        });

        JButton btnClose = new JButton(new FlatSVGIcon("icons/close.svg", 18, 18));
        btnClose.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        btnClose.setForeground(Color.WHITE);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        
        headerActions.add(btnEdit);
        headerActions.add(btnClose);
        
        headerPanel.add(lblIcon, "gapright 20");
        headerPanel.add(basicInfo, "growx");
        headerPanel.add(headerActions, "top, right");

        mainPanel.add(headerPanel, "grow, wrap");

        // --- Content Section ---
        JPanel contentPanel = new JPanel(new MigLayout("insets 30, gap 20, fill", "[grow]", "[]"));
        contentPanel.setOpaque(false);

        // Status Badge
        JPanel statusPill = new JPanel(new BorderLayout());
        JLabel lblStatus = new JLabel(course.getStatus());
        lblStatus.setFont(new Font("Inter", Font.BOLD, 12));
        lblStatus.setBorder(new EmptyBorder(4, 12, 4, 12));
        boolean isActive = "Active".equalsIgnoreCase(course.getStatus());
        lblStatus.setForeground(isActive ? new Color(0x15803D) : new Color(0x991B1B));
        statusPill.setBackground(isActive ? new Color(0xDCFCE7) : new Color(0xFEE2E2));
        statusPill.setBorder(new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(0,0,0,0), 
            statusPill.getBackground(), 0, 999));
        statusPill.add(lblStatus);
        
        contentPanel.add(statusPill, "left, wrap");

        // Two-column layout
        JPanel columnsPanel = new JPanel(new MigLayout("insets 0, gap 20, fill", "[350!] [grow]", "[grow]"));
        columnsPanel.setOpaque(false);

        // Left Column: Course Info
        JPanel leftColumn = new JPanel(new MigLayout("insets 0, gap 15, fillx", "[grow]", "[]"));
        leftColumn.setOpaque(false);

        leftColumn.add(createInfoCard("General Information", 
            new String[][] {
                {"Duration", course.getDuration() + " sessions"},
                {"Fee", com.languagecenter.util.CurrencyUtil.formatVND(course.getFee())},
                {"ID", "#" + course.getId()}
            }, new Color(0xECFCCB)), "growx, wrap");

        leftColumn.add(createDescriptionCard(), "growx, wrap");

        // Right Column: Classes (usually more rows)
        JPanel rightColumn = new JPanel(new MigLayout("insets 0, gap 15, fill", "[grow]", "[grow]"));
        rightColumn.setOpaque(false);
        rightColumn.add(createActiveClassesCard(), "grow, push");

        columnsPanel.add(leftColumn, "top");
        columnsPanel.add(rightColumn, "grow, push");

        // Wrap columnsPanel in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(columnsPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "trackArc: 999; thumbArc: 999; width: 6");

        contentPanel.add(scrollPane, "grow, push, wrap");
        
        mainPanel.add(contentPanel, "grow");
        add(mainPanel);
        
        // Draggable dialog
        MouseAdapter dragListener = new MouseAdapter() {
            private Point mouseDownCompCoords = null;
            @Override
            public void mouseReleased(MouseEvent e) { mouseDownCompCoords = null; }
            @Override
            public void mousePressed(MouseEvent e) { mouseDownCompCoords = e.getPoint(); }
            @Override
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
            }
        };
        headerPanel.addMouseListener(dragListener);
        headerPanel.addMouseMotionListener(dragListener);
    }

    private JPanel createDescriptionCard() {
        JPanel card = new JPanel(new MigLayout("insets 15, gap 10, fillx", "[grow]", "[]"));
        card.setBackground(Color.WHITE);
        card.setBorder(new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(0,0,0,0), new Color(0xE2E8F0), 1, 24));

        JLabel lblTitle = new JLabel("Mô tả khóa học");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 14));
        lblTitle.setForeground(new Color(0x334155));
        card.add(lblTitle, "gapbottom 5, wrap");

        JTextArea taDesc = new JTextArea(course.getDescription() != null && !course.getDescription().isEmpty() ? course.getDescription() : "Không có mô tả.");
        taDesc.setLineWrap(true);
        taDesc.setWrapStyleWord(true);
        taDesc.setEditable(false);
        taDesc.setFont(new Font("Inter", Font.PLAIN, 13));
        taDesc.setForeground(new Color(0x475569));
        taDesc.setOpaque(false);
        card.add(taDesc, "growx");

        return card;
    }

    private JPanel createActiveClassesCard() {
        JPanel card = new JPanel(new MigLayout("insets 15, gap 10, fillx", "[grow]", "[]"));
        card.setBackground(Color.WHITE);
        card.setBorder(new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(0,0,0,0), new Color(0xE2E8F0), 1, 24));

        JLabel lblTitle = new JLabel("Các lớp học đang mở");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 14));
        lblTitle.setForeground(new Color(0x334155));
        card.add(lblTitle, "gapbottom 5, wrap");

        java.util.List<CourseClass> classes = com.languagecenter.dao.CourseClassDAO.getInstance().getClassesByCourseId(course.getId());
        if (classes == null || classes.isEmpty()) {
            JLabel lblEmpty = new JLabel("Chưa có lớp học nào cho khóa này.");
            lblEmpty.setFont(new Font("Inter", Font.ITALIC, 13));
            lblEmpty.setForeground(new Color(0x94A3B8));
            card.add(lblEmpty, "pad 0 10 0 10");
        } else {
            for (CourseClass cc : classes) {
                JPanel item = new JPanel(new MigLayout("insets 10 0 10 0, fillx", "[grow][]", "[]"));
                item.setOpaque(false);
                
                JLabel lblClassName = new JLabel(cc.getClassName());
                lblClassName.setFont(new Font("Inter", Font.BOLD, 13));
                lblClassName.setForeground(new Color(0x1E293B));
                
                String teacherName = cc.getTeacher() != null ? cc.getTeacher().getFullName() : "No Teacher";
                JLabel lblTeacher = new JLabel("Teacher: " + teacherName + " • " + cc.getSchedulePattern());
                lblTeacher.setFont(new Font("Inter", Font.PLAIN, 12));
                lblTeacher.setForeground(new Color(0x64748B));
                
                JPanel statusBadge = new JPanel(new BorderLayout());
                JLabel lblStatusText = new JLabel(cc.getStatus());
                lblStatusText.setFont(new Font("Inter", Font.BOLD, 10));
                lblStatusText.setBorder(new EmptyBorder(2, 8, 2, 8));
                
                boolean isOnGoing = "On-going".equalsIgnoreCase(cc.getStatus());
                lblStatusText.setForeground(isOnGoing ? new Color(0x15803D) : new Color(0x64748B));
                statusBadge.setBackground(isOnGoing ? new Color(0xDCFCE7) : new Color(0xF1F5F9));
                statusBadge.setBorder(new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(0,0,0,0), 
                    statusBadge.getBackground(), 0, 10));
                statusBadge.add(lblStatusText);

                item.add(lblClassName, "split 2, flowy");
                item.add(lblTeacher);
                item.add(statusBadge, "right");
                
                card.add(item, "growx, wrap");
                
                JSeparator sep = new JSeparator();
                sep.setForeground(new Color(0xF1F5F9));
                card.add(sep, "growx, h 1!, wrap");
            }
        }
        return card;
    }

    private JPanel createInfoCard(String title, String[][] data, Color bg) {
        JPanel card = new JPanel(new MigLayout("insets 15, gap 10, fillx", "[130!][grow]", "[]"));
        card.setBackground(bg);
        card.setBorder(new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(0,0,0,0), new Color(0xE2E8F0), 1, 24));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 14));
        lblTitle.setForeground(new Color(0x334155));
        card.add(lblTitle, "span 2, gapbottom 5, wrap");

        for (String[] row : data) {
            JLabel lblKey = new JLabel(row[0]);
            lblKey.setFont(new Font("Inter", Font.PLAIN, 13));
            lblKey.setForeground(new Color(0x64748B));
            
            JLabel lblVal = new JLabel(row[1] != null && !row[1].isEmpty() ? row[1] : "-");
            lblVal.setFont(new Font("Inter", Font.PLAIN, 13));
            lblVal.setForeground(new Color(0x1E293B));
            
            card.add(lblKey);
            card.add(lblVal, "wrap");
        }
        return card;
    }
}
