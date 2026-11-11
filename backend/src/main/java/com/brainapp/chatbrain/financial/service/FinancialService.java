package com.brainapp.chatbrain.financial.service;

import com.brainapp.chatbrain.dto.response.*;
import com.brainapp.chatbrain.financial.model.Bank;
import com.brainapp.chatbrain.financial.model.BankAccount;
import com.brainapp.chatbrain.financial.model.CreditCard;
import com.brainapp.chatbrain.financial.model.CreditCardProduct;
import com.brainapp.chatbrain.financial.provider.FinancialDataProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FinancialService {

    private final FinancialDataProvider dataProvider;

    public FinancialService(FinancialDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public List<BankResponse> getBanks() {
        return dataProvider.getBanks().stream()
                .map(b -> new BankResponse(b.id(), b.code(), b.name(), b.logoUrl()))
                .toList();
    }

    public List<BankAccountResponse> getAccounts(Long userId, String bankFilter) {
        return dataProvider.getAccounts(userId, bankFilter).stream()
                .map(a -> new BankAccountResponse(
                        a.id(), a.bankId(), a.bankName(), a.accountName(),
                        a.ibanLast4(), a.accountType(), a.balance(), a.currency(), a.active()))
                .toList();
    }

    public AccountBalanceResponse getAccountBalances(Long userId, String bankFilter) {
        return dataProvider.getAccountBalances(userId, bankFilter);
    }

    public CreditCardResponse getCreditCards(Long userId, String cardOrBankFilter) {
        return dataProvider.getCreditCards(userId, cardOrBankFilter);
    }

    public List<CreditCardProduct> getAllCreditCardsCatalogue() {
        return dataProvider.getAllCreditCardsCatalogue();
    }

    public BillResponse getBills(Long userId, String statusFilter) {
        return dataProvider.getBills(userId, statusFilter);
    }

    public TransactionResponse getTransactions(
            Long userId,
            String typeFilter,
            String categoryFilter,
            LocalDate startDate,
            LocalDate endDate,
            Integer limit
    ) {
        return dataProvider.getTransactions(userId, typeFilter, categoryFilter, startDate, endDate, limit);
    }

    public SpendingCategoryResponse getSpendingCategories(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            String focusCategory
    ) {
        return dataProvider.getSpendingCategories(userId, startDate, endDate, focusCategory);
    }

    public FinancialSummaryResponse getFinancialSummary(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return dataProvider.getFinancialSummary(userId, startDate, endDate);
    }
}
