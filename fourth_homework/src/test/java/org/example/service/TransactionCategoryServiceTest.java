package org.example.service;

import liquibase.exception.LiquibaseException;
import org.example.CurrentUser;
import org.example.config.MyTestConfig;
import org.example.controller.dto.TransactionCategoryDTO;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

class TransactionCategoryServiceTest {
    TransactionCategoryService categoryService;
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
        categoryService = context.getBean(TransactionCategoryService.class);

        for (TransactionCategoryDTO category : categoryService.findAll()) {
            categoryService.delete(category.getId());
        }

        for (UserEntity user : userRepository.findAll()) {
            userRepository.delete(user);
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
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

        categoryDTO.setName("t");

        categoryDTO = categoryService.add(categoryDTO);

        TransactionCategoryDTO savedCategoryDTO = categoryService.add(categoryDTO);

        Assertions.assertNotEquals(0, savedCategoryDTO.getId());
        Assertions.assertEquals(categoryDTO, savedCategoryDTO);

        TransactionCategoryDTO categoryDTO3 = new TransactionCategoryDTO();

        categoryDTO3.setName("t3");

        categoryDTO3 = categoryService.add(categoryDTO3);

        Assertions.assertNotEquals(categoryDTO, categoryDTO3);
    }

    @Test
    void addGoalTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());

        categoryDTO.setName("t");
        categoryDTO.setNeededSum(BigDecimal.valueOf(10.0));

        TransactionCategoryDTO savedCategoryDTO = categoryService.add(categoryDTO);

        Assertions.assertNotEquals(0, savedCategoryDTO.getId());
        Assertions.assertEquals(categoryDTO, savedCategoryDTO);

        TransactionCategoryDTO categoryDTO3 = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());

        categoryDTO3.setName("t3");

        categoryDTO3 = categoryService.add(categoryDTO3);

        Assertions.assertNotEquals(categoryDTO, categoryDTO3);
    }

    @Test
    void findByIdTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

        categoryDTO.setName("t");

        categoryDTO = categoryService.add(categoryDTO);

        Assertions.assertEquals(categoryService.findById(categoryDTO.getId()), categoryDTO);

        categoryDTO.setName("t0");

        Assertions.assertNotEquals(categoryService.findById(categoryDTO.getId()), categoryDTO);

        Assertions.assertNull(categoryService.findById(10));
    }

    @Test
    void findByNameTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

        categoryDTO.setName("t");

        categoryService.add(categoryDTO);

        Assertions.assertEquals(categoryService.findByName("t"), categoryDTO);

        TransactionCategoryDTO categoryDTO2 = new TransactionCategoryDTO();

        categoryDTO2.setName("t0");

        categoryService.add(categoryDTO2);

        Assertions.assertNotEquals(categoryService.findByName("t"), categoryDTO2);
        Assertions.assertEquals(categoryService.findByName("t0"), categoryDTO2);
        Assertions.assertNull(categoryService.findByName("t2"));
    }

    @Test
    void findAllTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

        categoryDTO.setName("t");

        TransactionCategoryDTO categoryDTO2 = new TransactionCategoryDTO();

        categoryDTO2.setName("t2");

        TransactionCategoryDTO categoryDTO3 = new TransactionCategoryDTO();

        categoryDTO3.setName("t3");

        List<TransactionCategoryDTO> categoryEntities = List.of(categoryDTO, categoryDTO2, categoryDTO3);

        List<TransactionCategoryDTO> transactionCategoryEntitiesReturned;

        categoryService.add(categoryDTO);
        categoryService.add(categoryDTO2);
        categoryService.add(categoryDTO3);

        transactionCategoryEntitiesReturned = categoryService.findAll();

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);

        TransactionCategoryDTO categoryDTO4 = new TransactionCategoryDTO();

        categoryDTO4.setName("t4");

        categoryEntities = List.of(categoryDTO, categoryDTO2, categoryDTO3, categoryDTO4);

        categoryService.add(categoryDTO4);

        transactionCategoryEntitiesReturned = categoryService.findAll();

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);

        categoryDTO.setName("t0");

        Assertions.assertNotEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }

    @Test
    void findCommonCategoriesOrGoalsWithUserIdTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

        categoryDTO.setName("t");

        TransactionCategoryDTO categoryDTO2 = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());

        categoryDTO2.setName("t2");
        categoryDTO2.setNeededSum(BigDecimal.valueOf(20.0));

        TransactionCategoryDTO categoryDTO3 = new TransactionCategoryDTO();

        categoryDTO3.setName("t3");

        TransactionCategoryDTO categoryDTO4 = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());

        categoryDTO4.setName("t4");
        categoryDTO4.setNeededSum(BigDecimal.valueOf(40.4));

        List<TransactionCategoryDTO> categoryEntities = List.of(categoryDTO2, categoryDTO4);

        List<TransactionCategoryDTO> transactionCategoryEntitiesReturned;

        categoryService.add(categoryDTO);
        categoryService.add(categoryDTO2);
        categoryService.add(categoryDTO3);
        categoryService.add(categoryDTO4);

        transactionCategoryEntitiesReturned = categoryService.findCommonCategoriesOrGoalsByUserId(CurrentUser.currentUser.getId());

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }

    @Test
    void findAllGoalsWithUserIdTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

        categoryDTO.setName("t");

        TransactionCategoryDTO categoryDTO2 = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());

        categoryDTO2.setName("t2");
        categoryDTO2.setNeededSum(BigDecimal.valueOf(20.0));

        TransactionCategoryDTO categoryDTO3 = new TransactionCategoryDTO();

        categoryDTO3.setName("t3");

        TransactionCategoryDTO categoryDTO4 = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());

        categoryDTO4.setName("t4");
        categoryDTO4.setNeededSum(BigDecimal.valueOf(40.4));

        List<TransactionCategoryDTO> categoryEntities = List.of(categoryDTO2, categoryDTO4);


        List<TransactionCategoryDTO> transactionCategoryEntitiesReturned;

        categoryService.add(categoryDTO);
        categoryService.add(categoryDTO2);
        categoryService.add(categoryDTO3);
        categoryService.add(categoryDTO4);

        transactionCategoryEntitiesReturned = categoryService.findAllGoalsByUserId(CurrentUser.currentUser.getId());

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }

    @Test
    void updateTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

        categoryDTO.setName("t");

        categoryDTO = categoryService.add(categoryDTO);

        TransactionCategoryDTO categoryDTO2 = new TransactionCategoryDTO(categoryDTO.getId(), null);

        categoryDTO2.setName("t2");

        categoryService.update(categoryDTO2, categoryDTO.getId());

        Assertions.assertEquals(categoryService.findById(categoryDTO.getId()), categoryDTO2);

        categoryDTO2.setName("t0");

        Assertions.assertNotEquals(categoryService.findById(categoryDTO.getId()), categoryDTO2);
    }

    @Test
    void deleteTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();

        categoryDTO.setName("t");

        TransactionCategoryDTO categoryDTO2 = new TransactionCategoryDTO();

        categoryDTO2.setName("t2");

        TransactionCategoryDTO categoryDTO3 = new TransactionCategoryDTO();

        categoryDTO3.setName("t3");

        List<TransactionCategoryDTO> categoryEntities = List.of(categoryDTO, categoryDTO2, categoryDTO3);

        categoryDTO = categoryService.add(categoryDTO);
        categoryService.add(categoryDTO2);
        categoryService.add(categoryDTO3);

        List<TransactionCategoryDTO> transactionCategoryEntitiesReturned = categoryService.findAll();

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);

        categoryService.delete(categoryDTO.getId());
        transactionCategoryEntitiesReturned = categoryService.findAll();

        Assertions.assertNotEquals(categoryEntities, transactionCategoryEntitiesReturned);

        categoryEntities = List.of(categoryDTO2, categoryDTO3);

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }
}