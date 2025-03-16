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
        Assertions.assertEquals(transactionEntity.getId(), transactionEntity2.getId());

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

        Assertions.assertEquals(transactionRepository.findById(transactionEntity.getId()), transactionEntity);

        transactionEntity.setSum(BigDecimal.valueOf(1.5));

        Assertions.assertNotEquals(transactionRepository.findById(transactionEntity.getId()), transactionEntity);

        Assertions.assertNull(transactionRepository.findById(10));
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
    void findAllWithUserTest() {
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

        List<TransactionEntity> transactionEntitiesReturned = transactionRepository.findAllWithUser(CurrentUser.currentUser);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        TransactionEntity transactionEntity4 = new TransactionEntity(CurrentUser.currentUser);

        date = new Date(System.currentTimeMillis());
        transactionEntity4.setSum(BigDecimal.valueOf(10.10));
        transactionEntity4.setCategory(category);
        transactionEntity4.setDate(date);
        transactionEntity4.setDescription("t4");

        transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3, transactionEntity4);
        transactionRepository.add(transactionEntity4);
        transactionEntitiesReturned = transactionRepository.findAllWithUser(CurrentUser.currentUser);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        UserEntity user = new UserEntity();

        user.setName("t2");
        user.setEmail("t2");
        user.setPassword("t2");
        user.setBlocked(false);

        transactionRepository.delete(transactionEntity);

        transactionEntity = new TransactionEntity(user);

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(category);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        transactionRepository.add(transactionEntity);

        transactionEntitiesReturned = transactionRepository.findAllWithUser(CurrentUser.currentUser);

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);
    }

    @Test
    void findAllWithDateAndCategoryAndTypeAndUserTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser);
        Date date = new Date(System.currentTimeMillis());

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(category);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        TransactionEntity transactionEntity2 = new TransactionEntity(CurrentUser.currentUser);
        Date date2 = new Date(System.currentTimeMillis() + 86_400_000);

        transactionEntity2.setSum(BigDecimal.valueOf(20.0));
        transactionEntity2.setCategory(category);
        transactionEntity2.setDate(date2);
        transactionEntity2.setDescription("t2");

        UserEntity user = new UserEntity();

        user.setName("t2");
        user.setEmail("t2");
        user.setPassword("t2");
        user.setBlocked(false);

        TransactionEntity transactionEntity3 = new TransactionEntity(user);

        transactionEntity3.setSum(BigDecimal.valueOf(30.3));
        transactionEntity3.setCategory(category);
        transactionEntity3.setDate(date);
        transactionEntity3.setDescription("t3");

        TransactionEntity transactionEntity4 = new TransactionEntity(CurrentUser.currentUser);

        transactionEntity4.setSum(BigDecimal.valueOf(-10.10));
        transactionEntity4.setCategory(null);
        transactionEntity4.setDate(date2);
        transactionEntity4.setDescription(null);

        List<TransactionEntity> transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity4);

        transactionRepository.add(transactionEntity);
        transactionRepository.add(transactionEntity2);
        transactionRepository.add(transactionEntity3);
        transactionRepository.add(transactionEntity4);

        List<TransactionEntity> transactionEntitiesReturned = transactionRepository.findAllWithDateAndCategoryAndTypeAndUser(null, null, null, CurrentUser.currentUser);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntities = List.of(transactionEntity, transactionEntity2, transactionEntity3, transactionEntity4);

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntitiesReturned = transactionRepository.findAllWithDateAndCategoryAndTypeAndUser(date, null, null, CurrentUser.currentUser);
        transactionEntities = List.of(transactionEntity);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntitiesReturned = transactionRepository.findAllWithDateAndCategoryAndTypeAndUser(null, category, null, CurrentUser.currentUser);
        transactionEntities = List.of(transactionEntity, transactionEntity2);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntitiesReturned = transactionRepository.findAllWithDateAndCategoryAndTypeAndUser(null, null, "Pos", user);
        transactionEntities = List.of(transactionEntity3);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);
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

        TransactionEntity transactionEntity2 = new TransactionEntity(transactionEntity.getId(), CurrentUser.currentUser);

        date = new Date(System.currentTimeMillis());
        transactionEntity2.setSum(BigDecimal.valueOf(1.23));
        transactionEntity2.setCategory(category);
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription("t2");

        transactionRepository.update(transactionEntity2);

        Assertions.assertEquals(transactionRepository.findById(transactionEntity.getId()), transactionEntity2);

        transactionEntity2.setSum(BigDecimal.valueOf(2.2));

        Assertions.assertNotEquals(transactionRepository.findById(transactionEntity.getId()), transactionEntity2);
    }

    @Test
    void deleteTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser);
        Date date = new Date(System.currentTimeMillis());

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(category);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        TransactionEntity transactionEntity2 = new TransactionEntity(transactionEntity.getId(), CurrentUser.currentUser);

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