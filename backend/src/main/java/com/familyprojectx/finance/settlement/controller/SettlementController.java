package com.familyprojectx.finance.settlement.controller;

import com.familyprojectx.finance.common.security.CurrentUser;
import com.familyprojectx.finance.settlement.dto.SettlementResponse;
import com.familyprojectx.finance.settlement.service.SettlementService;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/families/{familyId}/settlements")
public class SettlementController {

    private final SettlementService settlementService;
    private final CurrentUser currentUser;

    public SettlementController(SettlementService settlementService, CurrentUser currentUser) {
        this.settlementService = settlementService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<SettlementResponse> list(@PathVariable UUID familyId, Authentication authentication) {
        return settlementService.list(familyId, currentUser.requireUserId(authentication));
    }

    @PatchMapping("/{settlementId}/settled")
    public SettlementResponse markSettled(@PathVariable UUID familyId, @PathVariable UUID settlementId, Authentication authentication) {
        return settlementService.markSettled(familyId, settlementId, currentUser.requireUserId(authentication));
    }
}
