package com.brainapp.chatbrain.financial.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record Bill(
        Long id,
        Long userId,
        String providerName,
        String category, // ELECTRICITY, WATER, NATURAL_GAS, INTERNET, MOBILE, OTHER
        BigDecimal amount,
        String currency,
        LocalDate dueDate,
        String status, // UNPAID, PAID
        LocalDate paidAt
) {
    public boolean isUpcoming(LocalDate today, long horizonDays) {
        return "UNPAID".equalsIgnoreCase(status) && dueDate != null &&
                !dueDate.isBefore(today) && !dueDate.isAfter(today.plusDays(horizonDays));
    }

    public boolean isOverdue(LocalDate today) {
        return "UNPAID".equalsIgnoreCase(status) && dueDate != null && dueDate.isBefore(today);
    }

    public Integer daysUntilDue(LocalDate today) {
        if (!"UNPAID".equalsIgnoreCase(status) || dueDate == null) return null;
        return (int) ChronoUnit.DAYS.between(today, dueDate);
    }
}
