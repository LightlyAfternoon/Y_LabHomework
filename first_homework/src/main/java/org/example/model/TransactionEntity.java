package org.example.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.UUID;

public class TransactionEntity {
    private final UUID uuid;
    /**
     * Field sum can be positive number as money arriving or negative number as money spending
     */
    private BigDecimal sum;
    /**
     * Field category is meant for a category or a goal of money spent
     */
    private TransactionCategoryEntity category;
    private Date date;
    private String description;
    private final UserEntity user;

    public TransactionEntity(UserEntity user) {
        this.uuid = UUID.randomUUID();
        this.user = user;
    }

    public TransactionEntity(UUID uuid, UserEntity user) {
        this.uuid = uuid;
        this.user = user;
    }

    public UUID getUuid() {
        return uuid;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public TransactionCategoryEntity getCategory() {
        return category;
    }

    public void setCategory(TransactionCategoryEntity category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserEntity getUser() {
        return user;
    }
}