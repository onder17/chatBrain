package com.brainapp.chatbrain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreditCardResponse(
        String type, // "CARD_DEBT"
        BigDecimal totalDebt,
        BigDecimal totalLimit,
        BigDecimal totalAvailableLimit,
        BigDecimal totalStatementBalance,
        String currency,
        int cardCount,
        String cardFilter,
        List<CardDetailResponse> cards
) {
    public record CardDetailResponse(
            Long id,
            Long bankId,
            String bankName,
            String cardName,
            String cardType,
            String cardLast4,
            BigDecimal creditLimit,
            BigDecimal currentDebt,
            BigDecimal availableLimit,
            BigDecimal statementBalance,
            BigDecimal minimumPayment,
            LocalDate nextDueDate,
            BigDecimal utilizationPercent,
            boolean active
    ) {}
}
