package com.familyprojectx.finance.family.repository;

import com.familyprojectx.finance.family.entity.FamilyInvitation;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyInvitationRepository extends JpaRepository<FamilyInvitation, UUID> {

    Optional<FamilyInvitation> findByToken(String token);
}
