package com.familyprojectx.finance.auth.service;

public interface EmailSender {

    void sendPasswordReset(String email, String resetLink);

    void sendFamilyInvitation(String email, String inviteLink);
}
