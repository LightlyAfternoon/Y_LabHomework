package org.example.controller;

import org.example.CurrentUser;
import org.example.controller.dto.TransactionDTO;
import org.example.model.UserEntity;
import org.example.service.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatusCode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

class TransactionControllerTest {
    TransactionService transactionService;
    TransactionController transactionController;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        transactionService = Mockito.mock(TransactionService.class);
        transactionController = new TransactionController(transactionService);
        objectMapper = new ObjectMapper();
        CurrentUser.currentUser = new UserEntity.UserBuilder("t", "t", "t").id(1).build();
    }

    @Test
    void doGetTest() throws IOException {
        TransactionDTO transaction = new TransactionDTO.TransactionBuilder(BigDecimal.valueOf(10), 1).
                id(1).description("t").build();

        Mockito.when(transactionService.findById(1)).thenReturn(transaction);

        Assertions.assertEquals(objectMapper.writeValueAsString(transaction), objectMapper.writeValueAsString(transactionController.getTransactionById(1).getBody()));

        TransactionDTO transaction2 = new TransactionDTO.TransactionBuilder(BigDecimal.valueOf(20), 1).
                id(2).description("t2").build();

        Mockito.when(transactionService.findAllByUserId(1)).thenReturn(List.of(transaction, transaction2));

        Assertions.assertEquals(objectMapper.writeValueAsString(List.of(transaction, transaction2)), objectMapper.writeValueAsString(transactionController.getAllTransactionsByUserId(1).getBody()));

        Date date = new Date(System.currentTimeMillis());
        TransactionDTO transaction3 = new TransactionDTO.TransactionBuilder(BigDecimal.valueOf(20), 1).
                id(2).date(date).description("t2").build();

        Mockito.when(transactionService.findAllByDateAndCategoryIdAndTypeAndUserId(transaction3.getDate(), 0, "Pos", 1)).thenReturn(List.of(transaction3));

        Assertions.assertEquals(objectMapper.writeValueAsString(List.of(transaction3)),
                objectMapper.writeValueAsString(transactionController.getAllTransactionsByDateAndCategoryAndTypeAndUserId(transaction3.getDate(), 0, "Pos", 1).getBody()));

        Mockito.when(transactionService.findAllByUserId(1)).thenReturn(new ArrayList<>());

        Assertions.assertEquals("null", objectMapper.writeValueAsString(transactionController.getAllTransactions().getBody()));
    }

    @Test
    void doPostTest() throws IOException {
        TransactionDTO transaction = new TransactionDTO.TransactionBuilder(BigDecimal.valueOf(10), 1).
                date(new Date(System.currentTimeMillis())).description("t").build();

        Mockito.when(transactionService.add(transaction)).thenReturn(transaction);

        Assertions.assertEquals(objectMapper.writeValueAsString(transaction), objectMapper.writeValueAsString(transactionController.createTransaction(transaction).getBody()));
    }

    @Test
    void doPutTest() throws IOException {
        TransactionDTO transaction = new TransactionDTO.TransactionBuilder(BigDecimal.valueOf(10), 1).
                id(99).date(new Date(System.currentTimeMillis())).description("t").build();

        Mockito.when(transactionService.update(transaction, 1)).thenReturn(transaction);
        Mockito.when(transactionService.findById(1)).thenReturn(transaction);

        Assertions.assertEquals(objectMapper.writeValueAsString(transaction),
                objectMapper.writeValueAsString(transactionController.updateTransaction(1, transaction).getBody()));

        TransactionDTO transaction2 = new TransactionDTO.TransactionBuilder(BigDecimal.valueOf(20), 1).
                id(50).date(new Date(System.currentTimeMillis())).description("t2").build();

        Mockito.when(transactionService.update(transaction2, 50)).thenReturn(null);
        Mockito.when(transactionService.findById(50)).thenReturn(null);

        Assertions.assertEquals("null", objectMapper.writeValueAsString(transactionController.updateTransaction(50, transaction).getBody()));
    }

    @Test
    void doDeleteTest() {
        TransactionDTO transaction = new TransactionDTO.TransactionBuilder(BigDecimal.valueOf(10), 1).
                id(99).date(new Date(System.currentTimeMillis())).description("t").build();

        Mockito.when(transactionService.findById(1)).thenReturn(transaction);
        Mockito.when(transactionService.delete(1)).thenReturn(true);

        Assertions.assertEquals(HttpStatusCode.valueOf(204), transactionController.deleteTransactionById(1).getStatusCode());

        Mockito.when(transactionService.findById(50)).thenReturn(null);

        Assertions.assertEquals(HttpStatusCode.valueOf(404), transactionController.deleteTransactionById(50).getStatusCode());
    }
}