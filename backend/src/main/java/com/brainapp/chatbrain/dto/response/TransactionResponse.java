package com.brainapp.chatbrain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TransactionResponse(
        String type, // "TRANSACTION_LIST"
        String periodLabel,
        String typeFilter,
        String categoryFilter,
        int totalCount,
        int returnedCount,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        List<TransactionItemResponse> transactions
) {
    public record TransactionItemResponse(
            Long id,
            LocalDate date,
            String type,
            String category,
            String categoryName,
            String merchant,
            String description,
            BigDecimal amount
    ) {}
}
