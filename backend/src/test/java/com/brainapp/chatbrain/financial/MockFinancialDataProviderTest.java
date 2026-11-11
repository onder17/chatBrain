package com.brainapp.chatbrain.financial;

import com.brainapp.chatbrain.dto.response.*;
import com.brainapp.chatbrain.financial.model.Bank;
import com.brainapp.chatbrain.financial.model.BankAccount;
import com.brainapp.chatbrain.financial.model.CreditCardProduct;
import com.brainapp.chatbrain.financial.provider.MockFinancialDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MockFinancialDataProviderTest {

    private MockFinancialDataProvider provider;

    @BeforeEach
    void setUp() {
        provider = new MockFinancialDataProvider();
    }

    @Test
    @DisplayName("Should contain exactly 10 simulated partner banks")
    void testBanksCount() {
        List<Bank> banks = provider.getBanks();
        assertNotNull(banks);
        assertEquals(10, banks.size(), "Should have exactly 10 banks");
    }

    @Test
    @DisplayName("Catalogue contains product-only cards while personal cards remain isolated")
    void testCreditCardsCatalogueAndIsolation() {
        List<CreditCardProduct> products = provider.getAllCreditCardsCatalogue();
        assertEquals(121, products.size());
        assertEquals(3, provider.getCreditCards(1L, null).cardCount());
        assertEquals(0, provider.getCreditCards(2L, null).cardCount());
        assertEquals(0, provider.getCreditCards(2L, null).totalDebt().compareTo(BigDecimal.ZERO));
        assertNotNull(products.get(0).productName());
    }

    @Test
    @DisplayName("User 1 accounts balance must equal 145.650,75 ₺")
    void testAccountBalances() {
        AccountBalanceResponse response = provider.getAccountBalances(1L, null);
        assertNotNull(response);
        assertEquals(3, response.accountCount(), "User 1 should have 3 accounts");
        assertEquals(new BigDecimal("145650.75"), response.totalBalance(), "Total balance must be 145650.75 ₺");
    }

    @Test
    @DisplayName("User 1 active cards must total 20.500,75 ₺ debt and 125.000,00 ₺ limit")
    void testCreditCardsSummary() {
        CreditCardResponse response = provider.getCreditCards(1L, null);
        assertNotNull(response);
        assertEquals(3, response.cardCount(), "User 1 should have 3 cards");
        assertEquals(new BigDecimal("20500.75"), response.totalDebt(), "Total debt must be 20500.75 ₺");
        assertEquals(new BigDecimal("125000.00"), response.totalLimit(), "Total limit must be 125000.00 ₺");
        assertEquals(new BigDecimal("104499.25"), response.totalAvailableLimit(), "Total available limit must be 104499.25 ₺");
        assertEquals(new BigDecimal("13970.00"), response.totalStatementBalance(), "Statement balance must be 13970.00 ₺");
    }

    @Test
    @DisplayName("Unpaid bills should total 3.428,05 ₺ with 1 overdue and 3 upcoming")
    void testBills() {
        BillResponse unpaidResp = provider.getBills(1L, "UNPAID");
        assertNotNull(unpaidResp);
        assertEquals(4, unpaidResp.count(), "Should have 4 unpaid bills");
        assertEquals(new BigDecimal("3428.05"), unpaidResp.totalAmount(), "Unpaid bills total must be 3428.05 ₺");
        assertEquals(1, unpaidResp.overdueCount(), "Must have exactly 1 overdue bill");
        assertEquals(new BigDecimal("649.90"), unpaidResp.overdueAmount(), "Overdue bill amount must be 649.90 ₺");

        BillResponse upcomingResp = provider.getBills(1L, "UPCOMING");
        assertEquals(3, upcomingResp.count(), "Must have 3 upcoming bills within 14 days");
        assertEquals(new BigDecimal("2778.15"), upcomingResp.totalAmount(), "Upcoming bills must total 2778.15 ₺");
    }

    @Test
    void overdueIsSeparateAndDueTodayIsNotOverdue() {
        var overdue = provider.getBills(1L, "OVERDUE");
        assertEquals(1, overdue.count());
        assertEquals(new BigDecimal("649.90"), overdue.totalAmount());
        var dueToday = new com.brainapp.chatbrain.financial.model.Bill(9L, 1L, "Demo", "OTHER",
                BigDecimal.ONE, "TRY", MockFinancialDataProvider.REFERENCE_TODAY, "UNPAID", null);
        assertTrue(dueToday.isUpcoming(MockFinancialDataProvider.REFERENCE_TODAY, 14));
        assertFalse(dueToday.isOverdue(MockFinancialDataProvider.REFERENCE_TODAY));
    }

    @Test
    void periodLabelsFollowCustomRangesAndLeapDay() {
        var leap = provider.getFinancialSummary(1L, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29));
        assertEquals("Şubat 2024", leap.periodLabel());
        var crossYear = provider.getFinancialSummary(1L, LocalDate.of(2025, 12, 20), LocalDate.of(2026, 1, 10));
        assertEquals("20 Aralık 2025 – 10 Ocak 2026", crossYear.periodLabel());
        var transactions = provider.getTransactions(1L, null, null, null, null, 10);
        assertEquals("Tüm dönem", transactions.periodLabel());
    }

    @Test
    @DisplayName("November 2026 financial summary must match seed: Income 45.500,00 ₺, Expense 21.245,49 ₺, Net 24.254,51 ₺")
    void testFinancialSummary() {
        FinancialSummaryResponse summary = provider.getFinancialSummary(1L, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30));
        assertNotNull(summary);
        assertEquals(new BigDecimal("45500.00"), summary.totalIncome());
        assertEquals(new BigDecimal("21245.49"), summary.totalExpense());
        assertEquals(new BigDecimal("24254.51"), summary.net());
        assertEquals(new BigDecimal("53.3"), summary.savingsRatePercent());
    }
}
