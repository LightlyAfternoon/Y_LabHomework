package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liquibase.exception.LiquibaseException;
import org.example.model.UserEntity;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.servlet.mapper.UserDTOMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

class UserServletTest {
    UserRepository userRepository;
    UserService userService;
    UserServlet userServlet;
    HttpServletRequest request;
    HttpServletResponse response;
    StringWriter stringWriter;
    ObjectMapper objectMapper;
    UserDTOMapper userDTOMapper;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
        userServlet = new UserServlet(userService);
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        objectMapper = new ObjectMapper();
        userDTOMapper = UserDTOMapper.INSTANCE;
    }

    @Test
    void doGetTest() throws SQLException, LiquibaseException, IOException, ServletException {
        UserEntity user = new UserEntity.UserBuilder("t", "t", "t").id(1).build();

        Mockito.when(userRepository.findById(1)).thenReturn(user);
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        userServlet.doGet(request, response);

        Assertions.assertEquals(stringWriter.toString(), objectMapper.writeValueAsString(userDTOMapper.mapToDTO(user)));

        stringWriter = new StringWriter();
        UserEntity user2 = new UserEntity.UserBuilder("t2", "t2", "t2").id(2).build();

        Mockito.when(userRepository.findAll()).thenReturn(List.of(user, user2));
        Mockito.when(request.getPathInfo()).thenReturn(null);
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        userServlet.doGet(request, response);

        Assertions.assertEquals(stringWriter.toString(), objectMapper.writeValueAsString(List.of(userDTOMapper.mapToDTO(user), userDTOMapper.mapToDTO(user2))));
    }

    @Test
    void doPostTest() throws SQLException, LiquibaseException, IOException, ServletException {
        UserEntity user = new UserEntity.UserBuilder("t", "t", "t").build();
        StringReader stringReader = new StringReader(objectMapper.writeValueAsString(userDTOMapper.mapToDTO(user)));

        Mockito.when(userRepository.add(user)).thenReturn(user);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        userServlet.doPost(request, response);

        Assertions.assertEquals(stringWriter.toString(), objectMapper.writeValueAsString(userDTOMapper.mapToDTO(user)));
    }

    @Test
    void doPutTest() throws SQLException, LiquibaseException, IOException, ServletException {
        UserEntity user = new UserEntity.UserBuilder("t", "t", "t").id(99).build();
        StringReader stringReader = new StringReader(objectMapper.writeValueAsString(userDTOMapper.mapToDTO(user)));

        Mockito.doNothing().when(userRepository).update(user);
        Mockito.when(userRepository.findById(1)).thenReturn(user);
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        userServlet.doPut(request, response);

        Assertions.assertEquals(stringWriter.toString(), objectMapper.writeValueAsString(userDTOMapper.mapToDTO(user)));
    }

    @Test
    void doDeleteTest() throws SQLException, LiquibaseException, IOException, ServletException {
        UserEntity user = new UserEntity.UserBuilder("t", "t", "t").id(99).build();
        StringReader stringReader = new StringReader(objectMapper.writeValueAsString(userDTOMapper.mapToDTO(user)));

        Mockito.doNothing().when(userRepository).update(user);
        Mockito.when(userRepository.findById(1)).thenReturn(user);
        Mockito.when(userRepository.delete(user)).thenReturn(true);
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        userServlet.doDelete(request, response);

        Assertions.assertEquals(stringWriter.toString(), "User deleted!");
    }
}