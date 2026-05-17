package com.familyprojectx.finance.auth.dto;

import java.util.UUID;

public record AuthResponse(
        String token,
        UUID userId,
        String email
) {
}
