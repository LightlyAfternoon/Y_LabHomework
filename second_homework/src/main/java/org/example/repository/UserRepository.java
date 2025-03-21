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
            try (Connection connection = ConnectionClass.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("select * from \"user\" " +
                         "where name = '"+entity.getName()+"' and email = '"+entity.getEmail()+"' and " +
                         "password = '"+entity.getPassword()+"' and role_id = "+(entity.getRole() == UserRole.ADMIN ? 1 : 2)+" and is_blocked = B'"+(entity.getBlocked() ? 1 : 0)+"'")) {

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    UserEntity user = new UserEntity(resultSet.getInt(1));

                    user.setName(resultSet.getString(2));
                    user.setEmail(resultSet.getString(3));
                    user.setPassword(resultSet.getString(4));
                    user.setBlocked(resultSet.getBoolean(5));
                    user.setRole(resultSet.getInt(6) == 1 ? UserRole.ADMIN : UserRole.USER);

                    return user;
                }
            }

            UserEntity newUser = new UserEntity();

            newUser.setName(entity.getName());
            newUser.setEmail(entity.getEmail());
            newUser.setPassword(entity.getPassword());
            newUser.setRole(entity.getRole());
            newUser.setBlocked(entity.getBlocked());

            try (Connection connection = ConnectionClass.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("insert into \"user\"(name, email, password, role_id, is_blocked) values (" +
                         "'"+entity.getName()+"', '"+entity.getEmail()+"', '"+entity.getPassword()+"', '" + (entity.getRole() ==  UserRole.ADMIN? 1 : 2) + "', B'"+(entity.getBlocked() ? 1 : 0)+"')")) {

                preparedStatement.executeUpdate();
            }

            try (Connection connection = ConnectionClass.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("select * from \"user\" " +
                         "where name = '"+entity.getName()+"' and email = '"+entity.getEmail()+"' and " +
                         "password = '"+entity.getPassword()+"' and role_id = '" + (entity.getRole() == UserRole.ADMIN ? 1 : 2) + "' and is_blocked = B'"+(entity.getBlocked() ? 1 : 0)+"'")) {

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    newUser = new UserEntity(resultSet.getInt(1));

                    newUser.setName(entity.getName());
                    newUser.setEmail(entity.getEmail());
                    newUser.setPassword(entity.getPassword());
                    newUser.setRole(entity.getRole());
                    newUser.setBlocked(entity.getBlocked());
                }
            }

            return newUser.getCopy();
        }

        return null;
    }

    @Override
    public UserEntity findById(int id) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from \"user\" where id = ?")) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                UserEntity user = new UserEntity(resultSet.getInt(1));

                user.setName(resultSet.getString(2));
                user.setEmail(resultSet.getString(3));
                user.setPassword(resultSet.getString(4));
                user.setBlocked(resultSet.getBoolean(5));
                user.setRole(resultSet.getInt(6) == 1 ? UserRole.ADMIN : UserRole.USER);

                return user;
            }
        }

        return null;
    }

    @Override
    public List<UserEntity> findAll() throws SQLException, LiquibaseException {
        List<UserEntity> userEntities = new ArrayList<>();

        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from \"user\"")) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                UserEntity user = new UserEntity(resultSet.getInt(1));

                user.setName(resultSet.getString(2));
                user.setEmail(resultSet.getString(3));
                user.setPassword(resultSet.getString(4));
                user.setBlocked(resultSet.getBoolean(5));
                user.setRole(resultSet.getInt(6) == 1 ? UserRole.ADMIN : UserRole.USER);

                userEntities.add(user);
            }
        }

        return userEntities;
    }

    @Override
    public void update(UserEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("update \"user\" " +
                     "set name = '"+entity.getName()+"', email = '"+entity.getEmail()+"', password = '"+entity.getPassword()+"', role_id = '" + (entity.getRole() ==  UserRole.ADMIN? 1 : 2) + "', is_blocked = B'"+(entity.getBlocked() ? 1 : 0)+"' where id = ?")) {
            preparedStatement.setInt(1, entity.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public boolean delete(UserEntity entity) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("delete from \"user\" where id = ?")) {
            preparedStatement.setInt(1, entity.getId());

            if (preparedStatement.executeUpdate() > 0) {
                return true;
            }
        }

        return false;
    }

    private boolean emailExists(String email) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from \"user\" where email = ?")) {
            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        }
    }

    public UserEntity findUserWithEmailAndPassword(String email, String password) throws SQLException, LiquibaseException {
        try (Connection connection = ConnectionClass.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("select * from \"user\" where email = ? and password = ?")) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                UserEntity user = new UserEntity(resultSet.getInt(1));

                user.setName(resultSet.getString(2));
                user.setEmail(resultSet.getString(3));
                user.setPassword(resultSet.getString(4));
                user.setBlocked(resultSet.getBoolean(5));
                user.setRole(resultSet.getInt(6) == 1 ? UserRole.ADMIN : UserRole.USER);

                return user;
            }
        }

        return null;
    }
}