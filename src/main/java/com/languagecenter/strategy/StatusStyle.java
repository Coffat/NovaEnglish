package com.languagecenter.strategy;

import java.awt.Color;

public class StatusStyle {
    private final Color foreground;
    private final Color background;

    public StatusStyle(Color foreground, Color background) {
        this.foreground = foreground;
        this.background = background;
    }

    public Color getForeground() { return foreground; }
    public Color getBackground() { return background; }
}
