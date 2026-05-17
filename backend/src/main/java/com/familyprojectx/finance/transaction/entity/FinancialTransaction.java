package com.familyprojectx.finance.transaction.entity;

import com.familyprojectx.finance.category.entity.Category;
import com.familyprojectx.finance.family.entity.Family;
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
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class FinancialTransaction {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "family_id")
    private Family family;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amountOriginal;

    @Column(nullable = false, length = 3)
    private String currencyOriginal;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal exchangeRate;

    @Column(nullable = false)
    private String exchangeRateSource;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amountBase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private LocalDate transactionDate;

    private String notes;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id")
    private UserAccount createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdDate = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paid_by_user_id")
    private UserAccount paidBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionOwnershipType ownershipType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.APPROVED;

    protected FinancialTransaction() {
    }

    public FinancialTransaction(
            Family family,
            BigDecimal amountOriginal,
            String currencyOriginal,
            BigDecimal exchangeRate,
            String exchangeRateSource,
            BigDecimal amountBase,
            Category category,
            LocalDate transactionDate,
            String notes,
            UserAccount createdBy,
            UserAccount paidBy,
            TransactionOwnershipType ownershipType,
            TransactionType transactionType
    ) {
        this.family = family;
        this.amountOriginal = amountOriginal;
        this.currencyOriginal = currencyOriginal.toUpperCase();
        this.exchangeRate = exchangeRate;
        this.exchangeRateSource = exchangeRateSource;
        this.amountBase = amountBase;
        this.category = category;
        this.transactionDate = transactionDate;
        this.notes = notes;
        this.createdBy = createdBy;
        this.paidBy = paidBy;
        this.ownershipType = ownershipType;
        this.transactionType = transactionType;
    }

    public UUID getId() {
        return id;
    }

    public Family getFamily() {
        return family;
    }

    public BigDecimal getAmountBase() {
        return amountBase;
    }

    public BigDecimal getAmountOriginal() {
        return amountOriginal;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public String getCurrencyOriginal() {
        return currencyOriginal;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public UserAccount getPaidBy() {
        return paidBy;
    }

    public TransactionOwnershipType getOwnershipType() {
        return ownershipType;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public TransactionStatus getStatus() {
        return status;
    }
}
