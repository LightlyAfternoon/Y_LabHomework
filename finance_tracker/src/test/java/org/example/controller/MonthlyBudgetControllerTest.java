package org.example.controller;

import org.example.controller.dto.MonthlyBudgetDTO;
import org.example.service.MonthlyBudgetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@DisplayName("Tests of monthly budget controller methods")
class MonthlyBudgetControllerTest {
    MonthlyBudgetService monthlyBudgetService;
    MonthlyBudgetController monthlyBudgetController;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        monthlyBudgetService = Mockito.mock(MonthlyBudgetService.class);
        monthlyBudgetController = new MonthlyBudgetController(monthlyBudgetService);
        objectMapper = new ObjectMapper();
    }

    @DisplayName("Test of the method for finding all monthly budgets")
    @Test
    void getAllMonthlyBudgetsTest() throws IOException {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetDTO monthlyBudget = new MonthlyBudgetDTO.MonthlyBudgetBuilder(1, BigDecimal.valueOf(10.10)).
                id(1).date(date).build();

        MonthlyBudgetDTO monthlyBudget2 = new MonthlyBudgetDTO.MonthlyBudgetBuilder(1, BigDecimal.valueOf(20)).
                id(2).date(new Date(System.currentTimeMillis())).build();

        Mockito.when(monthlyBudgetService.findAll()).thenReturn(List.of(monthlyBudget, monthlyBudget2));

        Assertions.assertEquals(objectMapper.writeValueAsString(List.of(monthlyBudget, monthlyBudget2)),
                objectMapper.writeValueAsString(monthlyBudgetController.getAllMonthlyBudgets().getBody()));

        Mockito.when(monthlyBudgetService.findByDateAndUserId(monthlyBudget.getDate(), 1)).thenReturn(monthlyBudget);

        Assertions.assertEquals(objectMapper.writeValueAsString(monthlyBudget),
                objectMapper.writeValueAsString(monthlyBudgetController.getAllMonthlyBudgetsByDateAndUserId(monthlyBudget.getDate(), 1).getBody()));
    }

    @DisplayName("Test of the method for finding monthly budget by date and user id")
    @Test
    void getAllMonthlyBudgetsByDateAndUserIdTest() throws IOException {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetDTO monthlyBudget = new MonthlyBudgetDTO.MonthlyBudgetBuilder(1, BigDecimal.valueOf(10.10)).
                id(1).date(date).build();

        MonthlyBudgetDTO monthlyBudget2 = new MonthlyBudgetDTO.MonthlyBudgetBuilder(1, BigDecimal.valueOf(20)).
                id(2).date(new Date(System.currentTimeMillis())).build();

        Mockito.when(monthlyBudgetService.findByDateAndUserId(monthlyBudget.getDate(), 1)).thenReturn(monthlyBudget);

        Assertions.assertEquals(objectMapper.writeValueAsString(monthlyBudget),
                objectMapper.writeValueAsString(monthlyBudgetController.getAllMonthlyBudgetsByDateAndUserId(monthlyBudget.getDate(), 1).getBody()));

        Mockito.when(monthlyBudgetService.findByDateAndUserId(monthlyBudget2.getDate(), 1)).thenReturn(monthlyBudget2);

        Assertions.assertEquals(objectMapper.writeValueAsString(monthlyBudget2),
                objectMapper.writeValueAsString(monthlyBudgetController.getAllMonthlyBudgetsByDateAndUserId(monthlyBudget2.getDate(), 1).getBody()));
    }

    @DisplayName("Test of the method for finding monthly budget by id")
    @Test
    void getMonthlyBudgetByIdTest() throws IOException {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetDTO monthlyBudget = new MonthlyBudgetDTO.MonthlyBudgetBuilder(1, BigDecimal.valueOf(10.10)).
                id(1).date(date).build();

        Mockito.when(monthlyBudgetService.findById(1)).thenReturn(monthlyBudget);

        Assertions.assertEquals(objectMapper.writeValueAsString(monthlyBudget),
                objectMapper.writeValueAsString(monthlyBudgetController.getMonthlyBudgetById(1).getBody()));

        Mockito.when(monthlyBudgetService.findById(50)).thenReturn(null);

        Assertions.assertEquals("null",
                objectMapper.writeValueAsString(monthlyBudgetController.getMonthlyBudgetById(50).getBody()));
    }

    @DisplayName("Test of the method for adding monthly budget")
    @Test
    void createMonthlyBudgetTest() throws IOException {
        MonthlyBudgetDTO monthlyBudget = new MonthlyBudgetDTO.MonthlyBudgetBuilder(1, BigDecimal.valueOf(10.10)).
                id(1).date(new Date(System.currentTimeMillis())).build();

        Mockito.when(monthlyBudgetService.add(monthlyBudget)).thenReturn(monthlyBudget);

        Assertions.assertEquals(objectMapper.writeValueAsString(monthlyBudget),
                objectMapper.writeValueAsString(monthlyBudgetController.createMonthlyBudget(monthlyBudget).getBody()));

        monthlyBudget = new MonthlyBudgetDTO.MonthlyBudgetBuilder(0, BigDecimal.valueOf(10.10)).
                id(1).date(new Date(System.currentTimeMillis())).build();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, monthlyBudgetController.createMonthlyBudget(monthlyBudget).getStatusCode());
    }

    @DisplayName("Test of the method for updating monthly budget")
    @Test
    void updateMonthlyBudgetTest() throws IOException {
        MonthlyBudgetDTO monthlyBudget = new MonthlyBudgetDTO.MonthlyBudgetBuilder(1, BigDecimal.valueOf(10.10)).
                id(1).date(new Date(System.currentTimeMillis())).build();

        Mockito.when(monthlyBudgetService.update(monthlyBudget, 1)).thenReturn(monthlyBudget);
        Mockito.when(monthlyBudgetService.findById(1)).thenReturn(monthlyBudget);

        Assertions.assertEquals(objectMapper.writeValueAsString(monthlyBudget),
                objectMapper.writeValueAsString(monthlyBudgetController.updateMonthlyBudget(1, monthlyBudget).getBody()));

        MonthlyBudgetDTO monthlyBudget2 = new MonthlyBudgetDTO.MonthlyBudgetBuilder(1, BigDecimal.valueOf(20)).
                id(50).date(new Date(System.currentTimeMillis())).build();

        Mockito.when(monthlyBudgetService.update(monthlyBudget2, 50)).thenReturn(null);
        Mockito.when(monthlyBudgetService.findById(50)).thenReturn(null);

        Assertions.assertEquals("null", objectMapper.writeValueAsString(monthlyBudgetController.updateMonthlyBudget(50, monthlyBudget).getBody()));

        monthlyBudget = new MonthlyBudgetDTO.MonthlyBudgetBuilder(0, BigDecimal.valueOf(10.10)).
                id(1).date(new Date(System.currentTimeMillis())).build();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, monthlyBudgetController.updateMonthlyBudget(1, monthlyBudget).getStatusCode());
    }

    @DisplayName("Test of the method for deleting monthly budget")
    @Test
    void deleteMonthlyBudgetByIdTest() {
        MonthlyBudgetDTO monthlyBudget = new MonthlyBudgetDTO.MonthlyBudgetBuilder(1, BigDecimal.valueOf(10.10)).
                id(1).date(new Date(System.currentTimeMillis())).build();

        Mockito.when(monthlyBudgetService.findById(1)).thenReturn(monthlyBudget);
        Mockito.when(monthlyBudgetService.delete(1)).thenReturn(true);

        Assertions.assertEquals(HttpStatusCode.valueOf(204), monthlyBudgetController.deleteMonthlyBudgetById(1).getStatusCode());

        Mockito.when(monthlyBudgetService.findById(50)).thenReturn(null);

        Assertions.assertEquals(HttpStatusCode.valueOf(404), monthlyBudgetController.deleteMonthlyBudgetById(50).getStatusCode());
    }
}