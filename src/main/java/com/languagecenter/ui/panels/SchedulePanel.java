package com.languagecenter.ui.panels;

import com.languagecenter.dao.ScheduleDAO;
import com.languagecenter.entity.Schedule;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.time.format.DateTimeFormatter;
import net.miginfocom.swing.MigLayout;
import java.util.List;
import java.util.stream.Collectors;

public class SchedulePanel extends JPanel {

    private ScheduleDAO scheduleDAO = ScheduleDAO.getInstance();
    private com.languagecenter.ui.MainFrame mainFrame;
    
    private LocalDate currentWeekStart;
    private String currentSearchText = "";
    
    // UI Components
    private JLabel lblWeekRange;
    private JPanel calendarGrid;
    private List<Schedule> allSchedules;
    private MouseAdapter closeSidePanelListener;
    

    public SchedulePanel(com.languagecenter.ui.MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        // Init to current week monday
        LocalDate today = LocalDate.now();
        currentWeekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        initUI();
    }

    public void bindSearchField(JTextField searchField) {
        if (searchField == null) return;
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateSearch(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateSearch(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateSearch(); }
            private void updateSearch() {
                currentSearchText = searchField.getText().toLowerCase().trim();
                reloadCalendar();
            }
        });
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        closeSidePanelListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mainFrame != null) mainFrame.toggleSidePanel(false);
            }
        };

        // 1. Header Section
        JPanel topSection = new JPanel(new MigLayout("fill, insets 15 25 15 25", "[grow][center][grow]")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0xF8FAFC), 0, getHeight(), new Color(0xF1F5F9));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        // Left Side: Title & Today Button
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Weekly Schedule");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0x1E293B));
        
        JButton btnToday = new JButton("Today");
        btnToday.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToday.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #FFFFFF; foreground: #475569; borderWidth: 1; borderColor: #E2E8F0");
        btnToday.addActionListener(e -> {
            currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            reloadCalendar();
        });
        titlePanel.add(lblTitle);
        titlePanel.add(btnToday);

        // Center: Navigation
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        navPanel.setOpaque(false);
        
        JButton btnPrev = new JButton("󰄼"); // Icon placeholder
        btnPrev.setFont(new Font("Material Design Icons", Font.PLAIN, 18));
        btnPrev.setText("❮");
        btnPrev.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrev.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #FFFFFF; foreground: #64748B; borderWidth: 1; borderColor: #E2E8F0; minimumWidth: 40; minimumHeight: 36");
        btnPrev.addActionListener(e -> {
            currentWeekStart = currentWeekStart.minusWeeks(1);
            reloadCalendar();
        });

        lblWeekRange = new JLabel();
        lblWeekRange.setFont(new Font("Inter", Font.BOLD, 16));
        lblWeekRange.setForeground(new Color(0x334155));
        
        JButton btnNext = new JButton("❯");
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNext.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #FFFFFF; foreground: #64748B; borderWidth: 1; borderColor: #E2E8F0; minimumWidth: 40; minimumHeight: 36");
        btnNext.addActionListener(e -> {
            currentWeekStart = currentWeekStart.plusWeeks(1);
            reloadCalendar();
        });

        navPanel.add(btnPrev);
        navPanel.add(lblWeekRange);
        navPanel.add(btnNext);

        // Right Side: Add Button
        JButton btnAdd = new JButton("Add Session");
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #6366F1; foreground: #FFFFFF; borderWidth: 0; margin: 8,16,8,16; font: bold");
        btnAdd.addActionListener(e -> {
             if (mainFrame != null) {
                mainFrame.openScheduleSidePanel(null, (newSchedule) -> {
                    if (newSchedule != null) scheduleDAO.addSchedule(newSchedule);
                    loadDataFromDB();
                    mainFrame.toggleSidePanel(false);
                });
            }
        });

        topSection.add(titlePanel, "growx");
        topSection.add(navPanel, "center");
        topSection.add(btnAdd, "right");
        
        add(topSection, BorderLayout.NORTH);

        // 2. Calendar Content Section
        JPanel calendarContainer = new JPanel(new BorderLayout());
        calendarContainer.setOpaque(false);
        calendarContainer.setBorder(new EmptyBorder(0, 15, 15, 15));

        // Use MigLayout for the grid to allow better scaling (co giãn)
        calendarGrid = new JPanel(new MigLayout("fill, insets 0, gap 10", "[grow, shrink 100, 80::][grow, shrink 100, 80::][grow, shrink 100, 80::][grow, shrink 100, 80::][grow, shrink 100, 80::][grow, shrink 100, 80::][grow, shrink 100, 80::]", "fill"));
        calendarGrid.setOpaque(false);
        
        calendarContainer.add(calendarGrid, BorderLayout.CENTER);
        add(calendarContainer, BorderLayout.CENTER);

        loadDataFromDB();
    }
    
    private void loadDataFromDB() {
        allSchedules = scheduleDAO.getAllSchedules();
        reloadCalendar();
    }

    private void reloadCalendar() {
        calendarGrid.removeAll();
        
        LocalDate weekEnd = currentWeekStart.plusDays(6);
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        DateTimeFormatter rangeFormatter = DateTimeFormatter.ofPattern("dd MMM");
        
        lblWeekRange.setText(currentWeekStart.format(rangeFormatter) + " - " + weekEnd.format(rangeFormatter) + " (" + currentWeekStart.format(monthFormatter) + ")");
        
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = currentWeekStart.plusDays(i);
            calendarGrid.add(createDayColumn(days[i], date), "grow, push");
        }
        
        calendarGrid.revalidate();
        calendarGrid.repaint();
    }

    private JPanel createDayColumn(String dayName, LocalDate date) {
        boolean isToday = date.equals(LocalDate.now());
        
        JPanel col = new JPanel(new BorderLayout());
        col.setOpaque(false);
        
        // Surface Panel for effect
        JPanel surface = new JPanel(new BorderLayout());
        surface.setBackground(Color.WHITE);
        surface.putClientProperty(FlatClientProperties.STYLE, "arc: 16");
        surface.setBorder(BorderFactory.createCompoundBorder(
            new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(0, 0, 0, 0), isToday ? new Color(0x6366F1) : new Color(0xF1F5F9), 1, 16),
            new EmptyBorder(12, 8, 12, 8)
        ));

        // Column Header
        JPanel headerPanel = new JPanel(new MigLayout("insets 0, gap 2, fillx", "[]", "[][]"));
        headerPanel.setOpaque(false);
        
        JLabel lblDay = new JLabel(dayName.toUpperCase());
        lblDay.setFont(new Font("Inter", Font.BOLD, 10));
        lblDay.setForeground(isToday ? new Color(0x6366F1) : new Color(0x94A3B8));
        
        JLabel lblDate = new JLabel(date.format(DateTimeFormatter.ofPattern("MMM dd")));
        lblDate.setFont(new Font("Inter", Font.BOLD, 15));
        lblDate.setForeground(isToday ? new Color(0x1E293B) : new Color(0x334155));
        
        headerPanel.add(lblDay, "wrap");
        headerPanel.add(lblDate, "gapbottom 15");
        
        surface.add(headerPanel, BorderLayout.NORTH);
        
        // Split view: 50% Morning, 50% Afternoon
        JPanel splitPanel = new JPanel(new MigLayout("fill, insets 0, gap 0", "[grow]", "[grow, 50%]2[grow, 50%]"));
        splitPanel.setOpaque(false);

        List<Schedule> daySchedules = allSchedules.stream()
            .filter(s -> s.getScheduleDate() != null && s.getScheduleDate().equals(date))
            .filter(s -> {
                if (currentSearchText.isEmpty()) return true;
                String className = s.getCourseClass() != null ? s.getCourseClass().getClassName().toLowerCase() : "";
                String teacherName = (s.getCourseClass() != null && s.getCourseClass().getTeacher() != null) 
                                   ? s.getCourseClass().getTeacher().getFullName().toLowerCase() : "";
                String room = String.valueOf(s.getRoomId());
                return className.contains(currentSearchText) || teacherName.contains(currentSearchText) || room.contains(currentSearchText);
            })
            .sorted((s1, s2) -> {
                if (s1.getStartTime() == null || s2.getStartTime() == null) return 0;
                return s1.getStartTime().compareTo(s2.getStartTime());
            })
            .collect(Collectors.toList());

        List<Schedule> morning = daySchedules.stream()
            .filter(s -> s.getStartTime() != null && s.getStartTime().getHour() < 12)
            .collect(Collectors.toList());
        List<Schedule> afternoon = daySchedules.stream()
            .filter(s -> s.getStartTime() != null && s.getStartTime().getHour() >= 12)
            .collect(Collectors.toList());

        splitPanel.add(createSectionPanel("MORNING", morning), "grow, push, wrap");
        splitPanel.add(createSectionPanel("AFTERNOON", afternoon), "grow, push");

        surface.add(splitPanel, BorderLayout.CENTER);
        surface.addMouseListener(closeSidePanelListener);

        col.add(surface, BorderLayout.CENTER);
        col.addMouseListener(closeSidePanelListener);
        
        return col;
    }

    private JPanel createSectionPanel(String title, List<Schedule> list) {
        JPanel p = new JPanel(new MigLayout("fill, insets 0, gap 0", "[grow]", "[][]0[grow]"));
        p.setOpaque(false);
        p.add(createSessionHeader(title), "growx, wrap");

        JPanel cardsContainer = new JPanel(new MigLayout("fillx, insets 0, gapy 8", "[grow]", "[]"));
        cardsContainer.setOpaque(false);

        if (list.isEmpty()) {
            JLabel lblEmpty = new JLabel("No sessions", SwingConstants.CENTER);
            lblEmpty.setFont(new Font("Inter", Font.ITALIC, 10));
            lblEmpty.setForeground(new Color(0xCBD5E1));
            cardsContainer.add(lblEmpty, "grow, center, gaptop 10");
        } else {
            for (Schedule s : list) {
                cardsContainer.add(createScheduleCard(s), "growx, wrap");
            }
        }

        JScrollPane scroll = new JScrollPane(cardsContainer);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        
        p.add(scroll, "grow, push");
        return p;
    }

    private JPanel createSessionHeader(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(5, 5, 5, 0));
        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", Font.BOLD, 10));
        label.setForeground(new Color(0x94A3B8));
        p.add(label, BorderLayout.WEST);
        return p;
    }

    private JPanel createScheduleCard(Schedule schedule) {
        JPanel card = new JPanel(new MigLayout("fillx, insets 6", "[]6[grow]", "[][][]"));
        card.setBackground(new Color(0xF8FAFC));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        card.setBorder(new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(0, 0, 0, 0), new Color(0xF1F5F9), 1, 12));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Class Info
        String className = schedule.getCourseClass() != null ? schedule.getCourseClass().getClassName() : "Unknown";
        JLabel lblClass = new JLabel(className);
        lblClass.setFont(new Font("Inter", Font.BOLD, 11));
        lblClass.setForeground(new Color(0x0F172A));
        
        String teacherName = (schedule.getCourseClass() != null && schedule.getCourseClass().getTeacher() != null)
                             ? schedule.getCourseClass().getTeacher().getFullName() : "No teacher";
        JLabel lblTeacher = new JLabel(teacherName);
        lblTeacher.setFont(new Font("Inter", Font.PLAIN, 9));
        lblTeacher.setForeground(new Color(0x64748B));

        String timeStr = (schedule.getStartTime() != null ? schedule.getStartTime().toString().substring(0, 5) : "--:--") 
                       + " - " 
                       + (schedule.getEndTime() != null ? schedule.getEndTime().toString().substring(0, 5) : "--:--");
        JLabel lblTime = new JLabel("󰥔 " + timeStr);
        lblTime.setFont(new Font("Inter", Font.PLAIN, 9));
        lblTime.setForeground(new Color(0x6366F1));

        JLabel lblRoom = new JLabel("󰔑 RM: " + (schedule.getRoomId() != null ? schedule.getRoomId() : "N/A"));
        lblRoom.setFont(new Font("Inter", Font.BOLD, 8));
        lblRoom.setForeground(new Color(0x94A3B8));
        
        // Icon/Pill for Class
        JPanel marker = new JPanel();
        marker.setPreferredSize(new Dimension(3, 24));
        marker.setBackground(new Color(0x6366F1));
        marker.putClientProperty(FlatClientProperties.STYLE, "arc: 8");

        card.add(marker, "span 1 3, h 24!");
        card.add(lblClass, "growx, wrap, gapbottom 0");
        card.add(lblTeacher, "growx, wrap, gapbottom 0");
        card.add(lblTime, "growx, split 2, gapright 3");
        card.add(lblRoom, "right");
        
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mainFrame != null) {
                    mainFrame.openScheduleSidePanel(schedule, (updatedSchedule) -> {
                        if (updatedSchedule != null) scheduleDAO.updateSchedule(updatedSchedule);
                        loadDataFromDB();
                        mainFrame.toggleSidePanel(false);
                    });
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(0xFFFFFF));
                card.setBorder(new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(0, 0, 0, 0), new Color(0x6366F1), 1, 16));
                card.putClientProperty(FlatClientProperties.STYLE, "arc: 16; [light]background: #ffffff; [dark]background: #2D3748");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(0xF8FAFC));
                card.setBorder(new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(0, 0, 0, 0), new Color(0xF1F5F9), 1, 16));
            }
        });
        
        return card;
    }
}
