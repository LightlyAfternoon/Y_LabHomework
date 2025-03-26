package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liquibase.exception.LiquibaseException;
import org.example.CurrentUser;
import org.example.model.TransactionCategoryEntity;
import org.example.model.UserEntity;
import org.example.repository.TransactionCategoryRepository;
import org.example.service.TransactionCategoryService;
import org.example.servlet.mapper.TransactionCategoryDTOMapper;
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

class TransactionCategoryServletTest {
    TransactionCategoryRepository transactionCategoryRepository;
    TransactionCategoryService transactionCategoryService;
    TransactionCategoryServlet transactionCategoryServlet;
    HttpServletRequest request;
    HttpServletResponse response;
    StringWriter stringWriter;
    ObjectMapper objectMapper;
    TransactionCategoryDTOMapper transactionCategoryDTOMapper;

    @BeforeEach
    void setUp() {
        transactionCategoryRepository = Mockito.mock(TransactionCategoryRepository.class);
        transactionCategoryService = new TransactionCategoryService(transactionCategoryRepository);
        transactionCategoryServlet = new TransactionCategoryServlet(transactionCategoryService);
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        objectMapper = new ObjectMapper();
        transactionCategoryDTOMapper = TransactionCategoryDTOMapper.INSTANCE;
        CurrentUser.currentUser = new UserEntity.UserBuilder("t", "t", "t").id(1).build();
    }

    @Test
    void doGetTest() throws SQLException, LiquibaseException, IOException, ServletException {
        TransactionCategoryEntity transactionCategory = new TransactionCategoryEntity.TransactionCategoryBuilder("t").
                id(1).neededSum(BigDecimal.valueOf(10.10)).userId(1).build();

        Mockito.when(transactionCategoryRepository.findById(1)).thenReturn(transactionCategory);
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        transactionCategoryServlet.doGet(request, response);

        Assertions.assertEquals(stringWriter.toString(), objectMapper.writeValueAsString(transactionCategoryDTOMapper.mapToDTO(transactionCategory)));

        stringWriter = new StringWriter();
        TransactionCategoryEntity transactionCategory2 = new TransactionCategoryEntity.TransactionCategoryBuilder("t2").
                id(2).neededSum(BigDecimal.valueOf(20)).userId(1).build();

        Mockito.when(transactionCategoryRepository.findCommonCategoriesOrGoalsWithUserId(1)).thenReturn(List.of(transactionCategory, transactionCategory2));
        Mockito.when(request.getPathInfo()).thenReturn(null);
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        transactionCategoryServlet.doGet(request, response);

        Assertions.assertEquals(stringWriter.toString(), objectMapper.writeValueAsString(List.of(transactionCategoryDTOMapper.mapToDTO(transactionCategory), transactionCategoryDTOMapper.mapToDTO(transactionCategory2))));
    }

    @Test
    void doPostTest() throws SQLException, LiquibaseException, IOException, ServletException {
        TransactionCategoryEntity transactionCategory = new TransactionCategoryEntity.TransactionCategoryBuilder("t").
                id(1).neededSum(BigDecimal.valueOf(10.10)).userId(1).build();
        transactionCategory = transactionCategoryDTOMapper.mapToEntity(transactionCategoryDTOMapper.mapToDTO(transactionCategory));
        StringReader stringReader = new StringReader(objectMapper.writeValueAsString(transactionCategoryDTOMapper.mapToDTO(transactionCategory)));

        Mockito.when(transactionCategoryRepository.add(transactionCategory)).thenReturn(transactionCategory);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        transactionCategoryServlet.doPost(request, response);

        Assertions.assertEquals(stringWriter.toString(), objectMapper.writeValueAsString(transactionCategoryDTOMapper.mapToDTO(transactionCategory)));
    }

    @Test
    void doPutTest() throws SQLException, LiquibaseException, IOException, ServletException {
        TransactionCategoryEntity transactionCategory = new TransactionCategoryEntity.TransactionCategoryBuilder("t").
                id(1).neededSum(BigDecimal.valueOf(10.10)).userId(1).build();
        StringReader stringReader = new StringReader(objectMapper.writeValueAsString(transactionCategoryDTOMapper.mapToDTO(transactionCategory)));

        Mockito.doNothing().when(transactionCategoryRepository).update(transactionCategory);
        Mockito.when(transactionCategoryRepository.findById(1)).thenReturn(transactionCategory);
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        transactionCategoryServlet.doPut(request, response);

        Assertions.assertEquals(stringWriter.toString(), objectMapper.writeValueAsString(transactionCategoryDTOMapper.mapToDTO(transactionCategory)));
    }

    @Test
    void doDeleteTest() throws SQLException, LiquibaseException, IOException, ServletException {
        TransactionCategoryEntity transactionCategory = new TransactionCategoryEntity.TransactionCategoryBuilder("t").
                id(1).neededSum(BigDecimal.valueOf(10.10)).userId(1).build();
        StringReader stringReader = new StringReader(objectMapper.writeValueAsString(transactionCategoryDTOMapper.mapToDTO(transactionCategory)));

        Mockito.doNothing().when(transactionCategoryRepository).update(transactionCategory);
        Mockito.when(transactionCategoryRepository.findById(1)).thenReturn(transactionCategory);
        Mockito.when(transactionCategoryRepository.delete(transactionCategory)).thenReturn(true);
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        transactionCategoryServlet.doDelete(request, response);

        Assertions.assertEquals(stringWriter.toString(), "TransactionCategory deleted!");
    }
}