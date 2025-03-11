package org.example.model;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionCategoryEntity {
    private final UUID uuid;
    private String name;
    /**
     * Fields user and neededSum are meant for users goals
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
        this.user = user.getCopy();
    }

    public TransactionCategoryEntity(UUID uuid, UserEntity user) {
        this.uuid = uuid;
        if (user != null) {
            this.user = user.getCopy();
        } else {
            this.user = null;
        }
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
        if (user != null) {
            return user.getCopy();
        } else {
            return null;
        }
    }

    public BigDecimal getNeededSum() {
        return neededSum;
    }

    public void setNeededSum(BigDecimal neededSum) {
        this.neededSum = neededSum;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TransactionCategoryEntity)) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        TransactionCategoryEntity transactionCategory = (TransactionCategoryEntity) obj;

        return this.name.equals(transactionCategory.name) &&
                ((this.user == null && transactionCategory.user == null) || (this.user != null && this.user.equals(transactionCategory.user))) &&
                ((this.neededSum == null && transactionCategory.neededSum == null) || (this.neededSum != null && this.neededSum.compareTo(transactionCategory.neededSum) == 0));
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();

        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (neededSum != null ? neededSum.hashCode() : 0);

        return result;
    }

    public TransactionCategoryEntity getCopy() {
        TransactionCategoryEntity transactionCategoryEntityCopy = new TransactionCategoryEntity(this.uuid, this.user);

        transactionCategoryEntityCopy.name = this.name;
        transactionCategoryEntityCopy.neededSum = this.neededSum;

        return transactionCategoryEntityCopy;
    }
}