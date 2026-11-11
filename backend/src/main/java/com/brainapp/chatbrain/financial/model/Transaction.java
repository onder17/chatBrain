package com.brainapp.chatbrain.financial.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Transaction(
        Long id,
        Long userId,
        Long accountId,
        Long cardId,
        String type, // INCOME, EXPENSE, TRANSFER
        String category, // RENT, MARKET, RESTAURANT, TRANSPORT, SHOPPING, etc.
        String categoryName,
        String merchant,
        String description,
        BigDecimal amount,
        String currency,
        LocalDate transactionDate
) {}
