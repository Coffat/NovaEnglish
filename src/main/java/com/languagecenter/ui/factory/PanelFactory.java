package com.languagecenter.ui.factory;

import com.languagecenter.ui.MainFrame;
import com.languagecenter.ui.panels.*;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;

public class PanelFactory {

    public static JPanel createSubPanel(String title, MainFrame mainFrame, JTextField searchField) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        
        switch (title) {
            case "Students":
                StudentPanel studentPanel = new StudentPanel(mainFrame);
                if (searchField != null) studentPanel.bindSearchField(searchField);
                wrapper.add(studentPanel, BorderLayout.CENTER);
                break;
            case "Teachers":
                TeacherPanel teacherPanel = new TeacherPanel(mainFrame);
                if (searchField != null) teacherPanel.bindSearchField(searchField);
                wrapper.add(teacherPanel, BorderLayout.CENTER);
                break;
            case "Courses":
                CoursePanel coursePanel = new CoursePanel(mainFrame);
                if (searchField != null) coursePanel.bindSearchField(searchField);
                wrapper.add(coursePanel, BorderLayout.CENTER);
                break;
            case "Classes":
                CourseClassPanel classPanel = new CourseClassPanel(mainFrame);
                if (searchField != null) classPanel.bindSearchField(searchField);
                wrapper.add(classPanel, BorderLayout.CENTER);
                break;
            case "Schedules":
                SchedulePanel schedulePanel = new SchedulePanel(mainFrame);
                if (searchField != null) schedulePanel.bindSearchField(searchField);
                wrapper.add(schedulePanel, BorderLayout.CENTER);
                break;
            case "Payments":
                PaymentPanel paymentPanel = new PaymentPanel(mainFrame);
                if (searchField != null) paymentPanel.bindSearchField(searchField);
                wrapper.add(paymentPanel, BorderLayout.CENTER);
                break;
            case "Reports":
                ReportPanel reportPanel = new ReportPanel();
                wrapper.add(reportPanel, BorderLayout.CENTER);
                break;
            default:
                wrapper.add(new PlaceholderPanel(title), BorderLayout.CENTER);
                break;
        }
        return wrapper;
    }
}
