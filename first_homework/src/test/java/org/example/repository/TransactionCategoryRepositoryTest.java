package org.example.repository;

import org.example.CurrentUser;
import org.example.model.TransactionCategoryEntity;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

class TransactionCategoryRepositoryTest {
    UserEntity userEntity;
    TransactionCategoryRepository categoryRepository = new TransactionCategoryRepository();

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();

        userEntity.setName("t");
        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        CurrentUser.currentUser = userEntity;

        for (TransactionCategoryEntity category : categoryRepository.findAll()) {
            categoryRepository.delete(category);
        }
    }

    @Test
    void addTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        categoryEntity = categoryRepository.add(categoryEntity);

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity();

        categoryEntity2.setName("t");

        categoryEntity2 = categoryRepository.add(categoryEntity2);

        Assertions.assertEquals(categoryEntity, categoryEntity2);
        Assertions.assertEquals(categoryEntity.getUuid(), categoryEntity2.getUuid());

        categoryEntity.setName("t0");

        Assertions.assertNotEquals(categoryEntity, categoryEntity2);

        TransactionCategoryEntity categoryEntity3 = new TransactionCategoryEntity();

        categoryEntity3.setName("t3");

        categoryEntity3 = categoryRepository.add(categoryEntity3);

        Assertions.assertNotEquals(categoryEntity, categoryEntity3);
    }

    @Test
    void addGoalTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity(CurrentUser.currentUser);

        categoryEntity.setName("t");
        categoryEntity.setNeededSum(BigDecimal.valueOf(10.0));

        categoryEntity = categoryRepository.addGoal(categoryEntity);

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity(CurrentUser.currentUser);

        categoryEntity2.setName("t");
        categoryEntity2.setNeededSum(BigDecimal.valueOf(10.00));

        categoryEntity2 = categoryRepository.addGoal(categoryEntity2);

        Assertions.assertEquals(categoryEntity, categoryEntity2);
        Assertions.assertEquals(categoryEntity.getUuid(), categoryEntity2.getUuid());

        categoryEntity.setName("t0");

        Assertions.assertNotEquals(categoryEntity, categoryEntity2);

        TransactionCategoryEntity categoryEntity3 = new TransactionCategoryEntity(CurrentUser.currentUser);

        categoryEntity3.setName("t3");
        categoryEntity2.setNeededSum(BigDecimal.valueOf(30.3));

        categoryEntity3 = categoryRepository.addGoal(categoryEntity3);

        Assertions.assertNotEquals(categoryEntity, categoryEntity3);
    }

    @Test
    void findByIdTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        categoryEntity = categoryRepository.add(categoryEntity);

        Assertions.assertEquals(categoryRepository.findById(categoryEntity.getUuid()), categoryEntity);

        categoryEntity.setName("t0");

        Assertions.assertNotEquals(categoryRepository.findById(categoryEntity.getUuid()), categoryEntity);

        Assertions.assertNull(categoryRepository.findById(UUID.randomUUID()));
    }

    @Test
    void findByNameTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        categoryRepository.add(categoryEntity);

        Assertions.assertEquals(categoryRepository.findByName("t"), categoryEntity);

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity();

        categoryEntity2.setName("t0");

        categoryRepository.add(categoryEntity2);

        Assertions.assertNotEquals(categoryRepository.findByName("t"), categoryEntity2);
        Assertions.assertEquals(categoryRepository.findByName("t0"), categoryEntity2);
        Assertions.assertNull(categoryRepository.findByName("t2"));
    }

    @Test
    void findAllTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity();

        categoryEntity2.setName("t2");

        TransactionCategoryEntity categoryEntity3 = new TransactionCategoryEntity();

        categoryEntity3.setName("t3");

        List<TransactionCategoryEntity> categoryEntities = List.of(categoryEntity, categoryEntity2, categoryEntity3);

        categoryRepository.add(categoryEntity);
        categoryRepository.add(categoryEntity2);
        categoryRepository.add(categoryEntity3);

        List<TransactionCategoryEntity> transactionCategoryEntitiesReturned = categoryRepository.findAll();

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);

        TransactionCategoryEntity categoryEntity4 = new TransactionCategoryEntity();

        categoryEntity4.setName("t4");

        categoryEntities = List.of(categoryEntity, categoryEntity2, categoryEntity3, categoryEntity4);
        categoryRepository.add(categoryEntity4);
        transactionCategoryEntitiesReturned = categoryRepository.findAll();

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);

        categoryEntity.setName("t0");

        Assertions.assertNotEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }

    @Test
    void findCommonCategoriesOrGoalsWithUserTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity(CurrentUser.currentUser);

        categoryEntity2.setName("t2");
        categoryEntity2.setNeededSum(BigDecimal.valueOf(20.0));

        TransactionCategoryEntity categoryEntity3 = new TransactionCategoryEntity();

        categoryEntity3.setName("t3");

        TransactionCategoryEntity categoryEntity4 = new TransactionCategoryEntity(CurrentUser.currentUser);

        categoryEntity4.setName("t4");

        List<TransactionCategoryEntity> categoryEntities = List.of(categoryEntity, categoryEntity2, categoryEntity3, categoryEntity4);

        categoryRepository.add(categoryEntity);
        categoryRepository.addGoal(categoryEntity2);
        categoryRepository.add(categoryEntity3);
        categoryRepository.addGoal(categoryEntity4);

        List<TransactionCategoryEntity> transactionCategoryEntitiesReturned = categoryRepository.findCommonCategoriesOrGoalsWithUser(CurrentUser.currentUser);

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }

    @Test
    void updateTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        categoryEntity = categoryRepository.add(categoryEntity);

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity(categoryEntity.getUuid());

        categoryEntity2.setName("t2");

        categoryRepository.update(categoryEntity2);

        Assertions.assertEquals(categoryRepository.findById(categoryEntity.getUuid()), categoryEntity2);

        categoryEntity2.setName("t0");

        Assertions.assertNotEquals(categoryRepository.findById(categoryEntity.getUuid()), categoryEntity2);
    }

    @Test
    void deleteTest() {
        TransactionCategoryEntity categoryEntity = new TransactionCategoryEntity();

        categoryEntity.setName("t");

        TransactionCategoryEntity categoryEntity2 = new TransactionCategoryEntity(categoryEntity.getUuid());

        categoryEntity2.setName("t2");

        TransactionCategoryEntity categoryEntity3 = new TransactionCategoryEntity();

        categoryEntity3.setName("t3");

        List<TransactionCategoryEntity> categoryEntities = List.of(categoryEntity, categoryEntity2, categoryEntity3);

        categoryEntity = categoryRepository.add(categoryEntity);
        categoryRepository.add(categoryEntity2);
        categoryRepository.add(categoryEntity3);

        List<TransactionCategoryEntity> transactionCategoryEntitiesReturned = categoryRepository.findAll();

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);

        categoryRepository.delete(categoryEntity);
        transactionCategoryEntitiesReturned = categoryRepository.findAll();

        Assertions.assertNotEquals(categoryEntities, transactionCategoryEntitiesReturned);

        categoryEntities = List.of(categoryEntity2, categoryEntity3);

        Assertions.assertEquals(categoryEntities, transactionCategoryEntitiesReturned);
    }
}