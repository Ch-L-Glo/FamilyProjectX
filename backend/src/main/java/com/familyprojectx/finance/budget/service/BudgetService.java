package com.familyprojectx.finance.budget.service;

import com.familyprojectx.finance.budget.dto.BudgetResponse;
import com.familyprojectx.finance.budget.dto.CreateBudgetRequest;
import com.familyprojectx.finance.budget.entity.Budget;
import com.familyprojectx.finance.budget.entity.BudgetScope;
import com.familyprojectx.finance.budget.repository.BudgetRepository;
import com.familyprojectx.finance.category.entity.Category;
import com.familyprojectx.finance.category.repository.CategoryRepository;
import com.familyprojectx.finance.common.exception.ApiException;
import com.familyprojectx.finance.family.entity.Family;
import com.familyprojectx.finance.family.repository.FamilyRepository;
import com.familyprojectx.finance.family.service.FamilyAccessService;
import com.familyprojectx.finance.user.entity.UserAccount;
import com.familyprojectx.finance.user.repository.UserAccountRepository;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final FamilyRepository familyRepository;
    private final CategoryRepository categoryRepository;
    private final UserAccountRepository userAccountRepository;
    private final FamilyAccessService familyAccessService;

    public BudgetService(
            BudgetRepository budgetRepository,
            FamilyRepository familyRepository,
            CategoryRepository categoryRepository,
            UserAccountRepository userAccountRepository,
            FamilyAccessService familyAccessService
    ) {
        this.budgetRepository = budgetRepository;
        this.familyRepository = familyRepository;
        this.categoryRepository = categoryRepository;
        this.userAccountRepository = userAccountRepository;
        this.familyAccessService = familyAccessService;
    }

    @Transactional
    public BudgetResponse create(UUID familyId, UUID actorUserId, CreateBudgetRequest request) {
        familyAccessService.requireMember(familyId, actorUserId);
        if (request.scope() == BudgetScope.FAMILY) {
            familyAccessService.requirePrimary(familyId, actorUserId);
        }
        if (request.scope() == BudgetScope.USER && request.userId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User budget requires userId");
        }
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Family not found"));
        Category category = categoryRepository.findByIdAndFamilyId(request.categoryId(), familyId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Category not found in family"));
        UserAccount user = null;
        if (request.scope() == BudgetScope.USER) {
            familyAccessService.requireMember(familyId, request.userId());
            user = userAccountRepository.findById(request.userId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        }
        return toResponse(budgetRepository.save(new Budget(
                family,
                request.scope(),
                user,
                category,
                request.amount(),
                YearMonth.parse(request.month())
        )));
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> list(UUID familyId, UUID userId) {
        familyAccessService.requireMember(familyId, userId);
        return budgetRepository.findByFamilyIdOrderByMonthDesc(familyId).stream()
                .map(this::toResponse)
                .toList();
    }

    private BudgetResponse toResponse(Budget budget) {
        return new BudgetResponse(
                budget.getId(),
                budget.getScope().name(),
                budget.getUser() == null ? null : budget.getUser().getId(),
                budget.getCategory().getId(),
                budget.getAmount(),
                budget.getMonth(),
                budget.getStatus().name()
        );
    }
}
