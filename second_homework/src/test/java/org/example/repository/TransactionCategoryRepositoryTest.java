package org.example.repository;

import liquibase.exception.LiquibaseException;
import org.example.CurrentUser;
import org.example.db.ConnectionClass;
import org.example.model.TransactionCategoryEntity;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

class TransactionCategoryRepositoryTest {
    UserEntity userEntity;
    TransactionCategoryRepository categoryRepository = new TransactionCategoryRepository();
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:17.4");

    @BeforeAll
    static void beforeAll() {
        container.start();

        ConnectionClass.setConfig(container.getJdbcUrl(), container.getUsername(), container.getPassword());
    }

    @AfterAll
    static void afterAll() {
        container.stop();

        ConnectionClass.nullConnection();
    }

    @BeforeEach
    void setUp() {
        try {
            UserRepository userRepository = new UserRepository();

            for (TransactionCategoryEntity category : categoryRepository.findAll()) {
                categoryRepository.delete(category);
            }

            for (UserEntity user : userRepository.findAll()) {
                userRepository.delete(user);
            }

            userEntity = new UserEntity();

            userEntity.setEmail("t");
            userEntity.setPassword("t");
            userEntity.setName("t");
            userEntity.setRole(UserRole.USER);
            userEntity.setBlocked(false);

            userEntity = userRepository.add(userEntity);

            CurrentUser.currentUser = userEntity;
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        try {
            categoryEntity = categoryRepository.add(categoryEntity);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity();

        categoryEntity2.setName("t");

        try {
            categoryEntity2 = categoryRepository.add(categoryEntity2);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(categoryEntity, categoryEntity2);
        Assertions.assertEquals(categoryEntity.getId(), categoryEntity2.getId());

        categoryEntity.setName("t0");

        Assertions.assertNotEquals(categoryEntity, categoryEntity2);

        TransactionCategoryEntity categoryEntity3 = new TransactionCategoryEntity();

        categoryEntity3.setName("t3");

        try {
            categoryEntity3 = categoryRepository.add(categoryEntity3);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertNotEquals(categoryEntity, categoryEntity3);
    }

    @Test
    void addGoalTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity(CurrentUser.currentUser);

        categoryEntity.setName("t");
        categoryEntity.setNeededSum(BigDecimal.valueOf(10.0));

        try {
            categoryEntity = categoryRepository.add(categoryEntity);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity(CurrentUser.currentUser);

        categoryEntity2.setName("t");
        categoryEntity2.setNeededSum(BigDecimal.valueOf(10.00));

        try {
            categoryEntity2 = categoryRepository.add(categoryEntity2);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(categoryEntity, categoryEntity2);
        Assertions.assertEquals(categoryEntity.getId(), categoryEntity2.getId());

        categoryEntity.setName("t0");

        Assertions.assertNotEquals(categoryEntity, categoryEntity2);

        TransactionCategoryEntity categoryEntity3 = new TransactionCategoryEntity(CurrentUser.currentUser);

        categoryEntity3.setName("t3");
        categoryEntity2.setNeededSum(BigDecimal.valueOf(30.3));

        try {
            categoryEntity3 = categoryRepository.add(categoryEntity3);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertNotEquals(categoryEntity, categoryEntity3);
    }

    @Test
    void findByIdTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        try {
            categoryEntity = categoryRepository.add(categoryEntity);

            Assertions.assertEquals(categoryRepository.findById(categoryEntity.getId()), categoryEntity);

            categoryEntity.setName("t0");

            Assertions.assertNotEquals(categoryRepository.findById(categoryEntity.getId()), categoryEntity);

            Assertions.assertNull(categoryRepository.findById(10));
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findByNameTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        try {
            categoryRepository.add(categoryEntity);

            Assertions.assertEquals(categoryRepository.findByName("t"), categoryEntity);

            TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity();

            categoryEntity2.setName("t0");

            categoryRepository.add(categoryEntity2);

            Assertions.assertNotEquals(categoryRepository.findByName("t"), categoryEntity2);
            Assertions.assertEquals(categoryRepository.findByName("t0"), categoryEntity2);
            Assertions.assertNull(categoryRepository.findByName("t2"));
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findAllTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity();

        categoryEntity2.setName("t2");

        TransactionCategoryEntity categoryEntity3 = new TransactionCategoryEntity();

        categoryEntity3.setName("t3");

        List<TransactionCategoryEntity> categoryEntities = List.of(categoryEntity, categoryEntity2, categoryEntity3);

        List<TransactionCategoryEntity> transactionCategoryEntitiesReturned;

        try {
            categoryRepository.add(categoryEntity);
            categoryRepository.add(categoryEntity2);
            categoryRepository.add(categoryEntity3);

            transactionCategoryEntitiesReturned = categoryRepository.findAll();
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);

        TransactionCategoryEntity categoryEntity4 = new TransactionCategoryEntity();

        categoryEntity4.setName("t4");

        categoryEntities = List.of(categoryEntity, categoryEntity2, categoryEntity3, categoryEntity4);
        try {
            categoryRepository.add(categoryEntity4);

            transactionCategoryEntitiesReturned = categoryRepository.findAll();
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);

        categoryEntity.setName("t0");

        Assertions.assertNotEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }

    @Test
    void findCommonCategoriesOrGoalsWithUserTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity(CurrentUser.currentUser);

        categoryEntity2.setName("t2");
        categoryEntity2.setNeededSum(BigDecimal.valueOf(20.0));

        TransactionCategoryEntity categoryEntity3 = new TransactionCategoryEntity();

        categoryEntity3.setName("t3");

        TransactionCategoryEntity categoryEntity4 = new TransactionCategoryEntity(CurrentUser.currentUser);

        categoryEntity4.setName("t4");
        categoryEntity4.setNeededSum(BigDecimal.valueOf(40.4));

        List<TransactionCategoryEntity> categoryEntities = List.of(categoryEntity, categoryEntity2, categoryEntity3, categoryEntity4);


        List<TransactionCategoryEntity> transactionCategoryEntitiesReturned;
        try {
            categoryRepository.add(categoryEntity);
            categoryRepository.add(categoryEntity2);
            categoryRepository.add(categoryEntity3);
            categoryRepository.add(categoryEntity4);

            transactionCategoryEntitiesReturned = categoryRepository.findCommonCategoriesOrGoalsWithUser(CurrentUser.currentUser);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }

    @Test
    void findAllUserGoalsTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity(CurrentUser.currentUser);

        categoryEntity2.setName("t2");
        categoryEntity2.setNeededSum(BigDecimal.valueOf(20.0));

        TransactionCategoryEntity categoryEntity3 = new TransactionCategoryEntity();

        categoryEntity3.setName("t3");

        TransactionCategoryEntity categoryEntity4 = new TransactionCategoryEntity(CurrentUser.currentUser);

        categoryEntity4.setName("t4");
        categoryEntity4.setNeededSum(BigDecimal.valueOf(40.4));

        List<TransactionCategoryEntity> categoryEntities = List.of(categoryEntity2, categoryEntity4);


        List<TransactionCategoryEntity> transactionCategoryEntitiesReturned;
        try {
            categoryRepository.add(categoryEntity);
            categoryRepository.add(categoryEntity2);
            categoryRepository.add(categoryEntity3);
            categoryRepository.add(categoryEntity4);

            transactionCategoryEntitiesReturned = categoryRepository.findAllUserGoals(CurrentUser.currentUser);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }

    @Test
    void updateTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        try {
            categoryEntity = categoryRepository.add(categoryEntity);

            TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity(categoryEntity.getId());

            categoryEntity2.setName("t2");

            categoryRepository.update(categoryEntity2);

            Assertions.assertEquals(categoryRepository.findById(categoryEntity.getId()), categoryEntity2);

            categoryEntity2.setName("t0");

            Assertions.assertNotEquals(categoryRepository.findById(categoryEntity.getId()), categoryEntity2);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity(categoryEntity.getId());

        categoryEntity2.setName("t2");

        TransactionCategoryEntity categoryEntity3 = new TransactionCategoryEntity();

        categoryEntity3.setName("t3");

        List<TransactionCategoryEntity> categoryEntities = List.of(categoryEntity, categoryEntity2, categoryEntity3);

        try {
            categoryEntity = categoryRepository.add(categoryEntity);
            categoryRepository.add(categoryEntity2);
            categoryRepository.add(categoryEntity3);

            List<TransactionCategoryEntity> transactionCategoryEntitiesReturned = categoryRepository.findAll();

            Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);

            categoryRepository.delete(categoryEntity);
            transactionCategoryEntitiesReturned = categoryRepository.findAll();

            Assertions.assertNotEquals(categoryEntities, transactionCategoryEntitiesReturned);

            categoryEntities = List.of(categoryEntity2, categoryEntity3);

            Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }
}