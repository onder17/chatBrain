package com.brainapp.chatbrain.financial.model;

/** Non-personal card product data. It intentionally has no account or balance fields. */
public record CreditCardProduct(
        Long id,
        Long bankId,
        String bankName,
        String productName,
        String cardType
) {}
