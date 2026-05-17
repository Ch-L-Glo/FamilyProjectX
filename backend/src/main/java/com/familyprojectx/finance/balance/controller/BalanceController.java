package com.familyprojectx.finance.balance.controller;

import com.familyprojectx.finance.balance.dto.BalanceResponse;
import com.familyprojectx.finance.balance.service.BalanceService;
import com.familyprojectx.finance.common.security.CurrentUser;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/families/{familyId}/balances")
public class BalanceController {

    private final BalanceService balanceService;
    private final CurrentUser currentUser;

    public BalanceController(BalanceService balanceService, CurrentUser currentUser) {
        this.balanceService = balanceService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public BalanceResponse calculate(@PathVariable UUID familyId, Authentication authentication) {
        return balanceService.calculate(familyId, currentUser.requireUserId(authentication));
    }
}
