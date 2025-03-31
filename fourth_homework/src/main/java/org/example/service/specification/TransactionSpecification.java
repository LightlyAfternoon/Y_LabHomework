package org.example.service.specification;

import org.example.model.TransactionEntity;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Date;

/**
 * This class is used to construct transactions filters
 */
public class TransactionSpecification {
    private TransactionSpecification() {}

    public static Specification<TransactionEntity> dateIs(Date date) {
        if (date != null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("date"), date);
        } else {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
    }

    public static Specification<TransactionEntity> categoryIdIs(int categoryId) {
        if (categoryId > 0) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("categoryId"), categoryId);
        } else {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
    }

    public static Specification<TransactionEntity> sumType(String type) {
        if (type.equals("Pos")) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThan(root.get("sum"), BigDecimal.valueOf(0));
        } else if (type.equals("Neg")) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("sum"), BigDecimal.valueOf(0));
        } else {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
    }

    public static Specification<TransactionEntity> userIdIs(int userId) {
        if (userId > 0) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId);
        } else {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
    }
}