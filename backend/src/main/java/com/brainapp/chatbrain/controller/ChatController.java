package com.brainapp.chatbrain.controller;

import com.brainapp.chatbrain.ai.CurrentUserProvider;
import com.brainapp.chatbrain.chat.ChatDtos.SendMessageRequest;
import com.brainapp.chatbrain.chat.ChatDtos.SendMessageResponse;
import com.brainapp.chatbrain.chat.ChatOrchestrator;
import com.brainapp.chatbrain.exception.InvalidParameterException;
import com.brainapp.chatbrain.security.RateLimiter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    private static final Pattern SESSION_ID = Pattern.compile("[A-Za-z0-9_-]{1,80}");
    private static final List<String> SUGGESTIONS = List.of(
            "Toplam banka bakiyem ne kadar?", "Kredi kartı borcum ne kadar?",
            "Bu ay toplam ne kadar harcadım?", "Yaklaşan faturalarım neler?",
            "En çok hangi kategoride harcama yaptım?", "Son işlemlerimi göster.");
    private final ChatOrchestrator orchestrator;
    private final CurrentUserProvider currentUser;
    private final RateLimiter rateLimiter;

    public ChatController(ChatOrchestrator orchestrator, CurrentUserProvider currentUser, RateLimiter rateLimiter) {
        this.orchestrator = orchestrator;
        this.currentUser = currentUser;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<SendMessageResponse> send(
            @PathVariable String sessionId,
            jakarta.servlet.http.HttpServletRequest servletRequest,
            @Valid @RequestBody SendMessageRequest request
    ) {
        if (!SESSION_ID.matcher(sessionId).matches()) throw new InvalidParameterException("Invalid session id");
        Long userId = currentUser.userId(servletRequest);
        rateLimiter.check(String.valueOf(userId));
        return ResponseEntity.ok(orchestrator.handle(userId, sessionId, request.content().trim()));
    }

    @GetMapping("/suggestions")
    public List<Map<String, String>> suggestions() {
        return java.util.stream.IntStream.range(0, SUGGESTIONS.size())
                .mapToObj(index -> Map.of("id", String.valueOf(index + 1), "text", SUGGESTIONS.get(index)))
                .toList();
    }
}
