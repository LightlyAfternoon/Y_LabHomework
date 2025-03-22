package org.example.command;

import liquibase.exception.LiquibaseException;
import org.example.CurrentUser;
import org.example.model.*;
import org.example.repository.MonthlyBudgetRepository;
import org.example.repository.TransactionCategoryRepository;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class CommandClassTest {
    UserRepository userRepository;
    MonthlyBudgetRepository monthlyBudgetRepository;
    TransactionCategoryRepository categoryRepository;
    TransactionRepository transactionRepository;
    CommandClass commandClass;

    @BeforeEach
    void setUp() throws SQLException, LiquibaseException {
        userRepository = Mockito.mock(UserRepository.class);
        monthlyBudgetRepository = Mockito.mock(MonthlyBudgetRepository.class);
        categoryRepository = Mockito.mock(TransactionCategoryRepository.class);
        transactionRepository = Mockito.mock(TransactionRepository.class);

        commandClass = new CommandClass(userRepository, transactionRepository, categoryRepository, monthlyBudgetRepository);

        UserEntity user = new UserEntity();

        user.setEmail("t");
        user.setPassword("t");
        user.setName("t");
        user.setRole(UserRole.USER);
        user.setBlocked(false);

        Mockito.when(userRepository.add(user)).thenReturn(user);

        user = userRepository.add(user);

        CurrentUser.currentUser = user;
    }

    @Test
    void getLoggedInUserRoleTest() throws SQLException, LiquibaseException {
        String emailAndPassword = "t\nt";
        InputStream in = new ByteArrayInputStream(emailAndPassword.getBytes());
        System.setIn(in);

        Mockito.when(userRepository.findUserWithEmailAndPassword("t", "t")).thenReturn(CurrentUser.currentUser);

        try {
            Assertions.assertEquals(UserRole.USER, commandClass.getLoggedInUserRole());

            emailAndPassword = "t2\nt";
            in = new ByteArrayInputStream(emailAndPassword.getBytes());
            System.setIn(in);

            Mockito.when(userRepository.findUserWithEmailAndPassword("t2", "t")).thenReturn(null);

            Assertions.assertNull(commandClass.getLoggedInUserRole());

            emailAndPassword = "t\nt2";
            in = new ByteArrayInputStream(emailAndPassword.getBytes());
            System.setIn(in);

            Mockito.when(userRepository.findUserWithEmailAndPassword("t", "t2")).thenReturn(null);

            Assertions.assertNull(commandClass.getLoggedInUserRole());
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllUsersTest() throws SQLException, LiquibaseException {
        List<UserEntity> userEntities = List.of(CurrentUser.currentUser);
        StringBuilder output = new StringBuilder();

        for (UserEntity userEntity : userEntities){
            output.append(userEntity).append("\n");
        }

        Mockito.when(userRepository.findAll()).thenReturn(userEntities);

        Assertions.assertEquals(output.toString(), commandClass.getAllUsers());
    }

    @Test
    void getRegisteredUserTest() throws SQLException, LiquibaseException {
        String emailAndPassword = "t\nt\nt";
        InputStream in = new ByteArrayInputStream(emailAndPassword.getBytes());
        System.setIn(in);

        UserEntity user2 = new UserEntity();
        user2.setEmail("t");
        user2.setPassword("t");
        user2.setName("t");
        user2.setRole(UserRole.USER);
        user2.setBlocked(false);

        Mockito.when(userRepository.add(user2)).thenReturn(null);

        Assertions.assertNull(commandClass.getRegisteredUser());

        emailAndPassword = "t2\nt\nt2";
        in = new ByteArrayInputStream(emailAndPassword.getBytes());
        System.setIn(in);

        user2 = new UserEntity();
        user2.setEmail("t2");
        user2.setPassword("t");
        user2.setName("t2");
        user2.setRole(UserRole.USER);
        user2.setBlocked(false);

        Mockito.when(userRepository.add(user2)).thenReturn(user2);

        Assertions.assertEquals(user2, commandClass.getRegisteredUser());
    }

    @Test
    void addBudgetTest() throws SQLException, LiquibaseException {
        String budget = "10,2";
        InputStream in = new ByteArrayInputStream(budget.getBytes());
        System.setIn(in);

        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser.getId());
        monthlyBudgetEntity.setSum(BigDecimal.valueOf(10.2));

        Mockito.when(monthlyBudgetRepository.findByDateAndUserId(monthlyBudgetEntity.getDate(), CurrentUser.currentUser.getId())).thenReturn(monthlyBudgetEntity);

        monthlyBudgetEntity = commandClass.addBudget();

        Assertions.assertEquals(BigDecimal.valueOf(10.2), monthlyBudgetEntity.getSum());
        int id = monthlyBudgetEntity.getId();

        budget = "312,5";
        in = new ByteArrayInputStream(budget.getBytes());
        System.setIn(in);

        monthlyBudgetEntity.setSum(BigDecimal.valueOf(312.5));

        Mockito.when(monthlyBudgetRepository.findByDateAndUserId(monthlyBudgetEntity.getDate(), CurrentUser.currentUser.getId())).thenReturn(monthlyBudgetEntity);

        monthlyBudgetEntity = commandClass.addBudget();
        int id2 = monthlyBudgetEntity.getId();

        Assertions.assertNotEquals(BigDecimal.valueOf(10.2), monthlyBudgetEntity.getSum());
        Assertions.assertEquals(BigDecimal.valueOf(312.5), monthlyBudgetEntity.getSum());
        Assertions.assertEquals(id2, id);
    }

    @Test
    void addGoalTest() throws SQLException, LiquibaseException {
        String goal = "t\n10,2";
        InputStream in = new ByteArrayInputStream(goal.getBytes());
        System.setIn(in);

        TransactionCategoryEntity category = new TransactionCategoryEntity(0, CurrentUser.currentUser.getId());
        category.setName("t");
        category.setNeededSum(BigDecimal.valueOf(10.2));

        Mockito.when(categoryRepository.add(category)).thenReturn(category);

        category = commandClass.addGoal();

        Assertions.assertEquals(BigDecimal.valueOf(10.2), category.getNeededSum());
        int id = category.getId();

        goal = "t\n312,5";
        in = new ByteArrayInputStream(goal.getBytes());
        System.setIn(in);

        category.setNeededSum(BigDecimal.valueOf(312.5));

        category = commandClass.addGoal();

        Assertions.assertNotEquals(BigDecimal.valueOf(10.2), category.getNeededSum());
        Assertions.assertEquals(BigDecimal.valueOf(312.5), category.getNeededSum());

        goal = "t\n10,2";
        in = new ByteArrayInputStream(goal.getBytes());
        System.setIn(in);

        category.setNeededSum(BigDecimal.valueOf(10.2));

        category = commandClass.addGoal();
        int id2 = category.getId();

        Assertions.assertEquals(id2, id);
    }

    @Test
    void addTransactionTest() throws SQLException, LiquibaseException, ParseException {
        TransactionCategoryEntity category = new TransactionCategoryEntity();
        category.setName("tt");
        try {
            Mockito.when(categoryRepository.add(category)).thenReturn(category);
            categoryRepository.add(category);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        String transaction = "10,2\ntt\n2021-02-10\ns";
        InputStream in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());
        transactionEntity.setSum(BigDecimal.valueOf(10.2));
        transactionEntity.setCategoryId(category.getId());
        transactionEntity.setDate(new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-10").getTime()));
        transactionEntity.setDescription("s");

        Mockito.when(categoryRepository.findCommonCategoriesOrGoalsWithUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(category));
        Mockito.when(categoryRepository.findByName("tt")).thenReturn(category);
        Mockito.when(transactionRepository.add(transactionEntity)).thenReturn(transactionEntity);

        transactionEntity = commandClass.addTransaction();

        Assertions.assertEquals(BigDecimal.valueOf(10.2), transactionEntity.getSum());
        int id = transactionEntity.getId();

        transaction = "12,2\ns\n\n ";
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());
        transactionEntity.setSum(BigDecimal.valueOf(12.2));
        transactionEntity.setCategoryId(0);
        transactionEntity.setDate(new Date(new SimpleDateFormat("yyyy-MM-dd").parse(new Date(System.currentTimeMillis()).toString()).getTime()));
        transactionEntity.setDescription(" ");

        Mockito.when(transactionRepository.add(transactionEntity)).thenReturn(transactionEntity);

        transactionEntity = commandClass.addTransaction();

        Assertions.assertNotEquals(BigDecimal.valueOf(10.2), transactionEntity.getSum());
        Assertions.assertEquals(BigDecimal.valueOf(12.2), transactionEntity.getSum());

        transaction = "10,2\ntt\n2021-02-10\ns";
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());
        transactionEntity.setSum(BigDecimal.valueOf(10.2));
        transactionEntity.setCategoryId(category.getId());
        transactionEntity.setDate(new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-10").getTime()));
        transactionEntity.setDescription("s");

        Mockito.when(transactionRepository.add(transactionEntity)).thenReturn(transactionEntity);

        transactionEntity = commandClass.addTransaction();
        int id2 = transactionEntity.getId();

        Assertions.assertEquals(id2, id);
    }

    @Test
    void deleteAccountTest() throws SQLException, LiquibaseException {
        Mockito.when(userRepository.delete(CurrentUser.currentUser)).thenReturn(true);

        Assertions.assertTrue(commandClass.deleteAccount());
    }

    @Test
    void getTransactionsTest() throws SQLException, LiquibaseException {
        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());
        Date date = new Date(System.currentTimeMillis());

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategoryId(0);
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        try {
            Mockito.when(transactionRepository.add(transactionEntity)).thenReturn(transactionEntity);

            transactionEntity = transactionRepository.add(transactionEntity);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        List<TransactionEntity> transactionEntities = List.of(transactionEntity);
        StringBuilder output = new StringBuilder();

        for (TransactionEntity transaction : transactionEntities){
            output.append(transaction).append("\n");
        }

        Mockito.when(transactionRepository.findAllWithUser(CurrentUser.currentUser.getId())).thenReturn(transactionEntities);

        Assertions.assertEquals(output.toString(), commandClass.getTransactions());
    }

    @Test
    void filterTransactionsTest() throws SQLException, LiquibaseException {
        TransactionCategoryEntity category = new TransactionCategoryEntity();

        category.setName("tt");

        try {
            Mockito.when(categoryRepository.add(category)).thenReturn(category);
            categoryRepository.add(category);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Date date = new Date(System.currentTimeMillis());
        Date date2 = new Date(System.currentTimeMillis() + 86_400_000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = new Date(simpleDateFormat.parse(date.toString()).getTime());
            date2 = new Date(simpleDateFormat.parse(date2.toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        String filter = date + "\ntt\nPos";
        InputStream in = new ByteArrayInputStream(filter.getBytes());
        System.setIn(in);

        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());

        transactionEntity.setSum(BigDecimal.valueOf(10.10));
        transactionEntity.setCategoryId(category.getId());
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        TransactionEntity transactionEntity2 = new TransactionEntity(CurrentUser.currentUser.getId());

        transactionEntity2.setSum(BigDecimal.valueOf(20.0));
        transactionEntity2.setCategoryId(category.getId());
        transactionEntity2.setDate(date2);
        transactionEntity2.setDescription("t2");

        UserEntity user2 = new UserEntity();

        user2.setEmail("t2");
        user2.setPassword("t2");
        user2.setName("t2");
        user2.setBlocked(false);

        TransactionEntity transactionEntity3 = new TransactionEntity(user2.getId());

        transactionEntity3.setSum(BigDecimal.valueOf(30.3));
        transactionEntity3.setCategoryId(category.getId());
        transactionEntity3.setDate(date);
        transactionEntity3.setDescription("t3");

        TransactionEntity transactionEntity4 = new TransactionEntity(CurrentUser.currentUser.getId());

        transactionEntity4.setSum(BigDecimal.valueOf(-10.10));
        transactionEntity4.setCategoryId(0);
        transactionEntity4.setDate(date2);
        transactionEntity4.setDescription(null);

        Mockito.when(categoryRepository.findCommonCategoriesOrGoalsWithUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(category));
        Mockito.when(categoryRepository.findByName("tt")).thenReturn(category);

        try {
            Mockito.when(transactionRepository.add(transactionEntity)).thenReturn(transactionEntity);
            Mockito.when(transactionRepository.add(transactionEntity2)).thenReturn(transactionEntity2);
            Mockito.when(transactionRepository.add(transactionEntity3)).thenReturn(transactionEntity3);
            Mockito.when(transactionRepository.add(transactionEntity4)).thenReturn(transactionEntity4);

            transactionEntity = transactionRepository.add(transactionEntity);
            transactionEntity2 = transactionRepository.add(transactionEntity2);
            transactionEntity3 = transactionRepository.add(transactionEntity3);
            transactionEntity4 = transactionRepository.add(transactionEntity4);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        List<TransactionEntity> transactionEntities = List.of(transactionEntity);

        Mockito.when(transactionRepository.findAllWithDateAndCategoryIdAndTypeAndUserId(date, category.getId(), "Pos", CurrentUser.currentUser.getId())).thenReturn(transactionEntities);

        String outputReturned = commandClass.filterTransactions();

        StringBuilder output = new StringBuilder();

        for (TransactionEntity entity : transactionEntities) {
            output.append(entity).append("\n");
        }

        Assertions.assertEquals(output.toString(), outputReturned);

        filter = date2 + "\n \n ";
        in = new ByteArrayInputStream(filter.getBytes());
        System.setIn(in);

        transactionEntities = List.of(transactionEntity2, transactionEntity4);

        Mockito.when(transactionRepository.findAllWithDateAndCategoryIdAndTypeAndUserId(date2, 0, " ", CurrentUser.currentUser.getId())).thenReturn(transactionEntities);

        outputReturned = commandClass.filterTransactions();

        output = new StringBuilder();

        for (TransactionEntity entity : transactionEntities) {
            output.append(entity).append("\n");
        }

        Assertions.assertEquals(output.toString(), outputReturned);
    }

    @Test
    void editTransactionTest() throws SQLException, LiquibaseException {
        MonthlyBudgetEntity budget = new MonthlyBudgetEntity(CurrentUser.currentUser.getId());

        budget.setSum(BigDecimal.valueOf(12));

        try {
            Mockito.when(monthlyBudgetRepository.add(budget)).thenReturn(budget);

            monthlyBudgetRepository.add(budget);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        TransactionCategoryEntity category = new TransactionCategoryEntity();
        category.setName("tt");
        try {
            Mockito.when(categoryRepository.add(category)).thenReturn(category);

            categoryRepository.add(category);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        String transaction = "10,2\ntt\n\ns";
        InputStream in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());

        Date date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionEntity.setSum(BigDecimal.valueOf(10.2));
        transactionEntity.setCategoryId(category.getId());
        transactionEntity.setDate(date);
        transactionEntity.setDescription("s");

        Mockito.when(categoryRepository.findCommonCategoriesOrGoalsWithUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(category));
        Mockito.when(categoryRepository.findByName("tt")).thenReturn(category);
        Mockito.when(transactionRepository.add(transactionEntity)).thenReturn(transactionEntity);

        transactionEntity = commandClass.addTransaction();
        int id = transactionEntity.getId();

        transaction = id + "\n12,2\n \n ";
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        Mockito.when(transactionRepository.findById(id)).thenReturn(transactionEntity);
        Mockito.when(categoryRepository.findCommonCategoriesOrGoalsWithUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(category));
        Mockito.when(categoryRepository.findByName("tt")).thenReturn(category);
        Mockito.doNothing().when(transactionRepository).update(transactionEntity);

        Assertions.assertTrue(commandClass.editTransaction());

        transaction = "50\n12,2\n \n ";
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        id = 50;

        Mockito.when(transactionRepository.findById(id)).thenReturn(null);

        Assertions.assertFalse(commandClass.editTransaction());
    }

    @Test
    void deleteTransactionTest() throws SQLException, LiquibaseException {
        TransactionCategoryEntity category = new TransactionCategoryEntity();
        category.setName("tt");
        try {
            Mockito.when(categoryRepository.add(category)).thenReturn(category);

            categoryRepository.add(category);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        String transaction = "10,2\ntt\n\ns";
        InputStream in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());
        Date date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionEntity.setSum(BigDecimal.valueOf(10.2));
        transactionEntity.setCategoryId(category.getId());
        transactionEntity.setDate(date);
        transactionEntity.setDescription("s");

        Mockito.when(transactionRepository.findById(1)).thenReturn(transactionEntity);
        Mockito.when(transactionRepository.delete(transactionEntity)).thenReturn(true);

        transaction = String.valueOf(1);
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        Assertions.assertTrue(commandClass.deleteTransaction());

        transaction = String.valueOf(50);
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        Mockito.when(transactionRepository.findById(50)).thenReturn(null);

        Assertions.assertFalse(commandClass.deleteTransaction());
    }

    @Test
    void getAllUserGoalsTest() throws SQLException, LiquibaseException {
        String goal = "t\n10,2";
        InputStream in = new ByteArrayInputStream(goal.getBytes());
        System.setIn(in);

        TransactionCategoryEntity goalEntity = new TransactionCategoryEntity(0, CurrentUser.currentUser.getId());
        goalEntity.setName("t");
        goalEntity.setNeededSum(BigDecimal.valueOf(10.2));

        Mockito.when(categoryRepository.add(goalEntity)).thenReturn(goalEntity);

        goalEntity = commandClass.addGoal();

        goal = "t2\n312,5";
        in = new ByteArrayInputStream(goal.getBytes());
        System.setIn(in);

        TransactionCategoryEntity goalEntity2 = new TransactionCategoryEntity(0, CurrentUser.currentUser.getId());
        goalEntity2.setName("t2");
        goalEntity2.setNeededSum(BigDecimal.valueOf(312.5));

        Mockito.when(categoryRepository.add(goalEntity2)).thenReturn(goalEntity2);

        goalEntity2 = commandClass.addGoal();

        List<TransactionCategoryEntity> categoryEntities = List.of(goalEntity, goalEntity2);

        Mockito.when(categoryRepository.findAllGoalsWithUserId(CurrentUser.currentUser.getId())).thenReturn(categoryEntities);
        Mockito.when(transactionRepository.findAllWithUser(CurrentUser.currentUser.getId())).thenReturn(new ArrayList<>());

        String outputReturned = commandClass.getAllUserGoals();

        StringBuilder output = new StringBuilder();

        for (TransactionCategoryEntity entity : categoryEntities) {
            output.append(entity).append(" Необходимая сумма = ").append(0).append("/").append(entity.getNeededSum()).append("\n");
        }

        Assertions.assertEquals(output.toString(), outputReturned);
    }

    @Test
    void getCurrentBalanceTest() throws SQLException, LiquibaseException {
        TransactionCategoryEntity category = new TransactionCategoryEntity();
        category.setName("tt");
        try {
            Mockito.when(categoryRepository.add(category)).thenReturn(category);

            categoryRepository.add(category);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        String transaction = "10,2\ntt\n2021-02-10\ns";
        InputStream in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());

        Date date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = new Date(simpleDateFormat.parse("2021-02-10").getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionEntity.setSum(BigDecimal.valueOf(10.2));
        transactionEntity.setCategoryId(category.getId());
        transactionEntity.setDate(date);
        transactionEntity.setDescription("s");

        Mockito.when(categoryRepository.findCommonCategoriesOrGoalsWithUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(category));
        Mockito.when(categoryRepository.findByName("tt")).thenReturn(category);
        Mockito.when(transactionRepository.add(transactionEntity)).thenReturn(transactionEntity);

        commandClass.addTransaction();

        transaction = "32,5\ns\n\n ";
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        TransactionEntity transactionEntity2 = new TransactionEntity(CurrentUser.currentUser.getId());

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionEntity2.setSum(BigDecimal.valueOf(32.5));
        transactionEntity2.setCategoryId(0);
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription(" ");

        Mockito.when(transactionRepository.add(transactionEntity)).thenReturn(transactionEntity);

        commandClass.addTransaction();

        Mockito.when(transactionRepository.findAllWithUser(CurrentUser.currentUser.getId())).thenReturn(List.of(transactionEntity, transactionEntity2));

        Assertions.assertEquals(BigDecimal.valueOf(42.7), commandClass.getCurrentBalance());
    }

    @Test
    void getIncomeForPeriodTest() throws SQLException, LiquibaseException {
        TransactionCategoryEntity category = new TransactionCategoryEntity();
        category.setName("tt");
        try {
            Mockito.when(categoryRepository.add(category)).thenReturn(category);

            categoryRepository.add(category);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        String transaction = "10,2\ntt\n2021-02-10\ns";
        InputStream in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());

        Date date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = new Date(simpleDateFormat.parse("2021-02-10").getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionEntity.setSum(BigDecimal.valueOf(10.2));
        transactionEntity.setCategoryId(category.getId());
        transactionEntity.setDate(date);
        transactionEntity.setDescription("s");

        Mockito.when(categoryRepository.findCommonCategoriesOrGoalsWithUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(category));
        Mockito.when(categoryRepository.findByName("tt")).thenReturn(category);
        Mockito.when(transactionRepository.add(transactionEntity)).thenReturn(transactionEntity);

        Mockito.when(transactionRepository.add(transactionEntity)).thenReturn(transactionEntity);

        commandClass.addTransaction();

        transaction = "-32,5\ns\n\n ";
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        TransactionEntity transactionEntity2 = new TransactionEntity(CurrentUser.currentUser.getId());

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionEntity2.setSum(BigDecimal.valueOf(-32.5));
        transactionEntity2.setCategoryId(0);
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription(" ");

        Mockito.when(transactionRepository.add(transactionEntity)).thenReturn(transactionEntity);

        commandClass.addTransaction();

        String filter = "2000-12-21\n2025-12-10";
        in = new ByteArrayInputStream(filter.getBytes());
        System.setIn(in);

        Scanner scanner = new Scanner(System.in);
        String text = scanner.nextLine();
        Date from;
        try {
            from = new Date(simpleDateFormat.parse(text).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        text = scanner.nextLine();
        Date to;
        try {
            to = new Date(simpleDateFormat.parse(text).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Mockito.when(transactionRepository.findAllWithUser(CurrentUser.currentUser.getId())).thenReturn(List.of(transactionEntity, transactionEntity2));

        Assertions.assertEquals(BigDecimal.valueOf(10.2), commandClass.getIncomeForPeriod(from, to));
    }

    @Test
    void getExpenseForPeriodTest() throws SQLException, LiquibaseException {
        TransactionCategoryEntity category = new TransactionCategoryEntity();
        category.setName("tt");
        try {
            Mockito.when(categoryRepository.add(category)).thenReturn(category);

            categoryRepository.add(category);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        String transaction = "10,2\ntt\n2021-02-10\ns";
        InputStream in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());

        Date date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = new Date(simpleDateFormat.parse("2021-02-10").getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionEntity.setSum(BigDecimal.valueOf(10.2));
        transactionEntity.setCategoryId(category.getId());
        transactionEntity.setDate(date);
        transactionEntity.setDescription("s");

        Mockito.when(categoryRepository.findCommonCategoriesOrGoalsWithUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(category));
        Mockito.when(categoryRepository.findByName("tt")).thenReturn(category);
        Mockito.when(transactionRepository.add(transactionEntity)).thenReturn(transactionEntity);

        Mockito.when(transactionRepository.add(transactionEntity)).thenReturn(transactionEntity);

        commandClass.addTransaction();

        transaction = "-32,5\ns\n\n ";
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        TransactionEntity transactionEntity2 = new TransactionEntity(CurrentUser.currentUser.getId());

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionEntity2.setSum(BigDecimal.valueOf(-32.5));
        transactionEntity2.setCategoryId(0);
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription(" ");

        Mockito.when(transactionRepository.add(transactionEntity)).thenReturn(transactionEntity);

        commandClass.addTransaction();

        String filter = "2000-12-21\n2025-12-10";
        in = new ByteArrayInputStream(filter.getBytes());
        System.setIn(in);

        Scanner scanner = new Scanner(System.in);
        String text = scanner.nextLine();
        Date from;
        try {
            from = new Date(simpleDateFormat.parse(text).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        text = scanner.nextLine();
        Date to;
        try {
            to = new Date(simpleDateFormat.parse(text).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Mockito.when(transactionRepository.findAllWithUser(CurrentUser.currentUser.getId())).thenReturn(List.of(transactionEntity, transactionEntity2));

        Assertions.assertEquals(BigDecimal.valueOf(32.5), commandClass.getExpenseForPeriod(from, to));
    }

    @Test
    void getCategoryExpensesTest() throws SQLException, LiquibaseException {
        TransactionCategoryEntity category = new TransactionCategoryEntity();
        category.setName("tt");
        try {
            Mockito.when(categoryRepository.add(category)).thenReturn(category);

            categoryRepository.add(category);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        TransactionEntity transactionEntity = new TransactionEntity(CurrentUser.currentUser.getId());
        Date date = new Date(System.currentTimeMillis());

        transactionEntity.setSum(BigDecimal.valueOf(-10.10));
        transactionEntity.setCategoryId(category.getId());
        transactionEntity.setDate(date);
        transactionEntity.setDescription("t");

        try {
            Mockito.when(transactionRepository.add(transactionEntity)).thenReturn(transactionEntity);

            transactionRepository.add(transactionEntity);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        TransactionEntity transactionEntity2 = new TransactionEntity(CurrentUser.currentUser.getId());

        transactionEntity2.setSum(BigDecimal.valueOf(-20.6));
        transactionEntity2.setCategoryId(category.getId());
        transactionEntity2.setDate(date);
        transactionEntity2.setDescription("t2");

        try {
            Mockito.when(transactionRepository.add(transactionEntity2)).thenReturn(transactionEntity2);

            transactionRepository.add(transactionEntity2);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        TransactionEntity transactionEntity3 = new TransactionEntity(CurrentUser.currentUser.getId());

        transactionEntity3.setSum(BigDecimal.valueOf(4));
        transactionEntity3.setCategoryId(category.getId());
        transactionEntity3.setDate(date);
        transactionEntity3.setDescription("t3");

        try {
            Mockito.when(transactionRepository.add(transactionEntity3)).thenReturn(transactionEntity3);

            transactionRepository.add(transactionEntity3);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        TransactionEntity transactionEntity4 = new TransactionEntity(CurrentUser.currentUser.getId());

        transactionEntity4.setSum(BigDecimal.valueOf(-20.6));
        transactionEntity4.setCategoryId(0);
        transactionEntity4.setDate(date);
        transactionEntity4.setDescription("t4");

        try {
            Mockito.when(transactionRepository.add(transactionEntity4)).thenReturn(transactionEntity4);

            transactionRepository.add(transactionEntity4);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        Mockito.when(categoryRepository.findAll()).thenReturn(List.of(category));
        Mockito.when(transactionRepository.findAllWithDateAndCategoryIdAndTypeAndUserId(null, category.getId(), "Neg", CurrentUser.currentUser.getId())).thenReturn(List.of(transactionEntity, transactionEntity2, transactionEntity4));

        Assertions.assertEquals(category.getName() + ": 51.3\n", commandClass.getCategoryExpenses());
    }
}