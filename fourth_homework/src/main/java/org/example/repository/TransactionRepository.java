package org.example.repository;

import org.example.model.TransactionEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;

import java.sql.Date;
import java.util.List;

@org.springframework.stereotype.Repository
public interface TransactionRepository extends Repository<TransactionEntity, Integer> {
    TransactionEntity findById(int id);

    List<TransactionEntity> findAll();

    List<TransactionEntity> findAllByUserId(int userId);

    List<TransactionEntity> findAllByDateAndCategoryIdAndTypeAndUserId(Date date, int categoryId, String type, int userId);

    default String getFilteredSql(Date date, int categoryId, String type) {
        String sql = "select * from not_public.transaction where";
        boolean moreThanOneParameters = false;

        if (date != null) {
            sql += " date = ?";
            moreThanOneParameters = true;
        }

        if (categoryId != 0) {
            if (moreThanOneParameters) {
                sql += " and";
            }

            sql += " category_id = ?";
            moreThanOneParameters = true;
        }

        if (type != null && !type.isBlank()) {
            if (moreThanOneParameters) {
                sql += " and";
            }

            if (type.equals("Pos")) {
                sql += " sum >= ?";
            } else {
                sql += " sum < ?";
            }
            moreThanOneParameters = true;
        }

        if (moreThanOneParameters) {
            sql += " and";
        }

        sql += " user_id = ?";

        return sql;
    }

    @Modifying
    TransactionEntity save(TransactionEntity entity);

    @Modifying
    void delete(TransactionEntity entity);
}