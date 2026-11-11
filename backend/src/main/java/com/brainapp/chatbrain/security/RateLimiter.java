package com.brainapp.chatbrain.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimiter {
    private record Window(long startMillis, AtomicInteger requests) {}
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    private final int maxRequests;
    private final Clock clock = Clock.systemUTC();

    public RateLimiter(@Value("${chatbrain.security.chat-requests-per-minute:30}") int maxRequests) {
        this.maxRequests = Math.max(1, maxRequests);
    }

    public void check(String key) {
        long now = clock.millis();
        Window window = windows.compute(key, (ignored, current) ->
                current == null || now - current.startMillis() >= 60_000
                        ? new Window(now, new AtomicInteger()) : current);
        if (window.requests().incrementAndGet() > maxRequests)
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Chat request limit exceeded");
        if (windows.size() > 10_000) windows.entrySet().removeIf(e -> now - e.getValue().startMillis() > 60_000);
    }
}
