package com.brainapp.chatbrain.integration.brainapp;

import com.brainapp.chatbrain.dto.response.*;
import com.brainapp.chatbrain.financial.model.*;

import java.time.LocalDate;
import java.util.List;

/** Contract implemented by BrainApp using its authenticated services and existing data store. */
public interface BrainAppFinancialDataSource {
    List<Bank> getBanks();
    List<BankAccount> getAccounts(Long userId, String bankFilter);
    AccountBalanceResponse getAccountBalances(Long userId, String bankFilter);
    CreditCardResponse getCreditCards(Long userId, String cardOrBankFilter);
    List<CreditCardProduct> getAllCreditCardsCatalogue();
    BillResponse getBills(Long userId, String statusFilter);
    TransactionResponse getTransactions(Long userId, String typeFilter, String categoryFilter,
                                        LocalDate startDate, LocalDate endDate, Integer limit);
    SpendingCategoryResponse getSpendingCategories(Long userId, LocalDate startDate, LocalDate endDate,
                                                    String focusCategory);
    FinancialSummaryResponse getFinancialSummary(Long userId, LocalDate startDate, LocalDate endDate);
}
