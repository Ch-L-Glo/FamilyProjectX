package com.familyprojectx.finance.family.controller;

import com.familyprojectx.finance.common.security.CurrentUser;
import com.familyprojectx.finance.family.dto.AcceptInvitationRequest;
import com.familyprojectx.finance.family.dto.FamilyMemberResponse;
import com.familyprojectx.finance.family.dto.InviteMemberRequest;
import com.familyprojectx.finance.family.service.FamilyService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/families/{familyId}")
public class FamilyController {

    private final FamilyService familyService;
    private final CurrentUser currentUser;

    public FamilyController(FamilyService familyService, CurrentUser currentUser) {
        this.familyService = familyService;
        this.currentUser = currentUser;
    }

    @GetMapping("/members")
    public List<FamilyMemberResponse> members(@PathVariable UUID familyId, Authentication authentication) {
        return familyService.members(familyId, currentUser.requireUserId(authentication));
    }

    @PostMapping("/invitations")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void invite(@PathVariable UUID familyId, Authentication authentication, @Valid @RequestBody InviteMemberRequest request) {
        familyService.invite(familyId, currentUser.requireUserId(authentication), request);
    }

    @PostMapping("/invitations/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptInvitation(Authentication authentication, @Valid @RequestBody AcceptInvitationRequest request) {
        familyService.acceptInvitation(currentUser.requireUserId(authentication), request);
    }
}
