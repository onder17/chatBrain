package com.brainapp.chatbrain.ai;

import jakarta.servlet.http.HttpServletRequest;

/** Resolves the authenticated financial user for each request. */
public interface CurrentUserProvider {
    Long userId(HttpServletRequest request);
}
