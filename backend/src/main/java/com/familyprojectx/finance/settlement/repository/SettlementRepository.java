package com.familyprojectx.finance.settlement.repository;

import com.familyprojectx.finance.settlement.entity.Settlement;
import com.familyprojectx.finance.settlement.entity.SettlementStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {

    List<Settlement> findByFamilyIdOrderByCreatedAtDesc(UUID familyId);

    List<Settlement> findByFamilyIdAndStatus(UUID familyId, SettlementStatus status);

    Optional<Settlement> findByIdAndFamilyId(UUID id, UUID familyId);
}
