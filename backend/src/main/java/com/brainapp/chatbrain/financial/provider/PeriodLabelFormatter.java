package com.brainapp.chatbrain.financial.provider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/** Formats user-visible date ranges from the actual query bounds. */
public final class PeriodLabelFormatter {
    private static final Locale TURKISH = Locale.forLanguageTag("tr-TR");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d MMMM yyyy", TURKISH);

    private PeriodLabelFormatter() {}

    public static String format(LocalDate from, LocalDate to) {
        if (from == null && to == null) return "Tüm dönem";
        if (from == null) return DATE.format(to) + " tarihine kadar";
        if (to == null) return DATE.format(from) + " tarihinden itibaren";
        if (from.getYear() == to.getYear() && from.getMonth() == to.getMonth())
            return capitalize(from.getMonth().getDisplayName(TextStyle.FULL, TURKISH)) + " " + from.getYear();
        if (from.getYear() == to.getYear())
            return from.getDayOfMonth() + " " + from.getMonth().getDisplayName(TextStyle.SHORT, TURKISH)
                    + " – " + to.getDayOfMonth() + " " + to.getMonth().getDisplayName(TextStyle.SHORT, TURKISH)
                    + " " + to.getYear();
        return DATE.format(from) + " – " + DATE.format(to);
    }

    private static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase(TURKISH) + value.substring(1);
    }
}
