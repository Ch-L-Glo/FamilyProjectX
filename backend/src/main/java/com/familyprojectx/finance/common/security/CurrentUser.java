package com.familyprojectx.finance.common.security;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.familyprojectx.finance.common.exception.ApiException;

@Component
public class CurrentUser {

    public UUID requireUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser securityUser)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return securityUser.id();
    }
}
