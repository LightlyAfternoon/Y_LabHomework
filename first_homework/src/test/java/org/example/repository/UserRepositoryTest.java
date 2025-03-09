package org.example.repository;

import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

class UserRepositoryTest {
    UserRepository userRepository = new UserRepository();

    @BeforeEach
    void setUp() {
        for (UserEntity user : userRepository.findAll()) {
            userRepository.delete(user);
        }
    }

    @Test
    void addTest() {
        UserEntity userEntity = new UserEntity();

        userEntity.setName("t");
        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        userEntity = userRepository.add(userEntity);

        UserEntity userEntity2 = new UserEntity(null);

        userEntity2.setName("t");
        userEntity2.setEmail("t");
        userEntity2.setPassword("t");
        userEntity2.setRole(UserRole.USER);
        userEntity2.setBlocked(false);

        userEntity2 = userRepository.add(userEntity2);

        Assertions.assertEquals(userEntity, userEntity2);
        Assertions.assertEquals(userEntity.getUuid(), userEntity2.getUuid());

        userEntity.setRole(UserRole.ADMIN);

        Assertions.assertNotEquals(userEntity, userEntity2);

        UserEntity userEntity3 = new UserEntity(null);

        userEntity3.setName("t2");
        userEntity3.setEmail("t");
        userEntity3.setPassword("t2");
        userEntity3.setRole(UserRole.USER);
        userEntity3.setBlocked(true);

        userEntity3 = userRepository.add(userEntity3);

        Assertions.assertNull(userEntity3);
    }

    @Test
    void findByIdTest() {
        UserEntity userEntity = new UserEntity(null);

        userEntity.setName("t");
        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        userEntity = userRepository.add(userEntity);

        Assertions.assertEquals(userRepository.findById(userEntity.getUuid()), userEntity);

        userEntity.setRole(UserRole.ADMIN);

        Assertions.assertNotEquals(userRepository.findById(userEntity.getUuid()), userEntity);

        Assertions.assertNull(userRepository.findById(UUID.randomUUID()));
    }

    @Test
    void findAllTest() {
        UserEntity userEntity = new UserEntity(null);

        userEntity.setName("t");
        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        UserEntity userEntity2 = new UserEntity(null);

        userEntity2.setName("t2");
        userEntity2.setEmail("t2");
        userEntity2.setPassword("t2");
        userEntity2.setRole(UserRole.USER);
        userEntity2.setBlocked(true);

        UserEntity userEntity3 = new UserEntity(null);

        userEntity3.setName("t3");
        userEntity3.setEmail("t3");
        userEntity3.setPassword("t2");
        userEntity3.setRole(UserRole.ADMIN);
        userEntity3.setBlocked(false);

        List<UserEntity> userEntities = List.of(userEntity, userEntity2, userEntity3);

        userRepository.add(userEntity);
        userRepository.add(userEntity2);
        userRepository.add(userEntity3);

        List<UserEntity> userEntitiesReturned = userRepository.findAll();

        Assertions.assertEquals(userEntities, userEntitiesReturned);

        UserEntity userEntity4 = new UserEntity(null);

        userEntity4.setName("t4");
        userEntity4.setEmail("t4");
        userEntity4.setPassword("t2");
        userEntity4.setRole(UserRole.ADMIN);
        userEntity4.setBlocked(false);

        userEntities = List.of(userEntity, userEntity2, userEntity3, userEntity4);
        userRepository.add(userEntity4);
        userEntitiesReturned = userRepository.findAll();

        Assertions.assertEquals(userEntities, userEntitiesReturned);

        userEntity.setRole(UserRole.ADMIN);

        Assertions.assertNotEquals(userEntities, userEntitiesReturned);
    }

    @Test
    void updateTest() {
        UserEntity userEntity = new UserEntity();

        userEntity.setName("t");
        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        userEntity = userRepository.add(userEntity);

        UserEntity userEntity2 = new UserEntity(userEntity.getUuid());

        userEntity2.setName("t2");
        userEntity2.setEmail("t2");
        userEntity2.setPassword("t2");
        userEntity2.setRole(UserRole.USER);
        userEntity2.setBlocked(false);

        userRepository.update(userEntity2);

        Assertions.assertEquals(userRepository.findById(userEntity.getUuid()), userEntity2);

        userEntity2.setRole(UserRole.ADMIN);

        Assertions.assertNotEquals(userRepository.findById(userEntity.getUuid()), userEntity2);
    }

    @Test
    void deleteTest() {
        UserEntity userEntity = new UserEntity(null);

        userEntity.setName("t");
        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        UserEntity userEntity2 = new UserEntity(null);

        userEntity2.setName("t2");
        userEntity2.setEmail("t2");
        userEntity2.setPassword("t2");
        userEntity2.setRole(UserRole.USER);
        userEntity2.setBlocked(true);

        UserEntity userEntity3 = new UserEntity(null);

        userEntity3.setName("t3");
        userEntity3.setEmail("t3");
        userEntity3.setPassword("t2");
        userEntity3.setRole(UserRole.ADMIN);
        userEntity3.setBlocked(false);

        List<UserEntity> userEntities = List.of(userEntity, userEntity2, userEntity3);

        userEntity = userRepository.add(userEntity);
        userRepository.add(userEntity2);
        userRepository.add(userEntity3);

        List<UserEntity> userEntitiesReturned = userRepository.findAll();

        Assertions.assertEquals(userEntities, userEntitiesReturned);

        userRepository.delete(userEntity);
        userEntitiesReturned = userRepository.findAll();

        Assertions.assertNotEquals(userEntities, userEntitiesReturned);

        userEntities = List.of(userEntity2, userEntity3);

        Assertions.assertEquals(userEntities, userEntitiesReturned);
    }

    @Test
    void findUserWithEmailAndPasswordTest() {
        UserEntity userEntity = new UserEntity();

        userEntity.setName("t");
        userEntity.setEmail("te");
        userEntity.setPassword("tp");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        userRepository.add(userEntity);

        Assertions.assertEquals(userRepository.findUserWithEmailAndPassword("te", "tp"), userEntity);

        UserEntity userEntity2 = new UserEntity(null);

        userEntity2.setName("t2");
        userEntity2.setEmail("te2");
        userEntity2.setPassword("tp2");
        userEntity2.setRole(UserRole.USER);
        userEntity2.setBlocked(false);

        userRepository.add(userEntity2);

        Assertions.assertEquals(userRepository.findUserWithEmailAndPassword("te2", "tp2"), userEntity2);
        Assertions.assertNotEquals(userRepository.findUserWithEmailAndPassword("te", "tp2"), userEntity2);
        Assertions.assertNotEquals(userRepository.findUserWithEmailAndPassword("te2", "tp"), userEntity2);
        Assertions.assertNotEquals(userRepository.findUserWithEmailAndPassword("tE2", "tp2"), userEntity2);
        Assertions.assertNotEquals(userRepository.findUserWithEmailAndPassword("tE2", "tP2"), userEntity2);
    }
}