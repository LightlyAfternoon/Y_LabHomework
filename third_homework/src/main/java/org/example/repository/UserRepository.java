package org.example.repository;

import liquibase.exception.LiquibaseException;
import org.example.db.ConnectionClass;
import org.example.model.UserEntity;
import org.example.model.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements Repository<UserEntity> {
    @Override
    public UserEntity add(UserEntity entity) throws SQLException, LiquibaseException {
        if (!emailExists(entity.getEmail())) {
            int id = getId(entity);

            if (id != 0) {
                return new UserEntity.UserBuilder(entity.getEmail(), entity.getPassword(), entity.getName()).
                        id(id).role(entity.getRole()).isBlocked(entity.getBlocked()).build();
            }

            try (Connection connection = ConnectionClass.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("insert into service.\"user\"(name, email, password, role_id, is_blocked)" +
                         "values(?, ?, ?, ?, ?::bit)")) {
                preparedStatement.setString(1, entity.getName());
                preparedStatement.setString(2, entity.getEmail());
                preparedStatement.setString(3, entity.getPassword());
                preparedStatement.setInt(4, (entity.getRole() == UserRole.ADMIN ? 1 : 2));
                preparedStatement.setString(5, String.valueOf(entity.getBlocked() ? 1 : 0));

                preparedStatement.executeUpdate();
            }

            id = getId(entity);

            if (id != 0) {
                return new UserEntity.UserBuilder(entity.getEmail(), entity.getPassword(), entity.getName()).
                        id(id).role(entity.getRole()).isBlocked(entity.getBlocked()).build();
            }
        }

        return null;
    }

    private int getId(UserEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select id from service.\"user\" " +
                     "where name = ? and email = ? and password = ? and role_id = ? and is_blocked = ?::bit")) {
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getEmail());
            preparedStatement.setString(3, entity.getPassword());
            preparedStatement.setInt(4, (entity.getRole() == UserRole.ADMIN ? 1 : 2));
            preparedStatement.setString(5, String.valueOf(entity.getBlocked() ? 1 : 0));

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

            return 0;
        }
    }

    @Override
    public UserEntity findById(int id) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from service.\"user\" where id = ?")) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String email = resultSet.getString(3);
                String password = resultSet.getString(4);
                String name = resultSet.getString(2);

                return new UserEntity.UserBuilder(email, password, name).
                        id(id).role(resultSet.getInt(6) == 1 ? UserRole.ADMIN : UserRole.USER).isBlocked(resultSet.getBoolean(5)).build();
            }
        }

        return null;
    }

    @Override
    public List<UserEntity> findAll() throws SQLException, LiquibaseException {
        List<UserEntity> userEntities = new ArrayList<>();

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from service.\"user\"")) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String email = resultSet.getString(3);
                String password = resultSet.getString(4);
                String name = resultSet.getString(2);

                UserEntity user = new UserEntity.UserBuilder(email, password, name).
                        id(resultSet.getInt(1)).role(resultSet.getInt(6) == 1 ? UserRole.ADMIN : UserRole.USER).isBlocked(resultSet.getBoolean(5)).build();

                userEntities.add(user);
            }
        }

        return userEntities;
    }

    @Override
    public void update(UserEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("update service.\"user\" " +
                     "set name = ?, email = ?, password = ?, role_id = ?, is_blocked = ?::bit where id = ?")) {
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getEmail());
            preparedStatement.setString(3, entity.getPassword());
            preparedStatement.setInt(4, (entity.getRole() == UserRole.ADMIN ? 1 : 2));
            preparedStatement.setString(5, String.valueOf(entity.getBlocked() ? 1 : 0));
            preparedStatement.setInt(6, entity.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public boolean delete(UserEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("delete from service.\"user\" where id = ?")) {
            preparedStatement.setInt(1, entity.getId());

            if (preparedStatement.executeUpdate() > 0) {
                return true;
            }
        }

        return false;
    }

    private boolean emailExists(String email) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from service.\"user\" where email = ?")) {
            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        }
    }

    public UserEntity findUserWithEmailAndPassword(String email, String password) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from service.\"user\" where email = ? and password = ?")) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString(2);

                return new UserEntity.UserBuilder(email, password, name).
                        id(resultSet.getInt(1)).role(resultSet.getInt(6) == 1 ? UserRole.ADMIN : UserRole.USER).isBlocked(resultSet.getBoolean(5)).build();
            }
        }

        return null;
    }
}