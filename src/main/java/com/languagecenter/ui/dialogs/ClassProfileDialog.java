package com.languagecenter.ui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.languagecenter.dao.EnrollmentDAO;
import com.languagecenter.entity.CourseClass;
import com.languagecenter.entity.Enrollment;
import com.languagecenter.entity.Student;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClassProfileDialog extends JDialog {

    private final CourseClass courseClass;
    private final com.languagecenter.ui.MainFrame mainFrame;
    private final java.util.function.Consumer<CourseClass> onSaveCallback;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public ClassProfileDialog(Window parent, CourseClass courseClass, com.languagecenter.ui.MainFrame mainFrame, java.util.function.Consumer<CourseClass> onSaveCallback) {
        super(parent, "Class Details", ModalityType.MODELESS);
        this.courseClass = courseClass;
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
        setSize(1000, 680);
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
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0x3B82F6), getWidth(), getHeight(), new Color(0x2563EB));
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight() + 30, 30, 30));
                g2.dispose();
            }
        };

        // Class Icon
        JLabel lblIcon = new JLabel();
        try {
            FlatSVGIcon classIcon = new FlatSVGIcon("icons/classes.svg", 100, 100);
            classIcon.setColorFilter(new FlatSVGIcon.ColorFilter(c -> Color.WHITE));
            lblIcon.setIcon(classIcon);
        } catch (Exception e) {}
        
        // Class Basic Info
        JPanel basicInfo = new JPanel(new MigLayout("insets 0, gap 0, fillx", "[grow]", "[][]"));
        basicInfo.setOpaque(false);
        
        JLabel lblClassName = new JLabel(courseClass.getClassName());
        lblClassName.setFont(new Font("Inter", Font.BOLD, 24));
        lblClassName.setForeground(Color.WHITE);
        
        String courseName = courseClass.getCourse() != null ? courseClass.getCourse().getName() : "Unknown Course";
        JLabel lblCourseName = new JLabel(courseName);
        lblCourseName.setFont(new Font("Inter", Font.PLAIN, 15));
        lblCourseName.setForeground(new Color(0xDBEAFE));
        
        basicInfo.add(lblClassName, "wrap");
        basicInfo.add(lblCourseName, "wrap");

        // Header Action Buttons
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerActions.setOpaque(false);

        JButton btnEdit = new JButton(new FlatSVGIcon("icons/edit.svg", 18, 18));
        btnEdit.setToolTipText("Edit Class");
        btnEdit.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.addActionListener(e -> {
            dispose();
            if (mainFrame != null) {
                mainFrame.openClassSidePanel(courseClass, updatedClass -> {
                    if (onSaveCallback != null) onSaveCallback.accept(updatedClass);
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
        JLabel lblStatusText = new JLabel(courseClass.getStatus());
        lblStatusText.setFont(new Font("Inter", Font.BOLD, 12));
        lblStatusText.setBorder(new EmptyBorder(4, 12, 4, 12));
        boolean isOnGoing = "On-going".equalsIgnoreCase(courseClass.getStatus());
        lblStatusText.setForeground(isOnGoing ? new Color(0x15803D) : new Color(0x1E293B));
        statusPill.setBackground(isOnGoing ? new Color(0xDCFCE7) : new Color(0xE2E8F0));
        statusPill.setBorder(new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(0,0,0,0), 
            statusPill.getBackground(), 0, 999));
        statusPill.add(lblStatusText);
        
        contentPanel.add(statusPill, "left, wrap");

        // Two-column layout
        JPanel columnsPanel = new JPanel(new MigLayout("insets 0, gap 20, fill", "[380!] [grow]", "[grow]"));
        columnsPanel.setOpaque(false);

        // Left Column: Details
        JPanel leftColumn = new JPanel(new MigLayout("insets 0, gap 15, fillx", "[grow]", "[]"));
        leftColumn.setOpaque(false);

        leftColumn.add(createInfoCard("Chi tiết lớp học", 
            new String[][] {
                {"Giảng viên", courseClass.getTeacher() != null ? courseClass.getTeacher().getFullName() : "N/A"},
                {"Lịch học", courseClass.getSchedulePattern() + " | " + 
                    (courseClass.getStartTime() != null ? courseClass.getStartTime() : "") + " - " + 
                    (courseClass.getEndTime() != null ? courseClass.getEndTime() : "")},
                {"Thời gian", (courseClass.getStartDate() != null ? courseClass.getStartDate().format(formatter) : "-") + " " +
                             (courseClass.getEndDate() != null ? "đến " + courseClass.getEndDate().format(formatter) : "")},
                {"Sĩ số", com.languagecenter.dao.EnrollmentDAO.getInstance().getEnrollmentsByClassId(courseClass.getId()).size() + "/" + courseClass.getMaxStudent()}
            }, new Color(0xDBEAFE)), "growx, wrap");

        // Right Column: Student List
        JPanel rightColumn = new JPanel(new MigLayout("insets 0, gap 15, fill", "[grow]", "[grow]"));
        rightColumn.setOpaque(false);
        rightColumn.add(createStudentListCard(), "grow, push");

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

    private JPanel createStudentListCard() {
        JPanel card = new JPanel(new MigLayout("insets 15, gap 10, fillx", "[grow]", "[]"));
        card.setBackground(Color.WHITE);
        card.setBorder(new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(0,0,0,0), new Color(0xE2E8F0), 1, 24));

        JLabel lblTitle = new JLabel("Danh sách học viên (" + com.languagecenter.dao.EnrollmentDAO.getInstance().getEnrollmentsByClassId(courseClass.getId()).size() + ")");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 14));
        lblTitle.setForeground(new Color(0x334155));
        card.add(lblTitle, "gapbottom 5, wrap");

        List<Enrollment> enrollments = EnrollmentDAO.getInstance().getEnrollmentsByClassId(courseClass.getId());
        if (enrollments == null || enrollments.isEmpty()) {
            JLabel lblEmpty = new JLabel("Chưa có học viên nào đăng ký.");
            lblEmpty.setFont(new Font("Inter", Font.ITALIC, 13));
            lblEmpty.setForeground(new Color(0x94A3B8));
            card.add(lblEmpty, "pad 0 10 0 10");
        } else {
            for (Enrollment e : enrollments) {
                JPanel item = new JPanel(new MigLayout("insets 10 0 10 0, fillx", "[grow][]", "[]"));
                item.setOpaque(false);
                
                Student student = e.getStudent();
                String name = student != null ? student.getFullName() : "Unknown Student";
                
                JLabel lblName = new JLabel(name);
                lblName.setFont(new Font("Inter", Font.BOLD, 13));
                lblName.setForeground(new Color(0x1E293B));
                
                JLabel lblSub = new JLabel(e.getStatus() + " • Đăng ký ngày: " + (e.getEnrollmentDate() != null ? e.getEnrollmentDate().format(formatter) : "-"));
                lblSub.setFont(new Font("Inter", Font.PLAIN, 12));
                lblSub.setForeground(new Color(0x64748B));
                
                String scoreStr = e.getResult() != null ? String.valueOf(e.getResult()) : "-";
                JLabel lblScore = new JLabel("Điểm: " + scoreStr);
                lblScore.setFont(new Font("Inter", Font.BOLD, 12));
                lblScore.setForeground(new Color(0x6366F1));

                item.add(lblName, "split 2, flowy");
                item.add(lblSub);
                item.add(lblScore, "right");
                
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
