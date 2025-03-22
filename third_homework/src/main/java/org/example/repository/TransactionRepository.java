package org.example.repository;

import liquibase.exception.LiquibaseException;
import org.example.db.ConnectionClass;
import org.example.model.TransactionEntity;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository implements Repository<TransactionEntity> {
    @Override
    public TransactionEntity add(TransactionEntity entity) throws SQLException, LiquibaseException {
        int id = getId(entity);

        if (id != 0) {
            return new TransactionEntity.TransactionBuilder(entity.getSum(), entity.getUserId()).
                    id(id).date(entity.getDate()).description(entity.getDescription()).categoryId(entity.getCategoryId()).build();
        }

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("insert into transaction(sum, date, description, category_id, user_id) " +
                     "values (?, ?, ?, ?, ?)")) {
            preparedStatement.setBigDecimal(1, entity.getSum());
            preparedStatement.setDate(2, entity.getDate());
            preparedStatement.setString(3, entity.getDescription());
            if (entity.getCategoryId() != 0) {
                preparedStatement.setInt(4, entity.getCategoryId());
            } else {
                preparedStatement.setNull(4, Types.INTEGER);
            }
            if (entity.getUserId() != 0) {
                preparedStatement.setInt(5, entity.getUserId());
            } else {
                preparedStatement.setNull(5, Types.INTEGER);
            }

            preparedStatement.executeUpdate();
        }

        id = getId(entity);

        if (id != 0) {
            return new TransactionEntity.TransactionBuilder(entity.getSum(), entity.getUserId()).
                    id(id).date(entity.getDate()).description(entity.getDescription()).categoryId(entity.getCategoryId()).build();
        }

        return null;
    }

    private int getId(TransactionEntity entity) throws SQLException, LiquibaseException {
        int parameterIndex = 1;
        String sql = "select id from transaction " +
                "where sum = ? and date = ? and description = ?";

        if (entity.getCategoryId() != 0) {
            sql += " and category_id = ?";
        } else {
            sql += " and category_id is null";
        }

        sql += " and user_id = ?";

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBigDecimal(parameterIndex++, entity.getSum());
            preparedStatement.setDate(parameterIndex++, entity.getDate());
            preparedStatement.setString(parameterIndex++, entity.getDescription());
            if (entity.getCategoryId() != 0) {
                preparedStatement.setInt(parameterIndex++, entity.getCategoryId());
            }
            preparedStatement.setInt(parameterIndex, entity.getUserId());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

            return 0;
        }
    }

    @Override
    public TransactionEntity findById(int id) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction where id = ?")) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                BigDecimal sum = resultSet.getBigDecimal(2);
                int userId = resultSet.getInt(6);

                return new TransactionEntity.TransactionBuilder(sum, userId).
                        id(id).date(resultSet.getDate(3)).description(resultSet.getString(4)).categoryId(resultSet.getInt(5)).build();
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
                BigDecimal sum = resultSet.getBigDecimal(2);
                int userId = resultSet.getInt(6);

                TransactionEntity transaction = new TransactionEntity.TransactionBuilder(sum, userId).
                        id(resultSet.getInt(1)).date(resultSet.getDate(3)).description(resultSet.getString(4)).categoryId(resultSet.getInt(5)).build();

                transactionEntities.add(transaction);
            }

            return transactionEntities;
        }
    }

    public List<TransactionEntity> findAllWithUser(int userId) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from transaction where user_id = ?")) {
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<TransactionEntity> transactionEntities = new ArrayList<>();

            while (resultSet.next()) {
                BigDecimal sum = resultSet.getBigDecimal(2);

                TransactionEntity transaction = new TransactionEntity.TransactionBuilder(sum, userId).
                        id(resultSet.getInt(1)).date(resultSet.getDate(3)).description(resultSet.getString(4)).categoryId(resultSet.getInt(5)).build();

                transactionEntities.add(transaction);
            }

            return transactionEntities;
        }
    }

    public List<TransactionEntity> findAllWithDateAndCategoryIdAndTypeAndUserId(Date date, int categoryId, String type, int userId) throws SQLException, LiquibaseException {
        String sql = getFilteredSql(date, categoryId, type);

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int parameterIndex = 1;

            if (date != null) {
                preparedStatement.setDate(parameterIndex++, date);
            }
            if (categoryId != 0) {
                preparedStatement.setInt(parameterIndex++, categoryId);
            }
            if (type != null && !type.isBlank()) {
                preparedStatement.setInt(parameterIndex++, 0);
            }
            preparedStatement.setInt(parameterIndex, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<TransactionEntity> transactionEntities = new ArrayList<>();

            while (resultSet.next()) {
                BigDecimal sum = resultSet.getBigDecimal(2);

                TransactionEntity transaction = new TransactionEntity.TransactionBuilder(sum, userId).
                        id(resultSet.getInt(1)).date(resultSet.getDate(3)).description(resultSet.getString(4)).categoryId(resultSet.getInt(5)).build();

                transactionEntities.add(transaction);
            }

            return transactionEntities;
        }
    }

    private static String getFilteredSql(Date date, int categoryId, String type) {
        String sql = "select * from transaction where";
        boolean moreThanOneParameters = false;

        if (date != null) {
            sql += " date = ?";
            moreThanOneParameters = true;
        }

        if (categoryId != 0) {
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
        return sql;
    }

    @Override
    public void update(TransactionEntity entity) throws SQLException, LiquibaseException {
        int parameterIndex = 1;
        String sql = "update transaction " +
                "set sum = ?, date = ?, description = ?";

        if (entity.getCategoryId() != 0) {
            sql += ", category_id = ?";
        }

        sql += " where id = ?";

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBigDecimal(parameterIndex++, entity.getSum());
            preparedStatement.setDate(parameterIndex++, entity.getDate());
            preparedStatement.setString(parameterIndex++, entity.getDescription());
            if (entity.getCategoryId() != 0) {
                preparedStatement.setInt(parameterIndex++, entity.getCategoryId());
            }
            preparedStatement.setInt(parameterIndex, entity.getId());

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