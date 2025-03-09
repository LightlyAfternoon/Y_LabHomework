package org.example.repository;

import org.example.CurrentUser;
import org.example.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

class MonthlyBudgetRepositoryTest {
    UserEntity userEntity;
    MonthlyBudgetRepository monthlyBudgetRepository = new MonthlyBudgetRepository();

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();

        userEntity.setName("t");
        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        CurrentUser.currentUser = userEntity;

        for (MonthlyBudgetEntity budget : monthlyBudgetRepository.findAll()) {
            monthlyBudgetRepository.delete(budget);
        }
    }

    @Test
    void addTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        monthlyBudgetEntity = monthlyBudgetRepository.add(monthlyBudgetEntity);

        MonthlyBudgetEntity monthlyBudgetEntity2 = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity2.setSum(BigDecimal.valueOf(10.10));

        monthlyBudgetEntity2 = monthlyBudgetRepository.add(monthlyBudgetEntity2);

        Assertions.assertEquals(monthlyBudgetEntity, monthlyBudgetEntity2);
        Assertions.assertEquals(monthlyBudgetEntity.getUuid(), monthlyBudgetEntity2.getUuid());

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(20.0));

        Assertions.assertNotEquals(monthlyBudgetEntity, monthlyBudgetEntity2);

        MonthlyBudgetEntity monthlyBudgetEntity3 = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity3.setSum(BigDecimal.valueOf(10.0));

        monthlyBudgetEntity3 = monthlyBudgetRepository.add(monthlyBudgetEntity3);

        Assertions.assertNotEquals(monthlyBudgetEntity, monthlyBudgetEntity3);
    }

    @Test
    void findByIdTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        monthlyBudgetEntity = monthlyBudgetRepository.add(monthlyBudgetEntity);

        Assertions.assertEquals(monthlyBudgetRepository.findById(monthlyBudgetEntity.getUuid()), monthlyBudgetEntity);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(1.5));

        Assertions.assertNotEquals(monthlyBudgetRepository.findById(monthlyBudgetEntity.getUuid()), monthlyBudgetEntity);

        Assertions.assertNull(monthlyBudgetRepository.findById(UUID.randomUUID()));
    }

    @Test
    void findAllTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        MonthlyBudgetEntity monthlyBudgetEntity2 = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity2.setSum(BigDecimal.valueOf(20.0));

        MonthlyBudgetEntity monthlyBudgetEntity3 = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity3.setSum(BigDecimal.valueOf(30.3));

        List<MonthlyBudgetEntity> transactionEntities = List.of(monthlyBudgetEntity, monthlyBudgetEntity2, monthlyBudgetEntity3);

        monthlyBudgetRepository.add(monthlyBudgetEntity);
        monthlyBudgetRepository.add(monthlyBudgetEntity2);
        monthlyBudgetRepository.add(monthlyBudgetEntity3);

        List<MonthlyBudgetEntity> transactionEntitiesReturned = monthlyBudgetRepository.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        MonthlyBudgetEntity monthlyBudgetEntity4 = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity4.setSum(BigDecimal.valueOf(10.0));

        transactionEntities = List.of(monthlyBudgetEntity, monthlyBudgetEntity2, monthlyBudgetEntity3, monthlyBudgetEntity4);
        monthlyBudgetRepository.add(monthlyBudgetEntity4);
        transactionEntitiesReturned = monthlyBudgetRepository.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(1.1));

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);
    }

    @Test
    void updateTest() {
        Date date = new Date(System.currentTimeMillis());
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser, date);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        monthlyBudgetEntity = monthlyBudgetRepository.add(monthlyBudgetEntity);

        MonthlyBudgetEntity monthlyBudgetEntity2 = new MonthlyBudgetEntity(monthlyBudgetEntity.getUuid(), CurrentUser.currentUser, date);

        monthlyBudgetEntity2.setSum(BigDecimal.valueOf(1.23));

        monthlyBudgetRepository.update(monthlyBudgetEntity2);

        Assertions.assertEquals(monthlyBudgetRepository.findById(monthlyBudgetEntity.getUuid()), monthlyBudgetEntity2);

        monthlyBudgetEntity2.setSum(BigDecimal.valueOf(2.2));

        Assertions.assertNotEquals(monthlyBudgetRepository.findById(monthlyBudgetEntity.getUuid()), monthlyBudgetEntity2);
    }

    @Test
    void deleteTest() {
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser);
        Date date = new Date(System.currentTimeMillis());

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.10));

        MonthlyBudgetEntity monthlyBudgetEntity2 = new MonthlyBudgetEntity(monthlyBudgetEntity.getUuid(), CurrentUser.currentUser, date);

        monthlyBudgetEntity2.setSum(BigDecimal.valueOf(1.23));

        MonthlyBudgetEntity monthlyBudgetEntity3 = new MonthlyBudgetEntity(CurrentUser.currentUser);

        monthlyBudgetEntity3.setSum(BigDecimal.valueOf(30.3));

        List<MonthlyBudgetEntity> transactionEntities = List.of(monthlyBudgetEntity, monthlyBudgetEntity2, monthlyBudgetEntity3);

        monthlyBudgetEntity = monthlyBudgetRepository.add(monthlyBudgetEntity);
        monthlyBudgetRepository.add(monthlyBudgetEntity2);
        monthlyBudgetRepository.add(monthlyBudgetEntity3);

        List<MonthlyBudgetEntity> transactionEntitiesReturned = monthlyBudgetRepository.findAll();

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);

        monthlyBudgetRepository.delete(monthlyBudgetEntity);
        transactionEntitiesReturned = monthlyBudgetRepository.findAll();

        Assertions.assertNotEquals(transactionEntities, transactionEntitiesReturned);

        transactionEntities = List.of(monthlyBudgetEntity2, monthlyBudgetEntity3);

        Assertions.assertEquals(transactionEntities, transactionEntitiesReturned);
    }
}