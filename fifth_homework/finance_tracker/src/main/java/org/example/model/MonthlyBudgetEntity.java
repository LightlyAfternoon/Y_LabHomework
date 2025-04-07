package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.annotation.Default;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Getter
@Entity
@Table(name = "monthly_budget", schema = "not_public")
public class MonthlyBudgetEntity extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_monthly_budget_id")
    @SequenceGenerator(name = "seq_monthly_budget_id", allocationSize = 1)
    private int id;
    @Column(name = "user_id")
    private int userId;
    private Date date;
    @Setter
    private BigDecimal sum;

    @Transient
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

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");

        public MonthlyBudgetBuilder(int userId, BigDecimal sum) {
            this.userId = userId;
            this.sum = sum;
            try {
                this.date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        public MonthlyBudgetBuilder id(int id) {
            this.id = id;

            return this;
        }

        public MonthlyBudgetBuilder date(Date date) {
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

    public MonthlyBudgetEntity() {}

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