package com.familyprojectx.finance.family.service;

import com.familyprojectx.finance.auth.service.EmailSender;
import com.familyprojectx.finance.common.exception.ApiException;
import com.familyprojectx.finance.family.dto.AcceptInvitationRequest;
import com.familyprojectx.finance.family.dto.FamilyMemberResponse;
import com.familyprojectx.finance.family.dto.InviteMemberRequest;
import com.familyprojectx.finance.family.entity.Family;
import com.familyprojectx.finance.family.entity.FamilyInvitation;
import com.familyprojectx.finance.family.entity.FamilyMember;
import com.familyprojectx.finance.family.entity.FamilyRole;
import com.familyprojectx.finance.family.repository.FamilyInvitationRepository;
import com.familyprojectx.finance.family.repository.FamilyMemberRepository;
import com.familyprojectx.finance.family.repository.FamilyRepository;
import com.familyprojectx.finance.user.entity.UserAccount;
import com.familyprojectx.finance.user.repository.UserAccountRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyInvitationRepository familyInvitationRepository;
    private final UserAccountRepository userAccountRepository;
    private final FamilyAccessService familyAccessService;
    private final EmailSender emailSender;
    private final String frontendUrl;

    public FamilyService(
            FamilyRepository familyRepository,
            FamilyMemberRepository familyMemberRepository,
            FamilyInvitationRepository familyInvitationRepository,
            UserAccountRepository userAccountRepository,
            FamilyAccessService familyAccessService,
            EmailSender emailSender,
            @Value("${app.password-reset.frontend-url}") String frontendUrl
    ) {
        this.familyRepository = familyRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.familyInvitationRepository = familyInvitationRepository;
        this.userAccountRepository = userAccountRepository;
        this.familyAccessService = familyAccessService;
        this.emailSender = emailSender;
        this.frontendUrl = frontendUrl;
    }

    public List<FamilyMemberResponse> members(UUID familyId, UUID userId) {
        familyAccessService.requireMember(familyId, userId);
        return familyMemberRepository.findByFamilyId(familyId).stream()
                .map(member -> new FamilyMemberResponse(
                        member.getUser().getId(),
                        member.getUser().getEmail(),
                        member.getRole().name(),
                        member.getStatus().name()
                ))
                .toList();
    }

    @Transactional
    public void invite(UUID familyId, UUID userId, InviteMemberRequest request) {
        familyAccessService.requirePrimary(familyId, userId);
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Family not found"));
        UserAccount invitedBy = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        String token = UUID.randomUUID().toString();
        familyInvitationRepository.save(new FamilyInvitation(family, request.email().toLowerCase(), FamilyRole.NORMAL, token, invitedBy));
        emailSender.sendFamilyInvitation(request.email(), frontendUrl + "/family-invitation?token=" + token);
    }

    @Transactional
    public void acceptInvitation(UUID userId, AcceptInvitationRequest request) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        FamilyInvitation invitation = familyInvitationRepository.findByToken(request.token())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid invitation token"));
        if (invitation.isAccepted()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invitation already accepted");
        }
        if (!invitation.getEmail().equalsIgnoreCase(user.getEmail())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Invitation email does not match current user");
        }
        if (!familyMemberRepository.existsByFamilyIdAndUserId(invitation.getFamily().getId(), userId)) {
            familyMemberRepository.save(new FamilyMember(invitation.getFamily(), user, invitation.getRole()));
        }
        invitation.markAccepted();
    }
}
