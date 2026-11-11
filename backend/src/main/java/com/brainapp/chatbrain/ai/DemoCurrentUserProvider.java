package com.brainapp.chatbrain.ai;

import com.brainapp.chatbrain.exception.InvalidParameterException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;

/** Controlled identity resolver for standalone development only. */
@Component
@Profile("!brainapp")
public class DemoCurrentUserProvider implements CurrentUserProvider {
    private final Long defaultUserId;
    private final Set<Long> allowedDemoUserIds;
    private final boolean demoHeaderEnabled;

    public DemoCurrentUserProvider(
            @Value("${chatbrain.finance.demo-user-id:1}") Long defaultUserId,
            @Value("${chatbrain.security.allowed-demo-user-ids:1}") Set<Long> allowedDemoUserIds,
            @Value("${chatbrain.security.demo-header-enabled:true}") boolean demoHeaderEnabled
    ) {
        this.defaultUserId = defaultUserId;
        this.allowedDemoUserIds = allowedDemoUserIds;
        this.demoHeaderEnabled = demoHeaderEnabled;
    }

    @Override
    public Long userId(HttpServletRequest request) {
        String demoHeader = request.getHeader("X-Demo-User-Id");
        if (!demoHeaderEnabled || demoHeader == null || demoHeader.isBlank()) return defaultUserId;
        try {
            Long requested = Long.valueOf(demoHeader);
            if (allowedDemoUserIds.contains(requested)) return requested;
        } catch (NumberFormatException ignored) {
            throw new InvalidParameterException("Invalid demo user identity");
        }
        throw new InvalidParameterException("Demo user identity is not enabled");
    }
}
