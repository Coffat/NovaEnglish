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
import java.util.List;
import java.util.stream.Collectors;

public class SchedulePanel extends JPanel {

    private ScheduleDAO scheduleDAO = ScheduleDAO.getInstance();
    private com.languagecenter.ui.MainFrame mainFrame;
    
    private LocalDate currentWeekStart;
    
    // UI Components
    private JLabel lblWeekRange;
    private JPanel calendarGrid;
    private List<Schedule> allSchedules;
    
    // Click outside listener
    private MouseAdapter closeSidePanelListener;

    public SchedulePanel(com.languagecenter.ui.MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        // Init to current week monday
        LocalDate today = LocalDate.now();
        currentWeekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        initUI();
    }

    public void bindSearchField(JTextField searchField) {
        // Ignoring search field filtering for the calendar view to keep it intuitive
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setOpaque(false);

        closeSidePanelListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mainFrame != null) {
                    mainFrame.toggleSidePanel(false);
                }
            }
        };
        addMouseListener(closeSidePanelListener);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Navigation Panel (Center)
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        navPanel.setOpaque(false);
        
        JButton btnPrev = new JButton("<");
        btnPrev.putClientProperty(FlatClientProperties.STYLE, "arc: 999; margin: 5, 10, 5, 10");
        btnPrev.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrev.addActionListener(e -> {
            currentWeekStart = currentWeekStart.minusWeeks(1);
            reloadCalendar();
        });

        lblWeekRange = new JLabel();
        lblWeekRange.setFont(new Font("Inter", Font.BOLD, 16));
        lblWeekRange.setForeground(new Color(0x334155));
        
        JButton btnNext = new JButton(">");
        btnNext.putClientProperty(FlatClientProperties.STYLE, "arc: 999; margin: 5, 10, 5, 10");
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNext.addActionListener(e -> {
            currentWeekStart = currentWeekStart.plusWeeks(1);
            reloadCalendar();
        });

        navPanel.add(btnPrev);
        navPanel.add(lblWeekRange);
        navPanel.add(btnNext);
        
        // Actions Panel (Right)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        JButton btnAdd = new JButton("Add Schedule");
        btnAdd.putClientProperty(FlatClientProperties.STYLE, "background: #6366F1; foreground: #FFFFFF; arc: 15; borderWidth: 0");
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.openScheduleSidePanel(null, (newSchedule) -> {
                    scheduleDAO.addSchedule(newSchedule);
                    loadDataFromDB();
                    mainFrame.toggleSidePanel(false);
                });
            }
        });
        
        JButton btnToday = new JButton("Today");
        btnToday.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnToday.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToday.addActionListener(e -> {
             currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
             reloadCalendar();
        });
        
        actionPanel.add(btnToday);
        actionPanel.add(btnAdd);

        headerPanel.add(navPanel, BorderLayout.CENTER);
        headerPanel.add(actionPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // Calendar Area (7 Columns Mon -> Sun)
        calendarGrid = new JPanel(new GridLayout(1, 7, 10, 0));
        calendarGrid.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(calendarGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().addMouseListener(closeSidePanelListener);
        scrollPane.setMinimumSize(new Dimension(0, 0));

        add(scrollPane, BorderLayout.CENTER);

        loadDataFromDB();
    }
    
    private void loadDataFromDB() {
        allSchedules = scheduleDAO.getAllSchedules();
        reloadCalendar();
    }

    private void reloadCalendar() {
        calendarGrid.removeAll();
        
        LocalDate weekEnd = currentWeekStart.plusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        lblWeekRange.setText(currentWeekStart.format(formatter) + " - " + weekEnd.format(formatter));
        
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = currentWeekStart.plusDays(i);
            JPanel dayCol = createDayColumn(days[i], currentDate);
            calendarGrid.add(dayCol);
        }
        
        calendarGrid.revalidate();
        calendarGrid.repaint();
    }

    private JPanel createDayColumn(String dayName, LocalDate date) {
        JPanel col = new JPanel(new BorderLayout());
        col.setBackground(new Color(0xFFFFFF));
        col.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        col.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE2E8F0), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        // Highlight if today
        if (date.equals(LocalDate.now())) {
            col.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x6366F1), 2, true),
                new EmptyBorder(9, 9, 9, 9)
            ));
        }

        col.addMouseListener(closeSidePanelListener);

        // Day Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel lblDay = new JLabel(dayName);
        lblDay.setFont(new Font("Inter", Font.BOLD, 14));
        lblDay.setForeground(date.equals(LocalDate.now()) ? new Color(0x6366F1) : new Color(0x334155));
        
        JLabel lblDate = new JLabel(date.format(DateTimeFormatter.ofPattern("dd/MM")));
        lblDate.setFont(new Font("Inter", Font.PLAIN, 12));
        lblDate.setForeground(new Color(0x94A3B8));
        
        headerPanel.add(lblDay, BorderLayout.WEST);
        headerPanel.add(lblDate, BorderLayout.EAST);
        
        col.add(headerPanel, BorderLayout.NORTH);
        
        // Cards Container
        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);
        cardsContainer.addMouseListener(closeSidePanelListener);
        
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);
        List<Schedule> daySchedules = allSchedules.stream()
            .filter(s -> s.getScheduleDate() != null && s.getScheduleDate().toString().equals(sqlDate.toString()))
            .sorted((s1, s2) -> {
                if (s1.getStartTime() == null || s2.getStartTime() == null) return 0;
                return s1.getStartTime().compareTo(s2.getStartTime());
            })
            .collect(Collectors.toList());
            
        for (Schedule s : daySchedules) {
            cardsContainer.add(createScheduleCard(s));
            cardsContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        // Wrap cards in flow to align top
        JPanel alignTopPanel = new JPanel(new BorderLayout());
        alignTopPanel.setOpaque(false);
        alignTopPanel.add(cardsContainer, BorderLayout.NORTH);
        alignTopPanel.addMouseListener(closeSidePanelListener);

        col.add(alignTopPanel, BorderLayout.CENTER);
        
        return col;
    }

    private JPanel createScheduleCard(Schedule schedule) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(new Color(0xEEF2FF)); // light indigo
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Class Title
        String className = schedule.getCourseClass() != null ? schedule.getCourseClass().getClassName() : "Unknown Class";
        JLabel lblClass = new JLabel("<html><b>" + className + "</b></html>");
        lblClass.setFont(new Font("Inter", Font.PLAIN, 12));
        lblClass.setForeground(new Color(0x3730A3));
        
        // Start - End time
        String timeStr = (schedule.getStartTime() != null ? schedule.getStartTime().toString().substring(0, 5) : "--:--") 
                       + " - " 
                       + (schedule.getEndTime() != null ? schedule.getEndTime().toString().substring(0, 5) : "--:--");
        JLabel lblTime = new JLabel(timeStr);
        lblTime.setFont(new Font("Inter", Font.PLAIN, 11));
        lblTime.setForeground(new Color(0x4F46E5));
        
        // Room ID
        JLabel lblRoom = new JLabel("Room: " + (schedule.getRoomId() != null ? schedule.getRoomId() : "N/A"));
        lblRoom.setFont(new Font("Inter", Font.PLAIN, 11));
        lblRoom.setForeground(new Color(0x6366F1));
        
        card.add(lblClass, BorderLayout.NORTH);
        card.add(lblTime, BorderLayout.CENTER);
        card.add(lblRoom, BorderLayout.SOUTH);
        
        // Click to view/edit
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mainFrame != null) {
                    mainFrame.openScheduleSidePanel(schedule, (updatedSchedule) -> {
                        scheduleDAO.updateSchedule(updatedSchedule);
                        loadDataFromDB();
                        mainFrame.toggleSidePanel(false);
                    });
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(0xE0E7FF)); // hover darker indigo
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(0xEEF2FF)); // back to light indigo
            }
        });
        
        return card;
    }
}
