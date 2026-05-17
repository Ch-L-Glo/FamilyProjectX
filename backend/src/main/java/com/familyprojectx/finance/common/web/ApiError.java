package com.familyprojectx.finance.common.web;

import java.time.Instant;

public record ApiError(
        Instant timestamp,
        int status,
        String message,
        String path
) {
}
