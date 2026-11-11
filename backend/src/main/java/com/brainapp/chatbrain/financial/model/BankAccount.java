package com.brainapp.chatbrain.financial.model;

import java.math.BigDecimal;

public record BankAccount(
        Long id,
        Long userId,
        Long bankId,
        String bankName,
        String accountName,
        String ibanLast4,
        String accountType, // CHECKING, SAVINGS, DEPOSIT
        BigDecimal balance,
        String currency,
        boolean active
) {}
