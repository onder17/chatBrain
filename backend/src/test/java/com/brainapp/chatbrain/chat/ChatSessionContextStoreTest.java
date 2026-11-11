package com.brainapp.chatbrain.chat;

import com.brainapp.chatbrain.ai.FinancialIntent;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ChatSessionContextStoreTest {
    @Test
    void contextIsScopedByBothUserAndSessionAndStoresOnlyRoutingFields() {
        ChatSessionContextStore store = new ChatSessionContextStore();
        var period = new FinancialPeriodResolver("2026-11-15").resolve("geçen ay");
        store.put(1L, "shared-session-id", FinancialIntent.MONTHLY_SUMMARY, period);

        assertEquals(FinancialIntent.MONTHLY_SUMMARY, store.get(1L, "shared-session-id").intent());
        assertEquals(LocalDate.of(2026, 10, 1), store.get(1L, "shared-session-id").period().from());
        assertNull(store.get(2L, "shared-session-id"));
    }
}
