package com.brainapp.chatbrain.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Map;

public final class ChatDtos {
    private ChatDtos() {}
    public record SendMessageRequest(@NotBlank @Size(max = 1000) String content) {}
    public record MessageMeta(String intentSource, String answerSource, boolean degraded, String model,
                              long latencyMs, double confidence, Map<String, Object> parameters) {}
    public record ChatMessage(String id, String sessionId, String role, String content, String intent,
                              Object data, MessageMeta meta, String status, Instant createdAt) {}
    public record ChatSession(String id, String title, Instant createdAt, Instant updatedAt) {}
    public record SendMessageResponse(ChatMessage userMessage, ChatMessage assistantMessage, ChatSession session) {}
}
