package com.brainapp.chatbrain.ai;

import com.brainapp.chatbrain.chat.ChatOrchestrator;
import com.brainapp.chatbrain.chat.ChatSessionContextStore;
import com.brainapp.chatbrain.chat.FinancialPeriodResolver;
import com.brainapp.chatbrain.chat.FinancialQueryEngine;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChatOrchestratorReliabilityTest {
    private ChatOrchestrator orchestrator(OllamaClient ollama, FinancialQueryEngine engine) {
        return new ChatOrchestrator(ollama, engine, new FinancialPeriodResolver("2026-11-15"),
                new ChatSessionContextStore(), true, "qwen3:8b");
    }

    private FinancialQueryEngine.Result cardDebt() {
        return new FinancialQueryEngine.Result(Map.of("totalDebt", new BigDecimal("20500.75")),
                "CARD_DEBT", "Kredi kartı toplam borcun 20.500,75 TL.");
    }

    @Test
    void unavailableModelAndTimeoutKeepVerifiedTemplateAnswer() {
        for (String reason : new String[]{"Ollama unavailable", "Read timed out"}) {
            OllamaClient ollama = mock(OllamaClient.class);
            when(ollama.complete(anyString(), anyString())).thenThrow(new OllamaClient.AiUnavailableException(reason));
            FinancialQueryEngine engine = mock(FinancialQueryEngine.class);
            when(engine.execute(eq(1L), eq(FinancialIntent.CARD_DEBT), any())).thenReturn(cardDebt());

            var response = orchestrator(ollama, engine).handle(1L, "fallback-test", "Kredi kartı borcum ne kadar?");
            assertEquals("TEMPLATE", response.assistantMessage().meta().answerSource());
            assertTrue(response.assistantMessage().meta().degraded());
            assertEquals(cardDebt().verifiedAnswer(), response.assistantMessage().content());
            verify(ollama, times(2)).complete(anyString(), anyString());
        }
    }

    @Test
    void malformedOrUnsupportedModelAnswerFallsBackToVerifiedTemplate() {
        for (String invalid : new String[]{"not json", "{\"answer\":\"Borç 999 TL.\",\"claims\":[\"CARD_DEBT_TOTAL\"],\"entities\":[]}",
                "{\"answer\":\"Borç 20.500,75 TL.\",\"claims\":[\"INVESTMENT_ADVICE\"],\"entities\":[]}"}) {
            OllamaClient ollama = mock(OllamaClient.class);
            when(ollama.complete(anyString(), anyString())).thenReturn("CARD_DEBT", invalid);
            FinancialQueryEngine engine = mock(FinancialQueryEngine.class);
            when(engine.execute(eq(1L), eq(FinancialIntent.CARD_DEBT), any())).thenReturn(cardDebt());

            var response = orchestrator(ollama, engine).handle(1L, "invalid-model-test", "Kredi kartı borcum ne kadar?");
            assertEquals("TEMPLATE", response.assistantMessage().meta().answerSource());
            assertEquals(cardDebt().verifiedAnswer(), response.assistantMessage().content());
        }
    }

    @Test
    void supportedStructuredAnswerCanBeUsed() {
        OllamaClient ollama = mock(OllamaClient.class);
        when(ollama.complete(anyString(), anyString())).thenReturn("CARD_DEBT",
                "{\"answer\":\"Kredi kartı borcunuz 20.500,75 TL.\",\"claims\":[\"CARD_DEBT_TOTAL\"],\"entities\":[]}");
        FinancialQueryEngine engine = mock(FinancialQueryEngine.class);
        when(engine.execute(eq(1L), eq(FinancialIntent.CARD_DEBT), any())).thenReturn(cardDebt());

        var response = orchestrator(ollama, engine).handle(1L, "valid-model-test", "Kredi kartı borcum ne kadar?");
        assertEquals("LLM", response.assistantMessage().meta().answerSource());
        assertEquals("Kredi kartı borcunuz 20.500,75 TL.", response.assistantMessage().content());
    }
}
