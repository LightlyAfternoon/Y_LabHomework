package org.example.controller;

import org.example.model.UserEntity;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.controller.dto.LogInDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

class LogInControllerTest {
    UserRepository userRepository;
    @Autowired
    UserService userService;
    LogInController logInController;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        logInController = new LogInController(userService);
        objectMapper = new ObjectMapper();
    }

    @Test
    void doPostTest() throws IOException {
        UserEntity user = new UserEntity.UserBuilder("t", "t", "t").build();
        LogInDTO logInDTO = new LogInDTO.LogInBuilder("t", "t").build();

        Mockito.when(userRepository.findByEmailAndPassword("t", "t")).thenReturn(user);

        Assertions.assertEquals(objectMapper.writeValueAsString(user), logInController.getLoggedInUser(logInDTO));

        Mockito.when(userRepository.findByEmailAndPassword("t", "t")).thenReturn(null);

        Assertions.assertEquals("", logInController.getLoggedInUser(logInDTO));
    }
}