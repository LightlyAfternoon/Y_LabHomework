package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liquibase.exception.LiquibaseException;
import org.example.model.MonthlyBudgetEntity;
import org.example.repository.MonthlyBudgetRepository;
import org.example.service.MonthlyBudgetService;
import org.example.servlet.mapper.MonthlyBudgetDTOMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

class MonthlyBudgetServletTest {
    MonthlyBudgetRepository monthlyBudgetRepository;
    MonthlyBudgetService monthlyBudgetService;
    MonthlyBudgetServlet monthlyBudgetServlet;
    HttpServletRequest request;
    HttpServletResponse response;
    StringWriter stringWriter;
    ObjectMapper objectMapper;
    MonthlyBudgetDTOMapper monthlyBudgetDTOMapper;

    @BeforeEach
    void setUp() {
        monthlyBudgetRepository = Mockito.mock(MonthlyBudgetRepository.class);
        monthlyBudgetService = new MonthlyBudgetService(monthlyBudgetRepository);
        monthlyBudgetServlet = new MonthlyBudgetServlet(monthlyBudgetService);
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        objectMapper = new ObjectMapper();
        monthlyBudgetDTOMapper = MonthlyBudgetDTOMapper.INSTANCE;
    }

    @Test
    void doGetTest() throws SQLException, LiquibaseException, IOException, ServletException {
        MonthlyBudgetEntity monthlyBudget = new MonthlyBudgetEntity.MonthlyBudgetBuilder(1, BigDecimal.valueOf(10.10)).
                id(1).date(new Date(System.currentTimeMillis())).build();

        Mockito.when(monthlyBudgetRepository.findById(1)).thenReturn(monthlyBudget);
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        monthlyBudgetServlet.doGet(request, response);

        Assertions.assertEquals(stringWriter.toString(), objectMapper.writeValueAsString(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget)));

        stringWriter = new StringWriter();
        MonthlyBudgetEntity monthlyBudget2 = new MonthlyBudgetEntity.MonthlyBudgetBuilder(1, BigDecimal.valueOf(20)).
                id(2).date(new Date(System.currentTimeMillis())).build();

        Mockito.when(monthlyBudgetRepository.findAll()).thenReturn(List.of(monthlyBudget, monthlyBudget2));
        Mockito.when(request.getPathInfo()).thenReturn(null);
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        monthlyBudgetServlet.doGet(request, response);

        Assertions.assertEquals(stringWriter.toString(), objectMapper.writeValueAsString(List.of(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget), monthlyBudgetDTOMapper.mapToDTO(monthlyBudget2))));
    }

    @Test
    void doPostTest() throws SQLException, LiquibaseException, IOException, ServletException {
        MonthlyBudgetEntity monthlyBudget = new MonthlyBudgetEntity.MonthlyBudgetBuilder(1, BigDecimal.valueOf(10.10)).
                id(1).date(new Date(System.currentTimeMillis())).build();
        monthlyBudget = monthlyBudgetDTOMapper.mapToEntity(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget));
        StringReader stringReader = new StringReader(objectMapper.writeValueAsString(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget)));

        Mockito.when(monthlyBudgetRepository.add(monthlyBudget)).thenReturn(monthlyBudget);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        monthlyBudgetServlet.doPost(request, response);

        Assertions.assertEquals(stringWriter.toString(), objectMapper.writeValueAsString(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget)));
    }

    @Test
    void doPutTest() throws SQLException, LiquibaseException, IOException, ServletException {
        MonthlyBudgetEntity monthlyBudget = new MonthlyBudgetEntity.MonthlyBudgetBuilder(1, BigDecimal.valueOf(10.10)).
                id(1).date(new Date(System.currentTimeMillis())).build();
        StringReader stringReader = new StringReader(objectMapper.writeValueAsString(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget)));

        Mockito.doNothing().when(monthlyBudgetRepository).update(monthlyBudget);
        Mockito.when(monthlyBudgetRepository.findById(1)).thenReturn(monthlyBudget);
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        monthlyBudgetServlet.doPut(request, response);

        Assertions.assertEquals(stringWriter.toString(), objectMapper.writeValueAsString(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget)));
    }

    @Test
    void doDeleteTest() throws SQLException, LiquibaseException, IOException, ServletException {
        MonthlyBudgetEntity monthlyBudget = new MonthlyBudgetEntity.MonthlyBudgetBuilder(1, BigDecimal.valueOf(10.10)).
                id(1).date(new Date(System.currentTimeMillis())).build();
        StringReader stringReader = new StringReader(objectMapper.writeValueAsString(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget)));

        Mockito.doNothing().when(monthlyBudgetRepository).update(monthlyBudget);
        Mockito.when(monthlyBudgetRepository.findById(1)).thenReturn(monthlyBudget);
        Mockito.when(monthlyBudgetRepository.delete(monthlyBudget)).thenReturn(true);
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        monthlyBudgetServlet.doDelete(request, response);

        Assertions.assertEquals(stringWriter.toString(), "MonthlyBudget deleted!");
    }
}