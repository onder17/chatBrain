package com.brainapp.chatbrain.chat;

import com.brainapp.chatbrain.ai.FinancialIntent;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/** Keeps only safe routing context, scoped by user and session. */
@Component
public class ChatSessionContextStore {
    private static final Duration TTL = Duration.ofMinutes(30);
    private static final int MAX_SESSIONS = 5_000;
    private final ConcurrentHashMap<String, SessionContext> sessions = new ConcurrentHashMap<>();

    public SessionContext get(Long userId, String sessionId) {
        String key = key(userId, sessionId);
        SessionContext value = sessions.get(key);
        if (value != null && value.updatedAt().plus(TTL).isBefore(Instant.now())) {
            sessions.remove(key, value);
            return null;
        }
        return value;
    }

    public void put(Long userId, String sessionId, FinancialIntent intent,
                    FinancialPeriodResolver.PeriodRange period) {
        Instant now = Instant.now();
        if (sessions.size() >= MAX_SESSIONS) {
            sessions.entrySet().removeIf(entry -> entry.getValue().updatedAt().plus(TTL).isBefore(now));
            if (sessions.size() >= MAX_SESSIONS) sessions.entrySet().stream()
                    .min(java.util.Comparator.comparing(entry -> entry.getValue().updatedAt()))
                    .ifPresent(oldest -> sessions.remove(oldest.getKey(), oldest.getValue()));
        }
        sessions.put(key(userId, sessionId), new SessionContext(intent, period, now));
    }

    private String key(Long userId, String sessionId) { return userId + ":" + sessionId; }

    public record SessionContext(FinancialIntent intent, FinancialPeriodResolver.PeriodRange period,
                                 Instant updatedAt) {}
}
