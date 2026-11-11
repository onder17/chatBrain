package com.brainapp.chatbrain.chat;

import com.brainapp.chatbrain.ai.FinancialIntent;
import com.brainapp.chatbrain.dto.response.*;
import com.brainapp.chatbrain.financial.service.FinancialService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@Service
public class FinancialQueryEngine {
    private final FinancialService finance;
    private final FinancialDataAdapter adapter;

    public FinancialQueryEngine(FinancialService finance, FinancialDataAdapter adapter) {
        this.finance = finance;
        this.adapter = adapter;
    }

    public Result execute(Long userId, FinancialIntent intent, FinancialPeriodResolver.PeriodRange period) {
        LocalDate start = period.from(), end = period.to();
        Object dto;
        String answer;
        switch (intent) {
            case ACCOUNT_BALANCE -> {
                AccountBalanceResponse r = finance.getAccountBalances(userId, null);
                dto = r;
                answer = "Toplam banka hesap bakiyen " + money(r.totalBalance()) + " ₺. " + r.accountCount() + " aktif hesap bulunuyor.";
            }
            case CARD_DEBT -> {
                CreditCardResponse r = finance.getCreditCards(userId, null);
                dto = r;
                answer = "Kredi kartı toplam borcun " + money(r.totalDebt()) + " ₺. Kullanılabilir toplam limitin " + money(r.totalAvailableLimit()) + " ₺.";
            }
            case UPCOMING_BILLS -> {
                BillResponse r = finance.getBills(userId, "UPCOMING");
                dto = r;
                answer = "Önümüzdeki 14 gün içinde vadesi gelen " + r.count() + " faturanın toplamı " + money(r.totalAmount()) + " ₺.";
            }
            case OVERDUE_BILLS -> {
                BillResponse r = finance.getBills(userId, "OVERDUE");
                dto = r;
                answer = r.count() + " vadesi geçmiş fatura bulunuyor; toplam tutarı " + money(r.totalAmount()) + " ₺.";
            }
            case SPENDING_CATEGORIES -> {
                SpendingCategoryResponse r = finance.getSpendingCategories(userId, start, end, null);
                dto = r;
                String top = r.categories().isEmpty() ? "" : " En yüksek kategori " + r.categories().get(0).categoryName() + ": " + money(r.categories().get(0).total()) + " ₺.";
                answer = period.label() + " döneminde toplam harcama " + money(r.totalExpense()) + " ₺." + top;
            }
            case TRANSACTIONS -> {
                TransactionResponse r = finance.getTransactions(userId, null, null, start, end, 10);
                dto = r;
                answer = "Son " + r.returnedCount() + " işlem listelendi.";
            }
            case MONTHLY_SUMMARY -> {
                FinancialSummaryResponse r = finance.getFinancialSummary(userId, start, end);
                dto = r;
                answer = period.label() + " döneminde toplam harcama " + money(r.totalExpense()) + " ₺, toplam gelir " + money(r.totalIncome()) + " ₺ ve net fark " + money(r.net()) + " ₺.";
            }
            default -> { return new Result(null, null, "Bu demo sürümünde banka bakiyesi, kart borcu, faturalar ve harcama özetleri hakkında yardımcı olabilirim."); }
        }
        return new Result(adapter.adapt(dto, period.label()), intent.name(), answer);
    }

    private String money(java.math.BigDecimal amount) {
        return new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.forLanguageTag("tr-TR"))).format(amount);
    }
    public record Result(Map<String, Object> data, String intent, String verifiedAnswer) {}
}
