package com.familyprojectx.finance.transaction.repository;

import com.familyprojectx.finance.transaction.entity.FinancialTransaction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, UUID> {

    List<FinancialTransaction> findByFamilyIdAndActiveTrueOrderByTransactionDateDesc(UUID familyId);

    Optional<FinancialTransaction> findByIdAndFamilyIdAndActiveTrue(UUID id, UUID familyId);
}
