package org.example.model;

import liquibase.exception.LiquibaseException;
import org.example.CurrentUser;
import org.example.repository.TransactionRepository;

import java.math.BigDecimal;
import java.sql.SQLException;

public class TransactionCategoryEntity {
    private int id;
    private String name;
    private BigDecimal neededSum;
    /**
     * Fields user and neededSum are meant for users goals
     */
    private UserEntity user;

    public TransactionCategoryEntity() {}

    public TransactionCategoryEntity(int id) {
        this.id = id;
    }

    public TransactionCategoryEntity(UserEntity user) {
        if (user != null) {
            this.user = user.getCopy();
        } else {
            this.user = null;
        }
    }

    public TransactionCategoryEntity(int id, UserEntity user) {
        this.id = id;
        if (user != null) {
            this.user = user.getCopy();
        } else {
            this.user = null;
        }
    }

    public int getId() {
        return id;
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
        TransactionCategoryEntity transactionCategoryEntityCopy = new TransactionCategoryEntity(this.id, this.user);

        transactionCategoryEntityCopy.name = this.name;
        transactionCategoryEntityCopy.neededSum = this.neededSum;

        return transactionCategoryEntityCopy;
    }

    @Override
    public String toString() {
        return this.getName() + " id: " + this.getId() + (this.getUser() != null? " id пользователя: " + this.getUser().getId() : "");
    }
}