package com.familyprojectx.finance.transaction.dto;

import com.familyprojectx.finance.transaction.entity.TransactionOwnershipType;
import com.familyprojectx.finance.transaction.entity.TransactionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateTransactionRequest(
        @NotNull @Positive BigDecimal amountOriginal,
        @NotNull @Size(min = 3, max = 3) String currencyOriginal,
        @NotNull @Positive BigDecimal exchangeRate,
        @NotBlank String exchangeRateSource,
        UUID categoryId,
        @NotNull LocalDate date,
        String notes,
        @NotNull UUID paidByUserId,
        @NotNull TransactionOwnershipType ownershipType,
        @NotNull TransactionType transactionType,
        @Valid List<SplitRequest> splits
) {
}
