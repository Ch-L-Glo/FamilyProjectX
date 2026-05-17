package com.familyprojectx.finance.budget.controller;

import com.familyprojectx.finance.budget.dto.BudgetResponse;
import com.familyprojectx.finance.budget.dto.CreateBudgetRequest;
import com.familyprojectx.finance.budget.service.BudgetService;
import com.familyprojectx.finance.common.security.CurrentUser;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/families/{familyId}/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final CurrentUser currentUser;

    public BudgetController(BudgetService budgetService, CurrentUser currentUser) {
        this.budgetService = budgetService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BudgetResponse create(@PathVariable UUID familyId, Authentication authentication, @Valid @RequestBody CreateBudgetRequest request) {
        return budgetService.create(familyId, currentUser.requireUserId(authentication), request);
    }

    @GetMapping
    public List<BudgetResponse> list(@PathVariable UUID familyId, Authentication authentication) {
        return budgetService.list(familyId, currentUser.requireUserId(authentication));
    }
}
