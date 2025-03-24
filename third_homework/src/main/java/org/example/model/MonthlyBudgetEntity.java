package org.example.model;

import org.example.servlet.dto.Default;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MonthlyBudgetEntity {
    private int id;
    private final int userId;
    private final Date date;
    private BigDecimal sum;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");

    private MonthlyBudgetEntity(MonthlyBudgetBuilder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.date = builder.date;
        this.sum = builder.sum;
    }

    public static class MonthlyBudgetBuilder {
        private int id;
        private int userId;
        private Date date;
        private BigDecimal sum;

        public MonthlyBudgetBuilder(int userId, BigDecimal sum) {
            this.userId = userId;
            this.sum = sum;
        }

        public MonthlyBudgetBuilder id(int id) {
            this.id = id;

            return this;
        }

        public MonthlyBudgetBuilder date(Date date) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");

            try {
                this.date = new Date(simpleDateFormat.parse(String.valueOf(date)).getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            return this;
        }

        public MonthlyBudgetEntity build() {
            return new MonthlyBudgetEntity(this);
        }
    }

    public MonthlyBudgetEntity(int userId) {
        this.userId = userId;

        Date newDate = new Date(System.currentTimeMillis());

        try {
            this.date = new Date(simpleDateFormat.parse(String.valueOf(newDate)).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public MonthlyBudgetEntity(int userId, Date date) {
        this.userId = userId;

        try {
            this.date = new Date(simpleDateFormat.parse(String.valueOf(date)).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Default
    public MonthlyBudgetEntity(int id, int userId, Date date) {
        this.id = id;
        this.userId = userId;

        try {
            this.date = new Date(simpleDateFormat.parse(String.valueOf(date)).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
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

        return this.userId == monthlyBudgetEntity.userId &&
                this.date.equals(monthlyBudgetEntity.date) &&
                this.sum.equals(monthlyBudgetEntity.sum);
    }

    @Override
    public int hashCode() {
        int result = userId;

        result = 31 * result + date.hashCode();
        result = 31 * result + sum.hashCode();

        return result;
    }
}