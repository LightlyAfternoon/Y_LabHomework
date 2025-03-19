package org.example.repository;

import liquibase.exception.LiquibaseException;
import org.example.CurrentUser;
import org.example.db.ConnectionClass;
import org.example.model.TransactionCategoryEntity;
import org.example.model.TransactionEntity;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

class TransactionRepositoryTest {
    UserEntity userEntity;
    TransactionCategoryEntity categoryEntity;
    TransactionRepository transactionRepository = new TransactionRepository();
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
        UserRepository userRepository = new UserRepository();
        TransactionCategoryRepository categoryRepository = new TransactionCategoryRepository();

        try {
            for (TransactionEntity transaction : transactionRepository.findAll()) {
                transactionRepository.delete(transaction);
            }

            for (UserEntity user : userRepository.findAll()) {
                userRepository.delete(user);
            }

            for (TransactionCategoryEntity category : categoryRepository.findAll()) {
                categoryRepository.delete(category);
            }

            userEntity = new UserEntity();

            userEntity.setEmail("t");
            userEntity.setPassword("t");
            userEntity.setName("t");
            userEntity.setRole(UserRole.USER);
            userEntity.setBlocked(false);

            userEntity = userRepository.add(userEntity);

            CurrentUser.currentUser = userEntity;

            categoryEntity = new TransactionCategoryEntity();

            categoryEntity.setName("t");

            categoryEntity = categoryRepository.add(categoryEntity);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(categoryEntity);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        try {
            transactionEntity = transactionRepository.add(transactionEntity);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        TransactionEntity transactionEntity2 = new TransactionEntity(CurrentUser.currentUser);
        transactionEntity2.setSum(BigDecimal.valueOf(10.10));
        transactionEntity2.setCategory(categoryEntity);
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription("t");

        try {
            transactionEntity2 = transactionRepository.add(transactionEntity2);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(transactionEntity, transactionEntity2);

        transactionEntity.setSum(BigDecimal.valueOf(20.0));

        Assertions.assertNotEquals(transactionEntity, transactionEntity2);

        TransactionEntity transactionEntity3 = new TransactionEntity(CurrentUser.currentUser);

        transactionEntity3.setSum(BigDecimal.valueOf(10.0));
        transactionEntity3.setCategory(categoryEntity);
        transactionEntity3.setDate(date);
        transactionEntity3.setDescription("t2");

        try {
            transactionEntity3 = transactionRepository.add(transactionEntity3);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertNotEquals(transactionEntity, transactionEntity3);
    }

    @Test
    void findByIdTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(categoryEntity);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        try {
            transactionEntity = transactionRepository.add(transactionEntity);

            Assertions.assertEquals(transactionRepository.findById(transactionEntity.getId()), transactionEntity);

            transactionEntity.setSum(BigDecimal.valueOf(1.5));

            Assertions.assertNotEquals(transactionRepository.findById(transactionEntity.getId()), transactionEntity);

            Assertions.assertNull(transactionRepository.findById(10));
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findAllTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(categoryEntity);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        TransactionEntity transactionEntity2 = new TransactionEntity(CurrentUser.currentUser);

        transactionEntity2.setSum(BigDecimal.valueOf(20.0));
        transactionEntity2.setCategory(categoryEntity);
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription("t2");

        TransactionEntity transactionEntity3 = new TransactionEntity(CurrentUser.currentUser);

        transactionEntity3.setSum(BigDecimal.valueOf(30.3));
        transactionEntity3.setCategory(categoryEntity);
        transactionEntity3.setDate(date);
        transactionEntity3.setDescription("t3");

        List<TransactionEntity> transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3);

        List<TransactionEntity> transactionEntitiesReturned;
        try {
            transactionRepository.add(transactionEntity);
            transactionRepository.add(transactionEntity2);
            transactionRepository.add(transactionEntity3);

            transactionEntitiesReturned = transactionRepository.findAll();
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        TransactionEntity transactionEntity4 = new TransactionEntity(CurrentUser.currentUser);

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionEntity4.setSum(BigDecimal.valueOf(10.10));
        transactionEntity4.setCategory(categoryEntity);
        transactionEntity4.setDate(date);
        transactionEntity4.setDescription("t4");

        transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3, transactionEntity4);
        try {
            transactionRepository.add(transactionEntity4);
            transactionEntitiesReturned = transactionRepository.findAll();
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntity.setDescription("t5");

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);
    }

    @Test
    void findAllWithUserTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(categoryEntity);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        TransactionEntity transactionEntity2 = new TransactionEntity(CurrentUser.currentUser);

        transactionEntity2.setSum(BigDecimal.valueOf(20.0));
        transactionEntity2.setCategory(categoryEntity);
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription("t2");

        TransactionEntity transactionEntity3 = new TransactionEntity(CurrentUser.currentUser);

        transactionEntity3.setSum(BigDecimal.valueOf(30.3));
        transactionEntity3.setCategory(categoryEntity);
        transactionEntity3.setDate(date);
        transactionEntity3.setDescription("t3");

        List<TransactionEntity> transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3);

        List<TransactionEntity> transactionEntitiesReturned;
        try {
            transactionRepository.add(transactionEntity);
            transactionRepository.add(transactionEntity2);
            transactionRepository.add(transactionEntity3);

            transactionEntitiesReturned = transactionRepository.findAllWithUser(CurrentUser.currentUser);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        TransactionEntity transactionEntity4 = new TransactionEntity(CurrentUser.currentUser);

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionEntity4.setSum(BigDecimal.valueOf(10.10));
        transactionEntity4.setCategory(categoryEntity);
        transactionEntity4.setDate(date);
        transactionEntity4.setDescription("t4");

        transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3, transactionEntity4);
        try {
            transactionRepository.add(transactionEntity4);
            transactionEntitiesReturned = transactionRepository.findAllWithUser(CurrentUser.currentUser);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        UserEntity user = new UserEntity();

        user.setEmail("t2");
        user.setPassword("t2");
        user.setName("t2");
        user.setBlocked(false);

        try {
            user = new UserRepository().add(user);

            transactionRepository.delete(transactionEntity);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity = new TransactionEntity(user);

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(categoryEntity);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        try {
            transactionRepository.add(transactionEntity);

            transactionEntitiesReturned = transactionRepository.findAllWithUser(CurrentUser.currentUser);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntity = new TransactionEntity(CurrentUser.currentUser);

        transactionEntity.setSum(BigDecimal.valueOf(21.30));
        transactionEntity.setCategory(categoryEntity);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        try {
            transactionRepository.add(transactionEntity);

            transactionEntitiesReturned = transactionRepository.findAllWithUser(CurrentUser.currentUser);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);
    }

    @Test
    void findAllWithDateAndCategoryAndTypeAndUserTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(categoryEntity);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        TransactionEntity transactionEntity2 = new TransactionEntity(CurrentUser.currentUser);
        Date date2;
        try {
            date2 = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis() + 86_400_000).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity2.setSum(BigDecimal.valueOf(20.0));
        transactionEntity2.setCategory(categoryEntity);
        transactionEntity2.setDate(date2);
        transactionEntity2.setDescription("t2");

        UserEntity user = new UserEntity();

        user.setEmail("t2");
        user.setPassword("t2");
        user.setName("t2");
        user.setBlocked(false);

        try {
            user = new UserRepository().add(user);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        TransactionEntity transactionEntity3 = new TransactionEntity(user);

        transactionEntity3.setSum(BigDecimal.valueOf(30.3));
        transactionEntity3.setCategory(categoryEntity);
        transactionEntity3.setDate(date);
        transactionEntity3.setDescription("t3");

        TransactionEntity transactionEntity4 = new TransactionEntity(CurrentUser.currentUser);

        transactionEntity4.setSum(BigDecimal.valueOf(-10.10));
        transactionEntity4.setCategory(null);
        transactionEntity4.setDate(date2);
        transactionEntity4.setDescription(null);

        List<TransactionEntity> transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity4);
        List<TransactionEntity> transactionEntitiesReturned;

        try {
            transactionRepository.add(transactionEntity);
            transactionRepository.add(transactionEntity2);
            transactionRepository.add(transactionEntity3);
            transactionRepository.add(transactionEntity4);

            transactionEntitiesReturned = transactionRepository.findAllWithDateAndCategoryAndTypeAndUser(null, null, null, CurrentUser.currentUser);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3, transactionEntity4);

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);

        try {
            transactionEntitiesReturned = transactionRepository.findAllWithDateAndCategoryAndTypeAndUser(date, null, null, CurrentUser.currentUser);

            transactionEntities = List.of(transactionEntity);

            Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

            transactionEntitiesReturned = transactionRepository.findAllWithDateAndCategoryAndTypeAndUser(null, categoryEntity, null, CurrentUser.currentUser);
            transactionEntities = List.of(transactionEntity, transactionEntity2);

            Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

            transactionEntitiesReturned = transactionRepository.findAllWithDateAndCategoryAndTypeAndUser(null, null, "Pos", user);
            transactionEntities = List.of(transactionEntity3);

            Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(categoryEntity);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        try {
            transactionEntity = transactionRepository.add(transactionEntity);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        TransactionEntity transactionEntity2 = new TransactionEntity(transactionEntity.getId(), CurrentUser.currentUser);

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionEntity2.setSum(BigDecimal.valueOf(1.23));
        transactionEntity2.setCategory(categoryEntity);
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription("t2");

        try {
            transactionRepository.update(transactionEntity2);

            Assertions.assertEquals(transactionRepository.findById(transactionEntity.getId()), transactionEntity2);

            transactionEntity2.setSum(BigDecimal.valueOf(2.2));

            Assertions.assertNotEquals(transactionRepository.findById(transactionEntity.getId()), transactionEntity2);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(categoryEntity);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        TransactionEntity transactionEntity2 = new TransactionEntity(transactionEntity.getId(), CurrentUser.currentUser);

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionEntity2.setSum(BigDecimal.valueOf(1.23));
        transactionEntity2.setCategory(categoryEntity);
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription("t2");

        TransactionEntity transactionEntity3 = new TransactionEntity(CurrentUser.currentUser);

        transactionEntity3.setSum(BigDecimal.valueOf(30.3));
        transactionEntity3.setCategory(categoryEntity);
        transactionEntity3.setDate(date);
        transactionEntity3.setDescription("t3");

        List<TransactionEntity> transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3);

        try {
            transactionEntity = transactionRepository.add(transactionEntity);
            transactionRepository.add(transactionEntity2);
            transactionRepository.add(transactionEntity3);

            List<TransactionEntity> transactionEntitiesReturned = transactionRepository.findAll();

            Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

            transactionRepository.delete(transactionEntity);
            transactionEntitiesReturned = transactionRepository.findAll();

            Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);

            transactionEntities = List.of(transactionEntity2, transactionEntity3);

            Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }
}