package org.example.service;

import liquibase.exception.LiquibaseException;
import org.example.config.MyTestConfig;
import org.example.controller.dto.UserDTO;
import org.example.model.UserRole;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.SQLException;
import java.util.List;

class UserServiceTest {
    UserService userService;
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
        userService = context.getBean(UserService.class);

        for (UserDTO userDTO : userService.findAll()) {
            userService.delete(userDTO.getId());
        }
    }

    @Test
    void addTest() {
        UserDTO userDTO = new UserDTO();

        userDTO.setEmail("t");
        userDTO.setPassword("t");
        userDTO.setName("t");
        userDTO.setRole(UserRole.USER);
        userDTO.setBlocked(false);

        userDTO = userService.add(userDTO);

        Assertions.assertNotEquals(0, userDTO.getId());

        UserDTO userDTO2 = new UserDTO();

        userDTO2.setEmail("t");
        userDTO2.setPassword("t");
        userDTO2.setName("t");
        userDTO2.setRole(UserRole.USER);
        userDTO2.setBlocked(false);

        userDTO2 = userService.add(userDTO2);

        Assertions.assertNull(userDTO2);

        UserDTO userDTO3 = new UserDTO();

        userDTO3.setEmail("t");
        userDTO3.setPassword("t2");
        userDTO3.setName("t2");
        userDTO3.setRole(UserRole.USER);
        userDTO3.setBlocked(true);

        userDTO3 = userService.add(userDTO3);

        Assertions.assertNull(userDTO3);
    }

    @Test
    void findByIdTest() {
        UserDTO userDTO = new UserDTO();

        userDTO.setEmail("t");
        userDTO.setPassword("t");
        userDTO.setName("t");
        userDTO.setRole(UserRole.USER);
        userDTO.setBlocked(false);

        userDTO = userService.add(userDTO);

        Assertions.assertEquals(userService.findById(userDTO.getId()), userDTO);

        userDTO.setRole(UserRole.ADMIN);

        Assertions.assertNotEquals(userService.findById(userDTO.getId()), userDTO);

        Assertions.assertNull(userService.findById(50));
    }

    @Test
    void findAllTest() {
        UserDTO userDTO = new UserDTO();

        userDTO.setEmail("t");
        userDTO.setPassword("t");
        userDTO.setName("t");
        userDTO.setRole(UserRole.USER);
        userDTO.setBlocked(false);

        UserDTO userDTO2 = new UserDTO();

        userDTO2.setEmail("t2");
        userDTO2.setPassword("t2");
        userDTO2.setName("t2");
        userDTO2.setRole(UserRole.USER);
        userDTO2.setBlocked(true);

        UserDTO userDTO3 = new UserDTO();

        userDTO3.setEmail("t3");
        userDTO3.setPassword("t2");
        userDTO3.setName("t3");
        userDTO3.setRole(UserRole.ADMIN);
        userDTO3.setBlocked(false);

        List<UserDTO> userEntities = List.of(userDTO, userDTO2, userDTO3);

        List<UserDTO> userEntitiesReturned;

        userService.add(userDTO);
        userService.add(userDTO2);
        userService.add(userDTO3);

        userEntitiesReturned = userService.findAll();

        Assertions.assertEquals(userEntities, userEntitiesReturned);

        UserDTO userDTO4 = new UserDTO();

        userDTO4.setEmail("t4");
        userDTO4.setPassword("t2");
        userDTO4.setName("t4");
        userDTO4.setRole(UserRole.ADMIN);
        userDTO4.setBlocked(false);

        userEntities = List.of(userDTO, userDTO2, userDTO3, userDTO4);

        userService.add(userDTO4);

        userEntitiesReturned = userService.findAll();

        Assertions.assertEquals(userEntities, userEntitiesReturned);

        userDTO.setRole(UserRole.ADMIN);

        Assertions.assertNotEquals(userEntities, userEntitiesReturned);
    }

    @Test
    void updateTest() {
        UserDTO userDTO = new UserDTO();

        userDTO.setEmail("t");
        userDTO.setPassword("t");
        userDTO.setName("t");
        userDTO.setRole(UserRole.USER);
        userDTO.setBlocked(false);

        userDTO = userService.add(userDTO);

        UserDTO userDTO2 = new UserDTO(userDTO.getId());

        userDTO2.setEmail("t2");
        userDTO2.setPassword("t2");
        userDTO2.setName("t2");
        userDTO2.setRole(UserRole.USER);
        userDTO2.setBlocked(false);

        userService.update(userDTO2, userDTO2.getId());

        Assertions.assertEquals(userService.findById(userDTO.getId()), userDTO2);

        userDTO2.setRole(UserRole.ADMIN);

        Assertions.assertNotEquals(userService.findById(userDTO.getId()), userDTO2);
    }

    @Test
    void deleteTest() {
        UserDTO userDTO = new UserDTO();

        userDTO.setEmail("t");
        userDTO.setPassword("t");
        userDTO.setName("t");
        userDTO.setRole(UserRole.USER);
        userDTO.setBlocked(false);

        UserDTO userDTO2 = new UserDTO();

        userDTO2.setEmail("t2");
        userDTO2.setPassword("t2");
        userDTO2.setName("t2");
        userDTO2.setRole(UserRole.USER);
        userDTO2.setBlocked(true);

        UserDTO userDTO3 = new UserDTO();

        userDTO3.setEmail("t3");
        userDTO3.setPassword("t2");
        userDTO3.setName("t3");
        userDTO3.setRole(UserRole.ADMIN);
        userDTO3.setBlocked(false);

        List<UserDTO> userEntities = List.of(userDTO, userDTO2, userDTO3);

        userDTO = userService.add(userDTO);
        userService.add(userDTO2);
        userService.add(userDTO3);

        List<UserDTO> userEntitiesReturned;

        userEntitiesReturned = userService.findAll();

        Assertions.assertEquals(userEntities, userEntitiesReturned);

        userService.delete(userDTO.getId());
        userEntitiesReturned = userService.findAll();

        Assertions.assertNotEquals(userEntities, userEntitiesReturned);

        userEntities = List.of(userDTO2, userDTO3);

        Assertions.assertEquals(userEntities, userEntitiesReturned);
    }

    @Test
    void findUserWithEmailAndPasswordTest() {
        UserDTO userDTO = new UserDTO();

        userDTO.setEmail("te");
        userDTO.setPassword("tp");
        userDTO.setName("t");
        userDTO.setRole(UserRole.USER);
        userDTO.setBlocked(false);

        userService.add(userDTO);

        Assertions.assertEquals(userService.findUserByEmailAndPassword("te", "tp"), userDTO);

        UserDTO userDTO2 = new UserDTO();

        userDTO2.setEmail("te2");
        userDTO2.setPassword("tp2");
        userDTO2.setName("t2");
        userDTO2.setRole(UserRole.USER);
        userDTO2.setBlocked(false);

        userService.add(userDTO2);

        Assertions.assertEquals(userService.findUserByEmailAndPassword("te2", "tp2"), userDTO2);
        Assertions.assertNotEquals(userService.findUserByEmailAndPassword("te", "tp2"), userDTO2);
        Assertions.assertNotEquals(userService.findUserByEmailAndPassword("te2", "tp"), userDTO2);
        Assertions.assertNotEquals(userService.findUserByEmailAndPassword("tE2", "tp2"), userDTO2);
        Assertions.assertNotEquals(userService.findUserByEmailAndPassword("tE2", "tP2"), userDTO2);
    }
}