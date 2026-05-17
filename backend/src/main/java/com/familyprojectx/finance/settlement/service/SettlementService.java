package com.familyprojectx.finance.settlement.service;

import com.familyprojectx.finance.common.exception.ApiException;
import com.familyprojectx.finance.family.service.FamilyAccessService;
import com.familyprojectx.finance.settlement.dto.SettlementResponse;
import com.familyprojectx.finance.settlement.entity.Settlement;
import com.familyprojectx.finance.settlement.repository.SettlementRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final FamilyAccessService familyAccessService;

    public SettlementService(SettlementRepository settlementRepository, FamilyAccessService familyAccessService) {
        this.settlementRepository = settlementRepository;
        this.familyAccessService = familyAccessService;
    }

    @Transactional(readOnly = true)
    public List<SettlementResponse> list(UUID familyId, UUID userId) {
        familyAccessService.requireMember(familyId, userId);
        return settlementRepository.findByFamilyIdOrderByCreatedAtDesc(familyId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SettlementResponse markSettled(UUID familyId, UUID settlementId, UUID userId) {
        familyAccessService.requireMember(familyId, userId);
        Settlement settlement = settlementRepository.findByIdAndFamilyId(settlementId, familyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Settlement not found"));
        settlement.markSettled();
        return toResponse(settlement);
    }

    private SettlementResponse toResponse(Settlement settlement) {
        return new SettlementResponse(
                settlement.getId(),
                settlement.getFromUser().getId(),
                settlement.getToUser().getId(),
                settlement.getAmount(),
                settlement.getCurrency(),
                settlement.getRelatedTransaction().getId(),
                settlement.getStatus().name()
        );
    }
}
