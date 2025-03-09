package org.example.repository;

import org.example.CurrentUser;
import org.example.model.TransactionEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionRepository implements Repository<TransactionEntity> {
    List<TransactionEntity> transactionEntities = new ArrayList<>();

    @Override
    public TransactionEntity add(TransactionEntity entity) {
        if (!transactionEntities.contains(entity)) {
            TransactionEntity newTransaction = new TransactionEntity(CurrentUser.currentUser);

            newTransaction.setSum(entity.getSum());
            newTransaction.setCategory(entity.getCategory());
            newTransaction.setDate(entity.getDate());
            newTransaction.setDescription(entity.getDescription());

            transactionEntities.add(newTransaction);

            return newTransaction.getCopy();
        }

        for (TransactionEntity transaction : transactionEntities) {
            if (transaction.equals(entity)) {
                return transaction;
            }
        }

        return null;
    }

    @Override
    public TransactionEntity findById(UUID uuid) {
        for (TransactionEntity transaction : transactionEntities) {
            if (transaction.getUuid().equals(uuid)) {
                return transaction.getCopy();
            }
        }

        return null;
    }

    @Override
    public List<TransactionEntity> findAll() {
        return List.copyOf(transactionEntities);
    }

    @Override
    public void update(TransactionEntity entity) {
        for (TransactionEntity transaction : transactionEntities) {
            if (transaction.getUuid().equals(entity.getUuid()) && !transactionEntities.contains(entity)) {
                transaction.setSum(entity.getSum());
                transaction.setCategory(entity.getCategory());
                transaction.setDate(entity.getDate());
                transaction.setDescription(entity.getDescription());
            }
        }
    }

    @Override
    public boolean delete(TransactionEntity entity) {
        return transactionEntities.remove(entity);
    }
}
