package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.annotation.Default;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Getter
@Entity
@Table(name = "transaction", schema = "not_public")
public class TransactionEntity extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_transaction_id")
    @SequenceGenerator(name = "seq_transaction_id", allocationSize = 1)
    private int id;
    /**
     * Field sum can be positive number as money arriving or negative number as money spending
     */
    @Setter
    private BigDecimal sum;
    private Date date;
    @Setter
    private String description;
    /**
     * Field category is meant for a category or a goal of money spent
     */
    @Setter
    @Column(name = "category_id")
    private Integer categoryId;
    @Column(name = "user_id")
    private int userId;

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
        private Integer categoryId;
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
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            if (date != null) {
                try {
                    this.date = new Date(dateFormat.parse(date.toString()).getTime());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    this.date = new Date(dateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            return this;
        }

        public TransactionBuilder description(String description) {
            this.description = description;

            return this;
        }

        public TransactionBuilder categoryId(Integer categoryId) {
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

    public TransactionEntity() {}

    public void setDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (date != null) {
            try {
                this.date = new Date(dateFormat.parse(date.toString()).getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                this.date = new Date(dateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
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
                (((this.categoryId == null || this.categoryId == 0) && (transaction.categoryId == null || transaction.categoryId == 0))
                        || ((this.categoryId != null && this.categoryId != 0) && this.categoryId.equals(transaction.categoryId))) &&
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
        return "Дата:" + this.getDate() + " Сумма: " + this.getSum() + " id категории/цели: " + (this.getCategoryId() != null && this.getCategoryId() != 0 ? this.getCategoryId() : "none") + " id: " + this.getId() + " Описание: " + (this.getDescription() != null? this.getDescription() : "");
    }
}