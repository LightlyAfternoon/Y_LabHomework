package org.example.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.example.annotation.Default;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Getter
public class MonthlyBudgetDTO {
    private int id;
    private int userId;
    private Date date;
    @Setter
    private BigDecimal sum;

    @JsonIgnore
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");

    private MonthlyBudgetDTO(MonthlyBudgetBuilder builder) {
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

        public MonthlyBudgetDTO build() {
            return new MonthlyBudgetDTO(this);
        }
    }

    public MonthlyBudgetDTO(int userId) {
        this.userId = userId;

        Date newDate = new Date(System.currentTimeMillis());

        try {
            this.date = new Date(simpleDateFormat.parse(String.valueOf(newDate)).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public MonthlyBudgetDTO(int userId, Date date) {
        this.userId = userId;

        try {
            this.date = new Date(simpleDateFormat.parse(String.valueOf(date)).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Default
    public MonthlyBudgetDTO(int id, int userId, Date date) {
        this.id = id;
        this.userId = userId;

        try {
            this.date = new Date(simpleDateFormat.parse(String.valueOf(date)).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public MonthlyBudgetDTO() {}

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MonthlyBudgetDTO)) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        MonthlyBudgetDTO monthlyBudgetEntity = (MonthlyBudgetDTO) obj;

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

    public static boolean isValid(MonthlyBudgetDTO monthlyBudgetDTO) {
        return (monthlyBudgetDTO.getUserId() != 0) &&
                (monthlyBudgetDTO.getDate() != null) &&
                (monthlyBudgetDTO.getSum() != null && monthlyBudgetDTO.getSum().compareTo(BigDecimal.valueOf(0)) > 0);
    }
}