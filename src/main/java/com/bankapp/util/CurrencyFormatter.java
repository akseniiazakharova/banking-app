package com.bankapp.util;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {
    private static final NumberFormat FMT = NumberFormat.getCurrencyInstance(Locale.GERMAN);

    public static String format(double amount) {
        return FMT.format(amount);
    }
}
