package org.example.repository;

import org.example.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface TransactionRepository extends Repository<TransactionEntity, Integer>, JpaSpecificationExecutor<TransactionEntity> {
    TransactionEntity findById(int id);

    List<TransactionEntity> findAll();

    List<TransactionEntity> findAllByUserId(int userId);

    @Modifying
    TransactionEntity save(TransactionEntity entity);

    @Modifying
    void delete(TransactionEntity entity);
}