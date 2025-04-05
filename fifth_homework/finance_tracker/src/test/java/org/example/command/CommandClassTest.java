package org.example.command;

import org.example.CurrentUser;
import org.example.controller.dto.MonthlyBudgetDTO;
import org.example.controller.dto.TransactionCategoryDTO;
import org.example.controller.dto.TransactionDTO;
import org.example.controller.dto.UserDTO;
import org.example.model.TransactionCategoryEntity;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@DisplayName("Tests of class with methods for console input")
class CommandClassTest {
    UserService userService;
    MonthlyBudgetService monthlyBudgetService;
    TransactionCategoryService transactionCategoryService;
    TransactionService transactionService;
    CommandClass commandClass;
    HttpRequestsClass httpRequestsClass;
    UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        monthlyBudgetService = Mockito.mock(MonthlyBudgetService.class);
        transactionCategoryService = Mockito.mock(TransactionCategoryService.class);
        transactionService = Mockito.mock(TransactionService.class);
        httpRequestsClass = Mockito.mock(HttpRequestsClass.class);

        commandClass = new CommandClass(httpRequestsClass);

        UserEntity user = new UserEntity(1);

        user.setEmail("t");
        user.setPassword("t");
        user.setName("t");
        user.setRole(UserRole.USER);
        user.setBlocked(false);

        CurrentUser.currentUser = user;

        userDTO = new UserDTO.UserBuilder(CurrentUser.currentUser.getEmail(), CurrentUser.currentUser.getPassword(), CurrentUser.currentUser.getName()).
                id(CurrentUser.currentUser.getId()).role(CurrentUser.currentUser.getRole()).isBlocked(CurrentUser.currentUser.isBlocked()).build();
    }

    @DisplayName("Test of the method for getting logged in user by email and password")
    @Test
    void getLoggedInUserRoleTest() {
        String emailAndPassword = "t\nt";
        InputStream in = new ByteArrayInputStream(emailAndPassword.getBytes());
        System.setIn(in);

        Mockito.when(userService.findUserByEmailAndPassword("t", "t")).thenReturn(userDTO);
        Mockito.when(httpRequestsClass.getLoggedInUser("t", "t")).thenReturn(new UserDTO.UserBuilder("t", "t", "t").id(1).build());

        Assertions.assertEquals(UserRole.USER, commandClass.getLoggedInUserRole());

        emailAndPassword = "t2\nt";
        in = new ByteArrayInputStream(emailAndPassword.getBytes());
        System.setIn(in);

        Mockito.when(userService.findUserByEmailAndPassword("t2", "t")).thenReturn(null);
        Mockito.when(httpRequestsClass.getLoggedInUser("t2", "t")).thenReturn(null);

        Assertions.assertNull(commandClass.getLoggedInUserRole());

        emailAndPassword = "t\nt2";
        in = new ByteArrayInputStream(emailAndPassword.getBytes());
        System.setIn(in);

        Mockito.when(userService.findUserByEmailAndPassword("t", "t2")).thenReturn(null);
        Mockito.when(httpRequestsClass.getLoggedInUser("t", "t2")).thenReturn(null);

        Assertions.assertNull(commandClass.getLoggedInUserRole());
    }

    @DisplayName("Test of the method for getting all users")
    @Test
    void getAllUsersTest() {
        List<UserDTO> userDTOS = List.of(userDTO);
        StringBuilder output = new StringBuilder();

        for (UserDTO dto : userDTOS) {
            output.append(dto).append("\n");
        }

        Mockito.when(userService.findAll()).thenReturn(userDTOS);
        Mockito.when(httpRequestsClass.getAllUsers()).thenReturn(userDTOS);

        Assertions.assertEquals(output.toString(), commandClass.getAllUsers());
    }

    @DisplayName("Test of the method for getting registered user")
    @Test
    void getRegisteredUserTest() {
        String emailAndPassword = "t\nt\nt";
        InputStream in = new ByteArrayInputStream(emailAndPassword.getBytes());
        System.setIn(in);

        UserDTO userDTO2 = new UserDTO();
        userDTO2.setEmail("t");
        userDTO2.setPassword("t");
        userDTO2.setName("t");
        userDTO2.setRole(UserRole.USER);
        userDTO2.setBlocked(false);

        Mockito.when(userService.add(userDTO2)).thenReturn(null);
        Mockito.when(httpRequestsClass.getRegisteredUser("t", "t", "t")).thenReturn(null);

        Assertions.assertNull(commandClass.getRegisteredUser());

        emailAndPassword = "t2\nt\nt2";
        in = new ByteArrayInputStream(emailAndPassword.getBytes());
        System.setIn(in);

        userDTO2 = new UserDTO();
        userDTO2.setEmail("t2");
        userDTO2.setPassword("t");
        userDTO2.setName("t2");
        userDTO2.setRole(UserRole.USER);
        userDTO2.setBlocked(false);

        Mockito.when(userService.add(userDTO2)).thenReturn(userDTO2);
        Mockito.when(httpRequestsClass.getRegisteredUser("t2", "t", "t2")).thenReturn(userDTO2);

        Assertions.assertEquals(userDTO2, commandClass.getRegisteredUser());
    }

    @DisplayName("Test of the method for adding monthly budget")
    @Test
    void addBudgetTest() {
        String budget = "10,2";
        InputStream in = new ByteArrayInputStream(budget.getBytes());
        System.setIn(in);

        MonthlyBudgetDTO monthlyBudgetDTO = new MonthlyBudgetDTO(CurrentUser.currentUser.getId());
        monthlyBudgetDTO.setSum(BigDecimal.valueOf(10.2));

        Mockito.when(monthlyBudgetService.findByDateAndUserId(monthlyBudgetDTO.getDate(), CurrentUser.currentUser.getId())).thenReturn(monthlyBudgetDTO);
        Mockito.when(httpRequestsClass.getBudget()).thenReturn(monthlyBudgetDTO);
        Mockito.when(httpRequestsClass.addBudget(BigDecimal.valueOf(10.2))).thenReturn(monthlyBudgetDTO);

        monthlyBudgetDTO = commandClass.addBudget();

        Assertions.assertEquals(BigDecimal.valueOf(10.2), monthlyBudgetDTO.getSum());
        int id = monthlyBudgetDTO.getId();

        budget = "312,5";
        in = new ByteArrayInputStream(budget.getBytes());
        System.setIn(in);

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(312.5));

        Mockito.when(monthlyBudgetService.findByDateAndUserId(monthlyBudgetDTO.getDate(), CurrentUser.currentUser.getId())).thenReturn(monthlyBudgetDTO);
        Mockito.when(httpRequestsClass.getBudget()).thenReturn(monthlyBudgetDTO);
        Mockito.when(httpRequestsClass.addBudget(BigDecimal.valueOf(312.5))).thenReturn(monthlyBudgetDTO);

        monthlyBudgetDTO = commandClass.addBudget();
        int id2 = monthlyBudgetDTO.getId();

        Assertions.assertNotEquals(BigDecimal.valueOf(10.2), monthlyBudgetDTO.getSum());
        Assertions.assertEquals(BigDecimal.valueOf(312.5), monthlyBudgetDTO.getSum());
        Assertions.assertEquals(id2, id);
    }

    @DisplayName("Test of the method for adding goal")
    @Test
    void addGoalTest() {
        String goal = "t\n10,2";
        InputStream in = new ByteArrayInputStream(goal.getBytes());
        System.setIn(in);

        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());
        categoryDTO.setName("t");
        categoryDTO.setNeededSum(BigDecimal.valueOf(10.2));

        Mockito.when(transactionCategoryService.add(categoryDTO)).thenReturn(categoryDTO);
        Mockito.when(httpRequestsClass.addGoal("t", BigDecimal.valueOf(10.2))).thenReturn(categoryDTO);

        categoryDTO = commandClass.addGoal();

        Assertions.assertEquals(BigDecimal.valueOf(10.2), categoryDTO.getNeededSum());
        int id = categoryDTO.getId();

        goal = "t\n312,5";
        in = new ByteArrayInputStream(goal.getBytes());
        System.setIn(in);

        categoryDTO.setNeededSum(BigDecimal.valueOf(312.5));

        Mockito.when(httpRequestsClass.addGoal("t", BigDecimal.valueOf(312.5))).thenReturn(categoryDTO);

        categoryDTO = commandClass.addGoal();

        Assertions.assertNotEquals(BigDecimal.valueOf(10.2), categoryDTO.getNeededSum());
        Assertions.assertEquals(BigDecimal.valueOf(312.5), categoryDTO.getNeededSum());

        goal = "t\n10,2";
        in = new ByteArrayInputStream(goal.getBytes());
        System.setIn(in);

        categoryDTO.setNeededSum(BigDecimal.valueOf(10.2));

        Mockito.when(httpRequestsClass.addGoal("t", BigDecimal.valueOf(10.2))).thenReturn(categoryDTO);

        categoryDTO = commandClass.addGoal();

        int id2 = categoryDTO.getId();

        Assertions.assertEquals(id2, id);
    }

    @DisplayName("Test of the method for adding transaction")
    @Test
    void addTransactionTest() throws ParseException {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();
        categoryDTO.setName("tt");

        Mockito.when(transactionCategoryService.add(categoryDTO)).thenReturn(categoryDTO);
        transactionCategoryService.add(categoryDTO);

        String transaction = "10,2\ntt\n2021-02-10\ns";
        InputStream in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        Date date = new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-10").getTime());

        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        transactionDTO.setSum(BigDecimal.valueOf(10.2));
        transactionDTO.setCategoryId(categoryDTO.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("s");

        Mockito.when(transactionCategoryService.findCommonCategoriesOrGoalsByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(categoryDTO));
        Mockito.when(transactionCategoryService.findByName("tt")).thenReturn(categoryDTO);
        Mockito.when(transactionService.add(transactionDTO)).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.addTransaction(BigDecimal.valueOf(10.2), categoryDTO.getId(), date, "s")).thenReturn(transactionDTO);

        transactionDTO = commandClass.addTransaction();

        Assertions.assertEquals(BigDecimal.valueOf(10.2), transactionDTO.getSum());
        int id = transactionDTO.getId();

        transaction = "12,2\ns\n\n ";
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        transactionDTO.setSum(BigDecimal.valueOf(12.2));
        transactionDTO.setCategoryId(0);
        transactionDTO.setDate(null);
        transactionDTO.setDescription(" ");

        Mockito.when(transactionService.add(transactionDTO)).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.addTransaction(BigDecimal.valueOf(12.2), 0, null, " ")).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.getTransactions()).thenReturn(List.of(transactionDTO));
        Mockito.when(httpRequestsClass.getBudget()).thenReturn(new MonthlyBudgetDTO.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(100)).build());

        transactionDTO = commandClass.addTransaction();

        Assertions.assertNotEquals(BigDecimal.valueOf(10.2), transactionDTO.getSum());
        Assertions.assertEquals(BigDecimal.valueOf(12.2), transactionDTO.getSum());

        transaction = "10,2\ntt\n2021-02-10\ns";
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        transactionDTO.setSum(BigDecimal.valueOf(10.2));
        transactionDTO.setCategoryId(categoryDTO.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("s");

        Mockito.when(transactionService.add(transactionDTO)).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.addTransaction(BigDecimal.valueOf(10.2), categoryDTO.getId(), date, "s")).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.getTransactions()).thenReturn(List.of(transactionDTO));
        Mockito.when(httpRequestsClass.getBudget()).thenReturn(new MonthlyBudgetDTO.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(5)).build());

        transactionDTO = commandClass.addTransaction();
        int id2 = transactionDTO.getId();

        Assertions.assertEquals(id2, id);
    }

    @DisplayName("Test of the method for deleting account of current user")
    @Test
    void deleteAccountTest() {
        Mockito.when(userService.delete(CurrentUser.currentUser.getId())).thenReturn(true);
        Mockito.when(httpRequestsClass.deleteAccount(CurrentUser.currentUser.getId())).thenReturn(true);

        Assertions.assertTrue(commandClass.deleteAccount());
    }

    @DisplayName("Test of the method for getting transactions of current user")
    @Test
    void getTransactionsTest() {
        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        Date date = new Date(System.currentTimeMillis());

        transactionDTO.setSum(BigDecimal.valueOf(10.10));
        transactionDTO.setCategoryId(0);
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        Mockito.when(transactionService.add(transactionDTO)).thenReturn(transactionDTO);

        transactionDTO = transactionService.add(transactionDTO);
        Mockito.when(httpRequestsClass.addTransaction(BigDecimal.valueOf(10.1), 0, date, "t")).thenReturn(transactionDTO);

        List<TransactionDTO> transactionEntities = List.of(transactionDTO);
        StringBuilder output = new StringBuilder();

        for (TransactionDTO transaction : transactionEntities) {
            output.append(transaction).append("\n");
        }

        Mockito.when(transactionService.findAllByUserId(CurrentUser.currentUser.getId())).thenReturn(transactionEntities);
        Mockito.when(httpRequestsClass.getTransactions()).thenReturn(transactionEntities);

        Assertions.assertEquals(output.toString(), commandClass.getTransactions());
    }

    @DisplayName("Test of the method for filtering transactions")
    @Test
    void filterTransactionsTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO(1, null);

        categoryDTO.setName("tt");

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

        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO.setSum(BigDecimal.valueOf(10.10));
        transactionDTO.setCategoryId(categoryDTO.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("t");

        TransactionDTO transactionEntityDTO2 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionEntityDTO2.setSum(BigDecimal.valueOf(20.0));
        transactionEntityDTO2.setCategoryId(categoryDTO.getId());
        transactionEntityDTO2.setDate(date2);
        transactionEntityDTO2.setDescription("t2");

        UserEntity user2 = new UserEntity(2);

        user2.setEmail("t2");
        user2.setPassword("t2");
        user2.setName("t2");
        user2.setBlocked(false);

        TransactionDTO transactionDTO3 = new TransactionDTO(user2.getId());

        transactionDTO3.setSum(BigDecimal.valueOf(30.3));
        transactionDTO3.setCategoryId(categoryDTO.getId());
        transactionDTO3.setDate(date);
        transactionDTO3.setDescription("t3");

        TransactionDTO transactionDTO4 = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO4.setSum(BigDecimal.valueOf(-10.10));
        transactionDTO4.setCategoryId(0);
        transactionDTO4.setDate(date2);
        transactionDTO4.setDescription(null);

        Mockito.when(transactionCategoryService.findCommonCategoriesOrGoalsByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(categoryDTO));
        Mockito.when(transactionCategoryService.findByName("tt")).thenReturn(categoryDTO);

        Mockito.when(transactionService.add(transactionDTO)).thenReturn(transactionDTO);
        Mockito.when(transactionService.add(transactionEntityDTO2)).thenReturn(transactionEntityDTO2);
        Mockito.when(transactionService.add(transactionDTO3)).thenReturn(transactionDTO3);
        Mockito.when(transactionService.add(transactionDTO4)).thenReturn(transactionDTO4);

        transactionDTO = transactionService.add(transactionDTO);
        transactionEntityDTO2 = transactionService.add(transactionEntityDTO2);
        transactionDTO3 = transactionService.add(transactionDTO3);
        transactionDTO4 = transactionService.add(transactionDTO4);

        List<TransactionDTO> transactionDTOS = List.of(transactionDTO);

        Mockito.when(transactionService.findAllByDateAndCategoryIdAndTypeAndUserId(date, categoryDTO.getId(), "Pos", CurrentUser.currentUser.getId())).thenReturn(transactionDTOS);
        Mockito.when(httpRequestsClass.getCategoryOrGoalWithName("tt")).thenReturn(categoryDTO);
        Mockito.when(httpRequestsClass.filterTransactions(date, categoryDTO.getId(), "Pos", CurrentUser.currentUser.getId())).thenReturn(transactionDTOS);

        String outputReturned = commandClass.filterTransactions();

        StringBuilder output = new StringBuilder();

        for (TransactionDTO dto : transactionDTOS) {
            output.append(dto).append("\n");
        }

        Assertions.assertEquals(output.toString(), outputReturned);

        filter = date2 + "\n \n ";
        in = new ByteArrayInputStream(filter.getBytes());
        System.setIn(in);

        transactionDTOS = List.of(transactionEntityDTO2, transactionDTO4);

        Mockito.when(transactionService.findAllByDateAndCategoryIdAndTypeAndUserId(date2, 0, "", CurrentUser.currentUser.getId())).thenReturn(transactionDTOS);
        Mockito.when(httpRequestsClass.filterTransactions(date2, 0, " ", CurrentUser.currentUser.getId())).thenReturn(transactionDTOS);

        outputReturned = commandClass.filterTransactions();

        output = new StringBuilder();

        for (TransactionDTO dto : transactionDTOS) {
            output.append(dto).append("\n");
        }

        Assertions.assertEquals(output.toString(), outputReturned);
    }

    @DisplayName("Test of the method for editing transaction")
    @Test
    void editTransactionTest() {
        MonthlyBudgetDTO budgetDTO = new MonthlyBudgetDTO(CurrentUser.currentUser.getId());

        budgetDTO.setSum(BigDecimal.valueOf(12));

        Mockito.when(monthlyBudgetService.update(budgetDTO, 1)).thenReturn(budgetDTO);

        monthlyBudgetService.update(budgetDTO, 1);

        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO(1, null);
        categoryDTO.setName("tt");

        Mockito.when(transactionCategoryService.update(categoryDTO, 1)).thenReturn(categoryDTO);

        transactionCategoryService.update(categoryDTO, 1);

        TransactionCategoryDTO categoryDTO2 = new TransactionCategoryDTO(2, null);
        categoryDTO2.setName("tt2");

        Mockito.when(transactionCategoryService.update(categoryDTO2, 2)).thenReturn(categoryDTO2);

        transactionCategoryService.update(categoryDTO, 2);

        String transaction = "10,2\ntt\n\ns";
        InputStream in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO.setSum(BigDecimal.valueOf(10.2));
        transactionDTO.setCategoryId(categoryDTO.getId());
        transactionDTO.setDate(null);
        transactionDTO.setDescription("s");

        Mockito.when(transactionCategoryService.findCommonCategoriesOrGoalsByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(categoryDTO, categoryDTO2));
        Mockito.when(transactionCategoryService.findByName("tt")).thenReturn(categoryDTO);
        Mockito.when(transactionService.add(transactionDTO)).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.getAllCommonCategoriesOrGoalsWithCurrentUser()).thenReturn(List.of(categoryDTO, categoryDTO2));
        Mockito.when(httpRequestsClass.getCategoryOrGoalWithName(categoryDTO.getName())).thenReturn(categoryDTO);
        Mockito.when(httpRequestsClass.getCategoryOrGoalWithName(categoryDTO2.getName())).thenReturn(categoryDTO2);
        Mockito.when(httpRequestsClass.addTransaction(BigDecimal.valueOf(10.2), categoryDTO.getId(), null, "s")).thenReturn(transactionDTO);

        transactionDTO = commandClass.addTransaction();
        int id = transactionDTO.getId();

        transaction = id + "\n12,2\n\n2021-02-10\n ";
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        Date date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = new Date(simpleDateFormat.parse("2021-02-10").getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionDTO.setSum(BigDecimal.valueOf(12.2));
        transactionDTO.setCategoryId(0);
        transactionDTO.setDate(date);
        transactionDTO.setDescription(" ");

        Mockito.when(transactionService.findById(id)).thenReturn(transactionDTO);
        Mockito.when(transactionCategoryService.findCommonCategoriesOrGoalsByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(categoryDTO, categoryDTO2));
        Mockito.when(transactionService.update(transactionDTO, id)).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.editTransaction(id, BigDecimal.valueOf(12.2), 0, date, " ")).thenReturn(true);

        Assertions.assertTrue(commandClass.editTransaction());

        transaction = "50\n12,2\ntt2\n \n ";
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        id = 50;

        Mockito.when(transactionCategoryService.findByName("tt2")).thenReturn(categoryDTO2);
        Mockito.when(transactionService.findById(id)).thenReturn(null);
        Mockito.when(httpRequestsClass.editTransaction(id, BigDecimal.valueOf(12.2), categoryDTO2.getId(), null, " ")).thenReturn(false);

        Assertions.assertFalse(commandClass.editTransaction());
    }

    @DisplayName("Test of the method for deleting transaction")
    @Test
    void deleteTransactionTest() {
        TransactionCategoryEntity category = new TransactionCategoryEntity();
        category.setName("tt");

        String transaction = "10,2\ntt\n\ns";
        InputStream in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        Date date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        transactionDTO.setSum(BigDecimal.valueOf(10.2));
        transactionDTO.setCategoryId(category.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("s");

        Mockito.when(transactionService.findById(1)).thenReturn(transactionDTO);
        Mockito.when(transactionService.delete(1)).thenReturn(true);
        Mockito.when(httpRequestsClass.deleteTransaction(1)).thenReturn(true);

        transaction = String.valueOf(1);
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        Assertions.assertTrue(commandClass.deleteTransaction());

        transaction = String.valueOf(50);
        in = new ByteArrayInputStream(transaction.getBytes());
        System.setIn(in);

        Mockito.when(transactionService.findById(50)).thenReturn(null);
        Mockito.when(httpRequestsClass.deleteTransaction(50)).thenReturn(false);

        Assertions.assertFalse(commandClass.deleteTransaction());
    }

    @DisplayName("Test of the method for getting all goals of current user")
    @Test
    void getAllUserGoalsTest() {
        String goal = "t\n10,2";
        InputStream in = new ByteArrayInputStream(goal.getBytes());
        System.setIn(in);

        TransactionCategoryDTO goalDTO = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());
        goalDTO.setName("t");
        goalDTO.setNeededSum(BigDecimal.valueOf(10.2));

        Mockito.when(transactionCategoryService.add(goalDTO)).thenReturn(goalDTO);
        Mockito.when(httpRequestsClass.addGoal("t", BigDecimal.valueOf(10.2))).thenReturn(goalDTO);

        goal = "t2\n312,5";
        in = new ByteArrayInputStream(goal.getBytes());
        System.setIn(in);

        TransactionCategoryDTO goalDTO2 = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());
        goalDTO2.setName("t2");
        goalDTO2.setNeededSum(BigDecimal.valueOf(312.5));

        Mockito.when(transactionCategoryService.add(goalDTO2)).thenReturn(goalDTO2);
        Mockito.when(httpRequestsClass.addGoal("t2", BigDecimal.valueOf(312.5))).thenReturn(goalDTO2);

        List<TransactionCategoryDTO> categoryEntities = List.of(goalDTO, goalDTO2);

        Mockito.when(transactionCategoryService.findAllGoalsByUserId(CurrentUser.currentUser.getId())).thenReturn(categoryEntities);
        Mockito.when(transactionService.findAllByUserId(CurrentUser.currentUser.getId())).thenReturn(new ArrayList<>());
        Mockito.when(httpRequestsClass.getAllUserGoals(CurrentUser.currentUser.getId())).thenReturn(categoryEntities);

        String outputReturned = commandClass.getAllUserGoals();

        StringBuilder output = new StringBuilder();

        for (TransactionCategoryDTO dto : categoryEntities) {
            output.append(dto).append(" Необходимая сумма = ").append(0).append("/").append(dto.getNeededSum()).append("\n");
        }

        Assertions.assertEquals(output.toString(), outputReturned);
    }

    @DisplayName("Test of the method for getting current balance (sum of all transactions positive sum) of current user")
    @Test
    void getCurrentBalanceTest() {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();
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

        commandClass.addTransaction();

        transaction = "32,5\ns\n\n ";
        in = new ByteArrayInputStream(transaction.getBytes());
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

        commandClass.addTransaction();

        Mockito.when(transactionService.findAllByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(transactionDTO, transactionDTO2));
        Mockito.when(httpRequestsClass.getTransactions()).thenReturn(List.of(transactionDTO, transactionDTO2));

        Assertions.assertEquals(BigDecimal.valueOf(42.7), commandClass.getCurrentBalance());
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

        commandClass.addTransaction();

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

        Mockito.when(transactionService.findAllByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(transactionDTO, transactionDTO2));
        Mockito.when(httpRequestsClass.getTransactions()).thenReturn(List.of(transactionDTO, transactionDTO2));

        Assertions.assertEquals(BigDecimal.valueOf(10.2), commandClass.getIncomeForPeriod(from, to));
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

        commandClass.addTransaction();

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

        Mockito.when(transactionService.findAllByUserId(CurrentUser.currentUser.getId())).thenReturn(List.of(transactionDTO, transactionDTO2));
        Mockito.when(httpRequestsClass.getTransactions()).thenReturn(List.of(transactionDTO, transactionDTO2));

        Assertions.assertEquals(BigDecimal.valueOf(32.5), commandClass.getExpenseForPeriod(from, to));
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

        Assertions.assertEquals(categoryDTO.getName() + ": 51.3\n", commandClass.getCategoryExpenses());
    }
}