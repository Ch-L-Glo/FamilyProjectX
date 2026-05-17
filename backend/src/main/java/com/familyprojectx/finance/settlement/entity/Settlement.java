package com.familyprojectx.finance.settlement.entity;

import com.familyprojectx.finance.family.entity.Family;
import com.familyprojectx.finance.transaction.entity.FinancialTransaction;
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
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "settlements")
public class Settlement {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "family_id")
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_user_id")
    private UserAccount fromUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_user_id")
    private UserAccount toUser;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "related_transaction_id")
    private FinancialTransaction relatedTransaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status = SettlementStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Settlement() {
    }

    public Settlement(Family family, UserAccount fromUser, UserAccount toUser, BigDecimal amount, String currency, FinancialTransaction relatedTransaction) {
        this.family = family;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
        this.currency = currency.toUpperCase();
        this.relatedTransaction = relatedTransaction;
    }

    public UUID getId() {
        return id;
    }

    public UserAccount getFromUser() {
        return fromUser;
    }

    public UserAccount getToUser() {
        return toUser;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public FinancialTransaction getRelatedTransaction() {
        return relatedTransaction;
    }

    public SettlementStatus getStatus() {
        return status;
    }

    public void markSettled() {
        this.status = SettlementStatus.SETTLED;
    }
}
