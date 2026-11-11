package com.brainapp.chatbrain.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record SpendingCategoryResponse(
        String type, // "SPENDING_BY_CATEGORY"
        String periodLabel,
        BigDecimal totalExpense,
        String focusCategory,
        List<CategoryDetailResponse> categories
) {
    public record CategoryDetailResponse(
            String category,
            String categoryName,
            BigDecimal total,
            int count,
            BigDecimal percentage
    ) {}
}
