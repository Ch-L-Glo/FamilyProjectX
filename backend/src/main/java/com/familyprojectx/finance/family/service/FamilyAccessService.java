package com.familyprojectx.finance.family.service;

import com.familyprojectx.finance.common.exception.ApiException;
import com.familyprojectx.finance.family.entity.FamilyRole;
import com.familyprojectx.finance.family.repository.FamilyMemberRepository;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FamilyAccessService {

    private final FamilyMemberRepository familyMemberRepository;

    public FamilyAccessService(FamilyMemberRepository familyMemberRepository) {
        this.familyMemberRepository = familyMemberRepository;
    }

    public void requireMember(UUID familyId, UUID userId) {
        if (!familyMemberRepository.existsByFamilyIdAndUserId(familyId, userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No access to this family");
        }
    }

    public void requirePrimary(UUID familyId, UUID userId) {
        if (!familyMemberRepository.existsByFamilyIdAndUserIdAndRole(familyId, userId, FamilyRole.PRIMARY)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Primary family member required");
        }
    }
}
