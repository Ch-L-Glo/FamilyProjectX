package com.familyprojectx.finance.split.entity;

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
import java.util.UUID;

@Entity
@Table(name = "transaction_splits")
public class TransactionSplit {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id")
    private FinancialTransaction transaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SplitType splitType;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal shareAmount;

    @Column(precision = 9, scale = 4)
    private BigDecimal sharePercentage;

    protected TransactionSplit() {
    }

    public TransactionSplit(FinancialTransaction transaction, UserAccount user, SplitType splitType, BigDecimal shareAmount, BigDecimal sharePercentage) {
        this.transaction = transaction;
        this.user = user;
        this.splitType = splitType;
        this.shareAmount = shareAmount;
        this.sharePercentage = sharePercentage;
    }

    public UUID getId() {
        return id;
    }

    public UserAccount getUser() {
        return user;
    }

    public BigDecimal getShareAmount() {
        return shareAmount;
    }

    public BigDecimal getSharePercentage() {
        return sharePercentage;
    }
}
