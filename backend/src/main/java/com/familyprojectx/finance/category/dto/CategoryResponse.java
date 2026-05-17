package com.familyprojectx.finance.category.dto;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String type,
        boolean active
) {
}
