package org.example.repository;

import liquibase.exception.LiquibaseException;
import org.example.config.MyTestConfig;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.SQLException;
import java.util.List;

@SpringBootTest
@DisplayName("Tests of user repository methods")
class UserRepositoryTest {
    UserRepository userRepository;
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
        userRepository = context.getBean(UserRepository.class);

        for (UserEntity user : userRepository.findAll()) {
            userRepository.delete(user);
        }
    }

    @DisplayName("Test of the method for adding user")
    @Test
    void addTest() {
        UserEntity userEntity = new UserEntity.UserBuilder("t", "t", "t").build();

        UserEntity savedUserEntity = userRepository.save(userEntity);

        Assertions.assertNotEquals(0, savedUserEntity.getId());
        Assertions.assertEquals(userEntity, savedUserEntity);

        userEntity = new UserEntity.UserBuilder("t2", "t2", "t2").role(UserRole.ADMIN).isBlocked(true).build();

        savedUserEntity = userRepository.save(userEntity);

        Assertions.assertNotEquals(0, savedUserEntity.getId());
        Assertions.assertEquals(userEntity, savedUserEntity);
    }

    @DisplayName("Test of the method for finding user by id")
    @Test
    void findByIdTest() {
        UserEntity userEntity = new UserEntity();

        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setName("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        userEntity = userRepository.save(userEntity);

        Assertions.assertEquals(userRepository.findById(userEntity.getId()), userEntity);

        userEntity.setRole(UserRole.ADMIN);

        Assertions.assertNotEquals(userRepository.findById(userEntity.getId()), userEntity);

        Assertions.assertNull(userRepository.findById(50));
    }

    @DisplayName("Test of the method for finding all users")
    @Test
    void findAllTest() {
        UserEntity userEntity = new UserEntity();

        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setName("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        UserEntity userEntity2 = new UserEntity();

        userEntity2.setEmail("t2");
        userEntity2.setPassword("t2");
        userEntity2.setName("t2");
        userEntity2.setRole(UserRole.USER);
        userEntity2.setBlocked(true);

        UserEntity userEntity3 = new UserEntity();

        userEntity3.setEmail("t3");
        userEntity3.setPassword("t2");
        userEntity3.setName("t3");
        userEntity3.setRole(UserRole.ADMIN);
        userEntity3.setBlocked(false);

        List<UserEntity> userEntities = List.of(userEntity, userEntity2, userEntity3);

        List<UserEntity> userEntitiesReturned;

        userRepository.save(userEntity);
        userRepository.save(userEntity2);
        userRepository.save(userEntity3);

        userEntitiesReturned = userRepository.findAll();

        Assertions.assertEquals(userEntities, userEntitiesReturned);

        UserEntity userEntity4 = new UserEntity();

        userEntity4.setEmail("t4");
        userEntity4.setPassword("t2");
        userEntity4.setName("t4");
        userEntity4.setRole(UserRole.ADMIN);
        userEntity4.setBlocked(false);

        userEntities = List.of(userEntity, userEntity2, userEntity3, userEntity4);

        userRepository.save(userEntity4);

        userEntitiesReturned = userRepository.findAll();

        Assertions.assertEquals(userEntities, userEntitiesReturned);

        userEntity.setRole(UserRole.ADMIN);

        Assertions.assertNotEquals(userEntities, userEntitiesReturned);
    }

    @DisplayName("Test of the method for updating user")
    @Test
    void updateTest() {
        UserEntity userEntity = new UserEntity();

        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setName("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        userEntity = userRepository.save(userEntity);

        UserEntity userEntity2 = new UserEntity(userEntity.getId());

        userEntity2.setEmail("t2");
        userEntity2.setPassword("t2");
        userEntity2.setName("t2");
        userEntity2.setRole(UserRole.USER);
        userEntity2.setBlocked(false);

        userRepository.save(userEntity2);

        Assertions.assertEquals(userRepository.findById(userEntity.getId()), userEntity2);

        userEntity2.setRole(UserRole.ADMIN);

        Assertions.assertNotEquals(userRepository.findById(userEntity.getId()), userEntity2);
    }

    @DisplayName("Test of the method for deleting user")
    @Test
    void deleteTest() {
        UserEntity userEntity = new UserEntity();

        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setName("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        UserEntity userEntity2 = new UserEntity();

        userEntity2.setEmail("t2");
        userEntity2.setPassword("t2");
        userEntity2.setName("t2");
        userEntity2.setRole(UserRole.USER);
        userEntity2.setBlocked(true);

        UserEntity userEntity3 = new UserEntity();

        userEntity3.setEmail("t3");
        userEntity3.setPassword("t2");
        userEntity3.setName("t3");
        userEntity3.setRole(UserRole.ADMIN);
        userEntity3.setBlocked(false);

        List<UserEntity> userEntities = List.of(userEntity, userEntity2, userEntity3);

        userEntity = userRepository.save(userEntity);
        userRepository.save(userEntity2);
        userRepository.save(userEntity3);

        List<UserEntity> userEntitiesReturned;

        userEntitiesReturned = userRepository.findAll();

        Assertions.assertEquals(userEntities, userEntitiesReturned);

        userRepository.delete(userEntity);
        userEntitiesReturned = userRepository.findAll();


        Assertions.assertNotEquals(userEntities, userEntitiesReturned);

        userEntities = List.of(userEntity2, userEntity3);

        Assertions.assertEquals(userEntities, userEntitiesReturned);
    }

    @DisplayName("Test of the method for finding user by email and password")
    @Test
    void findByEmailAndPasswordTest() {
        UserEntity userEntity = new UserEntity();

        userEntity.setEmail("te");
        userEntity.setPassword("tp");
        userEntity.setName("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        userRepository.save(userEntity);

        Assertions.assertEquals(userRepository.findByEmailAndPassword("te", "tp"), userEntity);

        UserEntity userEntity2 = new UserEntity();

        userEntity2.setEmail("te2");
        userEntity2.setPassword("tp2");
        userEntity2.setName("t2");
        userEntity2.setRole(UserRole.USER);
        userEntity2.setBlocked(false);

        userRepository.save(userEntity2);

        Assertions.assertEquals(userRepository.findByEmailAndPassword("te2", "tp2"), userEntity2);
        Assertions.assertNotEquals(userRepository.findByEmailAndPassword("te", "tp2"), userEntity2);
        Assertions.assertNotEquals(userRepository.findByEmailAndPassword("te2", "tp"), userEntity2);
        Assertions.assertNotEquals(userRepository.findByEmailAndPassword("tE2", "tp2"), userEntity2);
        Assertions.assertNotEquals(userRepository.findByEmailAndPassword("tE2", "tP2"), userEntity2);
    }
}