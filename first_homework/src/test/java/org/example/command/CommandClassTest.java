package org.example.command;

import org.example.CurrentUser;
import org.example.model.*;
import org.example.repository.MonthlyBudgetRepository;
import org.example.repository.TransactionCategoryRepository;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

class CommandClassTest {
    UserEntity user;

    @BeforeEach
    void setUp() {
        UserRepository userRepository = new UserRepository();
        MonthlyBudgetRepository monthlyBudgetRepository = new MonthlyBudgetRepository();
        TransactionCategoryRepository categoryRepository = new TransactionCategoryRepository();
        TransactionRepository transactionRepository = new TransactionRepository();

        for (UserEntity userEntity : userRepository.findAll()){
            userRepository.delete(userEntity);
        }

        for (MonthlyBudgetEntity budget : monthlyBudgetRepository.findAll()){
            monthlyBudgetRepository.delete(budget);
        }

        for (TransactionCategoryEntity category : categoryRepository.findAll()){
            categoryRepository.delete(category);
        }

        for (TransactionEntity transaction : transactionRepository.findAll()){
            transactionRepository.delete(transaction);
        }

        user = new UserEntity();

        user.setName("t");
        user.setEmail("t");
        user.setPassword("t");
        user.setRole(UserRole.USER);
        user.setBlocked(false);

        user = userRepository.add(user);

        CurrentUser.currentUser = user;
    }

    @Test
    void getLoggedInUserRoleTest() {
        String emailAndPassword = "t\nt";
        InputStream in = new ByteArrayInputStream(emailAndPassword.getBytes());
        System.setIn(in);

        Assertions.assertEquals(UserRole.USER, CommandClass.getLoggedInUserRole());

        emailAndPassword = "t2\nt";
        in = new ByteArrayInputStream(emailAndPassword.getBytes());
        System.setIn(in);

        Assertions.assertNull(CommandClass.getLoggedInUserRole());

        emailAndPassword = "t\nt2";
        in = new ByteArrayInputStream(emailAndPassword.getBytes());
        System.setIn(in);

        Assertions.assertNull(CommandClass.getLoggedInUserRole());
    }

    @Test
    void getAllUsersTest() {
        List<UserEntity> userEntities = List.of(CurrentUser.currentUser);
        StringBuilder output = new StringBuilder();

        for (UserEntity userEntity : userEntities){
            output.append(userEntity).append("\n");
        }

        Assertions.assertEquals(output.toString(), CommandClass.getAllUsers());
    }

    @Test
    void registerTest() {
        String emailAndPassword = "t\nt\nt";
        InputStream in = new ByteArrayInputStream(emailAndPassword.getBytes());
        System.setIn(in);

        Assertions.assertEquals("Пользователь с такой почтой уже существует\n", CommandClass.register());

        emailAndPassword = "t2\nt2\nt";
        in = new ByteArrayInputStream(emailAndPassword.getBytes());
        System.setIn(in);

        Assertions.assertEquals("Вы успешно зарегистрировались\n", CommandClass.register());
    }

    @Test
    void addBudgetTest() {
        String budget = "10,2";
        InputStream in = new ByteArrayInputStream(budget.getBytes());
        System.setIn(in);

        MonthlyBudgetEntity monthlyBudgetEntity = CommandClass.addBudget();

        Assertions.assertEquals(BigDecimal.valueOf(10.2), monthlyBudgetEntity.getSum());
        UUID uuid = monthlyBudgetEntity.getUuid();

        budget = "312,5";
        in = new ByteArrayInputStream(budget.getBytes());
        System.setIn(in);

        monthlyBudgetEntity = CommandClass.addBudget();
        UUID uuid2 = monthlyBudgetEntity.getUuid();

        Assertions.assertNotEquals(BigDecimal.valueOf(10.2), monthlyBudgetEntity.getSum());
        Assertions.assertEquals(BigDecimal.valueOf(312.5), monthlyBudgetEntity.getSum());
        Assertions.assertEquals(uuid2, uuid);
    }

    @Test
    void addGoalTest() {
        String goal = "t\n10,2";
        InputStream in = new ByteArrayInputStream(goal.getBytes());
        System.setIn(in);

        TransactionCategoryEntity category = CommandClass.addGoal();

        Assertions.assertEquals(BigDecimal.valueOf(10.2), category.getNeededSum());
        UUID uuid = category.getUuid();

        goal = "t\n312,5";
        in = new ByteArrayInputStream(goal.getBytes());
        System.setIn(in);

        category = CommandClass.addGoal();

        Assertions.assertNotEquals(BigDecimal.valueOf(10.2), category.getNeededSum());
        Assertions.assertEquals(BigDecimal.valueOf(312.5), category.getNeededSum());

        goal = "t\n10,2";
        in = new ByteArrayInputStream(goal.getBytes());
        System.setIn(in);

        category = CommandClass.addGoal();
        UUID uuid2 = category.getUuid();

        Assertions.assertEquals(uuid2, uuid);
    }

    @Test
    void addTransactionTest() {
        TransactionCategoryEntity category = new TransactionCategoryEntity();
        category.setName("tt");
        new TransactionCategoryRepository().add(category);

        String transaction = "10,2\ntt\n2021-02-10\ns";
        InputStream in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        TransactionEntity transactionEntity = CommandClass.addTransaction();

        Assertions.assertEquals(BigDecimal.valueOf(10.2), transactionEntity.getSum());
        UUID uuid = transactionEntity.getUuid();

        transaction = "12,2\ns\n\n ";
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        transactionEntity = CommandClass.addTransaction();

        Assertions.assertNotEquals(BigDecimal.valueOf(10.2), transactionEntity.getSum());
        Assertions.assertEquals(BigDecimal.valueOf(12.2), transactionEntity.getSum());

        transaction = "10,2\ntt\n2021-02-10\ns";
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        transactionEntity = CommandClass.addTransaction();
        UUID uuid2 = transactionEntity.getUuid();

        Assertions.assertEquals(uuid2, uuid);
    }

    @Test
    void deleteAccountTest() {
        Assertions.assertTrue(CommandClass.deleteAccount());
    }

    @Test
    void getTransactionsTest() {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser);
        Date date = new Date(System.currentTimeMillis());

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategory(null);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        transactionEntity = new TransactionRepository().add(transactionEntity);

        List<TransactionEntity> transactionEntities = List.of(transactionEntity);
        StringBuilder output = new StringBuilder();

        for (TransactionEntity transaction : transactionEntities){
            output.append(transaction).append("\n");
        }

        Assertions.assertEquals(output.toString(), CommandClass.getTransactions());
    }
}