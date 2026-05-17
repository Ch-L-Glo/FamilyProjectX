package com.familyprojectx.finance.category.repository;

import com.familyprojectx.finance.category.entity.Category;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByIdAndFamilyId(UUID id, UUID familyId);

    List<Category> findByFamilyIdAndActiveTrueOrderByName(UUID familyId);
}
