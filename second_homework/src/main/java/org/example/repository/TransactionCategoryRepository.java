package org.example.repository;

import org.example.CurrentUser;
import org.example.model.TransactionCategoryEntity;
import org.example.model.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionCategoryRepository implements Repository<TransactionCategoryEntity> {
    private static final List<TransactionCategoryEntity> transactionCategoryEntities = new ArrayList<>();

    @Override
    public TransactionCategoryEntity add(TransactionCategoryEntity entity) {
        if (!transactionCategoryEntities.contains(entity)) {
            TransactionCategoryEntity newTransactionCategoryEntity = new TransactionCategoryEntity();

            newTransactionCategoryEntity.setName(entity.getName());

            transactionCategoryEntities.add(newTransactionCategoryEntity);

            return newTransactionCategoryEntity.getCopy();
        }

        for (TransactionCategoryEntity transactionCategory : transactionCategoryEntities) {
            if (transactionCategory.equals(entity)) {
                return transactionCategory;
            }
        }

        return null;
    }

    public TransactionCategoryEntity addGoal(TransactionCategoryEntity entity) {
        if (!transactionCategoryEntities.contains(entity)) {
            TransactionCategoryEntity newTransactionCategoryEntity = new TransactionCategoryEntity(CurrentUser.currentUser);

            newTransactionCategoryEntity.setName(entity.getName());
            newTransactionCategoryEntity.setNeededSum(entity.getNeededSum());

            transactionCategoryEntities.add(newTransactionCategoryEntity);

            return newTransactionCategoryEntity.getCopy();
        }

        for (TransactionCategoryEntity transactionCategory : transactionCategoryEntities) {
            if (transactionCategory.equals(entity)) {
                return transactionCategory;
            }
        }

        return null;
    }

    @Override
    public TransactionCategoryEntity findById(UUID uuid) {
        for (TransactionCategoryEntity transactionCategory : transactionCategoryEntities) {
            if (transactionCategory.getUuid().equals(uuid)) {
                return transactionCategory.getCopy();
            }
        }

        return null;
    }

    public TransactionCategoryEntity findByName(String name) {
        for (TransactionCategoryEntity transactionCategory : transactionCategoryEntities) {
            if (transactionCategory.getName().equals(name)) {
                return transactionCategory.getCopy();
            }
        }

        return null;
    }

    @Override
    public List<TransactionCategoryEntity> findAll() {
        return List.copyOf(transactionCategoryEntities);
    }

    public List<TransactionCategoryEntity> findCommonCategoriesOrGoalsWithUser(UserEntity user) {
        List<TransactionCategoryEntity> categories = new ArrayList<>();

        for (TransactionCategoryEntity category : transactionCategoryEntities){
            if (category.getUser() == null || category.getUser().getUuid().equals(user.getUuid())) {
                categories.add(category);
            }
        }

        return categories;
    }

    public List<TransactionCategoryEntity> findAllUserGoals(UserEntity user) {
        List<TransactionCategoryEntity> categories = new ArrayList<>();

        for (TransactionCategoryEntity category : transactionCategoryEntities){
            if (category.getUser().getUuid().equals(user.getUuid())) {
                categories.add(category);
            }
        }

        return categories;
    }

    @Override
    public void update(TransactionCategoryEntity entity) {
        for (TransactionCategoryEntity transactionCategory : transactionCategoryEntities) {
            if (transactionCategory.getUuid().equals(entity.getUuid())) {
                transactionCategory.setName(entity.getName());
                transactionCategory.setNeededSum(entity.getNeededSum());
            }
        }
    }

    @Override
    public boolean delete(TransactionCategoryEntity entity) {
        return transactionCategoryEntities.remove(entity);
    }
}
