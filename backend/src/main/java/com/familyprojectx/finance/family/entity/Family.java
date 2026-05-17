package com.familyprojectx.finance.family.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "families")
public class Family {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 3)
    private String baseCurrency;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Family() {
    }

    public Family(String name, String baseCurrency) {
        this.name = name;
        this.baseCurrency = baseCurrency.toUpperCase();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }
}
