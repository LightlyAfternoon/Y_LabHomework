package org.example.model;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionCategoryEntity {
    private final UUID uuid;
    private String name;
    /**
     * Fields user and neededSum meant for users goals
     */
    private UserEntity user;
    private BigDecimal neededSum;

    public TransactionCategoryEntity() {
        this.uuid = UUID.randomUUID();
    }

    public TransactionCategoryEntity(UUID uuid) {
        this.uuid = uuid;
    }

    public TransactionCategoryEntity(UserEntity user) {
        this.uuid = UUID.randomUUID();
        this.user = user;
    }

    public TransactionCategoryEntity(UUID uuid, UserEntity user) {
        this.uuid = uuid;
        this.user = user;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserEntity getUser() {
        return user;
    }

    public BigDecimal getNeededSum() {
        return neededSum;
    }

    public void setNeededSum(BigDecimal neededSum) {
        this.neededSum = neededSum;
    }
}