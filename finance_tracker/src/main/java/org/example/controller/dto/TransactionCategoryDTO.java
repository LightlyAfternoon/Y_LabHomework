package org.example.controller.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.annotation.Default;

import java.math.BigDecimal;

@Getter
public class TransactionCategoryDTO {
    private int id;
    @Setter
    private String name;
    @Setter
    private BigDecimal neededSum;
    /**
     * Fields user and neededSum are meant for users goals
     */
    private Integer userId;

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
        private Integer userId;

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

        public TransactionCategoryBuilder userId(Integer userId) {
            this.userId = userId;

            return this;
        }

        public TransactionCategoryDTO build() {
            return new TransactionCategoryDTO(this);
        }
    }

    public TransactionCategoryDTO() {}

    @Default
    public TransactionCategoryDTO(int id, Integer userId) {
        this.id = id;
        this.userId = userId;
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

    public static boolean isValid(TransactionCategoryDTO transactionCategoryDTO) {
        return (transactionCategoryDTO.getName() != null && !transactionCategoryDTO.getName().isBlank()) &&
                (((transactionCategoryDTO.getUserId() == null || transactionCategoryDTO.getUserId() == 0) && (transactionCategoryDTO.getNeededSum() == null || transactionCategoryDTO.getNeededSum().compareTo(BigDecimal.valueOf(0)) == 0))) ||
                        (transactionCategoryDTO.getUserId() != null && transactionCategoryDTO.getUserId() != 0 && (transactionCategoryDTO.getNeededSum() != null && transactionCategoryDTO.getNeededSum().compareTo(BigDecimal.valueOf(0)) > 0));
    }
}