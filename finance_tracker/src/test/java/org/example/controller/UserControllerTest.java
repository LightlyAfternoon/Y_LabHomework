package org.example.controller;

import org.example.controller.dto.UserDTO;
import org.example.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@DisplayName("Tests of user controller methods")
class UserControllerTest {
    UserService userService;
    UserController userController;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        userController = new UserController(userService);
        objectMapper = new ObjectMapper();
    }

    @DisplayName("Test of the method for finding all users")
    @Test
    void getAllUsersTest() throws IOException {
        UserDTO user = new UserDTO.UserBuilder("t", "t", "t").id(1).build();

        UserDTO user2 = new UserDTO.UserBuilder("t2", "t2", "t2").id(2).build();

        Mockito.when(userService.findAll()).thenReturn(List.of(user, user2));

        Assertions.assertEquals(objectMapper.writeValueAsString(List.of(user, user2)), objectMapper.writeValueAsString(userController.getAllUsers().getBody()));

        Mockito.when(userService.findAll()).thenReturn(new ArrayList<>());

        Assertions.assertEquals("null", objectMapper.writeValueAsString(userController.getAllUsers().getBody()));
    }

    @DisplayName("Test of the method for finding user by id")
    @Test
    void getUserByIdTest() throws IOException {
        UserDTO user = new UserDTO.UserBuilder("t", "t", "t").id(1).build();

        Mockito.when(userService.findById(1)).thenReturn(user);

        Assertions.assertEquals(objectMapper.writeValueAsString(user), objectMapper.writeValueAsString(userController.getUserById(1).getBody()));

        Mockito.when(userService.findById(50)).thenReturn(null);

        Assertions.assertEquals("null", objectMapper.writeValueAsString(userController.getUserById(50).getBody()));
    }

    @DisplayName("Test of the method for adding user")
    @Test
    void createUserTest() throws IOException {
        UserDTO user = new UserDTO.UserBuilder("t", "t", "t").build();

        Mockito.when(userService.add(user)).thenReturn(user);

        Assertions.assertEquals(objectMapper.writeValueAsString(user), objectMapper.writeValueAsString(userController.createUser(user).getBody()));

        user = new UserDTO.UserBuilder("t", "t", null).build();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, userController.createUser(user).getStatusCode());
    }

    @DisplayName("Test of the method for updating user")
    @Test
    void updateUserTest() throws IOException {
        UserDTO user = new UserDTO.UserBuilder("t", "t", "t").id(99).build();

        Mockito.when(userService.update(user, 1)).thenReturn(user);
        Mockito.when(userService.findById(1)).thenReturn(user);

        Assertions.assertEquals(objectMapper.writeValueAsString(user), objectMapper.writeValueAsString(userController.updateUser(1, user).getBody()));

        UserDTO user2 = new UserDTO.UserBuilder("t2", "t2", "t2").id(50).build();

        Mockito.when(userService.update(user2, 50)).thenReturn(null);
        Mockito.when(userService.findById(50)).thenReturn(null);

        Assertions.assertEquals("null", objectMapper.writeValueAsString(userController.updateUser(50, user2).getBody()));

        user2 = new UserDTO.UserBuilder("t", "t", null).build();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, userController.updateUser(2 ,user2).getStatusCode());
    }

    @DisplayName("Test of the method for deleting user")
    @Test
    void deleteUserByIdTest() {
        UserDTO user = new UserDTO.UserBuilder("t", "t", "t").id(99).build();

        Mockito.when(userService.findById(1)).thenReturn(user);
        Mockito.when(userService.delete(1)).thenReturn(true);

        Assertions.assertEquals(HttpStatusCode.valueOf(204), userController.deleteUserById(1).getStatusCode());

        Mockito.when(userService.findById(50)).thenReturn(null);

        Assertions.assertEquals(HttpStatusCode.valueOf(404), userController.deleteUserById(50).getStatusCode());
    }
}