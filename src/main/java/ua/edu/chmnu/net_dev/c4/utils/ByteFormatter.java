package ua.edu.chmnu.net_dev.c4.utils;

import lombok.experimental.UtilityClass;

import java.util.Locale;

@UtilityClass
public class ByteFormatter {

    private final static String PREFIXES = " KMGTPE";

    public String format(long value, Locale locale) {
        if (value < 1024) {
            return value + " B";
        }

        int offset = (63 - Long.numberOfLeadingZeros(value)) / 10;

        double convertedValue = 1.0 * value / (1L << (offset * 10));

        return String.format(locale, "%.1f %siB", convertedValue, PREFIXES.charAt(offset));
    }

    public String format(long value) {
        return format(value, Locale.getDefault());
    }
}
