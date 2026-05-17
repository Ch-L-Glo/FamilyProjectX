package com.familyprojectx.finance.family.repository;

import com.familyprojectx.finance.family.entity.FamilyMember;
import com.familyprojectx.finance.family.entity.FamilyRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, UUID> {

    boolean existsByFamilyIdAndUserId(UUID familyId, UUID userId);

    Optional<FamilyMember> findByFamilyIdAndUserId(UUID familyId, UUID userId);

    List<FamilyMember> findByFamilyId(UUID familyId);

    List<FamilyMember> findByUserId(UUID userId);

    boolean existsByFamilyIdAndUserIdAndRole(UUID familyId, UUID userId, FamilyRole role);
}
