package org.example.repository;

import org.example.CurrentUser;
import org.example.model.TransactionCategoryEntity;
import org.example.model.TransactionEntity;
import org.example.model.UserEntity;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionRepository implements Repository<TransactionEntity> {
    private static final List<TransactionEntity> transactionEntities = new ArrayList<>();

    @Override
    public TransactionEntity add(TransactionEntity entity) {
        if (!transactionEntities.contains(entity)) {
            TransactionEntity newTransaction = new TransactionEntity(CurrentUser.currentUser);

            newTransaction.setSum(entity.getSum());
            newTransaction.setCategory(entity.getCategory());
            newTransaction.setDate(entity.getDate());
            newTransaction.setDescription(entity.getDescription());

            transactionEntities.add(newTransaction);
            System.out.println(transactionEntities.size());

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

    public List<TransactionEntity> findAllWithUser(UserEntity user) {
        List<TransactionEntity> transactions = new ArrayList<>();

        for (TransactionEntity transaction : transactionEntities) {
            if (transaction.getUser().equals(user)) {
                transactions.add(transaction);
            }
        }

        return transactions;
    }

    public List<TransactionEntity> findAllWithDateAndCategoryAndTypeAndUser(Date date, TransactionCategoryEntity category, String type, UserEntity user) {
        List<TransactionEntity> transactions = new ArrayList<>();

        for (TransactionEntity transaction : transactionEntities) {
            if (transaction.getUser().equals(user)) {
                transactions.add(transaction);
            }
        }

        List<TransactionEntity> filteredTransactions = new ArrayList<>();

        if (date != null) {
            for (TransactionEntity transaction : transactions) {
                if (transaction.getDate().equals(date)) {
                    filteredTransactions.add(transaction);
                }
            }

            transactions = new ArrayList<>(filteredTransactions);
            filteredTransactions = new ArrayList<>();
        }

        if (category != null) {
            for (TransactionEntity transaction : transactions) {
                if (transaction.getCategory().equals(category)) {
                    filteredTransactions.add(transaction);
                }
            }

            transactions = new ArrayList<>(filteredTransactions);
            filteredTransactions = new ArrayList<>();
        }

        if (type != null && !type.isBlank()) {
            for (TransactionEntity transaction : transactions) {
                if ((type.equals("Pos") && transaction.getSum().compareTo(BigDecimal.valueOf(0.0)) >= 0) ||
                        (type.equals("Neg") && transaction.getSum().compareTo(BigDecimal.valueOf(0.0)) < 0)) {
                    filteredTransactions.add(transaction);
                }
            }

            transactions = new ArrayList<>(filteredTransactions);
        }

        return transactions;
    }

    @Override
    public void update(TransactionEntity entity) {
        for (TransactionEntity transaction : transactionEntities) {
            if (transaction.getUuid().equals(entity.getUuid())) {
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
