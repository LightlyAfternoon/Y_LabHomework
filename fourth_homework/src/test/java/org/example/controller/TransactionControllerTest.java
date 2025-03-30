package org.example.controller;

import org.example.CurrentUser;
import org.example.model.TransactionEntity;
import org.example.model.UserEntity;
import org.example.repository.TransactionRepository;
import org.example.service.TransactionService;
import org.example.controller.mapper.TransactionDTOMapper;
import org.example.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

class TransactionControllerTest {
    TransactionRepository transactionRepository;
    TransactionService transactionService;
    TransactionController transactionController;
    ObjectMapper objectMapper;
    @Autowired
    TransactionDTOMapper transactionDTOMapper;

    @BeforeEach
    void setUp() {
        transactionRepository = Mockito.mock(TransactionRepository.class);
        transactionService = new TransactionServiceImpl(transactionRepository, transactionDTOMapper);
        transactionController = new TransactionController(transactionService);
        objectMapper = new ObjectMapper();
        CurrentUser.currentUser = new UserEntity.UserBuilder("t", "t", "t").id(1).build();
    }

    @Test
    void doGetTest() throws IOException {
        TransactionEntity transaction = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10), 1).
                id(1).description("t").build();

        Mockito.when(transactionRepository.findById(1)).thenReturn(transaction);

        Assertions.assertEquals(objectMapper.writeValueAsString(transactionDTOMapper.mapToDTO(transaction)), transactionController.getTransactionById(1));

        TransactionEntity transaction2 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(20), 1).
                id(2).description("t2").build();

        Mockito.when(transactionRepository.findAllByUserId(1)).thenReturn(List.of(transaction, transaction2));

        Assertions.assertEquals(objectMapper.writeValueAsString(List.of(transactionDTOMapper.mapToDTO(transaction), transactionDTOMapper.mapToDTO(transaction2))), transactionController.getAllTransactions());

        Date date = new Date(System.currentTimeMillis());
        TransactionEntity transaction3 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(20), 1).
                id(2).date(date).description("t2").build();

        Mockito.when(transactionRepository.findAllByDateAndCategoryIdAndTypeAndUserId(transaction3.getDate(), 0, "Pos", 1)).thenReturn(List.of(transaction3));

        Assertions.assertEquals(objectMapper.writeValueAsString(List.of(transactionDTOMapper.mapToDTO(transaction3))),
                transactionController.getAllTransactionsByDateAndCategoryAndTypeAndUserId(transaction3.getDate(), 0, "Pos", 1));

        Mockito.when(transactionRepository.findAllByUserId(1)).thenReturn(new ArrayList<>());

        Assertions.assertEquals("", transactionController.getAllTransactions());
    }

    @Test
    void doPostTest() throws IOException {
        TransactionEntity transaction = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10), 1).
                date(new Date(System.currentTimeMillis())).description("t").build();
        transaction = transactionDTOMapper.mapToEntity(transactionDTOMapper.mapToDTO(transaction));

        Mockito.when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertEquals(objectMapper.writeValueAsString(transactionDTOMapper.mapToDTO(transaction)), transactionController.createTransaction(transactionDTOMapper.mapToDTO(transaction)));
    }

    @Test
    void doPutTest() throws IOException {
        TransactionEntity transaction = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10), 1).
                id(99).date(new Date(System.currentTimeMillis())).description("t").build();

        Mockito.doNothing().when(transactionRepository).save(transaction);
        Mockito.when(transactionRepository.findById(1)).thenReturn(transaction);

        Assertions.assertEquals(objectMapper.writeValueAsString(transactionDTOMapper.mapToDTO(transaction)),
                transactionController.updateTransaction(1, transactionDTOMapper.mapToDTO(transaction)));

        TransactionEntity transaction2 = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(20), 1).
                id(50).date(new Date(System.currentTimeMillis())).description("t2").build();

        Mockito.doNothing().when(transactionRepository).save(transaction2);
        Mockito.when(transactionRepository.findById(50)).thenReturn(null);

        Assertions.assertEquals("", transactionController.updateTransaction(50, transactionDTOMapper.mapToDTO(transaction)));
    }

    @Test
    void doDeleteTest() {
        TransactionEntity transaction = new TransactionEntity.TransactionBuilder(BigDecimal.valueOf(10), 1).
                id(99).date(new Date(System.currentTimeMillis())).description("t").build();

        Mockito.when(transactionRepository.findById(1)).thenReturn(transaction);
        Mockito.doNothing().when(transactionRepository).delete(transaction);

        Assertions.assertEquals("Transaction deleted!", transactionController.deleteTransactionById(1));

        Mockito.when(transactionRepository.findById(50)).thenReturn(null);

        Assertions.assertEquals("Transaction NOT found!", transactionController.deleteTransactionById(50));
    }
}