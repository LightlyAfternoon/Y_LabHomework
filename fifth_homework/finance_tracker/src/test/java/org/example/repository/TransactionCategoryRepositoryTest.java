package org.example.repository;

import liquibase.exception.LiquibaseException;
import org.example.CurrentUser;
import org.example.config.MyTestConfig;
import org.example.model.TransactionCategoryEntity;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@SpringBootTest
@DisplayName("Tests of transaction category repository methods")
class TransactionCategoryRepositoryTest {
    UserEntity userEntity;
    TransactionCategoryRepository categoryRepository;
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
        categoryRepository = context.getBean(TransactionCategoryRepository.class);

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

        userEntity = userRepository.save(userEntity);

        CurrentUser.currentUser = userEntity;
    }

    @DisplayName("Test of the method for adding category")
    @Test
    void addTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity.TransactionCategoryBuilder("t").build();

        categoryEntity = categoryRepository.save(categoryEntity);

        TransactionCategoryEntity savedCategoryEntity = categoryRepository.save(categoryEntity);

        Assertions.assertNotEquals(0, savedCategoryEntity.getId());
        Assertions.assertEquals(categoryEntity, savedCategoryEntity);

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity.TransactionCategoryBuilder("t2").build();

        categoryEntity2 = categoryRepository.save(categoryEntity2);

        Assertions.assertNotEquals(categoryEntity, categoryEntity2);
    }

    @DisplayName("Test of the method for adding goal")
    @Test
    void addGoalTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity.TransactionCategoryBuilder("t")
                .neededSum(BigDecimal.valueOf(10.0)).userId(CurrentUser.currentUser.getId()).build();

        categoryEntity = categoryRepository.save(categoryEntity);

        TransactionCategoryEntity savedCategoryEntity = categoryRepository.save(categoryEntity);

        Assertions.assertNotEquals(0, savedCategoryEntity.getId());
        Assertions.assertEquals(categoryEntity, savedCategoryEntity);

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity.TransactionCategoryBuilder("t3")
                .neededSum(BigDecimal.valueOf(30.3)).userId(CurrentUser.currentUser.getId()).build();

        categoryEntity2 = categoryRepository.save(categoryEntity2);

        Assertions.assertNotEquals(categoryEntity, categoryEntity2);
    }

    @DisplayName("Test of the method for finding category by id")
    @Test
    void findByIdTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        categoryEntity = categoryRepository.save(categoryEntity);

        Assertions.assertEquals(categoryRepository.findById(categoryEntity.getId()), categoryEntity);

        categoryEntity.setName("t0");

        Assertions.assertNotEquals(categoryRepository.findById(categoryEntity.getId()), categoryEntity);

        Assertions.assertNull(categoryRepository.findById(10));
    }

    @DisplayName("Test of the method for finding category by name")
    @Test
    void findByNameTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        categoryRepository.save(categoryEntity);

        Assertions.assertEquals(categoryRepository.findByName("t"), categoryEntity);

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity();

        categoryEntity2.setName("t0");

        categoryRepository.save(categoryEntity2);

        Assertions.assertNotEquals(categoryRepository.findByName("t"), categoryEntity2);
        Assertions.assertEquals(categoryRepository.findByName("t0"), categoryEntity2);
        Assertions.assertNull(categoryRepository.findByName("t2"));
    }

    @DisplayName("Test of the method for finding all categories")
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

        categoryRepository.save(categoryEntity);
        categoryRepository.save(categoryEntity2);
        categoryRepository.save(categoryEntity3);

        transactionCategoryEntitiesReturned = categoryRepository.findAll();

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);

        TransactionCategoryEntity categoryEntity4 = new TransactionCategoryEntity();

        categoryEntity4.setName("t4");

        categoryEntities = List.of(categoryEntity, categoryEntity2, categoryEntity3, categoryEntity4);

        categoryRepository.save(categoryEntity4);

        transactionCategoryEntitiesReturned = categoryRepository.findAll();

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);

        categoryEntity.setName("t0");

        Assertions.assertNotEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }

    @DisplayName("Test of the method for finding all categories and goals with user id")
    @Test
    void findCommonCategoriesOrGoalsByUserIdTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity(0, CurrentUser.currentUser.getId());

        categoryEntity2.setName("t2");
        categoryEntity2.setNeededSum(BigDecimal.valueOf(20.0));

        TransactionCategoryEntity categoryEntity3 = new TransactionCategoryEntity();

        categoryEntity3.setName("t3");

        TransactionCategoryEntity categoryEntity4 = new TransactionCategoryEntity(0, CurrentUser.currentUser.getId());

        categoryEntity4.setName("t4");
        categoryEntity4.setNeededSum(BigDecimal.valueOf(40.4));

        List<TransactionCategoryEntity> categoryEntities = List.of(categoryEntity2, categoryEntity4);


        List<TransactionCategoryEntity> transactionCategoryEntitiesReturned;

        categoryRepository.save(categoryEntity);
        categoryRepository.save(categoryEntity2);
        categoryRepository.save(categoryEntity3);
        categoryRepository.save(categoryEntity4);

        transactionCategoryEntitiesReturned = categoryRepository.findCommonCategoriesOrGoalsByUserId(CurrentUser.currentUser.getId());

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }

    @DisplayName("Test of the method for finding all goals with user id")
    @Test
    void findAllGoalsByUserIdTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity(0, CurrentUser.currentUser.getId());

        categoryEntity2.setName("t2");
        categoryEntity2.setNeededSum(BigDecimal.valueOf(20.0));

        TransactionCategoryEntity categoryEntity3 = new TransactionCategoryEntity();

        categoryEntity3.setName("t3");

        TransactionCategoryEntity categoryEntity4 = new TransactionCategoryEntity(0, CurrentUser.currentUser.getId());

        categoryEntity4.setName("t4");
        categoryEntity4.setNeededSum(BigDecimal.valueOf(40.4));

        List<TransactionCategoryEntity> categoryEntities = List.of(categoryEntity2, categoryEntity4);


        List<TransactionCategoryEntity> transactionCategoryEntitiesReturned;

        categoryRepository.save(categoryEntity);
        categoryRepository.save(categoryEntity2);
        categoryRepository.save(categoryEntity3);
        categoryRepository.save(categoryEntity4);

        transactionCategoryEntitiesReturned = categoryRepository.findAllGoalsByUserId(CurrentUser.currentUser.getId());

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }

    @DisplayName("Test of the method for updating category")
    @Test
    void updateTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        categoryEntity = categoryRepository.save(categoryEntity);

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity(categoryEntity.getId(), null);

        categoryEntity2.setName("t2");

        categoryRepository.save(categoryEntity2);

        Assertions.assertEquals(categoryRepository.findById(categoryEntity.getId()), categoryEntity2);

        categoryEntity2.setName("t0");

        Assertions.assertNotEquals(categoryRepository.findById(categoryEntity.getId()), categoryEntity2);
    }

    @DisplayName("Test of the method for deleting category")
    @Test
    void deleteTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity(0, CurrentUser.currentUser.getId());

        categoryEntity2.setName("t2");
        categoryEntity2.setNeededSum(BigDecimal.valueOf(203));

        TransactionCategoryEntity categoryEntity3 = new TransactionCategoryEntity();

        categoryEntity3.setName("t3");

        List<TransactionCategoryEntity> categoryEntities = List.of(categoryEntity, categoryEntity2, categoryEntity3);

        categoryEntity = categoryRepository.save(categoryEntity);
        categoryRepository.save(categoryEntity2);
        categoryRepository.save(categoryEntity3);

        List<TransactionCategoryEntity> transactionCategoryEntitiesReturned = categoryRepository.findAll();

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);

        categoryRepository.delete(categoryEntity);
        transactionCategoryEntitiesReturned = categoryRepository.findAll();

        Assertions.assertNotEquals(categoryEntities, transactionCategoryEntitiesReturned);

        categoryEntities = List.of(categoryEntity2, categoryEntity3);

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }
}