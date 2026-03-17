package com.languagecenter.ui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.languagecenter.entity.Student;
import com.languagecenter.entity.Enrollment;
import com.languagecenter.dao.AttendanceDAO;
import com.languagecenter.dao.ScheduleDAO;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;

public class StudentProfileDialog extends JDialog {

    private final Student student;
    private final com.languagecenter.ui.MainFrame mainFrame;
    private final java.util.function.Consumer<Student> onSaveCallback;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public StudentProfileDialog(Window parent, Student student, com.languagecenter.ui.MainFrame mainFrame, java.util.function.Consumer<Student> onSaveCallback) {
        super(parent, "Student Profile", ModalityType.APPLICATION_MODAL);
        this.student = student;
        this.mainFrame = mainFrame;
        this.onSaveCallback = onSaveCallback;
        initUI();
    }

    private void initUI() {
        setUndecorated(true);
        setSize(520, 720); // Slightly taller for more content
        setLocationRelativeTo(getParent());
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        JPanel mainPanel = new JPanel(new MigLayout("insets 0, gap 0, fill", "[grow]", "[180!][grow]"));
        mainPanel.setBackground(new Color(0xF1F5F9));
        // Using Rounded border instead of arc style for safety
        mainPanel.setBorder(new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(0,0,0,0), new Color(0xF1F5F9), 0, 30));

        // --- Header Section with Gradient ---
        JPanel headerPanel = new JPanel(new MigLayout("insets 20 30 20 30, fill", "[][grow][]", "[]")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0x6366F1), getWidth(), getHeight(), new Color(0x4F46E5));
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight() + 30, 30, 30));
                g2.dispose();
            }
        };

        // Profile Avatar
        JLabel lblAvatar = new JLabel();
        try {
            FlatSVGIcon avatarIcon = new FlatSVGIcon("icons/user.svg", 100, 100);
            avatarIcon.setColorFilter(new FlatSVGIcon.ColorFilter(c -> Color.WHITE));
            lblAvatar.setIcon(avatarIcon);
        } catch (Exception e) {}
        
        // Student Basic Info
        JPanel basicInfo = new JPanel(new MigLayout("insets 0, gap 0, fillx", "[grow]", "[][]"));
        basicInfo.setOpaque(false);
        
        JLabel lblName = new JLabel(student.getFullName());
        lblName.setFont(new Font("Inter", Font.BOLD, 28));
        lblName.setForeground(Color.WHITE);
        
        JLabel lblId = new JLabel("Student ID: #" + student.getId());
        lblId.setFont(new Font("Inter", Font.PLAIN, 14));
        lblId.setForeground(new Color(0xC7D2FE));
        
        basicInfo.add(lblName, "wrap");
        basicInfo.add(lblId, "wrap");

        // Header Action Buttons
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerActions.setOpaque(false);

        JButton btnEdit = new JButton(new FlatSVGIcon("icons/edit.svg", 18, 18));
        btnEdit.setToolTipText("Edit Profile");
        btnEdit.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.addActionListener(e -> {
            dispose();
            if (mainFrame != null) {
                mainFrame.openStudentSidePanel(student, updatedStudent -> {
                    if (onSaveCallback != null) onSaveCallback.accept(updatedStudent);
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
        
        headerPanel.add(lblAvatar, "gapright 20");
        headerPanel.add(basicInfo, "growx");
        headerPanel.add(headerActions, "top, right");

        mainPanel.add(headerPanel, "grow, wrap");

        // --- Content Section ---
        JPanel contentPanel = new JPanel(new MigLayout("insets 30, gap 20, fill", "[grow]", "[]"));
        contentPanel.setOpaque(false);

        // Status Badge
        JPanel statusPill = new JPanel(new BorderLayout());
        JLabel lblStatus = new JLabel(student.getStatus());
        lblStatus.setFont(new Font("Inter", Font.BOLD, 12));
        lblStatus.setBorder(new EmptyBorder(4, 12, 4, 12));
        boolean isActive = "Active".equalsIgnoreCase(student.getStatus());
        lblStatus.setForeground(isActive ? new Color(0x15803D) : new Color(0x991B1B));
        statusPill.setBackground(isActive ? new Color(0xDCFCE7) : new Color(0xFEE2E2));
        statusPill.setBorder(new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(0,0,0,0), 
            statusPill.getBackground(), 0, 999));
        statusPill.add(lblStatus);
        
        contentPanel.add(statusPill, "left, gapbottom 10, wrap");

        // Info Cards Container
        JPanel cardsContainer = new JPanel(new MigLayout("insets 0, gap 15, fillx", "[grow]", "[]"));
        cardsContainer.setOpaque(false);

        cardsContainer.add(createInfoCard("Personal Details", 
            new String[][] {
                {"Gender", student.getGender()},
                {"Date of Birth", student.getDateOfBirth() != null ? student.getDateOfBirth().format(formatter) : "N/A"},
                {"Joined Date", student.getRegistrationDate() != null ? student.getRegistrationDate().format(formatter) : "N/A"}
            }, new Color(0xEFF6FF)), "growx, wrap");

        cardsContainer.add(createAcademicHistoryCard(), "growx, wrap");

        cardsContainer.add(createInfoCard("Contact Information", 
            new String[][] {
                {"Phone", student.getPhone()},
                {"Email", student.getEmail()},
                {"Address", student.getAddress()}
            }, new Color(0xF5F3FF)), "growx, wrap");

        // Wrap cardsContainer in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(cardsContainer);
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

    private JPanel createAcademicHistoryCard() {
        JPanel card = new JPanel(new MigLayout("insets 15, gap 10, fillx", "[grow]", "[]"));
        card.setBackground(Color.WHITE);
        card.setBorder(new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(0,0,0,0), new Color(0xE2E8F0), 1, 24));

        JLabel lblTitle = new JLabel("Lịch sử học tập & Kết quả");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 14));
        lblTitle.setForeground(new Color(0x334155));
        card.add(lblTitle, "gapbottom 5, wrap");

        if (student.getEnrollments() == null || student.getEnrollments().isEmpty()) {
            JLabel lblEmpty = new JLabel("Chưa có lịch sử học tập.");
            lblEmpty.setFont(new Font("Inter", Font.ITALIC, 13));
            lblEmpty.setForeground(new Color(0x94A3B8));
            card.add(lblEmpty, "pad 0 10 0 10");
        } else {
            for (Enrollment enrollment : student.getEnrollments()) {
                JPanel item = new JPanel(new MigLayout("insets 10 0 10 0, fillx", "[grow][]", "[]"));
                item.setOpaque(false);
                
                String courseName = enrollment.getCourseClass() != null && enrollment.getCourseClass().getCourse() != null 
                        ? enrollment.getCourseClass().getCourse().getName() : "Unknown Course";
                String className = enrollment.getCourseClass() != null ? enrollment.getCourseClass().getClassName() : "N/A";
                
                JLabel lblCourse = new JLabel(courseName);
                lblCourse.setFont(new Font("Inter", Font.BOLD, 13));
                lblCourse.setForeground(new Color(0x1E293B));
                
                JLabel lblClass = new JLabel(className + " • " + (enrollment.getEnrollmentDate() != null ? enrollment.getEnrollmentDate().format(formatter) : "N/A"));
                lblClass.setFont(new Font("Inter", Font.PLAIN, 12));
                lblClass.setForeground(new Color(0x64748B));

                // Attendance info
                int classId = enrollment.getCourseClass() != null ? enrollment.getCourseClass().getId() : 0;
                long attended = classId > 0 ? AttendanceDAO.getInstance().countStudentAttendance(student.getId(), classId) : 0;
                long totalHeld = classId > 0 ? ScheduleDAO.getInstance().countHeldSessions(classId) : 0;
                
                JLabel lblAttendance = new JLabel("Chuyên cần: " + attended + "/" + totalHeld + " buổi");
                lblAttendance.setFont(new Font("Inter", Font.BOLD, 11));
                lblAttendance.setForeground(new Color(0x6366F1));
                
                JPanel resultLabel = new JPanel(new BorderLayout());
                String scoreStr = enrollment.getResult() != null ? String.valueOf(enrollment.getResult()) : "-";
                JLabel lblScore = new JLabel(scoreStr);
                lblScore.setFont(new Font("Inter", Font.BOLD, 12));
                lblScore.setBorder(new EmptyBorder(2, 10, 2, 10));
                
                boolean hasResult = enrollment.getResult() != null;
                boolean isPassed = hasResult && enrollment.getResult() >= 5.0; // Assuming 5.0 is pass
                
                lblScore.setForeground(hasResult ? (isPassed ? new Color(0x15803D) : new Color(0x991B1B)) : new Color(0x64748B));
                resultLabel.setBackground(hasResult ? (isPassed ? new Color(0xDCFCE7) : new Color(0xFEE2E2)) : new Color(0xF1F5F9));
                resultLabel.setBorder(new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(0,0,0,0), 
                    resultLabel.getBackground(), 0, 10));
                resultLabel.add(lblScore);

                item.add(lblCourse, "split 3, flowy");
                item.add(lblClass);
                item.add(lblAttendance);
                item.add(resultLabel, "right");
                
                card.add(item, "growx, wrap");
                
                // Separator
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
