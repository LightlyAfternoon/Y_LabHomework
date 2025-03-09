package org.example.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class MonthlyBudgetEntity {
    private final UUID uuid;
    private final UserEntity user;
    private final Date date;
    private BigDecimal sum;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");

    public MonthlyBudgetEntity(UserEntity user) {
        this.uuid = UUID.randomUUID();
        this.user = user.getCopy();

        Date newDate = new Date(System.currentTimeMillis());

        try {
            this.date = new Date(simpleDateFormat.parse(String.valueOf(newDate)).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public MonthlyBudgetEntity(UserEntity user, Date date) {
        this.uuid = UUID.randomUUID();
        this.user = user.getCopy();

        try {
            this.date = new Date(simpleDateFormat.parse(String.valueOf(date)).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public MonthlyBudgetEntity(UUID uuid, UserEntity user, Date date) {
        this.uuid = uuid;
        this.user = user.getCopy();

        try {
            this.date = new Date(simpleDateFormat.parse(String.valueOf(date)).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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