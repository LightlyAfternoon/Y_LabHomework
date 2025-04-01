package org.example.repository;

import liquibase.exception.LiquibaseException;
import org.example.CurrentUser;
import org.example.config.MyTestConfig;
import org.example.model.*;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@DisplayName("Tests of monthly budget repository methods")
class MonthlyBudgetRepositoryTest {
    UserEntity userEntity;
    MonthlyBudgetRepository monthlyBudgetRepository;
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:17.4");
    UserRepository userRepository;
    AnnotationConfigApplicationContext context;

    @BeforeAll
    static void beforeAll() throws SQLException, LiquibaseException {
        container.start();

        MyTestConfig.setConfig(container.getJdbcUrl(), container.getUsername(), container.getPassword());
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(MyTestConfig.class);
        userRepository = context.getBean(UserRepository.class);
        monthlyBudgetRepository = context.getBean(MonthlyBudgetRepository.class);

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

        userEntity = userRepository.save(userEntity);

        CurrentUser.currentUser = userEntity;
    }

    @DisplayName("Test of the method for adding monthly budget")
    @Test
    void addTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser.getId(), date);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        MonthlyBudgetEntity savedMonthlyBudgetEntity = monthlyBudgetRepository.save(monthlyBudgetEntity);

        Assertions.assertNotEquals(0, savedMonthlyBudgetEntity.getId());
        Assertions.assertEquals(monthlyBudgetEntity, savedMonthlyBudgetEntity);

        MonthlyBudgetEntity monthlyBudgetEntity2 = new MonthlyBudgetEntity(CurrentUser.currentUser.getId(), date);

        monthlyBudgetEntity2.setSum(BigDecimal.valueOf(10.0));

        monthlyBudgetEntity2 = monthlyBudgetRepository.save(monthlyBudgetEntity2);

        Assertions.assertNotEquals(monthlyBudgetEntity, monthlyBudgetEntity2);
    }

    @DisplayName("Test of the method for finding monthly budget by id")
    @Test
    void findByIdTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser.getId(), date);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        monthlyBudgetEntity = monthlyBudgetRepository.save(monthlyBudgetEntity);

        Assertions.assertEquals(monthlyBudgetRepository.findById(monthlyBudgetEntity.getId()), monthlyBudgetEntity);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(1.5));

        Assertions.assertNotEquals(monthlyBudgetRepository.findById(monthlyBudgetEntity.getId()), monthlyBudgetEntity);

        Assertions.assertNull(monthlyBudgetRepository.findById(20));
    }

    @DisplayName("Test of the method for finding monthly budget by date and user id")
    @Test
    void findByDateAndUserIdTest() {
        Date date = Date.valueOf("2000-01-01");
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser.getId(), date);

        Date date2 = Date.valueOf("2000-02-01");
        MonthlyBudgetEntity monthlyBudgetEntity2 = new MonthlyBudgetEntity(CurrentUser.currentUser.getId(), date2);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        monthlyBudgetEntity = monthlyBudgetRepository.save(monthlyBudgetEntity);

        Assertions.assertEquals(monthlyBudgetRepository.findByDateAndUserId(date, CurrentUser.currentUser.getId()), monthlyBudgetEntity);

        monthlyBudgetEntity2.setSum(BigDecimal.valueOf(1.5));

        monthlyBudgetEntity2 = monthlyBudgetRepository.save(monthlyBudgetEntity2);

        Assertions.assertEquals(monthlyBudgetRepository.findByDateAndUserId(date2, CurrentUser.currentUser.getId()), monthlyBudgetEntity2);
        Assertions.assertNotEquals(monthlyBudgetRepository.findByDateAndUserId(date2, CurrentUser.currentUser.getId()), monthlyBudgetEntity);

        Assertions.assertNull(monthlyBudgetRepository.findByDateAndUserId(Date.valueOf("2001-01-01"), CurrentUser.currentUser.getId()));
    }

    @DisplayName("Test of the method for finding all monthly budgets")
    @Test
    void findAllTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser.getId(), date);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        MonthlyBudgetEntity monthlyBudgetEntity2 = new MonthlyBudgetEntity(CurrentUser.currentUser.getId(), date);

        monthlyBudgetEntity2.setSum(BigDecimal.valueOf(20.0));

        MonthlyBudgetEntity monthlyBudgetEntity3 = new MonthlyBudgetEntity(CurrentUser.currentUser.getId(), date);

        monthlyBudgetEntity3.setSum(BigDecimal.valueOf(30.3));

        List<MonthlyBudgetEntity> transactionEntities = List.of(monthlyBudgetEntity, monthlyBudgetEntity2, monthlyBudgetEntity3);


        List<MonthlyBudgetEntity> transactionEntitiesReturned;

        monthlyBudgetRepository.save(monthlyBudgetEntity);
        monthlyBudgetRepository.save(monthlyBudgetEntity2);
        monthlyBudgetRepository.save(monthlyBudgetEntity3);

        transactionEntitiesReturned = monthlyBudgetRepository.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        MonthlyBudgetEntity monthlyBudgetEntity4 = new MonthlyBudgetEntity(CurrentUser.currentUser.getId(), date);

        monthlyBudgetEntity4.setSum(BigDecimal.valueOf(10.0));

        transactionEntities = List.of(monthlyBudgetEntity, monthlyBudgetEntity2, monthlyBudgetEntity3, monthlyBudgetEntity4);

        monthlyBudgetRepository.save(monthlyBudgetEntity4);
        transactionEntitiesReturned = monthlyBudgetRepository.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(1.1));

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);
    }

    @DisplayName("Test of the method for updating monthly budget")
    @Test
    void updateTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser.getId(), date);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        monthlyBudgetEntity = monthlyBudgetRepository.save(monthlyBudgetEntity);

        MonthlyBudgetEntity monthlyBudgetEntity2 = new MonthlyBudgetEntity(monthlyBudgetEntity.getId(), CurrentUser.currentUser.getId(), date);

        monthlyBudgetEntity2.setSum(BigDecimal.valueOf(1.23));

        monthlyBudgetRepository.save(monthlyBudgetEntity2);

        Assertions.assertEquals(monthlyBudgetRepository.findById(monthlyBudgetEntity.getId()), monthlyBudgetEntity2);

        monthlyBudgetEntity2.setSum(BigDecimal.valueOf(2.2));

        Assertions.assertNotEquals(monthlyBudgetRepository.findById(monthlyBudgetEntity.getId()), monthlyBudgetEntity2);
    }

    @DisplayName("Test of the method for deleting monthly budget")
    @Test
    void deleteTest() {
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser.getId());
        Date date = new Date(System.currentTimeMillis());

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        MonthlyBudgetEntity monthlyBudgetEntity2 = new MonthlyBudgetEntity(monthlyBudgetEntity.getId(), CurrentUser.currentUser.getId(), date);

        monthlyBudgetEntity2.setSum(BigDecimal.valueOf(1.23));

        MonthlyBudgetEntity monthlyBudgetEntity3 = new MonthlyBudgetEntity(CurrentUser.currentUser.getId());

        monthlyBudgetEntity3.setSum(BigDecimal.valueOf(30.3));

        List<MonthlyBudgetEntity> transactionEntities = List.of(monthlyBudgetEntity, monthlyBudgetEntity2, monthlyBudgetEntity3);

        monthlyBudgetEntity = monthlyBudgetRepository.save(monthlyBudgetEntity);
        monthlyBudgetRepository.save(monthlyBudgetEntity2);
        monthlyBudgetRepository.save(monthlyBudgetEntity3);

        List<MonthlyBudgetEntity> transactionEntitiesReturned = monthlyBudgetRepository.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        monthlyBudgetRepository.delete(monthlyBudgetEntity);
        transactionEntitiesReturned = monthlyBudgetRepository.findAll();

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntities = List.of(monthlyBudgetEntity2, monthlyBudgetEntity3);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);
    }
}