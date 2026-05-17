package com.familyprojectx.finance.transaction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record SplitRequest(
        @NotNull UUID userId,
        @Positive BigDecimal shareAmount,
        @Positive BigDecimal sharePercentage
) {
}
