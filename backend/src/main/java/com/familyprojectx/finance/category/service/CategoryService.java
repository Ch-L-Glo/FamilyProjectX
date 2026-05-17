package com.familyprojectx.finance.category.service;

import com.familyprojectx.finance.category.dto.CategoryResponse;
import com.familyprojectx.finance.category.dto.CreateCategoryRequest;
import com.familyprojectx.finance.category.entity.Category;
import com.familyprojectx.finance.category.repository.CategoryRepository;
import com.familyprojectx.finance.common.exception.ApiException;
import com.familyprojectx.finance.family.entity.Family;
import com.familyprojectx.finance.family.repository.FamilyRepository;
import com.familyprojectx.finance.family.service.FamilyAccessService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final FamilyRepository familyRepository;
    private final FamilyAccessService familyAccessService;

    public CategoryService(CategoryRepository categoryRepository, FamilyRepository familyRepository, FamilyAccessService familyAccessService) {
        this.categoryRepository = categoryRepository;
        this.familyRepository = familyRepository;
        this.familyAccessService = familyAccessService;
    }

    @Transactional
    public CategoryResponse create(UUID familyId, UUID userId, CreateCategoryRequest request) {
        familyAccessService.requirePrimary(familyId, userId);
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Family not found"));
        return toResponse(categoryRepository.save(new Category(family, request.name(), request.type())));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list(UUID familyId, UUID userId) {
        familyAccessService.requireMember(familyId, userId);
        return categoryRepository.findByFamilyIdAndActiveTrueOrderByName(familyId).stream()
                .map(this::toResponse)
                .toList();
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getType().name(), category.isActive());
    }
}
