package org.example.repository;

import liquibase.exception.LiquibaseException;
import org.example.db.ConnectionClass;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

class UserRepositoryTest {
    UserRepository userRepository = new UserRepository();
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:17.4");

    @BeforeAll
    static void beforeAll() {
        container.start();
        try {
            ConnectionClass.setConfig(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @BeforeEach
    void setUp() {
        try {
            for (UserEntity user : userRepository.findAll()) {
                userRepository.delete(user);
            }
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
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

        try {
            userEntity = userRepository.add(userEntity);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        UserEntity userEntity2 = new UserEntity();

        userEntity2.setName("t");
        userEntity2.setEmail("t");
        userEntity2.setPassword("t");
        userEntity2.setRole(UserRole.USER);
        userEntity2.setBlocked(false);

        try {
            userEntity2 = userRepository.add(userEntity2);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertNull(userEntity2);

        UserEntity userEntity3 = new UserEntity();

        userEntity3.setName("t2");
        userEntity3.setEmail("t");
        userEntity3.setPassword("t2");
        userEntity3.setRole(UserRole.USER);
        userEntity3.setBlocked(true);

        try {
            userEntity3 = userRepository.add(userEntity3);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertNull(userEntity3);
    }

    @Test
    void findByIdTest() {
        UserEntity userEntity = new UserEntity();

        userEntity.setName("t");
        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        try {
            userEntity = userRepository.add(userEntity);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        try {
            Assertions.assertEquals(userRepository.findById(userEntity.getId()), userEntity);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        userEntity.setRole(UserRole.ADMIN);

        try {
            Assertions.assertNotEquals(userRepository.findById(userEntity.getId()), userEntity);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        try {
            Assertions.assertNull(userRepository.findById(50));
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findAllTest() {
        UserEntity userEntity = new UserEntity();

        userEntity.setName("t");
        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        UserEntity userEntity2 = new UserEntity();

        userEntity2.setName("t2");
        userEntity2.setEmail("t2");
        userEntity2.setPassword("t2");
        userEntity2.setRole(UserRole.USER);
        userEntity2.setBlocked(true);

        UserEntity userEntity3 = new UserEntity();

        userEntity3.setName("t3");
        userEntity3.setEmail("t3");
        userEntity3.setPassword("t2");
        userEntity3.setRole(UserRole.ADMIN);
        userEntity3.setBlocked(false);

        List<UserEntity> userEntities = List.of(userEntity, userEntity2, userEntity3);

        List<UserEntity> userEntitiesReturned;
        try {
            userRepository.add(userEntity);
            userRepository.add(userEntity2);
            userRepository.add(userEntity3);

            userEntitiesReturned = userRepository.findAll();
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(userEntities, userEntitiesReturned);

        UserEntity userEntity4 = new UserEntity();

        userEntity4.setName("t4");
        userEntity4.setEmail("t4");
        userEntity4.setPassword("t2");
        userEntity4.setRole(UserRole.ADMIN);
        userEntity4.setBlocked(false);

        userEntities = List.of(userEntity, userEntity2, userEntity3, userEntity4);
        try {
            userRepository.add(userEntity4);

            userEntitiesReturned = userRepository.findAll();
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

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

        try {
            userEntity = userRepository.add(userEntity);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        UserEntity userEntity2 = new UserEntity(userEntity.getId());

        userEntity2.setName("t2");
        userEntity2.setEmail("t2");
        userEntity2.setPassword("t2");
        userEntity2.setRole(UserRole.USER);
        userEntity2.setBlocked(false);

        try {
            userRepository.update(userEntity2);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        try {
            Assertions.assertEquals(userRepository.findById(userEntity.getId()), userEntity2);

            userEntity2.setRole(UserRole.ADMIN);

            Assertions.assertNotEquals(userRepository.findById(userEntity.getId()), userEntity2);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void deleteTest() {
        UserEntity userEntity = new UserEntity();

        userEntity.setName("t");
        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        UserEntity userEntity2 = new UserEntity();

        userEntity2.setName("t2");
        userEntity2.setEmail("t2");
        userEntity2.setPassword("t2");
        userEntity2.setRole(UserRole.USER);
        userEntity2.setBlocked(true);

        UserEntity userEntity3 = new UserEntity();

        userEntity3.setName("t3");
        userEntity3.setEmail("t3");
        userEntity3.setPassword("t2");
        userEntity3.setRole(UserRole.ADMIN);
        userEntity3.setBlocked(false);

        List<UserEntity> userEntities = List.of(userEntity, userEntity2, userEntity3);

        try {
            userEntity = userRepository.add(userEntity);
            userRepository.add(userEntity2);
            userRepository.add(userEntity3);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        List<UserEntity> userEntitiesReturned;
        try {
            userEntitiesReturned = userRepository.findAll();

            Assertions.assertEquals(userEntities, userEntitiesReturned);

            userRepository.delete(userEntity);
            userEntitiesReturned = userRepository.findAll();
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertNotEquals(userEntities, userEntitiesReturned);

        userEntities = List.of(userEntity2, userEntity3);

        Assertions.assertEquals(userEntities, userEntitiesReturned);
    }

    @Test
    void findUserWithEmailAndPasswordTest() throws SQLException, LiquibaseException {
        UserEntity userEntity = new UserEntity();

        userEntity.setName("t");
        userEntity.setEmail("te");
        userEntity.setPassword("tp");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        userRepository.add(userEntity);

        Assertions.assertEquals(userRepository.findUserWithEmailAndPassword("te", "tp"), userEntity);

        UserEntity userEntity2 = new UserEntity();

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