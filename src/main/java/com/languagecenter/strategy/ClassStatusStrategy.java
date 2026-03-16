package com.languagecenter.strategy;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ClassStatusStrategy implements StatusStrategy {
    private static final Map<String, StatusStyle> styles = new HashMap<>();

    static {
        styles.put("Opening", new StatusStyle(new Color(0x1D4ED8), new Color(0xDBEAFE)));
        styles.put("On-going", new StatusStyle(new Color(0x15803D), new Color(0xDCFCE7)));
        styles.put("Completed", new StatusStyle(new Color(0x334155), new Color(0xF1F5F9)));
        styles.put("Cancelled", new StatusStyle(new Color(0xB91C1C), new Color(0xFEE2E2)));
    }

    @Override
    public StatusStyle getStyle(String status) {
        return styles.getOrDefault(status, new StatusStyle(new Color(0x64748B), new Color(0xF8FAFC)));
    }
}
