package com.familyprojectx.finance.budget.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetResponse(
        UUID id,
        String scope,
        UUID userId,
        UUID categoryId,
        BigDecimal amount,
        String month,
        String status
) {
}
