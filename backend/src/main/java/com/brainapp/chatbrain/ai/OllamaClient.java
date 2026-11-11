package com.brainapp.chatbrain.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class OllamaClient {
    private final RestTemplate http;
    private final String endpoint;
    private final String model;
    private final boolean enabled;
    private final AtomicLong unavailableUntilNanos = new AtomicLong();

    public OllamaClient(
            @Value("${chatbrain.ai.ollama-url:http://127.0.0.1:11434}") String endpoint,
            @Value("${chatbrain.ai.model:qwen3:8b}") String model,
            @Value("${chatbrain.ai.enabled:true}") boolean enabled,
            @Value("${chatbrain.ai.timeout-ms:12000}") int timeoutMs
    ) {
        URI uri = URI.create(endpoint);
        if (!"http".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null ||
                !(uri.getHost().equalsIgnoreCase("127.0.0.1") || uri.getHost().equalsIgnoreCase("localhost") || uri.getHost().equalsIgnoreCase("::1"))) {
            throw new IllegalArgumentException("Ollama must be bound to a local loopback address.");
        }
        this.endpoint = endpoint.replaceAll("/+$", "");
        this.model = model;
        this.enabled = enabled;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        int safeTimeout = Math.max(500, Math.min(timeoutMs, 30000));
        factory.setConnectTimeout(Math.min(safeTimeout, 3000));
        factory.setReadTimeout(safeTimeout);
        this.http = new RestTemplate(factory);
    }

    public String complete(String system, String prompt) {
        if (!enabled) throw new AiUnavailableException("Local AI is disabled");
        if (System.nanoTime() < unavailableUntilNanos.get()) throw new AiUnavailableException("Local AI is in cooldown");
        try {
            Map<?, ?> response = http.postForObject(endpoint + "/api/chat", Map.of(
                    "model", model,
                    "stream", false,
                    // Qwen3 otherwise spends its token budget in a separate thinking field,
                    // leaving message.content empty for constrained JSON/classification prompts.
                    "think", false,
                    "messages", List.of(
                            Map.of("role", "system", "content", system),
                            Map.of("role", "user", "content", prompt)
                    ),
                    "options", Map.of("temperature", 0.0, "num_predict", 180)
            ), Map.class);
            if (response == null || !(response.get("message") instanceof Map<?, ?> message) || !(message.get("content") instanceof String content) || content.isBlank())
                throw new AiUnavailableException("Ollama returned an empty response");
            unavailableUntilNanos.set(0);
            return content.replaceAll("(?s)<think>.*?</think>", "").trim();
        } catch (RestClientException ex) {
            unavailableUntilNanos.set(System.nanoTime() + 15_000_000_000L);
            throw new AiUnavailableException("Local AI is unavailable", ex);
        }
    }

    public String model() { return model; }

    public static class AiUnavailableException extends RuntimeException {
        public AiUnavailableException(String message) { super(message); }
        public AiUnavailableException(String message, Throwable cause) { super(message, cause); }
    }
}
