package org.example.repository;

import liquibase.exception.LiquibaseException;
import org.example.db.ConnectionClass;
import org.example.model.MonthlyBudgetEntity;
import org.example.model.UserEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

public class MonthlyBudgetRepository implements Repository<MonthlyBudgetEntity> {
    @Override
    public MonthlyBudgetEntity add(MonthlyBudgetEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from monthly_budget " +
                     "where date = '" + entity.getDate() + "' and " +
                     "sum = " + entity.getSum() + " and " +
                     "user_id = " + entity.getUser().getId())) {

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                MonthlyBudgetEntity budget = new MonthlyBudgetEntity(resultSet.getInt(1), new UserRepository().findById(resultSet.getInt(4)), resultSet.getDate(2));

                budget.setSum(resultSet.getBigDecimal(3));

                return budget;
            }
        }

        MonthlyBudgetEntity newBudget = new MonthlyBudgetEntity(entity.getUser(), entity.getDate());

        newBudget.setSum(entity.getSum());

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("insert into monthly_budget(date, sum, user_id) values (" +
                     "'" + entity.getDate() + "', " + entity.getSum() + ", " + entity.getUser().getId() + ")")) {

            preparedStatement.executeUpdate();
        }

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from monthly_budget " +
                     "where date = '" + entity.getDate() + "' and " +
                     "sum = " + entity.getSum() + " and " +
                     "user_id = " + entity.getUser().getId())) {

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                newBudget = new MonthlyBudgetEntity(resultSet.getInt(1), entity.getUser(), entity.getDate());

                newBudget.setSum(entity.getSum());

                return newBudget.getCopy();
            }
        }

        return null;
    }

    @Override
    public MonthlyBudgetEntity findById(int id) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from monthly_budget where id = ?")) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt(4);
                MonthlyBudgetEntity budget = new MonthlyBudgetEntity(resultSet.getInt(1), new UserRepository().findById(userId), resultSet.getDate(2));

                budget.setSum(resultSet.getBigDecimal(3));

                return budget;
            }
        }

        return null;
    }

    public MonthlyBudgetEntity findByDateAndUser(Date date, UserEntity user) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from monthly_budget where date = ? and user_id = ?")) {
            preparedStatement.setDate(1, date);
            preparedStatement.setInt(2, user.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt(4);
                MonthlyBudgetEntity budget = new MonthlyBudgetEntity(resultSet.getInt(1), new UserRepository().findById(userId), resultSet.getDate(2));

                budget.setSum(resultSet.getBigDecimal(3));

                return budget;
            }
        }

        return null;
    }

    @Override
    public List<MonthlyBudgetEntity> findAll() throws SQLException, LiquibaseException {
        List<MonthlyBudgetEntity> budgetEntities = new ArrayList<>();

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from monthly_budget")) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt(4);
                MonthlyBudgetEntity budget = new MonthlyBudgetEntity(resultSet.getInt(1), new UserRepository().findById(userId), resultSet.getDate(2));

                budget.setSum(resultSet.getBigDecimal(3));

                budgetEntities.add(budget);
            }
        }

        return budgetEntities;
    }

    @Override
    public void update(MonthlyBudgetEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("update monthly_budget " +
                     "set sum = "+entity.getSum()+" where id = ?")) {
            preparedStatement.setInt(1, entity.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public boolean delete(MonthlyBudgetEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("delete from monthly_budget where id = ?")) {
            preparedStatement.setInt(1, entity.getId());

            if (preparedStatement.executeUpdate() > 0) {
                return true;
            }
        }

        return false;
    }
}