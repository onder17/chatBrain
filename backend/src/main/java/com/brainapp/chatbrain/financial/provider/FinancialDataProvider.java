package com.brainapp.chatbrain.financial.provider;

import com.brainapp.chatbrain.dto.response.*;
import com.brainapp.chatbrain.financial.model.Bank;
import com.brainapp.chatbrain.financial.model.BankAccount;
import com.brainapp.chatbrain.financial.model.Bill;
import com.brainapp.chatbrain.financial.model.CreditCard;
import com.brainapp.chatbrain.financial.model.CreditCardProduct;
import com.brainapp.chatbrain.financial.model.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface FinancialDataProvider {

    /**
     * Retrieves all partner banks.
     */
    List<Bank> getBanks();

    /**
     * Retrieves bank accounts for user, with optional bank name filter.
     */
    List<BankAccount> getAccounts(Long userId, String bankFilter);

    /**
     * Retrieves aggregated account balance response.
     */
    AccountBalanceResponse getAccountBalances(Long userId, String bankFilter);

    /**
     * Retrieves credit cards with summary metrics.
     */
    CreditCardResponse getCreditCards(Long userId, String cardOrBankFilter);

    /**
     * Retrieves all 121 credit card catalogue entries.
     */
    List<CreditCardProduct> getAllCreditCardsCatalogue();

    /**
     * Retrieves user bills with status filter (ALL, UNPAID, PAID, UPCOMING, OVERDUE).
     */
    BillResponse getBills(Long userId, String statusFilter);

    /**
     * Retrieves transactions with filtering options.
     */
    TransactionResponse getTransactions(
            Long userId,
            String typeFilter,
            String categoryFilter,
            LocalDate startDate,
            LocalDate endDate,
            Integer limit
    );

    /**
     * Retrieves expense breakdown by categories.
     */
    SpendingCategoryResponse getSpendingCategories(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            String focusCategory
    );

    /**
     * Retrieves income, expense and net savings summary.
     */
    FinancialSummaryResponse getFinancialSummary(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
}
