package com.brainapp.chatbrain.dto.response;

import java.math.BigDecimal;

public record BankAccountResponse(
        Long id,
        Long bankId,
        String bankName,
        String accountName,
        String ibanLast4,
        String accountType,
        BigDecimal balance,
        String currency,
        boolean active
) {}
