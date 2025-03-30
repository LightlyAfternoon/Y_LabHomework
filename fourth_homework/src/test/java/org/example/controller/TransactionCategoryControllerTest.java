package org.example.controller;

import org.example.CurrentUser;
import org.example.controller.dto.TransactionCategoryDTO;
import org.example.model.UserEntity;
import org.example.service.TransactionCategoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatusCode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;

class TransactionCategoryControllerTest {
    TransactionCategoryService transactionCategoryService;
    TransactionCategoryController transactionCategoryController;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        transactionCategoryService = Mockito.mock(TransactionCategoryService.class);
        transactionCategoryController = new TransactionCategoryController(transactionCategoryService);
        objectMapper = new ObjectMapper();
        CurrentUser.currentUser = new UserEntity.UserBuilder("t", "t", "t").id(1).build();
    }

    @Test
    void doGetTest() throws IOException {
        TransactionCategoryDTO transactionCategory = new TransactionCategoryDTO.TransactionCategoryBuilder("t").
                id(1).build();

        Mockito.when(transactionCategoryService.findById(1)).thenReturn(transactionCategory);

        Assertions.assertEquals(objectMapper.writeValueAsString(transactionCategory),
                objectMapper.writeValueAsString(transactionCategoryController.getTransactionCategoryById(1).getBody()));

        TransactionCategoryDTO transactionCategory2 = new TransactionCategoryDTO.TransactionCategoryBuilder("t2").
                id(2).neededSum(BigDecimal.valueOf(20)).userId(1).build();

        Mockito.when(transactionCategoryService.findCommonCategoriesOrGoalsByUserId(1)).thenReturn(List.of(transactionCategory, transactionCategory2));

        Assertions.assertEquals(objectMapper.writeValueAsString(List.of(transactionCategory, transactionCategory2)),
                objectMapper.writeValueAsString(transactionCategoryController.getAllTransactionCategories().getBody()));

        Mockito.when(transactionCategoryService.findAllGoalsByUserId(1)).thenReturn(List.of(transactionCategory2));

        Assertions.assertEquals(objectMapper.writeValueAsString(List.of(transactionCategory2)),
                objectMapper.writeValueAsString(transactionCategoryController.getAllTransactionCategoriesByUserId(1).getBody()));

        Mockito.when(transactionCategoryService.findByName("t")).thenReturn(transactionCategory);

        Assertions.assertEquals(objectMapper.writeValueAsString(transactionCategory),
                objectMapper.writeValueAsString(transactionCategoryController.getAllTransactionCategoriesByName("t").getBody()));

        Mockito.when(transactionCategoryService.findById(50)).thenReturn(null);

        Assertions.assertEquals("null", objectMapper.writeValueAsString(transactionCategoryController.getTransactionCategoryById(50).getBody()));
    }

    @Test
    void doPostTest() throws IOException {
        TransactionCategoryDTO transactionCategory = new TransactionCategoryDTO.TransactionCategoryBuilder("t").
                id(1).neededSum(BigDecimal.valueOf(10.10)).userId(1).build();

        Mockito.when(transactionCategoryService.add(transactionCategory)).thenReturn(transactionCategory);

        Assertions.assertEquals(objectMapper.writeValueAsString(transactionCategory),
                objectMapper.writeValueAsString(transactionCategoryController.createTransactionCategory(transactionCategory).getBody()));
    }

    @Test
    void doPutTest() throws IOException {
        TransactionCategoryDTO transactionCategory = new TransactionCategoryDTO.TransactionCategoryBuilder("t").
                id(1).neededSum(BigDecimal.valueOf(10.10)).userId(1).build();

        Mockito.when(transactionCategoryService.update(transactionCategory, 1)).thenReturn(transactionCategory);
        Mockito.when(transactionCategoryService.findById(1)).thenReturn(transactionCategory);

        Assertions.assertEquals(objectMapper.writeValueAsString(transactionCategory),
                objectMapper.writeValueAsString(transactionCategoryController.updateTransactionCategory(1, transactionCategory).getBody()));

        TransactionCategoryDTO transactionCategory2 = new TransactionCategoryDTO.TransactionCategoryBuilder("t2").
                id(50).neededSum(BigDecimal.valueOf(20)).userId(1).build();

        Mockito.when(transactionCategoryService.update(transactionCategory2, 50)).thenReturn(null);
        Mockito.when(transactionCategoryService.findById(50)).thenReturn(null);

        Assertions.assertEquals("null", objectMapper.writeValueAsString(transactionCategoryController.updateTransactionCategory(50, transactionCategory).getBody()));
    }

    @Test
    void doDeleteTest() {
        TransactionCategoryDTO transactionCategory = new TransactionCategoryDTO.TransactionCategoryBuilder("t").
                id(1).neededSum(BigDecimal.valueOf(10.10)).userId(1).build();

        Mockito.when(transactionCategoryService.findById(1)).thenReturn(transactionCategory);
        Mockito.when(transactionCategoryService.delete(1)).thenReturn(true);

        Assertions.assertEquals(HttpStatusCode.valueOf(204), transactionCategoryController.deleteTransactionCategoryById(1).getStatusCode());

        Mockito.when(transactionCategoryService.findById(50)).thenReturn(null);

        Assertions.assertEquals(HttpStatusCode.valueOf(404), transactionCategoryController.deleteTransactionCategoryById(50).getStatusCode());
    }
}