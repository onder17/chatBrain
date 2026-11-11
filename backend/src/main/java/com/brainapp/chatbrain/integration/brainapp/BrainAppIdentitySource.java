package com.brainapp.chatbrain.integration.brainapp;

import java.security.Principal;

/** BrainApp implementation maps its authenticated principal to its own financial user key. */
public interface BrainAppIdentitySource {
    Long resolveFinancialUserId(Principal principal);
}
