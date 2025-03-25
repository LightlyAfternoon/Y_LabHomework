package org.example.servlet.dto;

import java.math.BigDecimal;

public class TransactionCategoryDTO {
    private int id;
    private String name;
    private BigDecimal neededSum;
    /**
     * Fields user and neededSum are meant for users goals
     */
    private int userId;

    private TransactionCategoryDTO(TransactionCategoryBuilder builder) {
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

        public TransactionCategoryDTO build() {
            return new TransactionCategoryDTO(this);
        }
    }

    public TransactionCategoryDTO() {}

    @Default
    public TransactionCategoryDTO(int id, int userId) {
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

    public int getUserId() {
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
        if (!(obj instanceof TransactionCategoryDTO)) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        TransactionCategoryDTO transactionCategory = (TransactionCategoryDTO) obj;

        return this.name.equals(transactionCategory.name) &&
                ((this.userId == 0 && transactionCategory.userId == 0) || (this.userId != 0 && this.userId == transactionCategory.userId)) &&
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
        return this.getName() + " id: " + this.getId() + (this.getUserId() != 0 ? " id пользователя: " + this.getUserId() : "");
    }
}