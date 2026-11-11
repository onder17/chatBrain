package com.brainapp.chatbrain.financial;

import com.brainapp.chatbrain.financial.model.CreditCardProduct;
import com.brainapp.chatbrain.financial.provider.MockFinancialDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FinancialConsistencyTest {

    private MockFinancialDataProvider provider;

    @BeforeEach
    void setUp() {
        provider = new MockFinancialDataProvider();
    }

    @Test
    @DisplayName("Catalogue records contain product fields and no personal financial data")
    void testCreditCardCatalogueIsProductOnly() {
        List<CreditCardProduct> products = provider.getAllCreditCardsCatalogue();
        assertEquals(121, products.size());
        assertTrue(products.stream().allMatch(product -> product.bankName() != null && product.productName() != null));
        assertEquals(3, provider.getCreditCards(1L, null).cardCount());
        assertEquals(0, provider.getCreditCards(2L, null).cardCount());
    }

    @Test
    @DisplayName("Transaction sums must strictly match the financial summary for November 2026")
    void testTransactionAndSummaryConsistency() {
        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 30);

        var txnResp = provider.getTransactions(1L, null, null, start, end, 100);
        var summaryResp = provider.getFinancialSummary(1L, start, end);

        assertEquals(summaryResp.totalIncome(), txnResp.totalIncome(), "Income in summary must equal transaction sum");
        assertEquals(summaryResp.totalExpense(), txnResp.totalExpense(), "Expense in summary must equal transaction sum");
        assertEquals(summaryResp.net(), summaryResp.totalIncome().subtract(summaryResp.totalExpense()), "Net must equal income - expense");
    }

    @Test
    @DisplayName("Transaction date filters include both requested endpoints")
    void testTransactionDateBoundariesAreInclusive() {
        var oneDay = provider.getTransactions(1L, null, null,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 1), 50);
        assertEquals(2, oneDay.totalCount());
        var twoDays = provider.getTransactions(1L, null, null,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 2), 50);
        assertEquals(3, twoDays.totalCount());
        assertTrue(twoDays.transactions().stream().allMatch(t ->
                !t.date().isBefore(LocalDate.of(2026, 11, 1)) && !t.date().isAfter(LocalDate.of(2026, 11, 2))));
    }

    @Test
    @DisplayName("Overdue bill calculation must strictly align with reference date (15 Nov 2026)")
    void testBillDueDateConsistency() {
        var bills = provider.getBills(1L, "ALL");
        for (var bill : bills.invoices()) {
            if ("UNPAID".equalsIgnoreCase(bill.status())) {
                boolean shouldBeOverdue = bill.dueDate().isBefore(MockFinancialDataProvider.REFERENCE_TODAY);
                assertEquals(shouldBeOverdue, bill.overdue(), "Overdue status inconsistent for bill " + bill.id());
            } else {
                assertFalse(bill.overdue(), "Paid bill cannot be overdue");
            }
        }
    }
}
