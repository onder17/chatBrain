package com.brainapp.chatbrain.chat;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinancialPeriodResolverTest {
    private final FinancialPeriodResolver resolver = new FinancialPeriodResolver("2026-11-15");

    @Test
    void defaultsToFixtureMonth() {
        var period = resolver.resolve("Bu ay ne kadar harcadım?");
        assertEquals(LocalDate.of(2026, 11, 1), period.from());
        assertEquals(LocalDate.of(2026, 11, 30), period.to());
    }

    @Test
    void resolvesPreviousMonth() {
        var period = resolver.resolve("Geçen ay ne kadar harcadım?");
        assertEquals(LocalDate.of(2026, 10, 1), period.from());
        assertEquals(LocalDate.of(2026, 10, 31), period.to());
    }

    @Test
    void resolvesExplicitIsoRangeInclusively() {
        var period = resolver.resolve("2026-11-03 ile 2026-11-10 arasındaki harcamalarım");
        assertEquals(LocalDate.of(2026, 11, 3), period.from());
        assertEquals(LocalDate.of(2026, 11, 10), period.to());
    }

    @Test
    void rejectsReversedExplicitDates() {
        org.junit.jupiter.api.Assertions.assertThrows(
                com.brainapp.chatbrain.exception.InvalidParameterException.class,
                () -> resolver.resolve("2026-11-10 through 2026-11-03 spending"));
    }
}
