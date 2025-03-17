package org.example.repository;

import liquibase.exception.LiquibaseException;
import org.example.CurrentUser;
import org.example.db.ConnectionClass;
import org.example.model.TransactionCategoryEntity;
import org.example.model.UserEntity;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionCategoryRepository implements Repository<TransactionCategoryEntity> {
    private static final List<TransactionCategoryEntity> transactionCategoryEntities = new ArrayList<>();

    @Override
    public TransactionCategoryEntity add(TransactionCategoryEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction_category " +
                     "where name = '" + entity.getName() + "' and " +
                     "needed_sum " + (entity.getNeededSum() != null ? "= " + entity.getNeededSum() : "is " + null) + " and " +
                     "user_id " + (entity.getNeededSum() != null ? "= " + CurrentUser.currentUser.getId() : "is " + null))) {

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                TransactionCategoryEntity category = new TransactionCategoryEntity(resultSet.getInt(1), (resultSet.getInt(1) != 0 ? new UserRepository().findById(resultSet.getInt(4)) : null));
                BigDecimal neededSum = resultSet.getBigDecimal(3);

                category.setName(resultSet.getString(2));
                if (neededSum != null) {
                    category.setNeededSum(neededSum);
                }

                return category;
            }
        }

        TransactionCategoryEntity newCategory = new TransactionCategoryEntity(entity.getNeededSum() != null ? CurrentUser.currentUser : null);
        BigDecimal neededSum = entity.getNeededSum();

        newCategory.setName(entity.getName());
        if (neededSum != null) {
            newCategory.setNeededSum(neededSum);
        }

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("insert into transaction_category(name, needed_sum, user_id) values (" +
                     "'" + entity.getName() + "', " + (entity.getNeededSum() != null ? entity.getNeededSum() : null) + ", " + (entity.getNeededSum() != null ? CurrentUser.currentUser.getId() : null) + ")")) {

            preparedStatement.executeUpdate();
        }

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction_category " +
                     "where name = '" + entity.getName() + "' and " +
                     "needed_sum " + (entity.getNeededSum() != null ? "= " + entity.getNeededSum() : "is " + null) + " and " +
                     "user_id " + (entity.getNeededSum() != null ? "= " + CurrentUser.currentUser.getId() : "is " + null))) {

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                newCategory = new TransactionCategoryEntity(resultSet.getInt(1), entity.getNeededSum() != null ? CurrentUser.currentUser : null);

                newCategory.setName(entity.getName());
                if (neededSum != null) {
                    newCategory.setNeededSum(neededSum);
                }

                return newCategory.getCopy();
            }
        }

        return null;
    }

    @Override
    public TransactionCategoryEntity findById(int id) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction_category where id = ?")) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt(4);
                TransactionCategoryEntity category;
                if (userId != 0) {
                    category = new TransactionCategoryEntity(resultSet.getInt(1), new UserRepository().findById(userId));
                } else {
                    category = new TransactionCategoryEntity(resultSet.getInt(1));
                }

                category.setName(resultSet.getString(2));
                category.setNeededSum(resultSet.getBigDecimal(3));

                return category;
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
            if (category.getUser() == null || category.getUser().getId() == user.getId()) {
                categories.add(category);
            }
        }

        return categories;
    }

    public List<TransactionCategoryEntity> findAllUserGoals(UserEntity user) {
        List<TransactionCategoryEntity> categories = new ArrayList<>();

        for (TransactionCategoryEntity category : transactionCategoryEntities){
            if (category.getUser().getId() == user.getId()) {
                categories.add(category);
            }
        }

        return categories;
    }

    @Override
    public void update(TransactionCategoryEntity entity) {
        for (TransactionCategoryEntity transactionCategory : transactionCategoryEntities) {
            if (transactionCategory.getId() == entity.getId()) {
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