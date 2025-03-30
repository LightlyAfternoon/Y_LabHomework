package org.example.repository;

import org.example.model.TransactionCategoryEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface TransactionCategoryRepository extends Repository<TransactionCategoryEntity, Integer> {
    TransactionCategoryEntity findById(int id);

    TransactionCategoryEntity findByName(String name);

    List<TransactionCategoryEntity> findAll();

    List<TransactionCategoryEntity> findCommonCategoriesOrGoalsWithUserId(int userId);

    List<TransactionCategoryEntity> findAllGoalsWithUserId(int userId);

    @Modifying
    TransactionCategoryEntity save(TransactionCategoryEntity entity);

    @Modifying
    void delete(TransactionCategoryEntity entity);
}