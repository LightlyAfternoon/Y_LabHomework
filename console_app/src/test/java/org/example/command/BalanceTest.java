package org.example.command;

import org.example.CurrentUser;
import org.example.command.balance.Balance;
import org.example.command.budget.Budget;
import org.example.command.goal.Goal;
import org.example.command.transaction.Transaction;
import org.example.command.user.User;
import org.example.controller.dto.TransactionCategoryDTO;
import org.example.controller.dto.TransactionDTO;
import org.example.controller.dto.UserDTO;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.example.service.MonthlyBudgetService;
import org.example.service.TransactionCategoryService;
import org.example.service.TransactionService;
import org.example.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

@DisplayName("Tests of class with methods for console input")
class BalanceTest {
    UserService userService;
    MonthlyBudgetService monthlyBudgetService;
    TransactionCategoryService transactionCategoryService;
    TransactionService transactionService;
    Invoker invoker;
    User user;
    Transaction transaction;
    Goal goal;
    Budget budget;
    Balance balance;
    HttpRequestsClass httpRequestsClass;
    UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        monthlyBudgetService = Mockito.mock(MonthlyBudgetService.class);
        transactionCategoryService = Mockito.mock(TransactionCategoryService.class);
        transactionService = Mockito.mock(TransactionService.class);
        httpRequestsClass = Mockito.mock(HttpRequestsClass.class);

        UserEntity userEntity = new UserEntity(1);

        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setName("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        CurrentUser.currentUser = userEntity;

        userDTO = new UserDTO.UserBuilder(CurrentUser.currentUser.getEmail(), CurrentUser.currentUser.getPassword(), CurrentUser.currentUser.getName()).
                id(CurrentUser.currentUser.getId()).role(CurrentUser.currentUser.getRole()).isBlocked(CurrentUser.currentUser.isBlocked()).build();

        invoker = new Invoker();
        user = new User(httpRequestsClass);
        transaction = new Transaction(httpRequestsClass);
        goal = new Goal(httpRequestsClass);
        budget = new Budget(httpRequestsClass);
        balance = new Balance(httpRequestsClass);
    }

    @DisplayName("Test of the method for getting current balance (sum of all transactions positive sum) of current user")
    @Test
    void getCurrentBalanceTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();
        categoryDTO.setName("tt");

        String request = "10,2\ntt\n2021-02-10\ns";
        InputStream in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());

        Date date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = new Date(simpleDateFormat.parse("2021-02-10").getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionDTO.setSum(BigDecimal.valueOf(10.2));
        transactionDTO.setCategoryId(categoryDTO.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("s");

        Mockito.when(transactionCategoryService.findCommonCategoriesOrGoalsByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(categoryDTO));
        Mockito.when(transactionCategoryService.findByName("tt")).thenReturn(categoryDTO);
        Mockito.when(transactionService.add(transactionDTO)).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.addTransaction(BigDecimal.valueOf(10.2), categoryDTO.getId(), date, "s")).thenReturn(transactionDTO);

        request = "32,5\ns\n\n ";
        in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        TransactionDTO transactionDTO2 = new TransactionDTO(CurrentUser.currentUser.getId());

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionDTO2.setSum(BigDecimal.valueOf(32.5));
        transactionDTO2.setCategoryId(0);
        transactionDTO2.setDate(date);
        transactionDTO2.setDescription(" ");

        Mockito.when(transactionService.add(transactionDTO)).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.addTransaction(BigDecimal.valueOf(32.5), 0, date, " ")).thenReturn(transactionDTO);

        Mockito.when(transactionService.findAllByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(transactionDTO, transactionDTO2));
        Mockito.when(httpRequestsClass.getTransactions()).thenReturn(List.of(transactionDTO, transactionDTO2));

        Assertions.assertEquals(BigDecimal.valueOf(42.7), balance.getCurrentBalance());
    }

    @DisplayName("Test of the method for getting income for period of current user")
    @Test
    void getIncomeForPeriodTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO(1, null);
        categoryDTO.setName("tt");

        String transaction = "10,2\ntt\n2021-02-10\ns";
        InputStream in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());

        Date date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = new Date(simpleDateFormat.parse("2021-02-10").getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionDTO.setSum(BigDecimal.valueOf(10.2));
        transactionDTO.setCategoryId(categoryDTO.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("s");

        Mockito.when(transactionCategoryService.findCommonCategoriesOrGoalsByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(categoryDTO));
        Mockito.when(transactionCategoryService.findByName("tt")).thenReturn(categoryDTO);
        Mockito.when(transactionService.add(transactionDTO)).thenReturn(transactionDTO);

        Mockito.when(transactionService.add(transactionDTO)).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.addTransaction(BigDecimal.valueOf(10.2), categoryDTO.getId(), date, "s")).thenReturn(transactionDTO);

        transaction = "-32,5\ns\n\n ";
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        TransactionDTO transactionDTO2 = new TransactionDTO(CurrentUser.currentUser.getId());

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionDTO2.setSum(BigDecimal.valueOf(-32.5));
        transactionDTO2.setCategoryId(0);
        transactionDTO2.setDate(date);
        transactionDTO2.setDescription(" ");

        Mockito.when(transactionService.add(transactionDTO)).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.addTransaction(BigDecimal.valueOf(-32.5), 0, date, " ")).thenReturn(transactionDTO);

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

        Mockito.when(transactionService.findAllByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(transactionDTO, transactionDTO2));
        Mockito.when(httpRequestsClass.getTransactions()).thenReturn(List.of(transactionDTO, transactionDTO2));

        Assertions.assertEquals(BigDecimal.valueOf(10.2), balance.getIncomeForPeriod(from, to));
    }

    @DisplayName("Test of the method for getting expense for period of current user")
    @Test
    void getExpenseForPeriodTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO(1, null);
        categoryDTO.setName("tt");

        String transaction = "10,2\ntt\n2021-02-10\ns";
        InputStream in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());

        Date date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = new Date(simpleDateFormat.parse("2021-02-10").getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionDTO.setSum(BigDecimal.valueOf(10.2));
        transactionDTO.setCategoryId(categoryDTO.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("s");

        Mockito.when(transactionCategoryService.findCommonCategoriesOrGoalsByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(categoryDTO));
        Mockito.when(transactionCategoryService.findByName("tt")).thenReturn(categoryDTO);
        Mockito.when(transactionService.add(transactionDTO)).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.addTransaction(BigDecimal.valueOf(10.2), categoryDTO.getId(), date, "s")).thenReturn(transactionDTO);

        transaction = "-32,5\ns\n\n ";
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        TransactionDTO transactionDTO2 = new TransactionDTO(CurrentUser.currentUser.getId());

        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        transactionDTO2.setSum(BigDecimal.valueOf(-32.5));
        transactionDTO2.setCategoryId(0);
        transactionDTO2.setDate(date);
        transactionDTO2.setDescription(" ");

        Mockito.when(transactionService.add(transactionDTO)).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.addTransaction(BigDecimal.valueOf(-32.5), 0, date, " ")).thenReturn(transactionDTO);

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

        Mockito.when(transactionService.findAllByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(transactionDTO, transactionDTO2));
        Mockito.when(httpRequestsClass.getTransactions()).thenReturn(List.of(transactionDTO, transactionDTO2));

        Assertions.assertEquals(BigDecimal.valueOf(32.5), balance.getExpenseForPeriod(from, to));
    }

    @DisplayName("Test of the method for getting expense of current user for category")
    @Test
    void getCategoryExpensesTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO(1, null);
        categoryDTO.setName("tt");

        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        Date date = new Date(System.currentTimeMillis());

        transactionDTO.setSum(BigDecimal.valueOf(-10.10));
        transactionDTO.setCategoryId(categoryDTO.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        TransactionDTO transactionDTO2 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO2.setSum(BigDecimal.valueOf(-20.6));
        transactionDTO2.setCategoryId(categoryDTO.getId());
        transactionDTO2.setDate(date);
        transactionDTO2.setDescription("t2");

        TransactionDTO transactionDTO3 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO3.setSum(BigDecimal.valueOf(4));
        transactionDTO3.setCategoryId(categoryDTO.getId());
        transactionDTO3.setDate(date);
        transactionDTO3.setDescription("t3");

        TransactionDTO transactionDTO4 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO4.setSum(BigDecimal.valueOf(-20.6));
        transactionDTO4.setCategoryId(0);
        transactionDTO4.setDate(date);
        transactionDTO4.setDescription("t4");

        Mockito.when(transactionCategoryService.findAll()).thenReturn(List.of(categoryDTO));
        Mockito.when(transactionService.findAllByDateAndCategoryIdAndTypeAndUserId(null, categoryDTO.getId(), "Neg", CurrentUser.currentUser.getId())).thenReturn(List.of(transactionDTO, transactionDTO2, transactionDTO4));
        Mockito.when(httpRequestsClass.getAllCommonCategoriesOrGoalsWithCurrentUser()).thenReturn(List.of(categoryDTO));
        Mockito.when(httpRequestsClass.filterTransactions(null, categoryDTO.getId(), "Neg", CurrentUser.currentUser.getId())).thenReturn(List.of(transactionDTO, transactionDTO2, transactionDTO4));

        Assertions.assertEquals(categoryDTO.getName() + ": 51.3\n", balance.getCategoryExpenses());
    }
}