package org.example.repository;

import liquibase.exception.LiquibaseException;
import org.example.db.ConnectionClass;
import org.example.model.TransactionCategoryEntity;
import org.example.model.TransactionEntity;
import org.example.model.UserEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository implements Repository<TransactionEntity> {
    @Override
    public TransactionEntity add(TransactionEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction " +
                     "where sum = "+entity.getSum()+" and " +
                     "date = '"+entity.getDate()+"' and " +
                     "description "+(entity.getDescription() != null ? "= '" + entity.getDescription()+"'" : "is " + null)+" and " +
                     "category_id "+(entity.getCategory() != null ? "= " + entity.getCategory().getId() : "is " + null)+" and " +
                     "user_id = "+entity.getUser().getId())) {

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                TransactionEntity transaction = new TransactionEntity(resultSet.getInt(1), new UserRepository().findById(resultSet.getInt(6)));

                transaction.setSum(resultSet.getBigDecimal(2));
                transaction.setDate(resultSet.getDate(3));
                transaction.setDescription(resultSet.getString(4));
                transaction.setCategory(new TransactionCategoryRepository().findById(resultSet.getInt(5)));

                return transaction;
            }
        }

        TransactionEntity newTransaction = new TransactionEntity(entity.getUser());

        newTransaction.setSum(entity.getSum());
        newTransaction.setDate(entity.getDate());
        newTransaction.setDescription(entity.getDescription());
        newTransaction.setCategory(entity.getCategory());

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("insert into transaction(sum, date, description, category_id, user_id) values (" +
                     entity.getSum()+", '"+entity.getDate()+"', "+(entity.getDescription() != null ? "'"+entity.getDescription()+"'" : null)+", "+(entity.getCategory() != null ? entity.getCategory().getId() : null)+", "+entity.getUser().getId()+")")) {

            preparedStatement.executeUpdate();
        }

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction " +
                     "where sum = "+entity.getSum()+" and " +
                     "date = '"+entity.getDate()+"' and " +
                     "description "+(entity.getDescription() != null ? "= '" + entity.getDescription()+"'" : "is " + null)+" and " +
                     "category_id "+(entity.getCategory() != null ? "= " + entity.getCategory().getId() : "is " + null)+" and " +
                     "user_id = "+entity.getUser().getId())) {

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                newTransaction = new TransactionEntity(resultSet.getInt(1), entity.getUser());

                newTransaction.setSum(entity.getSum());
                newTransaction.setDate(entity.getDate());
                newTransaction.setDescription(entity.getDescription());
                newTransaction.setCategory(entity.getCategory());

                return newTransaction.getCopy();
            }
        }

        return null;
    }

    @Override
    public TransactionEntity findById(int id) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction where id = ?")) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                TransactionEntity transaction = new TransactionEntity(resultSet.getInt(1), new UserRepository().findById(resultSet.getInt(6)));

                transaction.setSum(resultSet.getBigDecimal(2));
                transaction.setDate(resultSet.getDate(3));
                transaction.setDescription(resultSet.getString(4));
                transaction.setCategory(new TransactionCategoryRepository().findById(resultSet.getInt(5)));

                return transaction;
            }
        }

        return null;
    }

    @Override
    public List<TransactionEntity> findAll() throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction")) {

            ResultSet resultSet = preparedStatement.executeQuery();

            List<TransactionEntity> transactionEntities = new ArrayList<>();

            while (resultSet.next()) {
                TransactionEntity transaction = new TransactionEntity(resultSet.getInt(1), new UserRepository().findById(resultSet.getInt(6)));

                transaction.setSum(resultSet.getBigDecimal(2));
                transaction.setDate(resultSet.getDate(3));
                transaction.setDescription(resultSet.getString(4));
                transaction.setCategory(new TransactionCategoryRepository().findById(resultSet.getInt(5)));

                transactionEntities.add(transaction);
            }

            return transactionEntities;
        }
    }

    public List<TransactionEntity> findAllWithUser(UserEntity user) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction where user_id = ?")) {
            preparedStatement.setInt(1, user.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            List<TransactionEntity> transactionEntities = new ArrayList<>();

            while (resultSet.next()) {
                TransactionEntity transaction = new TransactionEntity(resultSet.getInt(1), new UserRepository().findById(resultSet.getInt(6)));

                transaction.setSum(resultSet.getBigDecimal(2));
                transaction.setDate(resultSet.getDate(3));
                transaction.setDescription(resultSet.getString(4));
                transaction.setCategory(new TransactionCategoryRepository().findById(resultSet.getInt(5)));

                transactionEntities.add(transaction);
            }

            return transactionEntities;
        }
    }

    public List<TransactionEntity> findAllWithDateAndCategoryAndTypeAndUser(Date date, TransactionCategoryEntity category, String type, UserEntity user) throws SQLException, LiquibaseException {
        String sql = "select * from transaction where";
        boolean moreThanOneParameters = false;

        if (date != null) {
            sql += " date = ?";
            moreThanOneParameters = true;
        }

        if (category != null) {
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

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int parameterIndex = 1;

            if (date != null) {
                preparedStatement.setDate(parameterIndex++, date);
            }
            if (category != null) {
                preparedStatement.setInt(parameterIndex++, category.getId());
            }
            if (type != null && !type.isBlank()) {
                preparedStatement.setInt(parameterIndex++, 0);
            }
            preparedStatement.setInt(parameterIndex, user.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            List<TransactionEntity> transactionEntities = new ArrayList<>();

            while (resultSet.next()) {
                TransactionEntity transaction = new TransactionEntity(resultSet.getInt(1), new UserRepository().findById(resultSet.getInt(6)));

                transaction.setSum(resultSet.getBigDecimal(2));
                transaction.setDate(resultSet.getDate(3));
                transaction.setDescription(resultSet.getString(4));
                transaction.setCategory(new TransactionCategoryRepository().findById(resultSet.getInt(5)));

                transactionEntities.add(transaction);
            }

            return transactionEntities;
        }
    }

    @Override
    public void update(TransactionEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("update transaction " +
                     "set sum = "+entity.getSum()+", category_id = "+(entity.getCategory() != null ? entity.getCategory().getId() : null)+", date = '"+entity.getDate()+"', description = "+(entity.getDescription() != null ? "'"+entity.getDescription()+"'" : null)+" where id = ?")) {
            preparedStatement.setInt(1, entity.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public boolean delete(TransactionEntity entity) throws SQLException, LiquibaseException {
        if (entity != null) {
            try (Connection connection = ConnectionClass.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("delete from transaction where id = ?")) {
                preparedStatement.setInt(1, entity.getId());

                if (preparedStatement.executeUpdate() > 0) {
                    return true;
                }
            }
        }

        return false;
    }
}