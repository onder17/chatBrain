package com.brainapp.chatbrain.controller;

import com.brainapp.chatbrain.dto.response.*;
import com.brainapp.chatbrain.ai.CurrentUserProvider;
import com.brainapp.chatbrain.exception.InvalidParameterException;
import com.brainapp.chatbrain.financial.model.CreditCard;
import com.brainapp.chatbrain.financial.model.CreditCardProduct;
import com.brainapp.chatbrain.financial.service.FinancialService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/financial")
public class FinancialController {

    private final FinancialService financialService;
    private final CurrentUserProvider currentUser;

    public FinancialController(FinancialService financialService, CurrentUserProvider currentUser) {
        this.financialService = financialService;
        this.currentUser = currentUser;
    }

    private Long userId(jakarta.servlet.http.HttpServletRequest request) { return currentUser.userId(request); }

    private void validateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new InvalidParameterException("startDate must be on or before endDate");
        }
    }

    @GetMapping("/banks")
    public ResponseEntity<List<BankResponse>> getBanks() {
        return ResponseEntity.ok(financialService.getBanks());
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<BankAccountResponse>> getAccounts(
            jakarta.servlet.http.HttpServletRequest request,
            @RequestParam(required = false) String bank
    ) {
        return ResponseEntity.ok(financialService.getAccounts(userId(request), bank));
    }

    @GetMapping("/accounts/balances")
    public ResponseEntity<AccountBalanceResponse> getAccountBalances(
            jakarta.servlet.http.HttpServletRequest request,
            @RequestParam(required = false) String bank
    ) {
        return ResponseEntity.ok(financialService.getAccountBalances(userId(request), bank));
    }

    @GetMapping("/credit-cards")
    public ResponseEntity<CreditCardResponse> getCreditCards(
            jakarta.servlet.http.HttpServletRequest request,
            @RequestParam(required = false) String filter
    ) {
        return ResponseEntity.ok(financialService.getCreditCards(userId(request), filter));
    }

    @GetMapping("/credit-cards/catalogue")
    public ResponseEntity<List<CreditCardProduct>> getCreditCardsCatalogue() {
        return ResponseEntity.ok(financialService.getAllCreditCardsCatalogue());
    }

    @GetMapping("/bills")
    public ResponseEntity<BillResponse> getBills(
            jakarta.servlet.http.HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "ALL") String status
    ) {
        if (!List.of("ALL", "UNPAID", "PAID", "UPCOMING", "OVERDUE").contains(status.toUpperCase(java.util.Locale.ROOT)))
            throw new InvalidParameterException("status must be ALL, UNPAID, PAID, UPCOMING, or OVERDUE");
        return ResponseEntity.ok(financialService.getBills(userId(request), status));
    }

    @GetMapping("/transactions")
    public ResponseEntity<TransactionResponse> getTransactions(
            jakarta.servlet.http.HttpServletRequest request,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        validateRange(startDate, endDate);
        if (limit < 1 || limit > 50) throw new InvalidParameterException("limit must be between 1 and 50");
        if (type != null && !List.of("INCOME", "EXPENSE", "TRANSFER").contains(type.toUpperCase(java.util.Locale.ROOT)))
            throw new InvalidParameterException("type must be INCOME, EXPENSE, or TRANSFER");
        return ResponseEntity.ok(financialService.getTransactions(
                userId(request), type, category, startDate, endDate, limit
        ));
    }

    @GetMapping("/spending-categories")
    public ResponseEntity<SpendingCategoryResponse> getSpendingCategories(
            jakarta.servlet.http.HttpServletRequest request,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String focusCategory
    ) {
        validateRange(startDate, endDate);
        return ResponseEntity.ok(financialService.getSpendingCategories(
                userId(request), startDate, endDate, focusCategory
        ));
    }

    @GetMapping("/summary")
    public ResponseEntity<FinancialSummaryResponse> getFinancialSummary(
            jakarta.servlet.http.HttpServletRequest request,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        validateRange(startDate, endDate);
        return ResponseEntity.ok(financialService.getFinancialSummary(
                userId(request), startDate, endDate
        ));
    }
}
