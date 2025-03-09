package org.example.repository;

import org.example.model.UserEntity;

import java.util.*;

public class UserRepository implements Repository<UserEntity> {
    private static final List<UserEntity> userEntities = new ArrayList<>();

    @Override
    public UserEntity add(UserEntity entity) {
        if (!emailExists(entity.getEmail()) && !userEntities.contains(entity)) {
            UserEntity newUser = new UserEntity();

            newUser.setName(entity.getName());
            newUser.setEmail(entity.getEmail());
            newUser.setPassword(entity.getPassword());
            newUser.setRole(entity.getRole());
            newUser.setBlocked(entity.getBlocked());

            userEntities.add(newUser);

            return newUser.getCopy();
        }

        for (UserEntity user : userEntities) {
            if (user.equals(entity)) {
                return user;
            }
        }

        return null;
    }

    @Override
    public UserEntity findById(UUID uuid) {
        for (UserEntity user : userEntities) {
            if (user.getUuid().equals(uuid)) {
                return user.getCopy();
            }
        }

        return null;
    }

    @Override
    public List<UserEntity> findAll() {
        return List.copyOf(userEntities);
    }

    @Override
    public void update(UserEntity entity) {
        for (UserEntity user : userEntities) {
            if (user.getUuid().equals(entity.getUuid())) {
                user.setName(entity.getName());
                user.setEmail(entity.getEmail());
                user.setPassword(entity.getPassword());
                user.setRole(entity.getRole());
                user.setBlocked(entity.getBlocked());
            }
        }
    }

    @Override
    public boolean delete(UserEntity entity) {
        return userEntities.remove(entity);
    }

    private boolean emailExists(String email) {
        for (UserEntity user : userEntities) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    public UserEntity findUserWithEmailAndPassword(String email, String password) {
        for (UserEntity user : userEntities) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }

        return null;
    }
}