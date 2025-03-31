package org.example.service;

import liquibase.exception.LiquibaseException;
import org.example.CurrentUser;
import org.example.config.MyTestConfig;
import org.example.controller.dto.MonthlyBudgetDTO;
import org.example.controller.dto.UserDTO;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class MonthlyBudgetServiceTest {
    MonthlyBudgetService monthlyBudgetService;
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:17.4");
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
        UserRepository userRepository = context.getBean(UserRepository.class);
        UserService userService = context.getBean(UserService.class);
        monthlyBudgetService = context.getBean(MonthlyBudgetService.class);

        for (MonthlyBudgetDTO budgetDTO : monthlyBudgetService.findAll()) {
            System.out.println(budgetDTO.getId());
            monthlyBudgetService.delete(budgetDTO.getId());
        }

        for (UserDTO userDTO : userService.findAll()) {
            userService.delete(userDTO.getId());
        }

        UserEntity user = new UserEntity();

        user.setEmail("t");
        user.setPassword("t");
        user.setName("t");
        user.setRole(UserRole.USER);
        user.setBlocked(false);

        user = userRepository.save(user);

        CurrentUser.currentUser = user;
    }

    @Test
    void addTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetDTO monthlyBudgetDTO = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date);

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(10.10));

        MonthlyBudgetDTO savedMonthlyBudgetDTO = monthlyBudgetService.add(monthlyBudgetDTO);

        Assertions.assertNotEquals(0, savedMonthlyBudgetDTO.getId());
        Assertions.assertEquals(monthlyBudgetDTO, savedMonthlyBudgetDTO);

        MonthlyBudgetDTO monthlyBudgetDTO3 = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date);

        monthlyBudgetDTO3.setSum(BigDecimal.valueOf(10.0));

        monthlyBudgetDTO3 = monthlyBudgetService.add(monthlyBudgetDTO3);

        Assertions.assertNotEquals(monthlyBudgetDTO, monthlyBudgetDTO3);
    }

    @Test
    void findByIdTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetDTO monthlyBudgetDTO = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date);

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(10.10));

        monthlyBudgetDTO = monthlyBudgetService.add(monthlyBudgetDTO);

        Assertions.assertEquals(monthlyBudgetService.findById(monthlyBudgetDTO.getId()), monthlyBudgetDTO);

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(1.5));

        Assertions.assertNotEquals(monthlyBudgetService.findById(monthlyBudgetDTO.getId()), monthlyBudgetDTO);

        Assertions.assertNull(monthlyBudgetService.findById(20));
    }

    @Test
    void findByDateAndUserIdTest() {
        Date date = Date.valueOf("2000-01-01");
        MonthlyBudgetDTO monthlyBudgetDTO = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date);

        Date date2 = Date.valueOf("2000-02-01");
        MonthlyBudgetDTO monthlyBudgetDTO2 = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date2);

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(10.10));

        monthlyBudgetDTO = monthlyBudgetService.add(monthlyBudgetDTO);

        Assertions.assertEquals(monthlyBudgetService.findByDateAndUserId(date, CurrentUser.currentUser.getId()), monthlyBudgetDTO);

        monthlyBudgetDTO2.setSum(BigDecimal.valueOf(1.5));

        monthlyBudgetDTO2 = monthlyBudgetService.add(monthlyBudgetDTO2);

        Assertions.assertEquals(monthlyBudgetService.findByDateAndUserId(date2, CurrentUser.currentUser.getId()), monthlyBudgetDTO2);
        Assertions.assertNotEquals(monthlyBudgetService.findByDateAndUserId(date2, CurrentUser.currentUser.getId()), monthlyBudgetDTO);

        Assertions.assertNull(monthlyBudgetService.findByDateAndUserId(Date.valueOf("2001-01-01"), CurrentUser.currentUser.getId()));
    }

    @Test
    void findAllTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetDTO monthlyBudgetDTO = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date);

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(10.10));

        Date date2 = new Date(System.currentTimeMillis() + 2_678_400_000L);
        MonthlyBudgetDTO monthlyBudgetDTO2 = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date2);

        monthlyBudgetDTO2.setSum(BigDecimal.valueOf(20.0));

        MonthlyBudgetDTO monthlyBudgetDTO3 = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date);

        monthlyBudgetDTO3.setSum(BigDecimal.valueOf(30.3));

        List<MonthlyBudgetDTO> transactionEntities = List.of(monthlyBudgetDTO, monthlyBudgetDTO2);

        List<MonthlyBudgetDTO> transactionEntitiesReturned;

        monthlyBudgetService.add(monthlyBudgetDTO);
        monthlyBudgetService.add(monthlyBudgetDTO2);
        monthlyBudgetService.add(monthlyBudgetDTO3);

        transactionEntitiesReturned = monthlyBudgetService.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        Date date3 = new Date(date2.getTime() + 2_678_400_000L);
        MonthlyBudgetDTO monthlyBudgetDTO4 = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date3);

        monthlyBudgetDTO4.setSum(BigDecimal.valueOf(10.0));

        transactionEntities = List.of(monthlyBudgetDTO, monthlyBudgetDTO2, monthlyBudgetDTO4);

        monthlyBudgetService.add(monthlyBudgetDTO4);
        transactionEntitiesReturned = monthlyBudgetService.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(1.1));

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);
    }

    @Test
    void updateTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetDTO monthlyBudgetDTO = new MonthlyBudgetDTO(CurrentUser.currentUser.getId(), date);

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(10.10));

        monthlyBudgetDTO = monthlyBudgetService.add(monthlyBudgetDTO);

        MonthlyBudgetDTO monthlyBudgetDTO2 = new MonthlyBudgetDTO(monthlyBudgetDTO.getId(), CurrentUser.currentUser.getId(), date);

        monthlyBudgetDTO2.setSum(BigDecimal.valueOf(1.23));

        monthlyBudgetService.update(monthlyBudgetDTO2, monthlyBudgetDTO2.getId());

        Assertions.assertEquals(monthlyBudgetService.findById(monthlyBudgetDTO.getId()), monthlyBudgetDTO2);

        monthlyBudgetDTO2.setSum(BigDecimal.valueOf(2.2));

        Assertions.assertNotEquals(monthlyBudgetService.findById(monthlyBudgetDTO.getId()), monthlyBudgetDTO2);
    }

    @Test
    void deleteTest() {
        MonthlyBudgetDTO monthlyBudgetDTO = new MonthlyBudgetDTO(CurrentUser.currentUser.getId());
        Date date = new Date(System.currentTimeMillis());

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(10.10));

        MonthlyBudgetDTO monthlyBudgetDTO2 = new MonthlyBudgetDTO(monthlyBudgetDTO.getId(), CurrentUser.currentUser.getId(), date);

        monthlyBudgetDTO2.setSum(BigDecimal.valueOf(1.23));

        MonthlyBudgetDTO monthlyBudgetDTO3 = new MonthlyBudgetDTO(CurrentUser.currentUser.getId());

        monthlyBudgetDTO3.setSum(BigDecimal.valueOf(30.3));

        List<MonthlyBudgetDTO> transactionEntities = List.of(monthlyBudgetDTO);

        monthlyBudgetDTO = monthlyBudgetService.add(monthlyBudgetDTO);
        monthlyBudgetService.add(monthlyBudgetDTO2);
        monthlyBudgetService.add(monthlyBudgetDTO3);

        List<MonthlyBudgetDTO> transactionEntitiesReturned = monthlyBudgetService.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        monthlyBudgetService.delete(monthlyBudgetDTO.getId());
        transactionEntitiesReturned = monthlyBudgetService.findAll();

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntities = new ArrayList<>();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);
    }
}