package org.example.service;

import liquibase.exception.LiquibaseException;
import org.example.CurrentUser;
import org.example.config.MyTestConfig;
import org.example.controller.dto.TransactionCategoryDTO;
import org.example.controller.dto.TransactionDTO;
import org.example.controller.dto.UserDTO;
import org.example.model.TransactionCategoryEntity;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.example.repository.TransactionCategoryRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@DisplayName("Tests of transaction service methods")
class TransactionServiceTest {
    UserService userService;
    TransactionCategoryEntity category;
    TransactionService transactionService;
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
        TransactionCategoryRepository categoryRepository = context.getBean(TransactionCategoryRepository.class);
        userService = context.getBean(UserService.class);
        transactionService = context.getBean(TransactionService.class);
        TransactionCategoryService categoryService = context.getBean(TransactionCategoryService.class);

        for (TransactionDTO transactionDTO : transactionService.findAll()) {
            transactionService.delete(transactionDTO.getId());
        }

        for (UserDTO userDTO : userService.findAll()) {
            userService.delete(userDTO.getId());
        }

        for (TransactionCategoryDTO categoryDTO : categoryService.findAll()) {
            categoryService.delete(categoryDTO.getId());
        }

        UserEntity user = new UserEntity();

        user.setEmail("t");
        user.setPassword("t");
        user.setName("t");
        user.setRole(UserRole.USER);
        user.setBlocked(false);

        user = userRepository.save(user);

        CurrentUser.currentUser = user;

        category = new TransactionCategoryEntity();

        category.setName("t");

        category = categoryRepository.save(category);
    }

    @DisplayName("Test of the method for adding transaction")
    @Test
    void addTest() throws ParseException {
        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());

        transactionDTO.setSum(BigDecimal.valueOf(10.10));
        transactionDTO.setCategoryId(category.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        transactionDTO = transactionService.add(transactionDTO);

        TransactionDTO transactionDTO2 = new TransactionDTO(CurrentUser.currentUser.getId());
        transactionDTO2.setSum(BigDecimal.valueOf(10.10));
        transactionDTO2.setCategoryId(category.getId());
        transactionDTO2.setDate(date);
        transactionDTO2.setDescription("t");

        TransactionDTO savedTransactionDTO = transactionService.add(transactionDTO);

        Assertions.assertNotEquals(0, savedTransactionDTO.getId());
        Assertions.assertEquals(transactionDTO, savedTransactionDTO);

        TransactionDTO transactionDTO3 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO3.setSum(BigDecimal.valueOf(10.0));
        transactionDTO3.setCategoryId(category.getId());
        transactionDTO3.setDate(date);
        transactionDTO3.setDescription("t2");

        transactionDTO3 = transactionService.add(transactionDTO3);

        Assertions.assertNotEquals(transactionDTO, transactionDTO3);
    }

    @DisplayName("Test of the method for finding transaction by id")
    @Test
    void findByIdTest() throws ParseException {
        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());

        transactionDTO.setSum(BigDecimal.valueOf(10.10));
        transactionDTO.setCategoryId(category.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        transactionDTO = transactionService.add(transactionDTO);

        Assertions.assertEquals(transactionService.findById(transactionDTO.getId()), transactionDTO);

        transactionDTO.setSum(BigDecimal.valueOf(1.5));

        Assertions.assertNotEquals(transactionService.findById(transactionDTO.getId()), transactionDTO);

        Assertions.assertNull(transactionService.findById(10));
    }

    @DisplayName("Test of the method for finding all transactions")
    @Test
    void findAllTest() throws ParseException {
        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());

        transactionDTO.setSum(BigDecimal.valueOf(10.10));
        transactionDTO.setCategoryId(category.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        TransactionDTO transactionDTO2 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO2.setSum(BigDecimal.valueOf(20.0));
        transactionDTO2.setCategoryId(category.getId());
        transactionDTO2.setDate(date);
        transactionDTO2.setDescription("t2");

        TransactionDTO transactionDTO3 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO3.setSum(BigDecimal.valueOf(30.3));
        transactionDTO3.setCategoryId(category.getId());
        transactionDTO3.setDate(date);
        transactionDTO3.setDescription("t3");

        List<TransactionDTO> transactionEntities = List.of(transactionDTO, transactionDTO2, transactionDTO3);

        List<TransactionDTO> transactionEntitiesReturned;

        transactionService.add(transactionDTO);
        transactionService.add(transactionDTO2);
        transactionService.add(transactionDTO3);

        transactionEntitiesReturned = transactionService.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        TransactionDTO transactionDTO4 = new TransactionDTO(CurrentUser.currentUser.getId());

        date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());

        transactionDTO4.setSum(BigDecimal.valueOf(10.10));
        transactionDTO4.setCategoryId(category.getId());
        transactionDTO4.setDate(date);
        transactionDTO4.setDescription("t4");

        transactionEntities = List.of(transactionDTO, transactionDTO2, transactionDTO3, transactionDTO4);

        transactionService.add(transactionDTO4);
        transactionEntitiesReturned = transactionService.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionDTO.setDescription("t5");

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);
    }

    @DisplayName("Test of the method for finding all transactions by user id")
    @Test
    void findAllByUserIdTest() {
        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionDTO.setSum(BigDecimal.valueOf(10.10));
        transactionDTO.setCategoryId(category.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        TransactionDTO transactionDTO2 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO2.setSum(BigDecimal.valueOf(20.0));
        transactionDTO2.setCategoryId(category.getId());
        transactionDTO2.setDate(date);
        transactionDTO2.setDescription("t2");

        TransactionDTO transactionDTO3 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO3.setSum(BigDecimal.valueOf(30.3));
        transactionDTO3.setCategoryId(category.getId());
        transactionDTO3.setDate(date);
        transactionDTO3.setDescription("t3");

        List<TransactionDTO> transactionEntities = List.of(transactionDTO, transactionDTO2, transactionDTO3);

        List<TransactionDTO> transactionEntitiesReturned;

        transactionService.add(transactionDTO);
        transactionService.add(transactionDTO2);
        transactionService.add(transactionDTO3);

        transactionEntitiesReturned = transactionService.findAllByUserId(CurrentUser.currentUser.getId());

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        TransactionDTO transactionDTO4 = new TransactionDTO(CurrentUser.currentUser.getId());

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionDTO4.setSum(BigDecimal.valueOf(10.10));
        transactionDTO4.setCategoryId(category.getId());
        transactionDTO4.setDate(date);
        transactionDTO4.setDescription("t4");

        transactionEntities = List.of(transactionDTO, transactionDTO2, transactionDTO3, transactionDTO4);

        transactionService.add(transactionDTO4);
        transactionEntitiesReturned = transactionService.findAllByUserId(CurrentUser.currentUser.getId());

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        UserDTO userDTO = new UserDTO();

        userDTO.setEmail("t2");
        userDTO.setPassword("t2");
        userDTO.setName("t2");
        userDTO.setBlocked(false);

        userDTO = userService.add(userDTO);

        TransactionDTO transactionDTO5 = new TransactionDTO(userDTO.getId());

        transactionDTO5.setSum(BigDecimal.valueOf(10.10));
        transactionDTO5.setCategoryId(category.getId());
        transactionDTO5.setDate(date);
        transactionDTO5.setDescription("t5");

        transactionService.add(transactionDTO5);

        transactionEntitiesReturned = transactionService.findAllByUserId(CurrentUser.currentUser.getId());

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO.setSum(BigDecimal.valueOf(21.30));
        transactionDTO.setCategoryId(category.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        transactionService.add(transactionDTO);

        transactionEntitiesReturned = transactionService.findAllByUserId(CurrentUser.currentUser.getId());

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);
    }

    @DisplayName("Test of the method for finding all transactions by user id")
    @Test
    void findAllByDateAndCategoryIdAndTypeAndUserIdTest() {
        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionDTO.setSum(BigDecimal.valueOf(10.10));
        transactionDTO.setCategoryId(category.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        TransactionDTO transactionDTO2 = new TransactionDTO(CurrentUser.currentUser.getId());
        Date date2;
        try {
            date2 = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis() + 86_400_000).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionDTO2.setSum(BigDecimal.valueOf(20.0));
        transactionDTO2.setCategoryId(category.getId());
        transactionDTO2.setDate(date2);
        transactionDTO2.setDescription("t2");

        UserDTO user = new UserDTO();

        user.setEmail("t2");
        user.setPassword("t2");
        user.setName("t2");
        user.setBlocked(false);

        user = userService.add(user);

        TransactionDTO transactionDTO3 = new TransactionDTO(user.getId());

        transactionDTO3.setSum(BigDecimal.valueOf(30.3));
        transactionDTO3.setCategoryId(category.getId());
        transactionDTO3.setDate(date);
        transactionDTO3.setDescription("t3");

        TransactionDTO transactionDTO4 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO4.setSum(BigDecimal.valueOf(-10.10));
        transactionDTO4.setDate(date2);
        transactionDTO4.setDescription(null);

        List<TransactionDTO> transactionEntities = List.of(transactionDTO, transactionDTO2, transactionDTO4);
        List<TransactionDTO> transactionEntitiesReturned;

        transactionService.add(transactionDTO);
        transactionService.add(transactionDTO2);
        transactionService.add(transactionDTO3);
        transactionService.add(transactionDTO4);

        transactionEntitiesReturned = transactionService.findAllByDateAndCategoryIdAndTypeAndUserId(null, null, null, CurrentUser.currentUser.getId());

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntities = List.of(transactionDTO, transactionDTO2, transactionDTO3, transactionDTO4);

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntitiesReturned = transactionService.findAllByDateAndCategoryIdAndTypeAndUserId(date, 0, null, CurrentUser.currentUser.getId());

        transactionEntities = List.of(transactionDTO);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntitiesReturned = transactionService.findAllByDateAndCategoryIdAndTypeAndUserId(null, category.getId(), null, CurrentUser.currentUser.getId());
        transactionEntities = List.of(transactionDTO, transactionDTO2);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntitiesReturned = transactionService.findAllByDateAndCategoryIdAndTypeAndUserId(null, 0, "Pos", user.getId());
        transactionEntities = List.of(transactionDTO3);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);
    }

    @DisplayName("Test of the method for updating transaction")
    @Test
    void updateTest() {
        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionDTO.setSum(BigDecimal.valueOf(10.10));
        transactionDTO.setCategoryId(category.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        transactionDTO = transactionService.add(transactionDTO);

        TransactionDTO transactionDTO2 = new TransactionDTO(transactionDTO.getId(), CurrentUser.currentUser.getId());

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionDTO2.setSum(BigDecimal.valueOf(1.23));
        transactionDTO2.setCategoryId(category.getId());
        transactionDTO2.setDate(date);
        transactionDTO2.setDescription("t2");

        transactionService.update(transactionDTO2, transactionDTO2.getId());

        Assertions.assertEquals(transactionService.findById(transactionDTO.getId()), transactionDTO2);

        transactionDTO2.setSum(BigDecimal.valueOf(2.2));

        Assertions.assertNotEquals(transactionService.findById(transactionDTO.getId()), transactionDTO2);
    }

    @DisplayName("Test of the method for deleting transaction")
    @Test
    void deleteTest() {
        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionDTO.setSum(BigDecimal.valueOf(10.10));
        transactionDTO.setCategoryId(category.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        TransactionDTO transactionDTO2 = new TransactionDTO(transactionDTO.getId(), CurrentUser.currentUser.getId());

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionDTO2.setSum(BigDecimal.valueOf(1.23));
        transactionDTO2.setCategoryId(category.getId());
        transactionDTO2.setDate(date);
        transactionDTO2.setDescription("t2");

        TransactionDTO transactionDTO3 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO3.setSum(BigDecimal.valueOf(30.3));
        transactionDTO3.setCategoryId(category.getId());
        transactionDTO3.setDate(date);
        transactionDTO3.setDescription("t3");

        List<TransactionDTO> transactionEntities = List.of(transactionDTO, transactionDTO2, transactionDTO3);

        transactionDTO = transactionService.add(transactionDTO);
        transactionService.add(transactionDTO2);
        transactionService.add(transactionDTO3);

        List<TransactionDTO> transactionEntitiesReturned = transactionService.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionService.delete(transactionDTO.getId());
        transactionEntitiesReturned = transactionService.findAll();

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntities = List.of(transactionDTO2, transactionDTO3);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);
    }
}