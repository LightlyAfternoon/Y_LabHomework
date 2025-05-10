package org.example.repository;

import org.example.model.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface UserRepository extends Repository<UserEntity, Integer> {
    UserEntity findById(int id);

    List<UserEntity> findAll();

    @Modifying
    UserEntity save(UserEntity entity);

    @Modifying
    void delete(UserEntity entity);

    UserEntity findByEmail(String email);

    UserEntity findByEmailAndPassword(String email, String password);
}