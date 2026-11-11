package com.brainapp.chatbrain.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record AccountBalanceResponse(
        String type, // "ACCOUNT_BALANCE"
        BigDecimal totalBalance,
        String currency,
        int accountCount,
        String bankFilter,
        List<BankAccountResponse> accounts,
        List<BankTotalResponse> banks
) {
    public record BankTotalResponse(
            String bankName,
            int accountCount,
            BigDecimal total
    ) {}
}
