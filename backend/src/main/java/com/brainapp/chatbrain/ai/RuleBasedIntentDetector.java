package com.brainapp.chatbrain.ai;

import java.text.Normalizer;
import java.util.Locale;

public class RuleBasedIntentDetector {
    public DetectedIntent detect(String text) {
        String q = normalize(text);
        if (q.contains("kredi kart") || q.contains("kart borc") || q.contains("kart limit"))
            return new DetectedIntent(FinancialIntent.CARD_DEBT, "RULES", 0.92);
        if (q.contains("gecikmis fatura") || q.contains("vadesi gecmis") || q.contains("geciken fatura"))
            return new DetectedIntent(FinancialIntent.OVERDUE_BILLS, "RULES", 0.92);
        if (q.contains("fatura") || q.contains("faturalar") || q.contains("odeme"))
            return new DetectedIntent(FinancialIntent.UPCOMING_BILLS, "RULES", 0.90);
        if ((q.contains("kategori") && (q.contains("harcama") || q.contains("harcad"))) || q.contains("en cok nereye"))
            return new DetectedIntent(FinancialIntent.SPENDING_CATEGORIES, "RULES", 0.86);
        if (q.contains("son islem") || q.contains("islemlerim") || q.contains("transaction"))
            return new DetectedIntent(FinancialIntent.TRANSACTIONS, "RULES", 0.86);
        if (q.contains("harcama") || q.contains("harcad") || q.contains("gider") || q.contains("gelir"))
            return new DetectedIntent(FinancialIntent.MONTHLY_SUMMARY, "RULES", 0.82);
        if (q.contains("bakiye") || q.contains("hesap") || q.contains("param"))
            return new DetectedIntent(FinancialIntent.ACCOUNT_BALANCE, "RULES", 0.82);
        return new DetectedIntent(FinancialIntent.UNKNOWN, "RULES", 0.0);
    }

    public static String normalize(String value) {
        String decomposed = Normalizer.normalize(value.toLowerCase(Locale.forLanguageTag("tr")), Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{M}+", "").replace('ı', 'i').replaceAll("[^a-z0-9 ]", " ").replaceAll("\\s+", " ").trim();
    }
}
