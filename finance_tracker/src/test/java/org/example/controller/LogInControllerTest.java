package org.example.controller;

import org.example.controller.dto.UserDTO;
import org.example.service.UserService;
import org.example.controller.dto.LogInDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

@DisplayName("Tests of logging in controller methods")
class LogInControllerTest {
    UserService userService;
    LogInController logInController;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        logInController = new LogInController(userService);
        objectMapper = new ObjectMapper();
    }

    @DisplayName("Test of the method for finding logged in user")
    @Test
    void getLoggedInUserTest() throws IOException {
        UserDTO user = new UserDTO.UserBuilder("t", "t", "t").build();
        LogInDTO logInDTO = new LogInDTO.LogInBuilder("t", "t").build();

        Mockito.when(userService.findUserByEmailAndPassword("t", "t")).thenReturn(user);

        Assertions.assertEquals(objectMapper.writeValueAsString(user), objectMapper.writeValueAsString(logInController.getLoggedInUser(logInDTO).getBody()));

        logInDTO = new LogInDTO();

        logInDTO.setEmail("t");
        logInDTO.setPassword("t");

        Assertions.assertEquals(objectMapper.writeValueAsString(user), objectMapper.writeValueAsString(logInController.getLoggedInUser(logInDTO).getBody()));

        Mockito.when(userService.findUserByEmailAndPassword("t", "t")).thenReturn(null);

        Assertions.assertEquals("null", objectMapper.writeValueAsString(logInController.getLoggedInUser(logInDTO).getBody()));
    }
}