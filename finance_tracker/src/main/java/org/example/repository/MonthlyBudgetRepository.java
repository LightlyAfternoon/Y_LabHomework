package org.example.repository;

import org.example.model.MonthlyBudgetEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;

import java.sql.Date;
import java.util.List;

@org.springframework.stereotype.Repository
public interface MonthlyBudgetRepository extends Repository<MonthlyBudgetEntity, Integer> {
    MonthlyBudgetEntity findById(int id);

    MonthlyBudgetEntity findByDateAndUserId(Date date, int userId);

    List<MonthlyBudgetEntity> findAll();

    @Modifying
    MonthlyBudgetEntity save(MonthlyBudgetEntity entity);

    @Modifying
    void delete(MonthlyBudgetEntity entity);
}