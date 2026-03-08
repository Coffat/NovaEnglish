package com.languagecenter.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import com.languagecenter.ui.panels.PlaceholderPanel;
import com.languagecenter.ui.panels.StudentPanel;
import com.languagecenter.ui.panels.StudentDetailSidePanel;
import com.languagecenter.ui.panels.TeacherPanel;
import com.languagecenter.ui.panels.TeacherDetailSidePanel;
import com.languagecenter.ui.panels.CoursePanel;
import com.languagecenter.ui.panels.CourseDetailSidePanel;
import com.languagecenter.ui.panels.CourseClassPanel;
import com.languagecenter.ui.panels.CourseClassDetailSidePanel;
import com.languagecenter.ui.panels.AttendancePanel;
import com.languagecenter.ui.panels.AttendanceDetailSidePanel;
import com.languagecenter.ui.panels.EnrollmentPanel;
import com.languagecenter.ui.panels.EnrollmentDetailSidePanel;
import com.languagecenter.ui.panels.SchedulePanel;
import com.languagecenter.ui.panels.ScheduleDetailSidePanel;
import com.languagecenter.ui.panels.PaymentPanel;
import com.languagecenter.ui.panels.PaymentDetailSidePanel;

public class MainFrame extends JFrame {

    private final Color primaryAccent = new Color(0x6366F1); // Indigo
    private final Color sidebarBg = new Color(0xF1F5F9); // Light Gray Sidebar
    private final Color contentBg = new Color(0xF8FAFC); // Main Light Background
    private final Color cardBg = new Color(0xFFFFFF); // White Card

    private JPanel sidebarPanel;
    private JPanel centerContentPanel;
    private StudentDetailSidePanel rightSidePanel;
    private TeacherDetailSidePanel rightTeacherSidePanel;
    private CourseDetailSidePanel rightCourseSidePanel;
    private CourseClassDetailSidePanel rightClassSidePanel;
    private AttendanceDetailSidePanel rightAttendanceSidePanel;
    private EnrollmentDetailSidePanel rightEnrollmentSidePanel;
    private ScheduleDetailSidePanel rightScheduleSidePanel;
    private PaymentDetailSidePanel rightPaymentSidePanel;
    private JPanel rightSideContainer;
    private CardLayout rightSideCardLayout;

    private JPanel centerHeaderPanel;
    private JPanel cardContainer;
    private CardLayout cardLayout;

    private Map<String, JButton> navButtons = new HashMap<>();

    private JPanel mainBodyPanel;
    private JTextField searchField;

    private boolean isSidePanelVisible = false;
    private int currentSideWidth = 0;
    private Timer sidePanelTimer;
    private final int MAX_SIDE_WIDTH = 380;

    public MainFrame() {
        setTitle("Dashboard - Language Center");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1350, 850);
        setLocationRelativeTo(null);
        getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
        setupGlobalStyles();

        // 2-Column Layout: [260!]0[grow]
        setLayout(new MigLayout("fill, insets 0, gap 0", "[260!]0[grow]", "[grow]"));
        getContentPane().setBackground(contentBg);

        rightSidePanel = new StudentDetailSidePanel(() -> toggleSidePanel(false));
        rightSidePanel.putClientProperty(FlatClientProperties.STYLE, "arc: 0");
        rightSidePanel.setBackground(new Color(0xFFFFFF));

        rightTeacherSidePanel = new TeacherDetailSidePanel(() -> toggleSidePanel(false));
        rightTeacherSidePanel.putClientProperty(FlatClientProperties.STYLE, "arc: 0");
        rightTeacherSidePanel.setBackground(new Color(0xFFFFFF));

        rightCourseSidePanel = new CourseDetailSidePanel(() -> toggleSidePanel(false));
        rightCourseSidePanel.putClientProperty(FlatClientProperties.STYLE, "arc: 0");
        rightCourseSidePanel.setBackground(new Color(0xFFFFFF));

        rightClassSidePanel = new CourseClassDetailSidePanel(() -> toggleSidePanel(false));
        rightClassSidePanel.putClientProperty(FlatClientProperties.STYLE, "arc: 0");
        rightClassSidePanel.setBackground(new Color(0xFFFFFF));

        rightAttendanceSidePanel = new AttendanceDetailSidePanel(() -> toggleSidePanel(false));
        rightAttendanceSidePanel.putClientProperty(FlatClientProperties.STYLE, "arc: 0");
        rightAttendanceSidePanel.setBackground(new Color(0xFFFFFF));

        rightEnrollmentSidePanel = new EnrollmentDetailSidePanel(() -> toggleSidePanel(false));
        rightEnrollmentSidePanel.putClientProperty(FlatClientProperties.STYLE, "arc: 0");
        rightEnrollmentSidePanel.setBackground(new Color(0xFFFFFF));

        rightScheduleSidePanel = new ScheduleDetailSidePanel(() -> toggleSidePanel(false));
        rightScheduleSidePanel.putClientProperty(FlatClientProperties.STYLE, "arc: 0");
        rightScheduleSidePanel.setBackground(new Color(0xFFFFFF));

        rightPaymentSidePanel = new PaymentDetailSidePanel(() -> toggleSidePanel(false));
        rightPaymentSidePanel.putClientProperty(FlatClientProperties.STYLE, "arc: 0");
        rightPaymentSidePanel.setBackground(new Color(0xFFFFFF));

        rightSideCardLayout = new CardLayout();
        rightSideContainer = new JPanel(rightSideCardLayout);
        rightSideContainer.add(rightSidePanel, "Student");
        rightSideContainer.add(rightTeacherSidePanel, "Teacher");
        rightSideContainer.add(rightCourseSidePanel, "Course");
        rightSideContainer.add(rightClassSidePanel, "Class");
        rightSideContainer.add(rightAttendanceSidePanel, "Attendance");
        rightSideContainer.add(rightEnrollmentSidePanel, "Enrollment");
        rightSideContainer.add(rightScheduleSidePanel, "Schedule");
        rightSideContainer.add(rightPaymentSidePanel, "Payment");
        rightSideContainer.setOpaque(false);

        buildSidebar();
        buildCenterArea();

        add(sidebarPanel, "growy, cell 0 0");
        add(centerContentPanel, "grow, push, cell 1 0");

        switchPanel("Students");
    }

    private void setupGlobalStyles() {
        UIManager.put("Button.arc", 25);
        UIManager.put("Component.arc", 25);
        UIManager.put("TextComponent.arc", 25);
        UIManager.put("ProgressBar.arc", 25);
        UIManager.put("ScrollPane.smoothScrolling", true);
        UIManager.put("Table.selectionBackground", new Color(0xE0E7FF)); // Light Indigo
        UIManager.put("Table.selectionForeground", new Color(0x4338CA)); // Dark Indigo Text
        UIManager.put("Table.selectionInactiveBackground", new Color(0xE0E7FF));
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.rowHeight", 50);
        UIManager.put("Table.gridColor", new Color(0xF1F5F9)); // Horizontal grid color
        UIManager.put("Component.outlineWidth", 0); // Remove drop shadow standard FlatLaf width for a soft look
    }

    private void buildSidebar() {
        sidebarPanel = new JPanel(new MigLayout("wrap 1, insets 30 20 30 20, gapy 12", "[grow]", "[]30[][grow][]"));
        sidebarPanel.setBackground(sidebarBg);
        sidebarPanel.setOpaque(true);
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0xE2E8F0))); // Subtle Right Border

        JLabel logoLabel = new JLabel(" Language Center", SwingConstants.LEFT);
        logoLabel.setFont(new Font("Inter", Font.BOLD, 18));
        logoLabel.setForeground(primaryAccent);

        try {
            URL url = getClass().getResource("/icons/classes.svg");
            if (url != null)
                logoLabel.setIcon(new FlatSVGIcon("icons/classes.svg", 28, 28));
        } catch (Exception e) {
        }
        sidebarPanel.add(logoLabel, "growx, gapbottom 10");

        JPanel navPanel = new JPanel(new MigLayout("wrap 1, fillx, insets 0, gapy 12", "[grow]", ""));
        navPanel.setOpaque(false);

        String[] navItems = {
                "Students", "Teachers", "Courses", "Classes",
                "Enrollments", "Schedules", "Attendance", "Payments", "Reports"
        };

        for (String item : navItems) {
            JButton btn = createNavButton(item);
            navButtons.put(item, btn);
            navPanel.add(btn, "growx, h 45!");
            btn.addActionListener(e -> switchPanel(item));
        }

        sidebarPanel.add(navPanel, "growx");
        sidebarPanel.add(new JLabel(), "growy, pushy");

        // Bottom User Setup Icon
        JButton btnSettings = new JButton("Settings");
        btnSettings.setHorizontalAlignment(SwingConstants.LEFT);
        btnSettings.setIconTextGap(15);
        try {
            URL url = getClass().getResource("/icons/settings.svg");
            if (url != null)
                btnSettings.setIcon(new FlatSVGIcon("icons/settings.svg", 20, 20));
        } catch (Exception e) {
        }
        btnSettings.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        btnSettings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSettings.setFont(new Font("Inter", Font.PLAIN, 15));
        btnSettings.setForeground(new Color(0x475569));
        sidebarPanel.add(btnSettings, "growx, h 45!");
    }

    private JButton createNavButton(String title) {
        JButton btn = new JButton(title);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(15);
        try {
            URL url = getClass().getResource("/icons/" + title.toLowerCase() + ".svg");
            if (url != null) {
                btn.setIcon(new FlatSVGIcon("icons/" + title.toLowerCase() + ".svg", 20, 20));
            }
        } catch (Exception e) {
        }

        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        btn.putClientProperty(FlatClientProperties.STYLE, "");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Inter", Font.PLAIN, 15));
        btn.setMargin(new Insets(10, 15, 10, 15));

        return btn;
    }

    private void buildCenterArea() {
        centerContentPanel = new JPanel(new MigLayout("fill, insets 30 40 30 40", "[grow]", "[][grow]"));
        centerContentPanel.setOpaque(false);

        centerHeaderPanel = new JPanel(new BorderLayout());
        centerHeaderPanel.setOpaque(false);

        JPanel welcomePanel = new JPanel(new MigLayout("insets 0, gap 0", "[]", "[][]"));
        welcomePanel.setOpaque(false);
        JLabel lblWelcome = new JLabel("Hi Vũ Toàn Thắng");
        lblWelcome.setFont(new Font("Inter", Font.BOLD, 28));
        lblWelcome.setForeground(new Color(0x1E293B));

        JLabel lblSub = new JLabel("Manage your center with ease.");
        lblSub.setFont(new Font("Inter", Font.PLAIN, 14));
        lblSub.setForeground(new Color(0x64748B));

        welcomePanel.add(lblWelcome, "wrap");
        welcomePanel.add(lblSub);

        JPanel controlsPanel = new JPanel(new MigLayout("insets 0, gap 20, right", "[][][]", "center"));
        controlsPanel.setOpaque(false);

        searchField = new JTextField();
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search...");
        searchField.putClientProperty(FlatClientProperties.STYLE,
                "arc: 999; margin: 8, 15, 8, 15; borderColor: #E2E8F0; focusedBorderColor: #6366F1; background: #FFFFFF");
        try {
            URL url = getClass().getResource("/icons/search.svg");
            if (url != null)
                searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
                        new FlatSVGIcon("icons/search.svg", 16, 16));
        } catch (Exception e) {
        }

        JButton btnNotif = new JButton();
        try {
            URL url = getClass().getResource("/icons/bell.svg");
            if (url != null)
                btnNotif.setIcon(new FlatSVGIcon("icons/bell.svg", 20, 20));
        } catch (Exception e) {
            btnNotif.setText("!");
        }
        btnNotif.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        btnNotif.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblAvatar = new JLabel();
        try {
            URL url = getClass().getResource("/icons/user.svg");
            if (url != null)
                lblAvatar.setIcon(new FlatSVGIcon("icons/user.svg", 42, 42));
        } catch (Exception e) {
            lblAvatar.setText("(U)");
        }
        lblAvatar.putClientProperty(FlatClientProperties.STYLE, "arc: 999");

        controlsPanel.add(searchField, "w 250!");
        controlsPanel.add(btnNotif);
        controlsPanel.add(lblAvatar);

        centerHeaderPanel.add(welcomePanel, BorderLayout.WEST);
        centerHeaderPanel.add(controlsPanel, BorderLayout.EAST);

        centerContentPanel.add(centerHeaderPanel, "growx, wrap, gapbottom 30");

        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setOpaque(false);

        for (String item : navButtons.keySet()) {
            cardContainer.add(createSubPanel(item), item);
        }

        mainBodyPanel = new JPanel(new MigLayout("fill, insets 0, gap 0", "[grow]0[0!]", "[grow]"));
        mainBodyPanel.setOpaque(false);
        mainBodyPanel.add(cardContainer, "grow, push");
        mainBodyPanel.add(rightSideContainer, "grow, pushx, hidemode 3");

        centerContentPanel.add(mainBodyPanel, "grow, push");
    }

    private JPanel createSubPanel(String title) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        if (title.equals("Students")) {
            StudentPanel studentPanel = new StudentPanel(this);
            if (searchField != null) {
                studentPanel.bindSearchField(searchField);
            }
            wrapper.add(studentPanel, BorderLayout.CENTER);
        } else if (title.equals("Teachers")) {
            TeacherPanel teacherPanel = new TeacherPanel(this);
            if (searchField != null) {
                teacherPanel.bindSearchField(searchField);
            }
            wrapper.add(teacherPanel, BorderLayout.CENTER);
        } else if (title.equals("Courses")) {
            CoursePanel coursePanel = new CoursePanel(this);
            if (searchField != null) {
                coursePanel.bindSearchField(searchField);
            }
            wrapper.add(coursePanel, BorderLayout.CENTER);
        } else if (title.equals("Classes")) {
            CourseClassPanel classPanel = new CourseClassPanel(this);
            if (searchField != null) {
                classPanel.bindSearchField(searchField);
            }
            wrapper.add(classPanel, BorderLayout.CENTER);
        } else if (title.equals("Attendance")) {
            AttendancePanel attendancePanel = new AttendancePanel(this);
            if (searchField != null) {
                attendancePanel.bindSearchField(searchField);
            }
            wrapper.add(attendancePanel, BorderLayout.CENTER);
        } else if (title.equals("Enrollments")) {
            EnrollmentPanel enrollmentPanel = new EnrollmentPanel(this);
            if (searchField != null) {
                enrollmentPanel.bindSearchField(searchField);
            }
            wrapper.add(enrollmentPanel, BorderLayout.CENTER);
        } else if (title.equals("Schedules")) {
            SchedulePanel schedulePanel = new SchedulePanel(this);
            if (searchField != null) {
                schedulePanel.bindSearchField(searchField);
            }
            wrapper.add(schedulePanel, BorderLayout.CENTER);
        } else if (title.equals("Payments")) {
            PaymentPanel paymentPanel = new PaymentPanel(this);
            if (searchField != null) {
                paymentPanel.bindSearchField(searchField);
            }
            wrapper.add(paymentPanel, BorderLayout.CENTER);
        } else {
            wrapper.add(new PlaceholderPanel(title), BorderLayout.CENTER);
        }
        return wrapper;
    }

    private void switchPanel(String name) {
        toggleSidePanel(false); // Hide side panel when switching views
        cardLayout.show(cardContainer, name);

        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(name)) {
                btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, null); // remove borderless to enforce
                                                                               // background
                btn.putClientProperty(FlatClientProperties.STYLE, "background: #E0E7FF");
                btn.setForeground(new Color(0x4338CA));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, primaryAccent),
                        BorderFactory.createEmptyBorder(0, 11, 0, 0) // compensate for 4px left border
                ));
            } else {
                btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
                btn.putClientProperty(FlatClientProperties.STYLE, "");
                btn.setBackground(null);
                btn.setForeground(new Color(0x475569));
                btn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
            }
            btn.revalidate();
            btn.repaint();
        }
    }

    public void toggleSidePanel(boolean show) {
        if (isSidePanelVisible == show)
            return;
        isSidePanelVisible = show;

        if (sidePanelTimer != null && sidePanelTimer.isRunning()) {
            sidePanelTimer.stop();
        }

        int targetWidth = show ? MAX_SIDE_WIDTH : 0;
        int step = show ? 30 : -30;

        sidePanelTimer = new Timer(15, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentSideWidth += step;
                if ((show && currentSideWidth >= targetWidth) || (!show && currentSideWidth <= 0)) {
                    currentSideWidth = targetWidth;
                    sidePanelTimer.stop();
                }

                String colConstraints = "[grow]0[" + currentSideWidth + "!]";
                ((MigLayout) mainBodyPanel.getLayout()).setColumnConstraints(colConstraints);
                mainBodyPanel.revalidate();
            }
        });
        sidePanelTimer.start();
    }

    public void openStudentSidePanel(com.languagecenter.entity.Student student,
            java.util.function.Consumer<com.languagecenter.entity.Student> onSave) {
        rightSideCardLayout.show(rightSideContainer, "Student");
        rightSidePanel.setStudent(student, onSave);
        toggleSidePanel(true);
    }

    public void openTeacherSidePanel(com.languagecenter.entity.Teacher teacher,
            java.util.function.Consumer<com.languagecenter.entity.Teacher> onSave) {
        rightSideCardLayout.show(rightSideContainer, "Teacher");
        rightTeacherSidePanel.setTeacher(teacher, onSave);
        toggleSidePanel(true);
    }

    public void openCourseSidePanel(com.languagecenter.entity.Course course,
            java.util.function.Consumer<com.languagecenter.entity.Course> onSave) {
        rightSideCardLayout.show(rightSideContainer, "Course");
        rightCourseSidePanel.setCourse(course, onSave);
        toggleSidePanel(true);
    }

    public void openClassSidePanel(com.languagecenter.entity.CourseClass courseClass,
            java.util.function.Consumer<com.languagecenter.entity.CourseClass> onSave) {
        rightSideCardLayout.show(rightSideContainer, "Class");
        rightClassSidePanel.setClass(courseClass, onSave);
        toggleSidePanel(true);
    }

    public void openAttendanceSidePanel(com.languagecenter.entity.Attendance attendance,
            java.util.function.Consumer<com.languagecenter.entity.Attendance> onSave) {
        rightSideCardLayout.show(rightSideContainer, "Attendance");
        rightAttendanceSidePanel.setAttendance(attendance, onSave);
        toggleSidePanel(true);
    }

    public void openEnrollmentSidePanel(com.languagecenter.entity.Enrollment enrollment,
            java.util.function.Consumer<com.languagecenter.entity.Enrollment> onSave) {
        rightSideCardLayout.show(rightSideContainer, "Enrollment");
        rightEnrollmentSidePanel.setEnrollment(enrollment, onSave);
        toggleSidePanel(true);
    }

    public void openScheduleSidePanel(com.languagecenter.entity.Schedule schedule,
            java.util.function.Consumer<com.languagecenter.entity.Schedule> onSave) {
        rightSideCardLayout.show(rightSideContainer, "Schedule");
        rightScheduleSidePanel.setSchedule(schedule, onSave);
        toggleSidePanel(true);
    }

    public void openPaymentSidePanel(com.languagecenter.entity.Payment payment,
            java.util.function.Consumer<com.languagecenter.entity.Payment> onSave) {
        rightSideCardLayout.show(rightSideContainer, "Payment");
        rightPaymentSidePanel.setPayment(payment, onSave);
        toggleSidePanel(true);
    }
}
