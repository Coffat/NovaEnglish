package com.languagecenter.ui.factory;

import com.formdev.flatlaf.FlatClientProperties;
import com.languagecenter.ui.panels.*;
import javax.swing.JPanel;
import java.awt.Color;

public class SidePanelFactory {

    public static JPanel createSidePanel(String type, Runnable onClose) {
        JPanel sidePanel = null;
        switch (type) {
            case "Student":
                sidePanel = new StudentDetailSidePanel(onClose);
                break;
            case "Teacher":
                sidePanel = new TeacherDetailSidePanel(onClose);
                break;
            case "Course":
                sidePanel = new CourseDetailSidePanel(onClose);
                break;
            case "Class":
                sidePanel = new CourseClassDetailSidePanel(onClose);
                break;
            case "Schedule":
                sidePanel = new ScheduleDetailSidePanel(onClose);
                break;
            case "Payment":
                sidePanel = new PaymentDetailSidePanel(onClose);
                break;
            default:
                throw new IllegalArgumentException("Unknown Side Panel Type: " + type);
        }
        if (sidePanel != null) {
            sidePanel.putClientProperty(FlatClientProperties.STYLE, "arc: 0");
            sidePanel.setBackground(new Color(0xFFFFFF));
        }
        return sidePanel;
    }
}
