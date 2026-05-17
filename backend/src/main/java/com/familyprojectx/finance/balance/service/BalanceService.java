package com.familyprojectx.finance.balance.service;

import com.familyprojectx.finance.balance.dto.BalanceResponse;
import com.familyprojectx.finance.common.exception.ApiException;
import com.familyprojectx.finance.family.entity.Family;
import com.familyprojectx.finance.family.repository.FamilyMemberRepository;
import com.familyprojectx.finance.family.repository.FamilyRepository;
import com.familyprojectx.finance.family.service.FamilyAccessService;
import com.familyprojectx.finance.settlement.entity.Settlement;
import com.familyprojectx.finance.settlement.entity.SettlementStatus;
import com.familyprojectx.finance.settlement.repository.SettlementRepository;
import com.familyprojectx.finance.transaction.entity.FinancialTransaction;
import com.familyprojectx.finance.transaction.entity.TransactionType;
import com.familyprojectx.finance.transaction.repository.FinancialTransactionRepository;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceService {

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final FinancialTransactionRepository transactionRepository;
    private final SettlementRepository settlementRepository;
    private final FamilyAccessService familyAccessService;

    public BalanceService(
            FamilyRepository familyRepository,
            FamilyMemberRepository familyMemberRepository,
            FinancialTransactionRepository transactionRepository,
            SettlementRepository settlementRepository,
            FamilyAccessService familyAccessService
    ) {
        this.familyRepository = familyRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.transactionRepository = transactionRepository;
        this.settlementRepository = settlementRepository;
        this.familyAccessService = familyAccessService;
    }

    @Transactional(readOnly = true)
    public BalanceResponse calculate(UUID familyId, UUID userId) {
        familyAccessService.requireMember(familyId, userId);
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Family not found"));
        Map<UUID, MutableBalance> balances = new LinkedHashMap<>();
        familyMemberRepository.findByFamilyId(familyId).forEach(member -> balances.put(member.getUser().getId(), new MutableBalance(member.getUser().getId())));

        for (FinancialTransaction transaction : transactionRepository.findByFamilyIdAndActiveTrueOrderByTransactionDateDesc(familyId)) {
            MutableBalance balance = balances.get(transaction.getPaidBy().getId());
            if (balance == null) {
                continue;
            }
            if (transaction.getTransactionType() == TransactionType.INCOME) {
                balance.transactionNetAmount = balance.transactionNetAmount.add(transaction.getAmountBase());
            } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                balance.transactionNetAmount = balance.transactionNetAmount.subtract(transaction.getAmountBase());
            }
        }

        for (Settlement settlement : settlementRepository.findByFamilyIdAndStatus(familyId, SettlementStatus.PENDING)) {
            MutableBalance from = balances.get(settlement.getFromUser().getId());
            MutableBalance to = balances.get(settlement.getToUser().getId());
            if (from != null) {
                from.pendingPayable = from.pendingPayable.add(settlement.getAmount());
            }
            if (to != null) {
                to.pendingReceivable = to.pendingReceivable.add(settlement.getAmount());
            }
        }

        return new BalanceResponse(
                familyId,
                family.getBaseCurrency(),
                balances.values().stream()
                        .map(balance -> new BalanceResponse.UserBalance(
                                balance.userId,
                                balance.transactionNetAmount,
                                balance.pendingReceivable,
                                balance.pendingPayable,
                                balance.pendingReceivable.subtract(balance.pendingPayable)
                        ))
                        .toList()
        );
    }

    private static class MutableBalance {
        private final UUID userId;
        private BigDecimal transactionNetAmount = BigDecimal.ZERO;
        private BigDecimal pendingReceivable = BigDecimal.ZERO;
        private BigDecimal pendingPayable = BigDecimal.ZERO;

        private MutableBalance(UUID userId) {
            this.userId = userId;
        }
    }
}
