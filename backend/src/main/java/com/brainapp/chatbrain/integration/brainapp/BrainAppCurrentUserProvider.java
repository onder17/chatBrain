package com.brainapp.chatbrain.integration.brainapp;

import com.brainapp.chatbrain.ai.CurrentUserProvider;
import com.brainapp.chatbrain.exception.ChatBrainException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Profile("brainapp")
public class BrainAppCurrentUserProvider implements CurrentUserProvider {
    private final BrainAppIdentitySource identitySource;
    public BrainAppCurrentUserProvider(BrainAppIdentitySource identitySource) { this.identitySource = identitySource; }

    @Override
    public Long userId(HttpServletRequest request) {
        if (request.getUserPrincipal() == null) throw new UnauthenticatedRequestException();
        Long userId = identitySource.resolveFinancialUserId(request.getUserPrincipal());
        if (userId == null) throw new UnauthenticatedRequestException();
        return userId;
    }

    public static class UnauthenticatedRequestException extends ChatBrainException {
        public UnauthenticatedRequestException() { super(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "Authentication is required."); }
    }
}
