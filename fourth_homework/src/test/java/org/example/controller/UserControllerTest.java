package org.example.controller;

import org.example.controller.dto.UserDTO;
import org.example.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatusCode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    @Test
    void doGetTest() throws IOException {
        UserDTO user = new UserDTO.UserBuilder("t", "t", "t").id(1).build();

        Mockito.when(userService.findById(1)).thenReturn(user);

        Assertions.assertEquals(objectMapper.writeValueAsString(user), objectMapper.writeValueAsString(userController.getUserById(1).getBody()));

        Mockito.when(userService.findById(50)).thenReturn(null);

        Assertions.assertEquals("null", objectMapper.writeValueAsString(userController.getUserById(50).getBody()));

        UserDTO user2 = new UserDTO.UserBuilder("t2", "t2", "t2").id(2).build();

        Mockito.when(userService.findAll()).thenReturn(List.of(user, user2));

        Assertions.assertEquals(objectMapper.writeValueAsString(List.of(user, user2)), objectMapper.writeValueAsString(userController.getAllUsers().getBody()));

        Mockito.when(userService.findAll()).thenReturn(new ArrayList<>());

        Assertions.assertEquals("null", objectMapper.writeValueAsString(userController.getAllUsers().getBody()));
    }

    @Test
    void doPostTest() throws IOException {
        UserDTO user = new UserDTO.UserBuilder("t", "t", "t").build();

        Mockito.when(userService.add(user)).thenReturn(user);

        Assertions.assertEquals(objectMapper.writeValueAsString(user), objectMapper.writeValueAsString(userController.createUser(user).getBody()));
    }

    @Test
    void doPutTest() throws IOException {
        UserDTO user = new UserDTO.UserBuilder("t", "t", "t").id(99).build();

        Mockito.when(userService.update(user, 1)).thenReturn(user);
        Mockito.when(userService.findById(1)).thenReturn(user);

        Assertions.assertEquals(objectMapper.writeValueAsString(user), objectMapper.writeValueAsString(userController.updateUser(1, user).getBody()));

        UserDTO user2 = new UserDTO.UserBuilder("t2", "t2", "t2").id(50).build();

        Mockito.when(userService.update(user2, 50)).thenReturn(null);
        Mockito.when(userService.findById(50)).thenReturn(null);

        Assertions.assertEquals("null", objectMapper.writeValueAsString(userController.updateUser(50, user2).getBody()));
    }

    @Test
    void doDeleteTest() {
        UserDTO user = new UserDTO.UserBuilder("t", "t", "t").id(99).build();

        Mockito.when(userService.findById(1)).thenReturn(user);
        Mockito.when(userService.delete(1)).thenReturn(true);

        Assertions.assertEquals(HttpStatusCode.valueOf(204), userController.deleteUserById(1).getStatusCode());

        Mockito.when(userService.findById(50)).thenReturn(null);

        Assertions.assertEquals(HttpStatusCode.valueOf(404), userController.deleteUserById(50).getStatusCode());
    }
}