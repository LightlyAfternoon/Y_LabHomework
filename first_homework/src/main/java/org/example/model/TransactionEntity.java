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
        this.user = user.getCopy();
    }

    public TransactionEntity(UUID uuid, UserEntity user) {
        this.uuid = uuid;
        this.user = user.getCopy();
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
        if (category != null) {
            return category.getCopy();
        } else {
            return null;
        }
    }

    public void setCategory(TransactionCategoryEntity category) {
        if (category != null) {
            this.category = category.getCopy();
        } else {
            this.category = null;
        }
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
        return user.getCopy();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TransactionEntity)) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        TransactionEntity transaction = (TransactionEntity) obj;

        return this.sum.compareTo(transaction.sum) == 0 &&
                ((this.category == null && transaction.category == null) || (this.category != null && this.category.equals(transaction.category))) &&
                this.date.equals(transaction.date) &&
                ((this.description == null && transaction.description == null) || (this.description != null && this.description.equals(transaction.description))) &&
                this.user.equals(transaction.user);
    }

    @Override
    public int hashCode() {
        int result = sum.hashCode();

        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + date.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + user.hashCode();

        return result;
    }

    public TransactionEntity getCopy() {
        TransactionEntity transactionEntityCopy = new TransactionEntity(this.uuid, this.user);

        transactionEntityCopy.sum = this.sum;
        transactionEntityCopy.category = this.category;
        transactionEntityCopy.date = this.date;
        transactionEntityCopy.description = this.description;

        return transactionEntityCopy;
    }

    @Override
    public String toString() {
        return this.getDate() + " " + this.getSum() + " " + (this.getCategory() != null? this.getCategory().getName() : "none") + " " + this.getUuid() + " " + (this.getDescription() != null? this.getDescription() : "");
    }
}