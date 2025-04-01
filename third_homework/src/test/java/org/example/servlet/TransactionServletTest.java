package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liquibase.exception.LiquibaseException;
import org.example.CurrentUser;
import org.example.model.TransactionEntity;
import org.example.model.UserEntity;
import org.example.repository.TransactionRepository;
import org.example.service.TransactionService;
import org.example.servlet.mapper.TransactionDTOMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class TransactionServletTest {
    TransactionRepository transactionRepository;
    TransactionService transactionService;
    TransactionServlet transactionServlet;
    HttpServletRequest request;
    HttpServletResponse response;
    StringWriter stringWriter;
    ObjectMapper objectMapper;
    TransactionDTOMapper transactionDTOMapper;

    @BeforeEach
    void setUp() {
        transactionRepository = Mockito.mock(TransactionRepository.class);
        transactionService = new TransactionService(transactionRepository);
        transactionServlet = new TransactionServlet(transactionService);
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        objectMapper = new ObjectMapper();
        transactionDTOMapper = TransactionDTOMapper.INSTANCE;
        CurrentUser.currentUser = new UserEntity.UserBuilder("t", "t", "t").id(1).build();
    }

    @Test
    void doGetTest() throws SQLException, LiquibaseException, IOException, ServletException {
        TransactionEntity transaction = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10), 1).
                id(1).description("t").build();

        Mockito.when(transactionRepository.findById(1)).thenReturn(transaction);
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        transactionServlet.doGet(request, response);

        Assertions.assertEquals(objectMapper.writeValueAsString(transactionDTOMapper.mapToDTO(transaction)), stringWriter.toString());

        stringWriter = new StringWriter();
        TransactionEntity transaction2 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(20), 1).
                id(2).description("t2").build();

        Mockito.when(transactionRepository.findAllWithUser(1)).thenReturn(List.of(transaction, transaction2));
        Mockito.when(request.getPathInfo()).thenReturn(null);
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        transactionServlet.doGet(request, response);

        Assertions.assertEquals(objectMapper.writeValueAsString(List.of(transactionDTOMapper.mapToDTO(transaction), transactionDTOMapper.mapToDTO(transaction2))), stringWriter.toString());

        stringWriter = new StringWriter();
        Date date = new Date(System.currentTimeMillis());
        TransactionEntity transaction3 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(20), 1).
                id(2).date(date).description("t2").build();

        Mockito.when(transactionRepository.findAllWithDateAndCategoryIdAndTypeAndUserId(transaction3.getDate(), 0, "Pos", 1)).thenReturn(List.of(transaction3));
        Mockito.when(request.getPathInfo()).thenReturn(null);
        Mockito.when(request.getParameter("date")).thenReturn(transaction3.getDate().toString());
        Mockito.when(request.getParameter("category")).thenReturn(String.valueOf(0));
        Mockito.when(request.getParameter("type")).thenReturn("Pos");
        Mockito.when(request.getParameter("user")).thenReturn(String.valueOf(1));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        transactionServlet.doGet(request, response);

        Assertions.assertEquals(objectMapper.writeValueAsString(List.of(transactionDTOMapper.mapToDTO(transaction3))), stringWriter.toString());

        stringWriter = new StringWriter();

        Mockito.when(transactionRepository.findAllWithUser(1)).thenReturn(new ArrayList<>());
        Mockito.when(request.getPathInfo()).thenReturn(null);
        Mockito.when(request.getParameter("date")).thenReturn(null);
        Mockito.when(request.getParameter("category")).thenReturn(null);
        Mockito.when(request.getParameter("type")).thenReturn(null);
        Mockito.when(request.getParameter("user")).thenReturn(null);
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        transactionServlet.doGet(request, response);

        Assertions.assertEquals("", stringWriter.toString());
    }

    @Test
    void doPostTest() throws SQLException, LiquibaseException, IOException, ServletException {
        TransactionEntity transaction = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10), 1).
                date(new Date(System.currentTimeMillis())).description("t").build();
        transaction = transactionDTOMapper.mapToEntity(transactionDTOMapper.mapToDTO(transaction));
        StringReader stringReader = new StringReader(objectMapper.writeValueAsString(transactionDTOMapper.mapToDTO(transaction)));

        Mockito.when(transactionRepository.add(transaction)).thenReturn(transaction);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        transactionServlet.doPost(request, response);

        Assertions.assertEquals(objectMapper.writeValueAsString(transactionDTOMapper.mapToDTO(transaction)), stringWriter.toString());
    }

    @Test
    void doPutTest() throws SQLException, LiquibaseException, IOException, ServletException {
        TransactionEntity transaction = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10), 1).
                id(99).date(new Date(System.currentTimeMillis())).description("t").build();
        StringReader stringReader = new StringReader(objectMapper.writeValueAsString(transactionDTOMapper.mapToDTO(transaction)));

        Mockito.doNothing().when(transactionRepository).update(transaction);
        Mockito.when(transactionRepository.findById(1)).thenReturn(transaction);
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        transactionServlet.doPut(request, response);

        Assertions.assertEquals(objectMapper.writeValueAsString(transactionDTOMapper.mapToDTO(transaction)), stringWriter.toString());

        stringWriter = new StringWriter();

        TransactionEntity transaction2 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(20), 1).
                id(50).date(new Date(System.currentTimeMillis())).description("t2").build();
        stringReader = new StringReader(objectMapper.writeValueAsString(transactionDTOMapper.mapToDTO(transaction)));

        Mockito.doNothing().when(transactionRepository).update(transaction2);
        Mockito.when(transactionRepository.findById(50)).thenReturn(null);
        Mockito.when(request.getPathInfo()).thenReturn("/50");
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(stringReader));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        transactionServlet.doPut(request, response);

        Assertions.assertEquals("", stringWriter.toString());
    }

    @Test
    void doDeleteTest() throws SQLException, LiquibaseException, IOException, ServletException {
        TransactionEntity transaction = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10), 1).
                id(99).date(new Date(System.currentTimeMillis())).description("t").build();

        Mockito.when(transactionRepository.findById(1)).thenReturn(transaction);
        Mockito.when(transactionRepository.delete(transaction)).thenReturn(true);
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        transactionServlet.doDelete(request, response);

        Assertions.assertEquals("Transaction deleted!", stringWriter.toString());

        stringWriter = new StringWriter();

        Mockito.when(transactionRepository.findById(50)).thenReturn(null);
        Mockito.when(request.getPathInfo()).thenReturn("/50");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        transactionServlet.doDelete(request, response);

        Assertions.assertEquals("Transaction NOT found!", stringWriter.toString());
    }
}