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
@Table(name = "family_members")
public class FamilyMember {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "family_id")
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FamilyRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FamilyMemberStatus status = FamilyMemberStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private Instant joinedAt = Instant.now();

    protected FamilyMember() {
    }

    public FamilyMember(Family family, UserAccount user, FamilyRole role) {
        this.family = family;
        this.user = user;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public Family getFamily() {
        return family;
    }

    public UserAccount getUser() {
        return user;
    }

    public FamilyRole getRole() {
        return role;
    }

    public FamilyMemberStatus getStatus() {
        return status;
    }
}
