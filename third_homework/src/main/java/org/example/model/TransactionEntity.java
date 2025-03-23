package org.example.model;

import org.example.servlet.dto.Default;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TransactionEntity {
    private int id;
    /**
     * Field sum can be positive number as money arriving or negative number as money spending
     */
    private BigDecimal sum;
    private Date date;
    private String description;
    /**
     * Field category is meant for a category or a goal of money spent
     */
    private int categoryId;
    private final int userId;

    private TransactionEntity(TransactionBuilder builder) {
        this.id = builder.id;
        this.sum = builder.sum;
        this.date = builder.date;
        this.description = builder.description;
        this.categoryId = builder.categoryId;
        this.userId = builder.userId;
    }

    public static class TransactionBuilder {
        private int id;
        private BigDecimal sum;
        private Date date;
        private String description;
        private int categoryId;
        private int userId;

        public TransactionBuilder(BigDecimal sum, int userId) {
            this.sum = sum;
            this.userId = userId;
        }

        public TransactionBuilder id(int id) {
            this.id = id;

            return this;
        }

        public TransactionBuilder date(Date date) {
            if (date != null) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    this.date = new Date(dateFormat.parse(date.toString()).getTime());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                this.date = null;
            }

            return this;
        }

        public TransactionBuilder description(String description) {
            this.description = description;

            return this;
        }

        public TransactionBuilder categoryId(int categoryId) {
            this.categoryId = categoryId;

            return this;
        }

        public TransactionEntity build() {
            return new TransactionEntity(this);
        }
    }

    public TransactionEntity(int userId) {
        this.userId = userId;
    }

    @Default
    public TransactionEntity(int id, int userId) {
        this.id = id;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                this.date = new Date(dateFormat.parse(date.toString()).getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.date = null;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserId() {
        return userId;
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
                ((this.categoryId == 0 && transaction.categoryId == 0) || (this.categoryId != 0 && this.categoryId == transaction.categoryId)) &&
                this.date.equals(transaction.date) &&
                ((this.description == null && transaction.description == null) || (this.description != null && this.description.equals(transaction.description))) &&
                this.userId == transaction.userId;
    }

    @Override
    public int hashCode() {
        int result = sum.hashCode();

        result = 31 * result + categoryId;
        result = 31 * result + date.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + userId;

        return result;
    }

    @Override
    public String toString() {
        return "Дата:" + this.getDate() + " Сумма: " + this.getSum() + " id категории/цели: " + (this.getCategoryId() != 0? this.getCategoryId() : "none") + " id: " + this.getId() + " Описание: " + (this.getDescription() != null? this.getDescription() : "");
    }
}