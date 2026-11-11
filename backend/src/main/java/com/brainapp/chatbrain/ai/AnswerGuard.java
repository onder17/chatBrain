package com.brainapp.chatbrain.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Validates structured model rewrites against facts calculated by the backend. */
public class AnswerGuard {
    private static final Pattern NUMBER = Pattern.compile("(?<![\\p{L}])[-+]?\\d[\\d.,]*(?![\\p{L}])");
    private static final Pattern DATE = Pattern.compile("\\b(?:\\d{4}-\\d{2}-\\d{2}|\\d{1,2}\\s+[\\p{L}]+\\s+\\d{4})\\b", Pattern.CASE_INSENSITIVE);
    private static final ObjectMapper JSON = new ObjectMapper();

    public boolean acceptsStructured(String candidateJson, String verifiedFacts, String intent, Map<String, Object> data) {
        if (candidateJson == null || verifiedFacts == null || intent == null || data == null) return false;
        try {
            JsonNode root = JSON.readTree(candidateJson);
            JsonNode answerNode = root.get("answer");
            JsonNode claims = root.get("claims");
            JsonNode entities = root.get("entities");
            if (answerNode == null || !answerNode.isTextual() || claims == null || !claims.isArray() ||
                    claims.isEmpty() || entities == null || !entities.isArray()) return false;
            String answer = answerNode.asText();
            if (!accepts(answer, verifiedFacts)) return false;
            Set<String> allowedClaims = allowedClaims(intent, data);
            for (JsonNode claim : claims) {
                if (!claim.isTextual() || !allowedClaims.contains(claim.asText())) return false;
            }
            String normalizedEvidence = normalize(verifiedFacts);
            Set<String> groundedEntities = new HashSet<>();
            collectEntities(data, groundedEntities);
            for (JsonNode entity : entities) {
                if (!entity.isTextual() || entity.asText().isBlank()) return false;
                String value = normalize(entity.asText());
                if (!groundedEntities.contains(value) && !normalizedEvidence.contains(value)) return false;
            }
            if (!everySentenceHasVerifiedAnchor(answer, groundedEntities)) return false;
            if (!namedTokensAreGrounded(answer, normalizedEvidence)) return false;
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public String structuredAnswer(String candidateJson) {
        try {
            JsonNode answer = JSON.readTree(candidateJson).get("answer");
            return answer != null && answer.isTextual() ? answer.asText() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    public boolean accepts(String answer, String verifiedText) {
        if (answer == null || verifiedText == null || answer.isBlank() || answer.length() > 1200 ||
                answer.matches("(?is).*https?://.*") || answer.matches("(?is).*<\\s*/?\\s*[a-z!].*")) return false;
        if (!numbers(verifiedText).containsAll(numbers(answer))) return false;
        return strings(DATE, verifiedText).containsAll(strings(DATE, answer));
    }

    private Set<String> allowedClaims(String intent, Map<String, Object> data) {
        return switch (intent) {
            case "ACCOUNT_BALANCE" -> data.get("banks") instanceof List<?> banks && !banks.isEmpty()
                    ? Set.of("BALANCE_TOTAL", "ACCOUNT_COUNT", "BANK_BREAKDOWN")
                    : Set.of("BALANCE_TOTAL", "ACCOUNT_COUNT");
            case "CARD_DEBT" -> Set.of("CARD_DEBT_TOTAL", "CARD_LIMIT_TOTAL", "CARD_COUNT");
            case "UPCOMING_BILLS" -> Set.of("UPCOMING_BILLS_TOTAL", "UPCOMING_BILL_COUNT");
            case "OVERDUE_BILLS" -> Set.of("OVERDUE_BILLS_TOTAL", "OVERDUE_BILL_COUNT");
            case "SPENDING_CATEGORIES" -> hasItems(data.get("categories"))
                    ? Set.of("SPENDING_TOTAL", "TOP_CATEGORY") : Set.of("SPENDING_TOTAL");
            case "MONTHLY_SUMMARY" -> hasItems(data.get("topExpenseCategories"))
                    ? Set.of("INCOME_TOTAL", "SPENDING_TOTAL", "NET_TOTAL", "TOP_CATEGORY")
                    : Set.of("INCOME_TOTAL", "SPENDING_TOTAL", "NET_TOTAL");
            case "TRANSACTIONS" -> Set.of("TRANSACTION_COUNT", "TRANSACTION_LIST");
            default -> Set.of();
        };
    }

    private boolean hasItems(Object value) { return value instanceof List<?> list && !list.isEmpty(); }

    private void collectEntities(Object value, Set<String> entities) {
        if (value instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String key && Set.of("bankName", "providerName", "categoryName", "merchant", "cardName", "productName", "accountName").contains(key)
                        && entry.getValue() instanceof String text && !text.isBlank()) entities.add(normalize(text));
                else collectEntities(entry.getValue(), entities);
            }
        } else if (value instanceof List<?> list) {
            list.forEach(item -> collectEntities(item, entities));
        }
    }

    private Set<String> numbers(String text) { return strings(NUMBER, text); }
    private Set<String> strings(Pattern pattern, String text) {
        Set<String> found = new HashSet<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) found.add(matcher.group().toLowerCase(Locale.ROOT));
        return found;
    }
    private String normalize(String text) {
        return text.toLowerCase(Locale.forLanguageTag("tr-TR")).replaceAll("\\s+", " ").trim();
    }

    private boolean namedTokensAreGrounded(String answer, String normalizedEvidence) {
        Set<String> ordinarySentenceStarts = Set.of("toplam", "kredi", "banka", "hesap", "hesabınız", "hesabiniz",
                "bakiye", "bakiyeniz", "kart", "borcunuz", "borç", "borc", "önümüzdeki", "onumuzdeki",
                "yaklaşan", "yaklasan", "gecikmiş", "gecikmis", "bu", "son", "en", "harcama", "gelir",
                "gider", "net", "fatura", "faturanız", "faturalarınız", "islemleriniz", "işlemleriniz", "tl");
        boolean sentenceStart = true;
        for (String raw : answer.split("\\s+")) {
            String token = raw.replaceAll("^[^\\p{L}]+|[^\\p{L}]+$", "");
            if (token.isBlank()) continue;
            String normalized = normalize(token);
            if (Character.isUpperCase(token.codePointAt(0)) &&
                    !(sentenceStart && ordinarySentenceStarts.contains(normalized)) &&
                    !normalizedEvidence.contains(normalized) &&
                    !(normalized.equals("tl") && normalizedEvidence.contains("try"))) return false;
            sentenceStart = raw.matches(".*[.!?][\\\"'”’)]*$");
        }
        return true;
    }

    private boolean everySentenceHasVerifiedAnchor(String answer, Set<String> entities) {
        for (String sentence : answer.split("(?<=[.!?])\\s+(?=[\\p{Lu}])")) {
            if (!numbers(sentence).isEmpty() || !strings(DATE, sentence).isEmpty()) continue;
            String normalizedSentence = normalize(sentence);
            boolean hasEntity = entities.stream().anyMatch(entity -> normalizedSentence.contains(entity));
            if (!hasEntity) return false;
        }
        return true;
    }
}
