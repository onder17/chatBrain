package com.brainapp.chatbrain.ai;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AnswerGuardTest {
    private final AnswerGuard guard = new AnswerGuard();

    @Test
    void acceptsOnlyNumbersPresentInVerifiedAnswer() {
        assertTrue(guard.accepts("Bakiyen 145.650,75 ₺ olarak görünüyor.", "Bakiyen 145.650,75 ₺."));
        assertFalse(guard.accepts("Bakiyen 1.000.000 ₺.", "Bakiyen 145.650,75 ₺."));
    }

    @Test
    void rejectsLinksMarkupAndOversizedOutput() {
        assertFalse(guard.accepts("Visit https://example.com", "Verified"));
        assertFalse(guard.accepts("<script>alert(1)</script>", "Verified"));
        assertFalse(guard.accepts("x".repeat(1201), "Verified"));
    }

    @Test
    void structuredClaimsAndNamedEntitiesMustBeGrounded() {
        String evidence = "Toplam bakiye 145650.75 TRY; Garanti BBVA hesabı 1.";
        Map<String, Object> data = Map.of("banks", java.util.List.of(Map.of("bankName", "Garanti BBVA")));
        assertTrue(guard.acceptsStructured("{\"answer\":\"Garanti BBVA hesabındaki bakiye 145650.75 TL.\",\"claims\":[\"BALANCE_TOTAL\"],\"entities\":[\"Garanti BBVA\"]}", evidence, "ACCOUNT_BALANCE", data));
        assertFalse(guard.acceptsStructured("{\"answer\":\"Garanti BBVA bakiyesi 999 TL.\",\"claims\":[\"BALANCE_TOTAL\"],\"entities\":[\"Garanti BBVA\"]}", evidence, "ACCOUNT_BALANCE", data));
        assertFalse(guard.acceptsStructured("{\"answer\":\"145650.75 TL\",\"claims\":[\"INVESTMENT_ADVICE\"],\"entities\":[]}", evidence, "ACCOUNT_BALANCE", data));
        assertFalse(guard.acceptsStructured("{not json", evidence, "ACCOUNT_BALANCE", data));
    }

    @Test
    void rejectsUnsupportedDatesAndNamedEntities() {
        assertFalse(guard.accepts("Ödeme 2027-01-01 tarihinde.", "Ödeme 2026-11-15 tarihinde."));
        assertFalse(guard.acceptsStructured("{\"answer\":\"Yapı Kredi bakiyesi 145650.75 TL.\",\"claims\":[\"BALANCE_TOTAL\"],\"entities\":[\"Yapı Kredi\"]}", "Garanti BBVA 145650.75", "ACCOUNT_BALANCE", Map.of("banks", java.util.List.of(Map.of("bankName", "Garanti BBVA")) )));
        assertFalse(guard.acceptsStructured("{\"answer\":\"Akbank bakiyesi 145650.75 TL.\",\"claims\":[\"BALANCE_TOTAL\"],\"entities\":[]}", "Garanti BBVA 145650.75 TL", "ACCOUNT_BALANCE", Map.of("banks", java.util.List.of(Map.of("bankName", "Garanti BBVA")) )));
        assertFalse(guard.acceptsStructured("{\"answer\":\"Bakiyen 145650.75 TL. Finansal durumun mükemmel.\",\"claims\":[\"BALANCE_TOTAL\"],\"entities\":[]}", "Bakiyen 145650.75 TL.", "ACCOUNT_BALANCE", Map.of("banks", java.util.List.of())));
    }

    @Test
    void claimsMustBeSupportedByNonEmptyStructuredData() {
        assertFalse(guard.acceptsStructured("{\"answer\":\"Bakiyen 0.00 TL.\",\"claims\":[\"BANK_BREAKDOWN\"],\"entities\":[]}",
                "Bakiyen 0.00 TL.", "ACCOUNT_BALANCE", Map.of("banks", java.util.List.of())));
    }
}
