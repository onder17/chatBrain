package com.brainapp.chatbrain.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record FinancialSummaryResponse(
        String type, // "PERIOD_SUMMARY"
        String periodLabel,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal net,
        BigDecimal savingsRatePercent,
        int transactionCount,
        List<SpendingCategoryResponse.CategoryDetailResponse> topCategories
) {}
