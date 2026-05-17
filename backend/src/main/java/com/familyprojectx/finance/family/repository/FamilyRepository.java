package com.familyprojectx.finance.family.repository;

import com.familyprojectx.finance.family.entity.Family;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRepository extends JpaRepository<Family, UUID> {
}
