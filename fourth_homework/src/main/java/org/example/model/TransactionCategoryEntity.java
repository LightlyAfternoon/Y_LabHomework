package org.example.model;

import jakarta.persistence.*;
import org.example.annotation.Default;

import java.math.BigDecimal;

@Entity
@Table(name = "transaction_category", schema = "not_public")
public class TransactionCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_transaction_category_id")
    @SequenceGenerator(name = "seq_transaction_category_id", allocationSize = 1)
    private int id;
    private String name;
    @Column(name = "needed_sum")
    private BigDecimal neededSum;
    /**
     * Fields user and neededSum are meant for users goals
     */
    @Column(name = "user_id")
    private Integer userId;

    private TransactionCategoryEntity(TransactionCategoryBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.neededSum = builder.neededSum;
        this.userId = builder.userId;
    }

    public static class TransactionCategoryBuilder {
        private int id;
        private String name;
        private BigDecimal neededSum;
        private int userId;

        public TransactionCategoryBuilder(String name) {
            this.name = name;
        }

        public TransactionCategoryBuilder id(int id) {
            this.id = id;

            return this;
        }

        public TransactionCategoryBuilder neededSum(BigDecimal neededSum) {
            this.neededSum = neededSum;

            return this;
        }

        public TransactionCategoryBuilder userId(int userId) {
            this.userId = userId;

            return this;
        }

        public TransactionCategoryEntity build() {
            return new TransactionCategoryEntity(this);
        }
    }

    public TransactionCategoryEntity() {}

    @Default
    public TransactionCategoryEntity(int id, Integer userId) {
        this.id = id;
        this.userId = userId;
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

    public Integer getUserId() {
        return userId;
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
                (((this.userId == null || this.userId == 0) && (transactionCategory.userId == null || transactionCategory.userId == 0))
                        || (this.userId != null && this.userId != 0 && this.userId.equals(transactionCategory.userId))) &&
                ((this.neededSum == null && transactionCategory.neededSum == null) || (this.neededSum != null && this.neededSum.compareTo(transactionCategory.neededSum) == 0));
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();

        result = 31 * result + userId;
        result = 31 * result + (neededSum != null ? neededSum.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return this.getName() + " id: " + this.getId() + (userId != null && this.getUserId() != 0 ? " id пользователя: " + this.getUserId() : "");
    }
}