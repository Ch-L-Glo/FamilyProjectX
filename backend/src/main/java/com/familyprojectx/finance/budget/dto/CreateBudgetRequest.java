package com.familyprojectx.finance.budget.dto;

import com.familyprojectx.finance.budget.entity.BudgetScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateBudgetRequest(
        @NotNull BudgetScope scope,
        UUID userId,
        @NotNull UUID categoryId,
        @NotNull @Positive BigDecimal amount,
        @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}") String month
) {
}
