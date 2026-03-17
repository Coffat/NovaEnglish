package com.languagecenter.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {
    private static final Locale VIETNAM = Locale.forLanguageTag("vi-VN");
    private static final NumberFormat VND_FORMAT = NumberFormat.getCurrencyInstance(VIETNAM);

    public static String formatVND(BigDecimal amount) {
        if (amount == null) return VND_FORMAT.format(BigDecimal.ZERO);
        return VND_FORMAT.format(amount);
    }
}
