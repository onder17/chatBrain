package com.brainapp.chatbrain.financial.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreditCard(
        Long id,
        Long userId,
        Long bankId,
        String bankName,
        String cardName,
        String cardType, // CLASSIC, GOLD, PLATINUM, BUSINESS
        String cardLast4,
        BigDecimal creditLimit,
        BigDecimal currentDebt,
        BigDecimal availableLimit,
        BigDecimal statementBalance,
        BigDecimal minimumPayment,
        LocalDate nextDueDate,
        int statementDay,
        boolean active
) {
    public BigDecimal calculateUtilizationPercent() {
        if (creditLimit == null || creditLimit.signum() <= 0) return BigDecimal.ZERO;
        return currentDebt.multiply(BigDecimal.valueOf(100))
                .divide(creditLimit, 1, java.math.RoundingMode.HALF_UP);
    }
}
