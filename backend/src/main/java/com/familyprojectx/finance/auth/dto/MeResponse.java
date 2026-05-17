package com.familyprojectx.finance.auth.dto;

import java.util.List;
import java.util.UUID;

public record MeResponse(
        UUID userId,
        String email,
        List<FamilySummary> families
) {
    public record FamilySummary(UUID familyId, String familyName, String role, String baseCurrency) {
    }
}
