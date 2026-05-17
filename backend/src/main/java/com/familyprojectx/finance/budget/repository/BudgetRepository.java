package com.familyprojectx.finance.budget.repository;

import com.familyprojectx.finance.budget.entity.Budget;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    List<Budget> findByFamilyIdOrderByMonthDesc(UUID familyId);
}
