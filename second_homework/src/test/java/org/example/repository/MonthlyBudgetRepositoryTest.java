package org.example.repository;

import liquibase.exception.LiquibaseException;
import org.example.CurrentUser;
import org.example.db.ConnectionClass;
import org.example.model.*;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

class MonthlyBudgetRepositoryTest {
    UserEntity userEntity;
    MonthlyBudgetRepository monthlyBudgetRepository = new MonthlyBudgetRepository();
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

            for (MonthlyBudgetEntity budget : monthlyBudgetRepository.findAll()) {
                monthlyBudgetRepository.delete(budget);
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
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        try {
            monthlyBudgetEntity = monthlyBudgetRepository.add(monthlyBudgetEntity);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        MonthlyBudgetEntity monthlyBudgetEntity2 = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity2.setSum(BigDecimal.valueOf(10.10));

        try {
            monthlyBudgetEntity2 = monthlyBudgetRepository.add(monthlyBudgetEntity2);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(monthlyBudgetEntity, monthlyBudgetEntity2);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(20.0));

        Assertions.assertNotEquals(monthlyBudgetEntity, monthlyBudgetEntity2);

        MonthlyBudgetEntity monthlyBudgetEntity3 = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity3.setSum(BigDecimal.valueOf(10.0));

        try {
            monthlyBudgetEntity3 = monthlyBudgetRepository.add(monthlyBudgetEntity3);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertNotEquals(monthlyBudgetEntity, monthlyBudgetEntity3);
    }

    @Test
    void findByIdTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        try {
            monthlyBudgetEntity = monthlyBudgetRepository.add(monthlyBudgetEntity);

            Assertions.assertEquals(monthlyBudgetRepository.findById(monthlyBudgetEntity.getId()), monthlyBudgetEntity);

            monthlyBudgetEntity.setSum(BigDecimal.valueOf(1.5));

            Assertions.assertNotEquals(monthlyBudgetRepository.findById(monthlyBudgetEntity.getId()), monthlyBudgetEntity);

            Assertions.assertNull(monthlyBudgetRepository.findById(20));
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findByDateAndUserTest() {
        Date date = Date.valueOf("2000-01-01");
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        Date date2 = Date.valueOf("2000-02-01");
        MonthlyBudgetEntity monthlyBudgetEntity2 = new MonthlyBudgetEntity(CurrentUser.currentUser, date2);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        try {
            monthlyBudgetEntity = monthlyBudgetRepository.add(monthlyBudgetEntity);

            Assertions.assertEquals(monthlyBudgetRepository.findByDateAndUser(date, CurrentUser.currentUser), monthlyBudgetEntity);

            monthlyBudgetEntity2.setSum(BigDecimal.valueOf(1.5));

            monthlyBudgetEntity2 = monthlyBudgetRepository.add(monthlyBudgetEntity2);

            Assertions.assertEquals(monthlyBudgetRepository.findByDateAndUser(date2, CurrentUser.currentUser), monthlyBudgetEntity2);
            Assertions.assertNotEquals(monthlyBudgetRepository.findByDateAndUser(date2, CurrentUser.currentUser), monthlyBudgetEntity);

            Assertions.assertNull(monthlyBudgetRepository.findByDateAndUser(Date.valueOf("2001-01-01"), CurrentUser.currentUser));
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findAllTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        MonthlyBudgetEntity monthlyBudgetEntity2 = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity2.setSum(BigDecimal.valueOf(20.0));

        MonthlyBudgetEntity monthlyBudgetEntity3 = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity3.setSum(BigDecimal.valueOf(30.3));

        List<MonthlyBudgetEntity> transactionEntities = List.of(monthlyBudgetEntity, monthlyBudgetEntity2, monthlyBudgetEntity3);


        List<MonthlyBudgetEntity> transactionEntitiesReturned;
        try {
            monthlyBudgetRepository.add(monthlyBudgetEntity);
            monthlyBudgetRepository.add(monthlyBudgetEntity2);
            monthlyBudgetRepository.add(monthlyBudgetEntity3);

            transactionEntitiesReturned = monthlyBudgetRepository.findAll();
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        MonthlyBudgetEntity monthlyBudgetEntity4 = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity4.setSum(BigDecimal.valueOf(10.0));

        transactionEntities = List.of(monthlyBudgetEntity, monthlyBudgetEntity2, monthlyBudgetEntity3, monthlyBudgetEntity4);
        try {
            monthlyBudgetRepository.add(monthlyBudgetEntity4);
            transactionEntitiesReturned = monthlyBudgetRepository.findAll();
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(1.1));

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);
    }

    @Test
    void updateTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        try {
            monthlyBudgetEntity = monthlyBudgetRepository.add(monthlyBudgetEntity);

            MonthlyBudgetEntity monthlyBudgetEntity2 = new MonthlyBudgetEntity(monthlyBudgetEntity.getId(), CurrentUser.currentUser, date);

            monthlyBudgetEntity2.setSum(BigDecimal.valueOf(1.23));

            monthlyBudgetRepository.update(monthlyBudgetEntity2);

            Assertions.assertEquals(monthlyBudgetRepository.findById(monthlyBudgetEntity.getId()), monthlyBudgetEntity2);

            monthlyBudgetEntity2.setSum(BigDecimal.valueOf(2.2));

            Assertions.assertNotEquals(monthlyBudgetRepository.findById(monthlyBudgetEntity.getId()), monthlyBudgetEntity2);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteTest() {
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser);
        Date date = new Date(System.currentTimeMillis());

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        MonthlyBudgetEntity monthlyBudgetEntity2 = new MonthlyBudgetEntity(monthlyBudgetEntity.getId(), CurrentUser.currentUser, date);

        monthlyBudgetEntity2.setSum(BigDecimal.valueOf(1.23));

        MonthlyBudgetEntity monthlyBudgetEntity3 = new MonthlyBudgetEntity(CurrentUser.currentUser);

        monthlyBudgetEntity3.setSum(BigDecimal.valueOf(30.3));

        List<MonthlyBudgetEntity> transactionEntities = List.of(monthlyBudgetEntity, monthlyBudgetEntity2, monthlyBudgetEntity3);

        try {
            monthlyBudgetEntity = monthlyBudgetRepository.add(monthlyBudgetEntity);
            monthlyBudgetRepository.add(monthlyBudgetEntity2);
            monthlyBudgetRepository.add(monthlyBudgetEntity3);

            List<MonthlyBudgetEntity> transactionEntitiesReturned = monthlyBudgetRepository.findAll();

            Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

            monthlyBudgetRepository.delete(monthlyBudgetEntity);
            transactionEntitiesReturned = monthlyBudgetRepository.findAll();

            Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);

            transactionEntities = List.of(monthlyBudgetEntity2, monthlyBudgetEntity3);

            Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }
}