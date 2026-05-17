package com.familyprojectx.finance.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConsoleEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailSender.class);

    @Override
    public void sendPasswordReset(String email, String resetLink) {
        log.info("Password reset email to {}: {}", email, resetLink);
    }

    @Override
    public void sendFamilyInvitation(String email, String inviteLink) {
        log.info("Family invitation email to {}: {}", email, inviteLink);
    }
}
