package org.example.repository;

import org.example.CurrentUser;
import org.example.model.MonthlyBudgetEntity;
import org.example.model.UserEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MonthlyBudgetRepository implements Repository<MonthlyBudgetEntity> {
    private static final List<MonthlyBudgetEntity> monthlyBudgetEntities = new ArrayList<>();

    @Override
    public MonthlyBudgetEntity add(MonthlyBudgetEntity entity) {
        if (!monthlyBudgetEntities.contains(entity)) {
            MonthlyBudgetEntity newMonthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser, entity.getDate());

            newMonthlyBudgetEntity.setSum(entity.getSum());

            monthlyBudgetEntities.add(newMonthlyBudgetEntity);

            return newMonthlyBudgetEntity.getCopy();
        }

        for (MonthlyBudgetEntity budget : monthlyBudgetEntities) {
            if (budget.equals(entity)) {
                return budget;
            }
        }

        return null;
    }

    @Override
    public MonthlyBudgetEntity findById(UUID uuid) {
        for (MonthlyBudgetEntity budget : monthlyBudgetEntities) {
            if (budget.getUuid().equals(uuid)) {
                return budget.getCopy();
            }
        }

        return null;
    }
    public MonthlyBudgetEntity findByDateAndUser(Date date, UserEntity user) {
        for (MonthlyBudgetEntity budget : monthlyBudgetEntities) {
            if (budget.getDate().equals(date) && budget.getUser().equals(user)) {
                return budget.getCopy();
            }
        }

        return null;
    }

    @Override
    public List<MonthlyBudgetEntity> findAll() {
        return List.copyOf(monthlyBudgetEntities);
    }

    @Override
    public void update(MonthlyBudgetEntity entity) {
        for (MonthlyBudgetEntity budget : monthlyBudgetEntities) {
            if (budget.getUuid().equals(entity.getUuid())) {
                budget.setSum(entity.getSum());
            }
        }
    }

    @Override
    public boolean delete(MonthlyBudgetEntity entity) {
        return monthlyBudgetEntities.remove(entity);
    }
}