package com.brainapp.chatbrain.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FinancialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/financial/banks returns 10 partner banks")
    void getBanks() throws Exception {
        mockMvc.perform(get("/api/v1/financial/banks").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].name", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/v1/financial/accounts/balances returns totalBalance 145650.75")
    void getAccountBalances() throws Exception {
        mockMvc.perform(get("/api/v1/financial/accounts/balances").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("ACCOUNT_BALANCE"))
                .andExpect(jsonPath("$.totalBalance").value("145650.75"))
                .andExpect(jsonPath("$.currency").value("TRY"))
                .andExpect(jsonPath("$.accountCount").value(3))
                .andExpect(jsonPath("$.accounts", hasSize(3)))
                .andExpect(jsonPath("$.banks", hasSize(3)));
    }

    @Test
    @DisplayName("GET /api/v1/financial/credit-cards returns active cards and totalDebt 20500.75")
    void getCreditCards() throws Exception {
        mockMvc.perform(get("/api/v1/financial/credit-cards").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("CARD_DEBT"))
                .andExpect(jsonPath("$.totalDebt").value("20500.75"))
                .andExpect(jsonPath("$.totalLimit").value("125000.00"))
                .andExpect(jsonPath("$.totalAvailableLimit").value("104499.25"))
                .andExpect(jsonPath("$.cardCount").value(3));
    }

    @Test
    @DisplayName("GET /api/v1/financial/credit-cards/catalogue returns all 121 cards")
    void getCreditCardsCatalogue() throws Exception {
        mockMvc.perform(get("/api/v1/financial/credit-cards/catalogue").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(121)))
                .andExpect(jsonPath("$[0].productName", notNullValue()))
                .andExpect(jsonPath("$[0].currentDebt").doesNotExist())
                .andExpect(jsonPath("$[0].creditLimit").doesNotExist())
                .andExpect(jsonPath("$[0].userId").doesNotExist())
                .andExpect(jsonPath("$[0].cardLast4").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/v1/financial/bills returns all bills")
    void getBills() throws Exception {
        mockMvc.perform(get("/api/v1/financial/bills").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("INVOICE_LIST"))
                .andExpect(jsonPath("$.count").value(5));
    }

    @Test
    void userWithoutBillsReceivesAnEmptyInvoiceCollection() throws Exception {
        mockMvc.perform(get("/api/v1/financial/bills").header("X-Demo-User-Id", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.invoices", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/v1/financial/bills?status=UNPAID returns 4 unpaid bills and 3428.05 total")
    void getUnpaidBills() throws Exception {
        mockMvc.perform(get("/api/v1/financial/bills?status=UNPAID").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(4))
                .andExpect(jsonPath("$.totalAmount").value("3428.05"))
                .andExpect(jsonPath("$.overdueCount").value(1))
                .andExpect(jsonPath("$.overdueAmount").value("649.90"));
    }

    @Test
    void upcomingAndOverdueBillFiltersAreDisjoint() throws Exception {
        mockMvc.perform(get("/api/v1/financial/bills?status=UPCOMING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3))
                .andExpect(jsonPath("$.totalAmount").value("2778.15"))
                .andExpect(jsonPath("$.invoices[0].overdue").value(false));
        mockMvc.perform(get("/api/v1/financial/bills?status=OVERDUE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.totalAmount").value("649.90"))
                .andExpect(jsonPath("$.invoices[0].overdue").value(true));
    }

    @Test
    void userTwoDoesNotInheritUserOneCreditCards() throws Exception {
        mockMvc.perform(get("/api/v1/financial/credit-cards").header("X-Demo-User-Id", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardCount").value(0))
                .andExpect(jsonPath("$.totalDebt").value("0.00"));
    }

    @Test
    @DisplayName("GET /api/v1/financial/transactions returns recent transactions")
    void getTransactions() throws Exception {
        mockMvc.perform(get("/api/v1/financial/transactions").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("TRANSACTION_LIST"))
                .andExpect(jsonPath("$.transactions", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/v1/financial/spending-categories returns category distribution")
    void getSpendingCategories() throws Exception {
        mockMvc.perform(get("/api/v1/financial/spending-categories").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("SPENDING_BY_CATEGORY"))
                .andExpect(jsonPath("$.totalExpense").value("21245.49"))
                .andExpect(jsonPath("$.categories", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/v1/financial/summary returns November 2026 financial summary")
    void getFinancialSummary() throws Exception {
        mockMvc.perform(get("/api/v1/financial/summary").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("PERIOD_SUMMARY"))
                .andExpect(jsonPath("$.totalIncome").value("45500.00"))
                .andExpect(jsonPath("$.totalExpense").value("21245.49"))
                .andExpect(jsonPath("$.net").value("24254.51"))
                .andExpect(jsonPath("$.savingsRatePercent").value("53.3"));
    }
}
