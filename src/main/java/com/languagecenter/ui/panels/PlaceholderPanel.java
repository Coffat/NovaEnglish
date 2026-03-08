package com.languagecenter.ui.panels;

import javax.swing.*;
import java.awt.*;

public class PlaceholderPanel extends JPanel {

    public PlaceholderPanel(String title) {
        setOpaque(false);
        setLayout(new BorderLayout());

        JLabel lbl = new JLabel(title + " Panel", SwingConstants.CENTER);
        lbl.setFont(new Font("Inter", Font.BOLD, 24));
        lbl.setForeground(new Color(0x808080));
        add(lbl, BorderLayout.CENTER);
    }
}
