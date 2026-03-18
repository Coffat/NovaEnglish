package com.languagecenter.strategy;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class PaymentStatusStrategy implements StatusStrategy {
    private static final Map<String, StatusStyle> styles = new HashMap<>();

    static {
        styles.put("Completed", new StatusStyle(new Color(0x15803D), new Color(0xDCFCE7)));
        styles.put("Paid", new StatusStyle(new Color(0x15803D), new Color(0xDCFCE7)));
        styles.put("Failed", new StatusStyle(new Color(0xB91C1C), new Color(0xFEE2E2)));
        styles.put("Refunded", new StatusStyle(new Color(0xB91C1C), new Color(0xFEE2E2)));
        styles.put("Pending", new StatusStyle(new Color(0x4338CA), new Color(0xE0E7FF))); // Indigo
        styles.put("Unpaid", new StatusStyle(new Color(0xB45309), new Color(0xFEF3C7))); // Yellow for old data
    }

    @Override
    public StatusStyle getStyle(String status) {
        return styles.getOrDefault(status, new StatusStyle(new Color(0xB45309), new Color(0xFEF3C7)));
    }
}
