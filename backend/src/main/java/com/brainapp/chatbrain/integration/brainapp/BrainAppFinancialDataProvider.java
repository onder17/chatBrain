package com.brainapp.chatbrain.integration.brainapp;

import com.brainapp.chatbrain.dto.response.*;
import com.brainapp.chatbrain.financial.model.*;
import com.brainapp.chatbrain.financial.provider.FinancialDataProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/** Profile adapter. BrainApp supplies the source bean backed by its existing authorized services. */
@Component
@Profile("brainapp")
public class BrainAppFinancialDataProvider implements FinancialDataProvider {
    private final BrainAppFinancialDataSource source;

    public BrainAppFinancialDataProvider(BrainAppFinancialDataSource source) { this.source = source; }
    @Override public List<Bank> getBanks() { return source.getBanks(); }
    @Override public List<BankAccount> getAccounts(Long userId, String bankFilter) { return source.getAccounts(userId, bankFilter); }
    @Override public AccountBalanceResponse getAccountBalances(Long userId, String bankFilter) { return source.getAccountBalances(userId, bankFilter); }
    @Override public CreditCardResponse getCreditCards(Long userId, String filter) { return source.getCreditCards(userId, filter); }
    @Override public List<CreditCardProduct> getAllCreditCardsCatalogue() { return source.getAllCreditCardsCatalogue(); }
    @Override public BillResponse getBills(Long userId, String status) { return source.getBills(userId, status); }
    @Override public TransactionResponse getTransactions(Long userId, String type, String category, LocalDate start,
                                                        LocalDate end, Integer limit) {
        return source.getTransactions(userId, type, category, start, end, limit);
    }
    @Override public SpendingCategoryResponse getSpendingCategories(Long userId, LocalDate start, LocalDate end,
                                                                    String focus) {
        return source.getSpendingCategories(userId, start, end, focus);
    }
    @Override public FinancialSummaryResponse getFinancialSummary(Long userId, LocalDate start, LocalDate end) {
        return source.getFinancialSummary(userId, start, end);
    }
}
