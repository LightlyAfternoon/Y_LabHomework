package org.example.repository;

import liquibase.exception.LiquibaseException;
import org.example.db.ConnectionClass;
import org.example.model.MonthlyBudgetEntity;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonthlyBudgetRepository implements Repository<MonthlyBudgetEntity> {
    @Override
    public MonthlyBudgetEntity add(MonthlyBudgetEntity entity) throws SQLException, LiquibaseException {
        int id = getId(entity);

        if (id != 0) {
            return new MonthlyBudgetEntity.MonthlyBudgetBuilder(entity.getUserId(), entity.getSum()).
                    id(id).date(entity.getDate()).build();
        }

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("insert into not_public.monthly_budget(date, sum, user_id) values (?, ?, ?)")) {
            preparedStatement.setDate(1, entity.getDate());
            preparedStatement.setBigDecimal(2, entity.getSum());
            preparedStatement.setInt(3, entity.getUserId());

            preparedStatement.executeUpdate();
        }

        id = getId(entity);

        if (id != 0) {
            return new MonthlyBudgetEntity.MonthlyBudgetBuilder(entity.getUserId(), entity.getSum()).
                    id(id).date(entity.getDate()).build();
        }

        return null;
    }

    private int getId(MonthlyBudgetEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select id from not_public.monthly_budget " +
                     "where date = ? and sum = ? and user_id = ?")) {
            preparedStatement.setDate(1, entity.getDate());
            preparedStatement.setBigDecimal(2, entity.getSum());
            preparedStatement.setInt(3, entity.getUserId());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

            return 0;
        }
    }

    @Override
    public MonthlyBudgetEntity findById(int id) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from not_public.monthly_budget where id = ?")) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt(4);
                BigDecimal sum = resultSet.getBigDecimal(3);

                return new MonthlyBudgetEntity.MonthlyBudgetBuilder(userId, sum).
                        id(id).date(resultSet.getDate(2)).build();
            }
        }

        return null;
    }

    public MonthlyBudgetEntity findByDateAndUserId(Date date, int userId) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from not_public.monthly_budget where date = ? and user_id = ?")) {
            preparedStatement.setDate(1, date);
            preparedStatement.setInt(2, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                BigDecimal sum = resultSet.getBigDecimal(3);

                return new MonthlyBudgetEntity.MonthlyBudgetBuilder(userId, sum).
                        id(resultSet.getInt(1)).date(date).build();
            }
        }

        return null;
    }

    @Override
    public List<MonthlyBudgetEntity> findAll() throws SQLException, LiquibaseException {
        List<MonthlyBudgetEntity> budgetEntities = new ArrayList<>();

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from not_public.monthly_budget")) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt(4);
                BigDecimal sum = resultSet.getBigDecimal(3);

                MonthlyBudgetEntity budget = new MonthlyBudgetEntity.MonthlyBudgetBuilder(userId, sum).
                        id(resultSet.getInt(1)).date(resultSet.getDate(2)).build();

                budgetEntities.add(budget);
            }
        }

        return budgetEntities;
    }

    @Override
    public void update(MonthlyBudgetEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("update not_public.monthly_budget " +
                     "set sum = ? where id = ?")) {
            preparedStatement.setBigDecimal(1, entity.getSum());
            preparedStatement.setInt(2, entity.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public boolean delete(MonthlyBudgetEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("delete from not_public.monthly_budget where id = ?")) {
            preparedStatement.setInt(1, entity.getId());

            if (preparedStatement.executeUpdate() > 0) {
                return true;
            }
        }

        return false;
    }
}