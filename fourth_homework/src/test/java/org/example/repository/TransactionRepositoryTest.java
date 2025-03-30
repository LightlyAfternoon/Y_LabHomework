package org.example.repository;

import org.example.CurrentUser;
import org.example.config.MyTestConfig;
import org.example.db.ConnectionClass;
import org.example.model.TransactionCategoryEntity;
import org.example.model.TransactionEntity;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

class TransactionRepositoryTest {
    UserEntity userEntity;
    TransactionCategoryEntity categoryEntity;
    TransactionRepository transactionRepository;
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:17.4");
    UserRepository userRepository;
    AnnotationConfigApplicationContext context;

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
        context = new AnnotationConfigApplicationContext(MyTestConfig.class);
        TransactionCategoryRepository categoryRepository = context.getBean(TransactionCategoryRepository.class);

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

        userEntity = userRepository.save(userEntity);

        CurrentUser.currentUser = userEntity;

        categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        categoryEntity = categoryRepository.save(categoryEntity);
    }

    @Test
    void addTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategoryId(categoryEntity.getId());
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        transactionEntity = transactionRepository.save(transactionEntity);

        TransactionEntity transactionEntity2 = new TransactionEntity(CurrentUser.currentUser.getId());
        transactionEntity2.setSum(BigDecimal.valueOf(10.10));
        transactionEntity2.setCategoryId(categoryEntity.getId());
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription("t");

        transactionEntity2 = transactionRepository.save(transactionEntity2);

        Assertions.assertEquals(transactionEntity, transactionEntity2);
        Assertions.assertEquals(transactionEntity.getId(), transactionEntity2.getId());

        transactionEntity.setSum(BigDecimal.valueOf(20.0));

        Assertions.assertNotEquals(transactionEntity, transactionEntity2);

        TransactionEntity transactionEntity3 = new TransactionEntity(CurrentUser.currentUser.getId());

        transactionEntity3.setSum(BigDecimal.valueOf(10.0));
        transactionEntity3.setCategoryId(categoryEntity.getId());
        transactionEntity3.setDate(date);
        transactionEntity3.setDescription("t2");

        transactionEntity3 = transactionRepository.save(transactionEntity3);

        Assertions.assertNotEquals(transactionEntity, transactionEntity3);
    }

    @Test
    void findByIdTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategoryId(categoryEntity.getId());
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        transactionEntity = transactionRepository.save(transactionEntity);

        Assertions.assertEquals(transactionRepository.findById(transactionEntity.getId()), transactionEntity);

        transactionEntity.setSum(BigDecimal.valueOf(1.5));

        Assertions.assertNotEquals(transactionRepository.findById(transactionEntity.getId()), transactionEntity);

        Assertions.assertNull(transactionRepository.findById(10));
    }

    @Test
    void findAllTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategoryId(categoryEntity.getId());
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        TransactionEntity transactionEntity2 = new TransactionEntity(CurrentUser.currentUser.getId());

        transactionEntity2.setSum(BigDecimal.valueOf(20.0));
        transactionEntity2.setCategoryId(categoryEntity.getId());
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription("t2");

        TransactionEntity transactionEntity3 = new TransactionEntity(CurrentUser.currentUser.getId());

        transactionEntity3.setSum(BigDecimal.valueOf(30.3));
        transactionEntity3.setCategoryId(categoryEntity.getId());
        transactionEntity3.setDate(date);
        transactionEntity3.setDescription("t3");

        List<TransactionEntity> transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3);

        List<TransactionEntity> transactionEntitiesReturned;

        transactionRepository.save(transactionEntity);
        transactionRepository.save(transactionEntity2);
        transactionRepository.save(transactionEntity3);

        transactionEntitiesReturned = transactionRepository.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        TransactionEntity transactionEntity4 = new TransactionEntity(CurrentUser.currentUser.getId());

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionEntity4.setSum(BigDecimal.valueOf(10.10));
        transactionEntity4.setCategoryId(categoryEntity.getId());
        transactionEntity4.setDate(date);
        transactionEntity4.setDescription("t4");

        transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3, transactionEntity4);

        transactionRepository.save(transactionEntity4);
        transactionEntitiesReturned = transactionRepository.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntity.setDescription("t5");

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);
    }

    @Test
    void findAllByUserIdTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategoryId(categoryEntity.getId());
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        TransactionEntity transactionEntity2 = new TransactionEntity(CurrentUser.currentUser.getId());

        transactionEntity2.setSum(BigDecimal.valueOf(20.0));
        transactionEntity2.setCategoryId(categoryEntity.getId());
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription("t2");

        TransactionEntity transactionEntity3 = new TransactionEntity(CurrentUser.currentUser.getId());

        transactionEntity3.setSum(BigDecimal.valueOf(30.3));
        transactionEntity3.setCategoryId(categoryEntity.getId());
        transactionEntity3.setDate(date);
        transactionEntity3.setDescription("t3");

        List<TransactionEntity> transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3);

        List<TransactionEntity> transactionEntitiesReturned;

        transactionRepository.save(transactionEntity);
        transactionRepository.save(transactionEntity2);
        transactionRepository.save(transactionEntity3);

        transactionEntitiesReturned = transactionRepository.findAllByUserId(CurrentUser.currentUser.getId());

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        TransactionEntity transactionEntity4 = new TransactionEntity(CurrentUser.currentUser.getId());

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionEntity4.setSum(BigDecimal.valueOf(10.10));
        transactionEntity4.setCategoryId(categoryEntity.getId());
        transactionEntity4.setDate(date);
        transactionEntity4.setDescription("t4");

        transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3, transactionEntity4);

        transactionRepository.save(transactionEntity4);
        transactionEntitiesReturned = transactionRepository.findAllByUserId(CurrentUser.currentUser.getId());

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        UserEntity user = new UserEntity();

        user.setEmail("t2");
        user.setPassword("t2");
        user.setName("t2");
        user.setBlocked(false);

        user = userRepository.save(user);

        transactionRepository.delete(transactionEntity);

        transactionEntity = new TransactionEntity(user.getId());

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategoryId(categoryEntity.getId());
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        transactionRepository.save(transactionEntity);

        transactionEntitiesReturned = transactionRepository.findAllByUserId(CurrentUser.currentUser.getId());

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());

        transactionEntity.setSum(BigDecimal.valueOf(21.30));
        transactionEntity.setCategoryId(categoryEntity.getId());
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        transactionRepository.save(transactionEntity);

        transactionEntitiesReturned = transactionRepository.findAllByUserId(CurrentUser.currentUser.getId());

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);
    }

    @Test
    void findAllByDateAndCategoryIdAndTypeAndUserIdTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategoryId(categoryEntity.getId());
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        TransactionEntity transactionEntity2 = new TransactionEntity(CurrentUser.currentUser.getId());
        Date date2;
        try {
            date2 = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis() + 86_400_000).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity2.setSum(BigDecimal.valueOf(20.0));
        transactionEntity2.setCategoryId(categoryEntity.getId());
        transactionEntity2.setDate(date2);
        transactionEntity2.setDescription("t2");

        UserEntity user = new UserEntity();

        user.setEmail("t2");
        user.setPassword("t2");
        user.setName("t2");
        user.setBlocked(false);

        user = userRepository.save(user);

        TransactionEntity transactionEntity3 = new TransactionEntity(user.getId());

        transactionEntity3.setSum(BigDecimal.valueOf(30.3));
        transactionEntity3.setCategoryId(categoryEntity.getId());
        transactionEntity3.setDate(date);
        transactionEntity3.setDescription("t3");

        TransactionEntity transactionEntity4 = new TransactionEntity(CurrentUser.currentUser.getId());

        transactionEntity4.setSum(BigDecimal.valueOf(-10.10));
        transactionEntity4.setCategoryId(0);
        transactionEntity4.setDate(date2);
        transactionEntity4.setDescription(null);

        List<TransactionEntity> transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity4);
        List<TransactionEntity> transactionEntitiesReturned;

        transactionRepository.save(transactionEntity);
        transactionRepository.save(transactionEntity2);
        transactionRepository.save(transactionEntity3);
        transactionRepository.save(transactionEntity4);

        transactionEntitiesReturned = transactionRepository.findAllByDateAndCategoryIdAndTypeAndUserId(null, 0, null, CurrentUser.currentUser.getId());

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3, transactionEntity4);

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntitiesReturned = transactionRepository.findAllByDateAndCategoryIdAndTypeAndUserId(date, 0, null, CurrentUser.currentUser.getId());

        transactionEntities = List.of(transactionEntity);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntitiesReturned = transactionRepository.findAllByDateAndCategoryIdAndTypeAndUserId(null, categoryEntity.getId(), null, CurrentUser.currentUser.getId());
        transactionEntities = List.of(transactionEntity, transactionEntity2);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntitiesReturned = transactionRepository.findAllByDateAndCategoryIdAndTypeAndUserId(null, 0, "Pos", user.getId());
        transactionEntities = List.of(transactionEntity3);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);
    }

    @Test
    void updateTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategoryId(categoryEntity.getId());
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        transactionEntity = transactionRepository.save(transactionEntity);

        TransactionEntity transactionEntity2 = new TransactionEntity(transactionEntity.getId(), CurrentUser.currentUser.getId());

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionEntity2.setSum(BigDecimal.valueOf(1.23));
        transactionEntity2.setCategoryId(categoryEntity.getId());
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription("t2");

        transactionRepository.save(transactionEntity2);

        Assertions.assertEquals(transactionRepository.findById(transactionEntity.getId()), transactionEntity2);

        transactionEntity2.setSum(BigDecimal.valueOf(2.2));

        Assertions.assertNotEquals(transactionRepository.findById(transactionEntity.getId()), transactionEntity2);
    }

    @Test
    void deleteTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategoryId(categoryEntity.getId());
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        TransactionEntity transactionEntity2 = new TransactionEntity(transactionEntity.getId(), CurrentUser.currentUser.getId());

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionEntity2.setSum(BigDecimal.valueOf(1.23));
        transactionEntity2.setCategoryId(categoryEntity.getId());
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription("t2");

        TransactionEntity transactionEntity3 = new TransactionEntity(CurrentUser.currentUser.getId());

        transactionEntity3.setSum(BigDecimal.valueOf(30.3));
        transactionEntity3.setCategoryId(categoryEntity.getId());
        transactionEntity3.setDate(date);
        transactionEntity3.setDescription("t3");

        List<TransactionEntity> transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3);

        transactionEntity = transactionRepository.save(transactionEntity);
        transactionRepository.save(transactionEntity2);
        transactionRepository.save(transactionEntity3);

        List<TransactionEntity> transactionEntitiesReturned = transactionRepository.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionRepository.delete(transactionEntity);
        transactionEntitiesReturned = transactionRepository.findAll();

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntities = List.of(transactionEntity2, transactionEntity3);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);
    }
}