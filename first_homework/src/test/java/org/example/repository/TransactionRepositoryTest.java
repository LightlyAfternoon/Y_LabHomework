package org.example.repository;

import org.example.CurrentUser;
import org.example.model.TransactionCategoryEntity;
import org.example.model.TransactionEntity;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

class TransactionRepositoryTest {
    UserEntity userEntity;
    TransactionCategoryEntity category;
    TransactionRepository transactionRepository = new TransactionRepository();

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();

        userEntity.setName("t");
        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        CurrentUser.currentUser = userEntity;

        category = new TransactionCategoryEntity();

        category.setName("t");

        for (TransactionEntity transaction : transactionRepository.findAll()) {
            transactionRepository.delete(transaction);
        }
    }

    @Test
    void addTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser);
        Date date = new Date(System.currentTimeMillis());

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(category);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        transactionEntity = transactionRepository.add(transactionEntity);

        TransactionEntity transactionEntity2 = new TransactionEntity(CurrentUser.currentUser);

        transactionEntity2.setSum(BigDecimal.valueOf(10.10));
        transactionEntity2.setCategory(category);
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription("t");

        transactionEntity2 = transactionRepository.add(transactionEntity2);

        Assertions.assertEquals(transactionEntity, transactionEntity2);
        Assertions.assertEquals(transactionEntity.getUuid(), transactionEntity2.getUuid());

        transactionEntity.setSum(BigDecimal.valueOf(20.0));

        Assertions.assertNotEquals(transactionEntity, transactionEntity2);

        TransactionEntity transactionEntity3 = new TransactionEntity(CurrentUser.currentUser);

        transactionEntity3.setSum(BigDecimal.valueOf(10.0));
        transactionEntity3.setCategory(category);
        transactionEntity3.setDate(date);
        transactionEntity3.setDescription("t2");

        transactionEntity3 = transactionRepository.add(transactionEntity3);

        Assertions.assertNotEquals(transactionEntity, transactionEntity3);
    }

    @Test
    void findByIdTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser);
        Date date = new Date(System.currentTimeMillis());

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(category);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        transactionEntity = transactionRepository.add(transactionEntity);

        Assertions.assertEquals(transactionRepository.findById(transactionEntity.getUuid()), transactionEntity);

        transactionEntity.setSum(BigDecimal.valueOf(1.5));

        Assertions.assertNotEquals(transactionRepository.findById(transactionEntity.getUuid()), transactionEntity);

        Assertions.assertNull(transactionRepository.findById(UUID.randomUUID()));
    }

    @Test
    void findAllTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser);
        Date date = new Date(System.currentTimeMillis());

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(category);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        TransactionEntity transactionEntity2 = new TransactionEntity(CurrentUser.currentUser);

        transactionEntity2.setSum(BigDecimal.valueOf(20.0));
        transactionEntity2.setCategory(category);
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription("t2");

        TransactionEntity transactionEntity3 = new TransactionEntity(CurrentUser.currentUser);

        transactionEntity3.setSum(BigDecimal.valueOf(30.3));
        transactionEntity3.setCategory(category);
        transactionEntity3.setDate(date);
        transactionEntity3.setDescription("t3");

        List<TransactionEntity> transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3);

        transactionRepository.add(transactionEntity);
        transactionRepository.add(transactionEntity2);
        transactionRepository.add(transactionEntity3);

        List<TransactionEntity> transactionEntitiesReturned = transactionRepository.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        TransactionEntity transactionEntity4 = new TransactionEntity(CurrentUser.currentUser);

        date = new Date(System.currentTimeMillis());
        transactionEntity4.setSum(BigDecimal.valueOf(10.10));
        transactionEntity4.setCategory(category);
        transactionEntity4.setDate(date);
        transactionEntity4.setDescription("t4");

        transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3, transactionEntity4);
        transactionRepository.add(transactionEntity4);
        transactionEntitiesReturned = transactionRepository.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntity.setDescription("t5");

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);
    }

    @Test
    void updateTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser);
        Date date = new Date(System.currentTimeMillis());

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(category);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        transactionEntity = transactionRepository.add(transactionEntity);

        TransactionEntity transactionEntity2 = new TransactionEntity(transactionEntity.getUuid(), CurrentUser.currentUser);

        date = new Date(System.currentTimeMillis());
        transactionEntity2.setSum(BigDecimal.valueOf(1.23));
        transactionEntity2.setCategory(category);
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription("t2");

        transactionRepository.update(transactionEntity2);

        Assertions.assertEquals(transactionRepository.findById(transactionEntity.getUuid()), transactionEntity2);

        transactionEntity2.setSum(BigDecimal.valueOf(2.2));

        Assertions.assertNotEquals(transactionRepository.findById(transactionEntity.getUuid()), transactionEntity2);
    }

    @Test
    void deleteTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser);
        Date date = new Date(System.currentTimeMillis());

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(category);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        TransactionEntity transactionEntity2 = new TransactionEntity(transactionEntity.getUuid(), CurrentUser.currentUser);

        date = new Date(System.currentTimeMillis());
        transactionEntity2.setSum(BigDecimal.valueOf(1.23));
        transactionEntity2.setCategory(category);
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription("t2");

        TransactionEntity transactionEntity3 = new TransactionEntity(CurrentUser.currentUser);

        transactionEntity3.setSum(BigDecimal.valueOf(30.3));
        transactionEntity3.setCategory(category);
        transactionEntity3.setDate(date);
        transactionEntity3.setDescription("t3");

        List<TransactionEntity> transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3);

        transactionEntity = transactionRepository.add(transactionEntity);
        transactionRepository.add(transactionEntity2);
        transactionRepository.add(transactionEntity3);

        List<TransactionEntity> transactionEntitiesReturned = transactionRepository.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionRepository.delete(transactionEntity);
        transactionEntitiesReturned = transactionRepository.findAll();

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntities = List.of(transactionEntity2, transactionEntity3);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);
    }
}