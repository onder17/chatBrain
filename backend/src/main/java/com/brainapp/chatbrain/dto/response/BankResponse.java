package com.brainapp.chatbrain.dto.response;

public record BankResponse(
        Long id,
        String code,
        String name,
        String logoUrl
) {}
