package com.familyprojectx.finance.family.dto;

import java.util.UUID;

public record FamilyMemberResponse(
        UUID userId,
        String email,
        String role,
        String status
) {
}
