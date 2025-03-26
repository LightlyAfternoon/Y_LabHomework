package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liquibase.exception.LiquibaseException;
import org.example.model.UserEntity;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.servlet.dto.LogInDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.sql.SQLException;

class LogInServletTest {
    UserRepository userRepository;
    UserService userService;
    LogInServlet logInServlet;
    HttpServletRequest request;
    HttpServletResponse response;
    StringWriter stringWriter;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
        logInServlet = new LogInServlet(userService);
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        objectMapper = new ObjectMapper();
    }

    @Test
    void doPostTest() throws SQLException, LiquibaseException, IOException, ServletException {
        UserEntity user = new UserEntity.UserBuilder("t", "t", "t").build();
        LogInDTO logInDTO = new LogInDTO.LogInBuilder("t", "t").build();
        StringReader stringReader = new StringReader(objectMapper.writeValueAsString(logInDTO));

        Mockito.when(userRepository.findUserWithEmailAndPassword("t", "t")).thenReturn(user);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        logInServlet.doPost(request, response);

        Assertions.assertEquals(objectMapper.writeValueAsString(user), stringWriter.toString());

        stringWriter = new StringWriter();

        Mockito.when(userRepository.findUserWithEmailAndPassword("t", "t")).thenReturn(null);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        logInServlet.doPost(request, response);

        Assertions.assertEquals("", stringWriter.toString());
    }
}