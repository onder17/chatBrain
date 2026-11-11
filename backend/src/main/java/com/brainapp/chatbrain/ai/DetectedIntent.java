package com.brainapp.chatbrain.ai;

public record DetectedIntent(FinancialIntent intent, String source, double confidence) {}
