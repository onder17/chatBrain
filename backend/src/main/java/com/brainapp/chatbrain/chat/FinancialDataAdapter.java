package com.brainapp.chatbrain.chat;

import com.brainapp.chatbrain.dto.response.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class FinancialDataAdapter {
    private final ObjectMapper mapper;
    public FinancialDataAdapter(ObjectMapper mapper) { this.mapper = mapper; }

    public Map<String, Object> adapt(Object dto, String periodLabel) {
        Map<String, Object> value = mapper.convertValue(dto, new TypeReference<>() {});
        if (value.containsKey("periodLabel")) value.put("periodLabel", periodLabel);
        if (dto instanceof BillResponse) value.put("period", null);
        if (dto instanceof TransactionResponse tx) {
            value.put("returnedCount", tx.returnedCount());
            value.put("truncated", tx.returnedCount() < tx.totalCount());
            value.put("period", period(periodLabel));
        }
        if (dto instanceof SpendingCategoryResponse) value.put("period", period(periodLabel));
        if (dto instanceof FinancialSummaryResponse summary) {
            value.put("period", period(periodLabel));
            value.put("topExpenseCategories", value.remove("topCategories"));
        }
        return new LinkedHashMap<>(value);
    }

    private Map<String, Object> period(String label) {
        return Map.of("from", "", "to", "", "type", "MONTH", "label", label == null ? "Seçilen dönem" : label);
    }
}
