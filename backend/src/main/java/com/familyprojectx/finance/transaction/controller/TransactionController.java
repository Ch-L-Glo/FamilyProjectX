package com.familyprojectx.finance.transaction.controller;

import com.familyprojectx.finance.common.security.CurrentUser;
import com.familyprojectx.finance.transaction.dto.CreateTransactionRequest;
import com.familyprojectx.finance.transaction.dto.TransactionResponse;
import com.familyprojectx.finance.transaction.service.TransactionService;
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
@RequestMapping("/api/families/{familyId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final CurrentUser currentUser;

    public TransactionController(TransactionService transactionService, CurrentUser currentUser) {
        this.transactionService = transactionService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@PathVariable UUID familyId, Authentication authentication, @Valid @RequestBody CreateTransactionRequest request) {
        return transactionService.create(familyId, currentUser.requireUserId(authentication), request);
    }

    @GetMapping
    public List<TransactionResponse> list(@PathVariable UUID familyId, Authentication authentication) {
        return transactionService.list(familyId, currentUser.requireUserId(authentication));
    }

    @GetMapping("/{transactionId}")
    public TransactionResponse get(@PathVariable UUID familyId, @PathVariable UUID transactionId, Authentication authentication) {
        return transactionService.get(familyId, transactionId, currentUser.requireUserId(authentication));
    }
}
