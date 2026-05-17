package com.familyprojectx.finance.balance.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record BalanceResponse(
        UUID familyId,
        String baseCurrency,
        List<UserBalance> users
) {
    public record UserBalance(
            UUID userId,
            BigDecimal transactionNetAmount,
            BigDecimal pendingReceivable,
            BigDecimal pendingPayable,
            BigDecimal settlementNetAmount
    ) {
    }
}
