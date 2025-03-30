package org.example.controller;

import org.example.CurrentUser;
import org.example.model.TransactionCategoryEntity;
import org.example.model.UserEntity;
import org.example.repository.TransactionCategoryRepository;
import org.example.service.TransactionCategoryService;
import org.example.controller.mapper.TransactionCategoryDTOMapper;
import org.example.service.impl.TransactionCategoryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;

class TransactionCategoryControllerTest {
    TransactionCategoryRepository transactionCategoryRepository;
    TransactionCategoryService transactionCategoryService;
    TransactionCategoryController transactionCategoryController;
    ObjectMapper objectMapper;
    @Autowired
    TransactionCategoryDTOMapper transactionCategoryDTOMapper;

    @BeforeEach
    void setUp() {
        transactionCategoryRepository = Mockito.mock(TransactionCategoryRepository.class);
        transactionCategoryService = new TransactionCategoryServiceImpl(transactionCategoryRepository, transactionCategoryDTOMapper);
        transactionCategoryController = new TransactionCategoryController(transactionCategoryService);
        objectMapper = new ObjectMapper();
        CurrentUser.currentUser = new UserEntity.UserBuilder("t", "t", "t").id(1).build();
    }

    @Test
    void doGetTest() throws IOException {
        TransactionCategoryEntity transactionCategory = new TransactionCategoryEntity.TransactionCategoryBuilder("t").
                id(1).build();

        Mockito.when(transactionCategoryRepository.findById(1)).thenReturn(transactionCategory);

        Assertions.assertEquals(objectMapper.writeValueAsString(transactionCategoryDTOMapper.mapToDTO(transactionCategory)),
                transactionCategoryController.getTransactionCategoryById(1));

        TransactionCategoryEntity transactionCategory2 = new TransactionCategoryEntity.TransactionCategoryBuilder("t2").
                id(2).neededSum(BigDecimal.valueOf(20)).userId(1).build();

        Mockito.when(transactionCategoryRepository.findCommonCategoriesOrGoalsWithUserId(1)).thenReturn(List.of(transactionCategory, transactionCategory2));

        Assertions.assertEquals(objectMapper.writeValueAsString(List.of(transactionCategoryDTOMapper.mapToDTO(transactionCategory),
                        transactionCategoryDTOMapper.mapToDTO(transactionCategory2))),
                transactionCategoryController.getAllTransactionCategories());

        Mockito.when(transactionCategoryRepository.findAllGoalsWithUserId(1)).thenReturn(List.of(transactionCategory2));

        Assertions.assertEquals(objectMapper.writeValueAsString(List.of(transactionCategoryDTOMapper.mapToDTO(transactionCategory2))),
                transactionCategoryController.getAllTransactionCategoriesByUserId(1));

        Mockito.when(transactionCategoryRepository.findByName("t")).thenReturn(transactionCategory);

        Assertions.assertEquals(objectMapper.writeValueAsString(transactionCategory), transactionCategoryController.getAllTransactionCategoriesByName("t"));

        Mockito.when(transactionCategoryRepository.findById(50)).thenReturn(null);

        Assertions.assertEquals("", transactionCategoryController.getTransactionCategoryById(50));
    }

    @Test
    void doPostTest() throws IOException {
        TransactionCategoryEntity transactionCategory = new TransactionCategoryEntity.TransactionCategoryBuilder("t").
                id(1).neededSum(BigDecimal.valueOf(10.10)).userId(1).build();
        transactionCategory = transactionCategoryDTOMapper.mapToEntity(transactionCategoryDTOMapper.mapToDTO(transactionCategory));

        Mockito.when(transactionCategoryRepository.save(transactionCategory)).thenReturn(transactionCategory);

        Assertions.assertEquals(objectMapper.writeValueAsString(transactionCategoryDTOMapper.mapToDTO(transactionCategory)),
                transactionCategoryController.createTransactionCategory(transactionCategoryDTOMapper.mapToDTO(transactionCategory)));
    }

    @Test
    void doPutTest() throws IOException {
        TransactionCategoryEntity transactionCategory = new TransactionCategoryEntity.TransactionCategoryBuilder("t").
                id(1).neededSum(BigDecimal.valueOf(10.10)).userId(1).build();

        Mockito.doNothing().when(transactionCategoryRepository).save(transactionCategory);
        Mockito.when(transactionCategoryRepository.findById(1)).thenReturn(transactionCategory);

        Assertions.assertEquals(objectMapper.writeValueAsString(transactionCategoryDTOMapper.mapToDTO(transactionCategory)),
                transactionCategoryController.updateTransactionCategory(1, transactionCategoryDTOMapper.mapToDTO(transactionCategory)));

        TransactionCategoryEntity transactionCategory2 = new TransactionCategoryEntity.TransactionCategoryBuilder("t2").
                id(50).neededSum(BigDecimal.valueOf(20)).userId(1).build();

        Mockito.doNothing().when(transactionCategoryRepository).save(transactionCategory2);
        Mockito.when(transactionCategoryRepository.findById(50)).thenReturn(null);

        Assertions.assertEquals("", transactionCategoryController.updateTransactionCategory(50, transactionCategoryDTOMapper.mapToDTO(transactionCategory2)));
    }

    @Test
    void doDeleteTest() {
        TransactionCategoryEntity transactionCategory = new TransactionCategoryEntity.TransactionCategoryBuilder("t").
                id(1).neededSum(BigDecimal.valueOf(10.10)).userId(1).build();

        Mockito.when(transactionCategoryRepository.findById(1)).thenReturn(transactionCategory);
        Mockito.doNothing().when(transactionCategoryRepository).delete(transactionCategory);

        Assertions.assertEquals("TransactionCategory deleted!", transactionCategoryController.deleteTransactionCategoryById(1));

        Mockito.when(transactionCategoryRepository.findById(50)).thenReturn(null);

        Assertions.assertEquals("TransactionCategory NOT found!", transactionCategoryController.deleteTransactionCategoryById(50));
    }
}