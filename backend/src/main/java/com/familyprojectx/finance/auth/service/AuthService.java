package com.familyprojectx.finance.auth.service;

import com.familyprojectx.finance.auth.dto.AuthResponse;
import com.familyprojectx.finance.auth.dto.LoginRequest;
import com.familyprojectx.finance.auth.dto.MeResponse;
import com.familyprojectx.finance.auth.dto.PasswordResetConfirmRequest;
import com.familyprojectx.finance.auth.dto.PasswordResetRequest;
import com.familyprojectx.finance.auth.dto.RegisterRequest;
import com.familyprojectx.finance.auth.entity.PasswordResetToken;
import com.familyprojectx.finance.auth.repository.PasswordResetTokenRepository;
import com.familyprojectx.finance.common.exception.ApiException;
import com.familyprojectx.finance.common.security.JwtService;
import com.familyprojectx.finance.family.entity.Family;
import com.familyprojectx.finance.family.entity.FamilyMember;
import com.familyprojectx.finance.family.entity.FamilyRole;
import com.familyprojectx.finance.family.repository.FamilyMemberRepository;
import com.familyprojectx.finance.family.repository.FamilyRepository;
import com.familyprojectx.finance.user.entity.UserAccount;
import com.familyprojectx.finance.user.repository.UserAccountRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailSender emailSender;
    private final long passwordResetExpirationMinutes;
    private final String frontendUrl;

    public AuthService(
            UserAccountRepository userAccountRepository,
            FamilyRepository familyRepository,
            FamilyMemberRepository familyMemberRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            EmailSender emailSender,
            @Value("${app.password-reset.expiration-minutes}") long passwordResetExpirationMinutes,
            @Value("${app.password-reset.frontend-url}") String frontendUrl
    ) {
        this.userAccountRepository = userAccountRepository;
        this.familyRepository = familyRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailSender = emailSender;
        this.passwordResetExpirationMinutes = passwordResetExpirationMinutes;
        this.frontendUrl = frontendUrl;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userAccountRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        }
        UserAccount user = userAccountRepository.save(new UserAccount(
                request.email().toLowerCase(),
                passwordEncoder.encode(request.password())
        ));
        Family family = familyRepository.save(new Family(request.familyName(), request.baseCurrency()));
        familyMemberRepository.save(new FamilyMember(family, user, FamilyRole.PRIMARY));
        return token(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserAccount user = userAccountRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return token(user);
    }

    @Transactional(readOnly = true)
    public MeResponse me(UUID userId) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        return new MeResponse(
                user.getId(),
                user.getEmail(),
                familyMemberRepository.findByUserId(userId).stream()
                        .map(member -> new MeResponse.FamilySummary(
                                member.getFamily().getId(),
                                member.getFamily().getName(),
                                member.getRole().name(),
                                member.getFamily().getBaseCurrency()
                        ))
                        .toList()
        );
    }

    @Transactional
    public void requestPasswordReset(PasswordResetRequest request) {
        userAccountRepository.findByEmailIgnoreCase(request.email()).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            passwordResetTokenRepository.save(new PasswordResetToken(
                    user,
                    token,
                    Instant.now().plusSeconds(passwordResetExpirationMinutes * 60)
            ));
            emailSender.sendPasswordReset(user.getEmail(), frontendUrl + "/reset-password?token=" + token);
        });
    }

    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid reset token"));
        if (!token.isUsable()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Reset token expired or already used");
        }
        token.getUser().setPasswordHash(passwordEncoder.encode(request.newPassword()));
        token.markUsed();
    }

    private AuthResponse token(UserAccount user) {
        return new AuthResponse(jwtService.generateToken(user.getId(), user.getEmail()), user.getId(), user.getEmail());
    }
}
