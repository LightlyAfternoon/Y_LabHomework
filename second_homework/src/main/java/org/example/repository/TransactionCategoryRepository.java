package org.example.repository;

import liquibase.exception.LiquibaseException;
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
    @Override
    public TransactionCategoryEntity add(TransactionCategoryEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction_category " +
                     "where name = '" + entity.getName() + "' and " +
                     "needed_sum " + (entity.getNeededSum() != null ? "= " + entity.getNeededSum() : "is " + null) + " and " +
                     "user_id " + (entity.getNeededSum() != null ? "= " + entity.getUser().getId() : "is " + null))) {

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

        TransactionCategoryEntity newCategory = new TransactionCategoryEntity(entity.getNeededSum() != null ? entity.getUser() : null);
        BigDecimal neededSum = entity.getNeededSum();

        newCategory.setName(entity.getName());
        if (neededSum != null) {
            newCategory.setNeededSum(neededSum);
        }

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("insert into transaction_category(name, needed_sum, user_id) values (" +
                     "'" + entity.getName() + "', " + (entity.getNeededSum() != null ? entity.getNeededSum() : null) + ", " + (entity.getNeededSum() != null ? entity.getUser().getId() : null) + ")")) {

            preparedStatement.executeUpdate();
        }

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction_category " +
                     "where name = '" + entity.getName() + "' and " +
                     "needed_sum " + (entity.getNeededSum() != null ? "= " + entity.getNeededSum() : "is " + null) + " and " +
                     "user_id " + (entity.getNeededSum() != null ? "= " + entity.getUser().getId() : "is " + null))) {

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                newCategory = new TransactionCategoryEntity(resultSet.getInt(1), entity.getNeededSum() != null ? entity.getUser() : null);

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

    public TransactionCategoryEntity findByName(String name) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction_category where name = ?")) {
            preparedStatement.setString(1, name);

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

    @Override
    public List<TransactionCategoryEntity> findAll() throws SQLException, LiquibaseException {
        List<TransactionCategoryEntity> categoryEntities = new ArrayList<>();

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction_category")) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt(4);
                TransactionCategoryEntity category;
                if (userId != 0) {
                    category = new TransactionCategoryEntity(resultSet.getInt(1), new UserRepository().findById(userId));
                } else {
                    category = new TransactionCategoryEntity(resultSet.getInt(1));
                }

                category.setName(resultSet.getString(2));
                category.setNeededSum(resultSet.getBigDecimal(3));

                categoryEntities.add(category);
            }
        }

        return categoryEntities;
    }

    public List<TransactionCategoryEntity> findCommonCategoriesOrGoalsWithUser(UserEntity user) throws SQLException, LiquibaseException {
        List<TransactionCategoryEntity> categoryEntities = new ArrayList<>();

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction_category where user_id = ? or user_id is null")) {
            preparedStatement.setInt(1, user.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt(4);
                TransactionCategoryEntity category;
                if (userId != 0) {
                    category = new TransactionCategoryEntity(resultSet.getInt(1), new UserRepository().findById(userId));
                } else {
                    category = new TransactionCategoryEntity(resultSet.getInt(1));
                }

                category.setName(resultSet.getString(2));
                category.setNeededSum(resultSet.getBigDecimal(3));

                categoryEntities.add(category);
            }
        }

        return categoryEntities;
    }

    public List<TransactionCategoryEntity> findAllUserGoals(UserEntity user) throws SQLException, LiquibaseException {
        List<TransactionCategoryEntity> categoryEntities = new ArrayList<>();

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction_category where user_id = ?")) {
            preparedStatement.setInt(1, user.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt(4);
                TransactionCategoryEntity category;
                if (userId != 0) {
                    category = new TransactionCategoryEntity(resultSet.getInt(1), new UserRepository().findById(userId));
                } else {
                    category = new TransactionCategoryEntity(resultSet.getInt(1));
                }

                category.setName(resultSet.getString(2));
                category.setNeededSum(resultSet.getBigDecimal(3));

                categoryEntities.add(category);
            }
        }

        return categoryEntities;
    }

    @Override
    public void update(TransactionCategoryEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("update transaction_category " +
                     "set name = '"+entity.getName()+"'"+(entity.getNeededSum() != null ? ", needed_sum = "+entity.getNeededSum() : "")+" where id = ?")) {
            preparedStatement.setInt(1, entity.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public boolean delete(TransactionCategoryEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("delete from transaction_category where id = ?")) {
            preparedStatement.setInt(1, entity.getId());

            if (preparedStatement.executeUpdate() > 0) {
                return true;
            }
        }

        return false;
    }
}