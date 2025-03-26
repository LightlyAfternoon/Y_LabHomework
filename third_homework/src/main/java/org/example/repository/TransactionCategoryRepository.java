package org.example.repository;

import liquibase.exception.LiquibaseException;
import org.example.db.ConnectionClass;
import org.example.model.TransactionCategoryEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionCategoryRepository implements Repository<TransactionCategoryEntity> {
    @Override
    public TransactionCategoryEntity add(TransactionCategoryEntity entity) throws SQLException, LiquibaseException {
        int id = getId(entity);

        if (id != 0) {
            return new TransactionCategoryEntity.TransactionCategoryBuilder(entity.getName()).
                    id(id).neededSum(entity.getNeededSum()).userId(entity.getUserId()).build();
        }

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("insert into not_public.transaction_category(name, needed_sum, user_id) values (?, ?, ?)")) {
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setBigDecimal(2, entity.getNeededSum());
            if (entity.getUserId() != 0) {
                preparedStatement.setInt(3, entity.getUserId());
            } else {
                preparedStatement.setNull(3, Types.INTEGER);
            }

            preparedStatement.executeUpdate();
        }

        id = getId(entity);

        if (id != 0) {
            return new TransactionCategoryEntity.TransactionCategoryBuilder(entity.getName()).
                    id(id).neededSum(entity.getNeededSum()).userId(entity.getUserId()).build();
        }

        return null;
    }

    private int getId(TransactionCategoryEntity entity) throws SQLException, LiquibaseException {
        int parameterIndex = 1;
        String sql = "select id from not_public.transaction_category where name = ?";

        if (entity.getNeededSum() != null) {
            sql += " and needed_sum = ?";
        } else {
            sql += " and needed_sum is null";
        }

        if (entity.getUserId() != 0) {
            sql += " and user_id = ?";
        } else {
            sql += " and user_id is null";
        }

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(parameterIndex++, entity.getName());
            if (entity.getNeededSum() != null) {
                preparedStatement.setBigDecimal(parameterIndex++, entity.getNeededSum());
            }
            if (entity.getUserId() != 0) {
                preparedStatement.setInt(parameterIndex, entity.getUserId());
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

            return 0;
        }
    }

    @Override
    public TransactionCategoryEntity findById(int id) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from not_public.transaction_category where id = ?")) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString(2);
                int userId = resultSet.getInt(4);

                return new TransactionCategoryEntity.TransactionCategoryBuilder(name).
                        id(resultSet.getInt(1)).neededSum(resultSet.getBigDecimal(3)).userId(userId).build();
            }
        }

        return null;
    }

    public TransactionCategoryEntity findByName(String name) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from not_public.transaction_category where name = ?")) {
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt(4);

                return new TransactionCategoryEntity.TransactionCategoryBuilder(name).
                        id(resultSet.getInt(1)).neededSum(resultSet.getBigDecimal(3)).userId(userId).build();
            }
        }

        return null;
    }

    @Override
    public List<TransactionCategoryEntity> findAll() throws SQLException, LiquibaseException {
        List<TransactionCategoryEntity> categoryEntities = new ArrayList<>();

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from not_public.transaction_category")) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString(2);
                int userId = resultSet.getInt(4);

                TransactionCategoryEntity category =  new TransactionCategoryEntity.TransactionCategoryBuilder(name).
                        id(resultSet.getInt(1)).neededSum(resultSet.getBigDecimal(3)).userId(userId).build();

                categoryEntities.add(category);
            }
        }

        return categoryEntities;
    }

    public List<TransactionCategoryEntity> findCommonCategoriesOrGoalsWithUserId(int userId) throws SQLException, LiquibaseException {
        List<TransactionCategoryEntity> categoryEntities = new ArrayList<>();

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from not_public.transaction_category where user_id = ? or user_id is null")) {
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString(2);

                TransactionCategoryEntity category =  new TransactionCategoryEntity.TransactionCategoryBuilder(name).
                        id(resultSet.getInt(1)).neededSum(resultSet.getBigDecimal(3)).userId(resultSet.getInt(4)).build();

                categoryEntities.add(category);
            }
        }

        return categoryEntities;
    }

    public List<TransactionCategoryEntity> findAllGoalsWithUserId(int userId) throws SQLException, LiquibaseException {
        List<TransactionCategoryEntity> categoryEntities = new ArrayList<>();

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from not_public.transaction_category where user_id = ?")) {
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString(2);

                TransactionCategoryEntity category =  new TransactionCategoryEntity.TransactionCategoryBuilder(name).
                        id(resultSet.getInt(1)).neededSum(resultSet.getBigDecimal(3)).userId(userId).build();

                categoryEntities.add(category);
            }
        }

        return categoryEntities;
    }

    @Override
    public void update(TransactionCategoryEntity entity) throws SQLException, LiquibaseException {
        int parameterIndex = 1;
        String sql = "update not_public.transaction_category set name = ?";

        if (entity.getNeededSum() != null) {
            sql += ", needed_sum = ?";
        }

        sql += " where id = ?";

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(parameterIndex++, entity.getName());
            if (entity.getNeededSum() != null) {
                preparedStatement.setBigDecimal(parameterIndex++, entity.getNeededSum());
            }
            preparedStatement.setInt(parameterIndex, entity.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public boolean delete(TransactionCategoryEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("delete from not_public.transaction_category where id = ?")) {
            preparedStatement.setInt(1, entity.getId());

            if (preparedStatement.executeUpdate() > 0) {
                return true;
            }
        }

        return false;
    }
}