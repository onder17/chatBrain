package com.brainapp.chatbrain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BillResponse(
        String type, // "INVOICE_LIST"
        String filter,
        int count,
        BigDecimal totalAmount,
        int overdueCount,
        BigDecimal overdueAmount,
        List<BillItemResponse> invoices
) {
    public record BillItemResponse(
            Long id,
            String providerName,
            String category,
            BigDecimal amount,
            LocalDate dueDate,
            String status,
            boolean overdue,
            Integer daysUntilDue,
            LocalDate paidAt
    ) {}
}
