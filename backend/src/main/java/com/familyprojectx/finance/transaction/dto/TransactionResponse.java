package com.familyprojectx.finance.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        BigDecimal amountOriginal,
        String currencyOriginal,
        BigDecimal exchangeRate,
        BigDecimal amountBase,
        UUID paidByUserId,
        String ownershipType,
        String transactionType,
        String status,
        LocalDate date,
        List<SplitResponse> splits
) {
    public record SplitResponse(UUID userId, BigDecimal shareAmount, BigDecimal sharePercentage) {
    }
}
