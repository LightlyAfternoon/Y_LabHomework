package org.example.controller;

import org.example.model.MonthlyBudgetEntity;
import org.example.repository.MonthlyBudgetRepository;
import org.example.service.MonthlyBudgetService;
import org.example.controller.mapper.MonthlyBudgetDTOMapper;
import org.example.service.impl.MonthlyBudgetServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

class MonthlyBudgetControllerTest {
    MonthlyBudgetRepository monthlyBudgetRepository;
    MonthlyBudgetService monthlyBudgetService;
    MonthlyBudgetController monthlyBudgetServlet;
    ObjectMapper objectMapper;
    @Autowired
    MonthlyBudgetDTOMapper monthlyBudgetDTOMapper;

    @BeforeEach
    void setUp() {
        monthlyBudgetRepository = Mockito.mock(MonthlyBudgetRepository.class);
        monthlyBudgetService = new MonthlyBudgetServiceImpl(monthlyBudgetRepository, monthlyBudgetDTOMapper);
        monthlyBudgetServlet = new MonthlyBudgetController(monthlyBudgetService);
        objectMapper = new ObjectMapper();
    }

    @Test
    void doGetTest() throws IOException {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetEntity monthlyBudget = new MonthlyBudgetEntity.MonthlyBudgetBuilder(1, BigDecimal.valueOf(10.10)).
                id(1).date(date).build();

        Mockito.when(monthlyBudgetRepository.findById(1)).thenReturn(monthlyBudget);

        Assertions.assertEquals(objectMapper.writeValueAsString(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget)), monthlyBudgetServlet.getMonthlyBudgetById(1));

        MonthlyBudgetEntity monthlyBudget2 = new MonthlyBudgetEntity.MonthlyBudgetBuilder(1, BigDecimal.valueOf(20)).
                id(2).date(new Date(System.currentTimeMillis())).build();

        Mockito.when(monthlyBudgetRepository.findAll()).thenReturn(List.of(monthlyBudget, monthlyBudget2));

        Assertions.assertEquals(objectMapper.writeValueAsString(List.of(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget), monthlyBudgetDTOMapper.mapToDTO(monthlyBudget2))),
                monthlyBudgetServlet.getAllMonthlyBudgets());

        Mockito.when(monthlyBudgetRepository.findByDateAndUserId(monthlyBudget.getDate(), 1)).thenReturn(monthlyBudget);

        Assertions.assertEquals(objectMapper.writeValueAsString(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget)),
                monthlyBudgetServlet.getAllMonthlyBudgetsByDateAndUserId(monthlyBudget.getDate(), 1));

        Mockito.when(monthlyBudgetRepository.findById(50)).thenReturn(monthlyBudget);

        Assertions.assertEquals(objectMapper.writeValueAsString(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget)), monthlyBudgetServlet.getAllMonthlyBudgetsByDateAndUserId(null, 0));
    }

    @Test
    void doPostTest() throws IOException {
        MonthlyBudgetEntity monthlyBudget = new MonthlyBudgetEntity.MonthlyBudgetBuilder(1, BigDecimal.valueOf(10.10)).
                id(1).date(new Date(System.currentTimeMillis())).build();
        monthlyBudget = monthlyBudgetDTOMapper.mapToEntity(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget));

        Mockito.when(monthlyBudgetRepository.save(monthlyBudget)).thenReturn(monthlyBudget);

        Assertions.assertEquals(objectMapper.writeValueAsString(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget)), monthlyBudgetServlet.createMonthlyBudget(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget)));
    }

    @Test
    void doPutTest() throws IOException {
        MonthlyBudgetEntity monthlyBudget = new MonthlyBudgetEntity.MonthlyBudgetBuilder(1, BigDecimal.valueOf(10.10)).
                id(1).date(new Date(System.currentTimeMillis())).build();

        Mockito.doNothing().when(monthlyBudgetRepository).save(monthlyBudget);
        Mockito.when(monthlyBudgetRepository.findById(1)).thenReturn(monthlyBudget);

        Assertions.assertEquals(objectMapper.writeValueAsString(monthlyBudgetDTOMapper.mapToDTO(monthlyBudget)),
                monthlyBudgetServlet.updateMonthlyBudget(1, monthlyBudgetDTOMapper.mapToDTO(monthlyBudget)));

        MonthlyBudgetEntity monthlyBudget2 = new MonthlyBudgetEntity.MonthlyBudgetBuilder(1, BigDecimal.valueOf(20)).
                id(50).date(new Date(System.currentTimeMillis())).build();

        Mockito.doNothing().when(monthlyBudgetRepository).save(monthlyBudget2);
        Mockito.when(monthlyBudgetRepository.findById(50)).thenReturn(null);

        Assertions.assertEquals("", monthlyBudgetServlet.updateMonthlyBudget(50, monthlyBudgetDTOMapper.mapToDTO(monthlyBudget)));
    }

    @Test
    void doDeleteTest() {
        MonthlyBudgetEntity monthlyBudget = new MonthlyBudgetEntity.MonthlyBudgetBuilder(1, BigDecimal.valueOf(10.10)).
                id(1).date(new Date(System.currentTimeMillis())).build();

        Mockito.when(monthlyBudgetRepository.findById(1)).thenReturn(monthlyBudget);
        Mockito.doNothing().when(monthlyBudgetRepository).delete(monthlyBudget);

        Assertions.assertEquals("MonthlyBudget deleted!", monthlyBudgetServlet.deleteMonthlyBudgetById(1));

        Mockito.when(monthlyBudgetRepository.findById(50)).thenReturn(null);

        Assertions.assertEquals("MonthlyBudget NOT found!", monthlyBudgetServlet.deleteMonthlyBudgetById(50));
    }
}