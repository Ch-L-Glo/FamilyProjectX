package com.familyprojectx.finance.category.dto;

import com.familyprojectx.finance.category.entity.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCategoryRequest(
        @NotBlank String name,
        @NotNull CategoryType type
) {
}
