package com.brainapp.chatbrain.financial.provider;

import com.brainapp.chatbrain.dto.response.*;
import com.brainapp.chatbrain.financial.model.*;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Profile("!brainapp")
public class MockFinancialDataProvider implements FinancialDataProvider {

    // Default reference date matching test fixture (15 November 2026)
    public static final LocalDate REFERENCE_TODAY = LocalDate.of(2026, 11, 15);
    private static final Locale LOCALE_TR = Locale.forLanguageTag("tr-TR");

    private static final List<Bank> BANKS = new ArrayList<>();
    private static final List<BankAccount> ACCOUNTS = new ArrayList<>();
    private static final List<CreditCard> CREDIT_CARDS = new ArrayList<>();
    private static final List<CreditCardProduct> CREDIT_CARD_PRODUCTS = new ArrayList<>();
    private static final List<Transaction> TRANSACTIONS = new ArrayList<>();
    private static final List<Bill> BILLS = new ArrayList<>();

    static {
        initBanks();
        initAccounts();
        initCreditCards();
        initCreditCardProducts();
        initTransactions();
        initBills();
    }

    private static void initBanks() {
        // Exactly 10 simulated partner banks
        BANKS.add(new Bank(1L, "ZIRAAT", "Ziraat Bankası", "/assets/banks/ziraat.png"));
        BANKS.add(new Bank(2L, "IS_BANKASI", "Türkiye İş Bankası", "/assets/banks/isbank.png"));
        BANKS.add(new Bank(3L, "GARANTI", "Garanti BBVA", "/assets/banks/garanti.png"));
        BANKS.add(new Bank(4L, "YAPI_KREDI", "Yapı Kredi", "/assets/banks/yapikredi.png"));
        BANKS.add(new Bank(5L, "AKBANK", "Akbank", "/assets/banks/akbank.png"));
        BANKS.add(new Bank(6L, "QNB", "QNB Finansbank", "/assets/banks/qnb.png"));
        BANKS.add(new Bank(7L, "VAKIFBANK", "Vakıfbank", "/assets/banks/vakifbank.png"));
        BANKS.add(new Bank(8L, "HALKBANK", "Halkbank", "/assets/banks/halkbank.png"));
        BANKS.add(new Bank(9L, "DENIZBANK", "DenizBank", "/assets/banks/denizbank.png"));
        BANKS.add(new Bank(10L, "TEB", "TEB", "/assets/banks/teb.png"));
    }

    private static void initAccounts() {
        // User 1 accounts: Total 145.650,75 ₺
        ACCOUNTS.add(new BankAccount(
                1L, 1L, 3L, "Garanti BBVA", "Vadesiz TL", "7710",
                "CHECKING", new BigDecimal("12400.00"), "TRY", true));
        ACCOUNTS.add(new BankAccount(
                2L, 1L, 2L, "Türkiye İş Bankası", "Birikim Hesabı", "0953",
                "SAVINGS", new BigDecimal("85000.00"), "TRY", true));
        ACCOUNTS.add(new BankAccount(
                3L, 1L, 1L, "Ziraat Bankası", "Vadesiz TL", "4821",
                "CHECKING", new BigDecimal("48250.75"), "TRY", true));
    }

    private static void initCreditCards() {
        CREDIT_CARDS.add(new CreditCard(
                1L, 1L, 3L, "Garanti BBVA", "Bonus Platinum", "PLATINUM", "1234",
                new BigDecimal("60000.00"), new BigDecimal("14320.50"),
                new BigDecimal("45679.50"), new BigDecimal("9870.00"),
                new BigDecimal("2961.00"), LocalDate.of(2026, 11, 20), 5, true));
        CREDIT_CARDS.add(new CreditCard(
                2L, 1L, 4L, "Yapı Kredi", "World Gold", "GOLD", "5678",
                new BigDecimal("40000.00"), new BigDecimal("6180.25"),
                new BigDecimal("33819.75"), new BigDecimal("4100.00"),
                new BigDecimal("1230.00"), LocalDate.of(2026, 11, 23), 12, true));
        CREDIT_CARDS.add(new CreditCard(
                3L, 1L, 5L, "Akbank", "Axess Free", "CLASSIC", "9012",
                new BigDecimal("25000.00"), new BigDecimal("0.00"),
                new BigDecimal("25000.00"), new BigDecimal("0.00"),
                new BigDecimal("0.00"), null, 20, true));
    }

    private static void initCreditCardProducts() {
        String[] types = {"CLASSIC", "GOLD", "PLATINUM", "BUSINESS"};
        String[][] families = {
                {"Bankkart", "Bankkart Genç", "Bankkart Başak", "Bankkart Combo"},
                {"Maximum", "Maximiles", "Maximiles Black", "İş'te Üniversiteli"},
                {"Bonus", "Miles&Smiles", "Shop&Fly", "Flexi"},
                {"World", "Adios", "Play", "Crystal"},
                {"Axess", "Wings", "Free", "Axess Business"},
                {"CardFinans", "Miles&Smiles QNB", "Enpara", "CardFinans Fix"},
                {"Worldcard", "Like Card", "MilPlus", "Platinum Plus"},
                {"Paraf", "Parafly", "Paraf Genç", "Paraf Esnaf"},
                {"Bonus Deniz", "Deniz Miles", "Black Card", "Üretici Kartı"},
                {"Bonus TEB", "TEB World", "Marifetli Kart", "Sade Kart"}
        };
        for (CreditCard card : CREDIT_CARDS) {
            CREDIT_CARD_PRODUCTS.add(new CreditCardProduct(card.id(), card.bankId(), card.bankName(),
                    card.cardName(), card.cardType()));
        }
        for (int i = 0; i < 118; i++) {
            int bankIndex = i % BANKS.size();
            String type = types[i % types.length];
            String name = families[bankIndex][(i / 10) % 4] + " " + (type.equals("CLASSIC") ? "Standart" : type);
            Bank bank = BANKS.get(bankIndex);
            CREDIT_CARD_PRODUCTS.add(new CreditCardProduct((long) (i + 4), bank.id(), bank.name(), name, type));
        }
    }

    private static void initTransactions() {
        // November 2026 (User 1)
        // Income: 45.500,00 ₺ (Maaş 42.000 + Freelance 3.500)
        // Expense: 21.245,49 ₺ (Kira 15.000 + Market 1.730,50 + Ulaşım 875 + Yeme-İçme 860 + Alışveriş 2.150 + Sinema 400 + Netflix 229,99)
        // Transfer: 5.000,00 ₺ (Kart ödemesi)
        long id = 1L;
        TRANSACTIONS.add(new Transaction(id++, 1L, 3L, null, "INCOME", "SALARY", "Maaş", "Şirket Bordro A.Ş.", "Aylık Maaş Ödemesi", new BigDecimal("42000.00"), "TRY", LocalDate.of(2026, 11, 1)));
        TRANSACTIONS.add(new Transaction(id++, 1L, 3L, null, "EXPENSE", "RENT", "Kira", "Mülk Sahibi", "Kasım 2026 Kira Bedeli", new BigDecimal("15000.00"), "TRY", LocalDate.of(2026, 11, 1)));
        TRANSACTIONS.add(new Transaction(id++, 1L, null, 1L, "EXPENSE", "MARKET", "Market", "Migros", "Mutfak Alışverişi", new BigDecimal("1250.00"), "TRY", LocalDate.of(2026, 11, 2)));
        TRANSACTIONS.add(new Transaction(id++, 1L, 1L, null, "EXPENSE", "TRANSPORT", "Ulaşım", "İETT Akbil", "Toplu Taşıma Yükleme", new BigDecimal("600.00"), "TRY", LocalDate.of(2026, 11, 3)));
        TRANSACTIONS.add(new Transaction(id++, 1L, 3L, null, "TRANSFER", "CARD_PAYMENT", "Kart Ödemesi", "Garanti BBVA", "Kredi Kartı Borç Ödeme", new BigDecimal("5000.00"), "TRY", LocalDate.of(2026, 11, 4)));
        TRANSACTIONS.add(new Transaction(id++, 1L, null, 1L, "EXPENSE", "RESTAURANT", "Yeme-İçme", "Starbucks", "Kahve ve Atıştırmalık", new BigDecimal("320.00"), "TRY", LocalDate.of(2026, 11, 5)));
        TRANSACTIONS.add(new Transaction(id++, 1L, null, 1L, "EXPENSE", "SUBSCRIPTION", "Abonelik", "Netflix", "Aylık Standart Plan", new BigDecimal("229.99"), "TRY", LocalDate.of(2026, 11, 6)));
        TRANSACTIONS.add(new Transaction(id++, 1L, null, 2L, "EXPENSE", "SHOPPING", "Alışveriş", "Trendyol", "Kışlık Giyim Alışverişi", new BigDecimal("2150.00"), "TRY", LocalDate.of(2026, 11, 7)));
        TRANSACTIONS.add(new Transaction(id++, 1L, 3L, null, "INCOME", "FREELANCE", "Serbest Kazanç", "Teknoloji Ltd.", "Danışmanlık Hakediş", new BigDecimal("3500.00"), "TRY", LocalDate.of(2026, 11, 8)));
        TRANSACTIONS.add(new Transaction(id++, 1L, null, 2L, "EXPENSE", "MARKET", "Market", "BİM", "Haftalık Alışveriş", new BigDecimal("480.50"), "TRY", LocalDate.of(2026, 11, 9)));
        TRANSACTIONS.add(new Transaction(id++, 1L, null, 2L, "EXPENSE", "ENTERTAINMENT", "Eğlence", "Paribu Cineverse", "Sinema Bileti", new BigDecimal("400.00"), "TRY", LocalDate.of(2026, 11, 10)));
        TRANSACTIONS.add(new Transaction(id++, 1L, null, 2L, "EXPENSE", "RESTAURANT", "Yeme-İçme", "Yemeksepeti", "Akşam Yemeği Siparişi", new BigDecimal("540.00"), "TRY", LocalDate.of(2026, 11, 11)));
        TRANSACTIONS.add(new Transaction(id++, 1L, 1L, null, "EXPENSE", "TRANSPORT", "Ulaşım", "Uber", "Taksi Ulaşım", new BigDecimal("275.00"), "TRY", LocalDate.of(2026, 11, 12)));

        // October 2026 (User 1): Income 42.000,00 ₺, Expense 22.779,99 ₺
        TRANSACTIONS.add(new Transaction(id++, 1L, 3L, null, "INCOME", "SALARY", "Maaş", "Şirket Bordro A.Ş.", "Ekim 2026 Maaş", new BigDecimal("42000.00"), "TRY", LocalDate.of(2026, 10, 1)));
        TRANSACTIONS.add(new Transaction(id++, 1L, 3L, null, "EXPENSE", "RENT", "Kira", "Mülk Sahibi", "Ekim 2026 Kira Bedeli", new BigDecimal("15000.00"), "TRY", LocalDate.of(2026, 10, 1)));
        TRANSACTIONS.add(new Transaction(id++, 1L, null, 1L, "EXPENSE", "SUBSCRIPTION", "Abonelik", "Netflix", "Abonelik", new BigDecimal("229.99"), "TRY", LocalDate.of(2026, 10, 6)));
        TRANSACTIONS.add(new Transaction(id++, 1L, null, 1L, "EXPENSE", "MARKET", "Market", "Migros", "Market Harcaması", new BigDecimal("2100.00"), "TRY", LocalDate.of(2026, 10, 7)));
        TRANSACTIONS.add(new Transaction(id++, 1L, null, 1L, "EXPENSE", "RESTAURANT", "Yeme-İçme", "Nusret", "Akşam Yemeği", new BigDecimal("1150.00"), "TRY", LocalDate.of(2026, 10, 11)));
        TRANSACTIONS.add(new Transaction(id++, 1L, 1L, null, "EXPENSE", "TRANSPORT", "Ulaşım", "İETT Akbil", "Aylık Akbil", new BigDecimal("900.00"), "TRY", LocalDate.of(2026, 10, 13)));
        TRANSACTIONS.add(new Transaction(id++, 1L, null, 2L, "EXPENSE", "SHOPPING", "Alışveriş", "Trendyol", "Elektronik Ürün", new BigDecimal("3400.00"), "TRY", LocalDate.of(2026, 10, 17)));
    }

    private static void initBills() {
        // User 1 Bills: Total Unpaid: 3.428,05 ₺ (Overdue: 649,90 ₺, Upcoming: 2.778,15 ₺)
        BILLS.add(new Bill(1L, 1L, "Türk Telekom", "INTERNET", new BigDecimal("649.90"), "TRY",
                LocalDate.of(2026, 11, 12), "UNPAID", null));
        BILLS.add(new Bill(2L, 1L, "BEDAŞ", "ELECTRICITY", new BigDecimal("912.75"), "TRY",
                LocalDate.of(2026, 11, 18), "UNPAID", null));
        BILLS.add(new Bill(3L, 1L, "İSKİ", "WATER", new BigDecimal("385.40"), "TRY",
                LocalDate.of(2026, 11, 20), "UNPAID", null));
        BILLS.add(new Bill(4L, 1L, "İGDAŞ", "NATURAL_GAS", new BigDecimal("1480.00"), "TRY",
                LocalDate.of(2026, 11, 28), "UNPAID", null));
        BILLS.add(new Bill(5L, 1L, "Turkcell", "MOBILE", new BigDecimal("520.00"), "TRY",
                LocalDate.of(2026, 11, 10), "PAID", LocalDate.of(2026, 11, 9)));
    }

    @Override
    public List<Bank> getBanks() {
        return Collections.unmodifiableList(BANKS);
    }

    @Override
    public List<BankAccount> getAccounts(Long userId, String bankFilter) {
        return ACCOUNTS.stream()
                .filter(a -> userId == null || a.userId().equals(userId))
                .filter(a -> bankFilter == null || bankFilter.isBlank() || a.bankName().toLowerCase(LOCALE_TR).contains(bankFilter.toLowerCase(LOCALE_TR)))
                .toList();
    }

    @Override
    public AccountBalanceResponse getAccountBalances(Long userId, String bankFilter) {
        List<BankAccount> filtered = getAccounts(userId, bankFilter);

        BigDecimal total = filtered.stream()
                .map(BankAccount::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        Map<String, List<BankAccount>> byBank = filtered.stream()
                .collect(Collectors.groupingBy(BankAccount::bankName, LinkedHashMap::new, Collectors.toList()));

        List<AccountBalanceResponse.BankTotalResponse> bankTotals = byBank.entrySet().stream()
                .map(e -> new AccountBalanceResponse.BankTotalResponse(
                        e.getKey(),
                        e.getValue().size(),
                        e.getValue().stream().map(BankAccount::balance).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();

        List<BankAccountResponse> accountResponses = filtered.stream()
                .map(a -> new BankAccountResponse(
                        a.id(), a.bankId(), a.bankName(), a.accountName(),
                        a.ibanLast4(), a.accountType(), a.balance(), a.currency(), a.active()))
                .toList();

        return new AccountBalanceResponse(
                "ACCOUNT_BALANCE", total, "TRY", filtered.size(), bankFilter, accountResponses, bankTotals);
    }

    @Override
    public CreditCardResponse getCreditCards(Long userId, String cardOrBankFilter) {
        // Only user-owned cards are included; catalogue products use a separate product-only model.
        List<CreditCard> userCards = CREDIT_CARDS.stream()
                .filter(c -> userId == null || c.userId().equals(userId))
                .filter(c -> {
                    if (cardOrBankFilter == null || cardOrBankFilter.isBlank()) return true;
                    String q = cardOrBankFilter.toLowerCase(LOCALE_TR);
                    return c.bankName().toLowerCase(LOCALE_TR).contains(q) ||
                           c.cardName().toLowerCase(LOCALE_TR).contains(q);
                })
                .toList();

        BigDecimal totalDebt = userCards.stream().map(CreditCard::currentDebt).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalLimit = userCards.stream().map(CreditCard::creditLimit).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAvailable = userCards.stream().map(CreditCard::availableLimit).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalStatement = userCards.stream().map(CreditCard::statementBalance).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

        List<CreditCardResponse.CardDetailResponse> details = userCards.stream()
                .map(c -> new CreditCardResponse.CardDetailResponse(
                        c.id(), c.bankId(), c.bankName(), c.cardName(), c.cardType(),
                        c.cardLast4(), c.creditLimit(), c.currentDebt(), c.availableLimit(),
                        c.statementBalance(), c.minimumPayment(), c.nextDueDate(),
                        c.calculateUtilizationPercent(), c.active()
                ))
                .toList();

        return new CreditCardResponse(
                "CARD_DEBT", totalDebt, totalLimit, totalAvailable, totalStatement,
                "TRY", userCards.size(), cardOrBankFilter, details
        );
    }

    @Override
    public List<CreditCardProduct> getAllCreditCardsCatalogue() {
        return Collections.unmodifiableList(CREDIT_CARD_PRODUCTS);
    }

    @Override
    public BillResponse getBills(Long userId, String statusFilter) {
        LocalDate today = REFERENCE_TODAY;
        List<Bill> filtered = BILLS.stream()
                .filter(b -> userId == null || b.userId().equals(userId))
                .filter(b -> {
                    String filter = statusFilter == null || statusFilter.isBlank()
                            ? "ALL" : statusFilter.toUpperCase(Locale.ROOT);
                    return switch (filter) {
                        case "ALL" -> true;
                        case "UPCOMING" -> b.isUpcoming(today, 14);
                        case "OVERDUE" -> b.isOverdue(today);
                        case "PAID", "UNPAID" -> filter.equalsIgnoreCase(b.status());
                        default -> false;
                    };
                })
                .toList();

        BigDecimal totalAmount = filtered.stream().map(Bill::amount).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        List<Bill> overdueList = filtered.stream().filter(b -> b.isOverdue(today)).toList();
        BigDecimal overdueAmount = overdueList.stream().map(Bill::amount).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

        List<BillResponse.BillItemResponse> items = filtered.stream()
                .map(b -> new BillResponse.BillItemResponse(
                        b.id(), b.providerName(), b.category(), b.amount(),
                        b.dueDate(), b.status(), b.isOverdue(today), b.daysUntilDue(today), b.paidAt()
                ))
                .toList();

        return new BillResponse(
                "INVOICE_LIST", statusFilter != null ? statusFilter : "ALL",
                filtered.size(), totalAmount, overdueList.size(), overdueAmount, items
        );
    }

    @Override
    public TransactionResponse getTransactions(
            Long userId,
            String typeFilter,
            String categoryFilter,
            LocalDate startDate,
            LocalDate endDate,
            Integer limit
    ) {
        List<Transaction> filtered = TRANSACTIONS.stream()
                .filter(t -> userId == null || t.userId().equals(userId))
                .filter(t -> typeFilter == null || typeFilter.isBlank() || t.type().equalsIgnoreCase(typeFilter))
                .filter(t -> categoryFilter == null || categoryFilter.isBlank() || t.category().equalsIgnoreCase(categoryFilter))
                .filter(t -> startDate == null || !t.transactionDate().isBefore(startDate))
                .filter(t -> endDate == null || !t.transactionDate().isAfter(endDate))
                .sorted(Comparator.comparing(Transaction::transactionDate).reversed()
                        .thenComparing(Transaction::id, Comparator.reverseOrder()))
                .toList();

        int totalCount = filtered.size();
        int maxLimit = limit != null && limit > 0 ? Math.min(limit, 50) : 10;
        List<Transaction> page = filtered.stream().limit(maxLimit).toList();

        BigDecimal totalIncome = filtered.stream()
                .filter(t -> "INCOME".equalsIgnoreCase(t.type()))
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalExpense = filtered.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.type()))
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

        List<TransactionResponse.TransactionItemResponse> items = page.stream()
                .map(t -> new TransactionResponse.TransactionItemResponse(
                        t.id(), t.transactionDate(), t.type(), t.category(),
                        t.categoryName(), t.merchant(), t.description(), t.amount()
                ))
                .toList();

        return new TransactionResponse(
                "TRANSACTION_LIST", PeriodLabelFormatter.format(startDate, endDate), typeFilter, categoryFilter,
                totalCount, page.size(), totalIncome, totalExpense, items
        );
    }

    @Override
    public SpendingCategoryResponse getSpendingCategories(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            String focusCategory
    ) {
        LocalDate start = startDate != null ? startDate : LocalDate.of(2026, 11, 1);
        LocalDate end = endDate != null ? endDate : LocalDate.of(2026, 11, 30);

        List<Transaction> expenses = TRANSACTIONS.stream()
                .filter(t -> userId == null || t.userId().equals(userId))
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.type()))
                .filter(t -> !t.transactionDate().isBefore(start) && !t.transactionDate().isAfter(end))
                .filter(t -> focusCategory == null || focusCategory.isBlank() ||
                        t.category().equalsIgnoreCase(focusCategory) || t.categoryName().equalsIgnoreCase(focusCategory))
                .toList();

        BigDecimal totalExpense = expenses.stream()
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

        Map<String, List<Transaction>> byCat = expenses.stream()
                .collect(Collectors.groupingBy(Transaction::category, LinkedHashMap::new, Collectors.toList()));

        List<SpendingCategoryResponse.CategoryDetailResponse> catDetails = new ArrayList<>();
        for (var entry : byCat.entrySet()) {
            BigDecimal sum = entry.getValue().stream().map(Transaction::amount).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
            BigDecimal pct = totalExpense.signum() > 0
                    ? sum.multiply(BigDecimal.valueOf(100)).divide(totalExpense, 1, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            String catName = entry.getValue().get(0).categoryName();
            catDetails.add(new SpendingCategoryResponse.CategoryDetailResponse(entry.getKey(), catName, sum, entry.getValue().size(), pct));
        }

        catDetails.sort((a, b) -> b.total().compareTo(a.total()));

        return new SpendingCategoryResponse(
                "SPENDING_BY_CATEGORY", PeriodLabelFormatter.format(start, end), totalExpense, focusCategory, catDetails
        );
    }

    @Override
    public FinancialSummaryResponse getFinancialSummary(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.of(2026, 11, 1);
        LocalDate end = endDate != null ? endDate : LocalDate.of(2026, 11, 30);

        List<Transaction> inPeriod = TRANSACTIONS.stream()
                .filter(t -> userId == null || t.userId().equals(userId))
                .filter(t -> !t.transactionDate().isBefore(start) && !t.transactionDate().isAfter(end))
                .toList();

        BigDecimal income = inPeriod.stream()
                .filter(t -> "INCOME".equalsIgnoreCase(t.type()))
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

        BigDecimal expense = inPeriod.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.type()))
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

        BigDecimal net = income.subtract(expense).setScale(2, RoundingMode.HALF_UP);
        BigDecimal savingsRate = income.signum() > 0
                ? net.multiply(BigDecimal.valueOf(100)).divide(income, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        SpendingCategoryResponse catResp = getSpendingCategories(userId, start, end, null);
        List<SpendingCategoryResponse.CategoryDetailResponse> topCategories = catResp.categories().stream().limit(5).toList();

        return new FinancialSummaryResponse(
                "PERIOD_SUMMARY", PeriodLabelFormatter.format(start, end), income, expense, net,
                savingsRate, inPeriod.size(), topCategories
        );
    }
}
