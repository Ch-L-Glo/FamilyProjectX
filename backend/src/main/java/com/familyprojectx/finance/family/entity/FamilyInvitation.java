package com.familyprojectx.finance.family.entity;

import com.familyprojectx.finance.user.entity.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "family_invitations")
public class FamilyInvitation {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "family_id")
    private Family family;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FamilyRole role;

    @Column(nullable = false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invited_by_user_id")
    private UserAccount invitedBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant acceptedAt;

    protected FamilyInvitation() {
    }

    public FamilyInvitation(Family family, String email, FamilyRole role, String token, UserAccount invitedBy) {
        this.family = family;
        this.email = email;
        this.role = role;
        this.token = token;
        this.invitedBy = invitedBy;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public FamilyRole getRole() {
        return role;
    }

    public Family getFamily() {
        return family;
    }

    public boolean isAccepted() {
        return acceptedAt != null;
    }

    public void markAccepted() {
        this.acceptedAt = Instant.now();
    }
}
