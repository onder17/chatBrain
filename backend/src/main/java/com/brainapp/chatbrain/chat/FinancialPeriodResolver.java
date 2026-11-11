package com.brainapp.chatbrain.chat;

import com.brainapp.chatbrain.exception.InvalidParameterException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Resolves the small, explicit set of natural-language date ranges supported by the demo. */
@Component
public class FinancialPeriodResolver {
    private final LocalDate referenceDate;
    private static final Pattern ISO_DATES = Pattern.compile("(20\\d{2}-\\d{2}-\\d{2}).{0,40}?(20\\d{2}-\\d{2}-\\d{2})");
    private static final Pattern YEAR = Pattern.compile("\\b(20\\d{2})\\b");

    public FinancialPeriodResolver(@Value("${chatbrain.finance.reference-date:}") String configuredReferenceDate) {
        this.referenceDate = configuredReferenceDate == null || configuredReferenceDate.isBlank()
                ? LocalDate.now() : LocalDate.parse(configuredReferenceDate);
    }

    public PeriodRange resolve(String question) {
        String query = com.brainapp.chatbrain.ai.RuleBasedIntentDetector.normalize(question);
        Matcher dates = ISO_DATES.matcher(question);
        if (dates.find()) {
            LocalDate from;
            LocalDate to;
            try {
                from = LocalDate.parse(dates.group(1));
                to = LocalDate.parse(dates.group(2));
            } catch (java.time.DateTimeException ex) {
                throw new InvalidParameterException("The requested date range is invalid");
            }
            if (from.isAfter(to)) throw new InvalidParameterException("The start date must not follow the end date");
            return new PeriodRange(from, to, from + "–" + to);
        }
        LocalDate reference = referenceDate;
        if (query.contains("gecen ay") || query.contains("onceki ay")) {
            YearMonth month = YearMonth.from(reference).minusMonths(1);
            return month(month);
        }
        if (query.contains("bu yil") || query.contains("bu sene")) {
            return new PeriodRange(reference.withDayOfYear(1), reference.withMonth(12).withDayOfMonth(31), String.valueOf(reference.getYear()));
        }
        Matcher year = YEAR.matcher(question);
        if (year.find()) {
            int selectedYear = Integer.parseInt(year.group(1));
            return new PeriodRange(LocalDate.of(selectedYear, 1, 1), LocalDate.of(selectedYear, 12, 31), String.valueOf(selectedYear));
        }
        return month(YearMonth.from(reference));
    }

    public boolean hasExplicitPeriod(String question) {
        String query = com.brainapp.chatbrain.ai.RuleBasedIntentDetector.normalize(question);
        return ISO_DATES.matcher(question).find() || YEAR.matcher(question).find() ||
                query.contains("gecen ay") || query.contains("onceki ay") ||
                query.contains("bu yil") || query.contains("bu sene");
    }

    private PeriodRange month(YearMonth month) {
        String label = month.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.forLanguageTag("tr-TR"));
        return new PeriodRange(month.atDay(1), month.atEndOfMonth(), label + " " + month.getYear());
    }

    public record PeriodRange(LocalDate from, LocalDate to, String label) {}
}
