package org.example.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.UUID;

public class MonthlyBudgetEntity {
    private final UUID uuid;
    private final UserEntity user;
    private final Date date;
    private BigDecimal sum;

    public MonthlyBudgetEntity(UserEntity user) {
        this.uuid = UUID.randomUUID();
        this.user = user.getCopy();
        this.date = new Date(System.currentTimeMillis());
    }

    public MonthlyBudgetEntity(UserEntity user, Date date) {
        this.uuid = UUID.randomUUID();
        this.user = user.getCopy();
        this.date = date;
    }

    public MonthlyBudgetEntity(UUID uuid, UserEntity user, Date date) {
        this.uuid = uuid;
        this.user = user.getCopy();
        this.date = date;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UserEntity getUser() {
        return user.getCopy();
    }

    public Date getDate() {
        return date;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MonthlyBudgetEntity)) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        MonthlyBudgetEntity monthlyBudgetEntity = (MonthlyBudgetEntity) obj;

        return this.user.equals(monthlyBudgetEntity.user) &&
                this.date.equals(monthlyBudgetEntity.date) &&
                this.sum.equals(monthlyBudgetEntity.sum);
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();

        result = 31 * result + date.hashCode();
        result = 31 * result + sum.hashCode();

        return result;
    }

    public MonthlyBudgetEntity getCopy() {
        MonthlyBudgetEntity monthlyBudgetEntityCopy = new MonthlyBudgetEntity(this.uuid, this.user, this.date);

        monthlyBudgetEntityCopy.sum = this.sum;

        return monthlyBudgetEntityCopy;
    }
}