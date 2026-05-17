package com.familyprojectx.finance.budget.entity;

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
import java.time.YearMonth;
import java.util.UUID;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "family_id")
    private Family family;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetScope scope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 7)
    private String month;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetStatus status = BudgetStatus.ACTIVE;

    protected Budget() {
    }

    public Budget(Family family, BudgetScope scope, UserAccount user, Category category, BigDecimal amount, YearMonth month) {
        this.family = family;
        this.scope = scope;
        this.user = user;
        this.category = category;
        this.amount = amount;
        this.month = month.toString();
    }

    public UUID getId() {
        return id;
    }

    public BudgetScope getScope() {
        return scope;
    }

    public UserAccount getUser() {
        return user;
    }

    public Category getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getMonth() {
        return month;
    }

    public BudgetStatus getStatus() {
        return status;
    }
}
