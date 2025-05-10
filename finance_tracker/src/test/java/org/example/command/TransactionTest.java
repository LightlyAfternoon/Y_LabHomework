package org.example.command;

import org.example.CurrentUser;
import org.example.command.transaction.Transaction;
import org.example.controller.dto.MonthlyBudgetDTO;
import org.example.controller.dto.TransactionCategoryDTO;
import org.example.controller.dto.TransactionDTO;
import org.example.model.TransactionCategoryEntity;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.example.service.TransactionService;
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

@DisplayName("Tests of class with methods for console input")
class TransactionTest {
    TransactionService transactionService;
    Transaction transaction;
    HttpRequestsClass httpRequestsClass;

    @BeforeEach
    void setUp() {
        transactionService = Mockito.mock(TransactionService.class);
        httpRequestsClass = Mockito.mock(HttpRequestsClass.class);

        UserEntity userEntity = new UserEntity(1);

        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setName("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        CurrentUser.currentUser = userEntity;

        transaction = new Transaction(httpRequestsClass);
    }

    @DisplayName("Test of the method for adding transaction")
    @Test
    void addTransactionTest() throws ParseException {
        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO();
        categoryDTO.setName("tt");

        String request = "10,2\ntt\n2021-02-10\ns";
        InputStream in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        Date date = new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-10").getTime());

        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        transactionDTO.setSum(BigDecimal.valueOf(10.2));
        transactionDTO.setCategoryId(categoryDTO.getId());
        transactionDTO.setDate(date);
        transactionDTO.setDescription("s");

        Mockito.when(transactionService.add(transactionDTO)).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.addTransaction(BigDecimal.valueOf(10.2), categoryDTO.getId(), date, "s")).thenReturn(transactionDTO);

        transactionDTO = transaction.returnAddedTransaction();

        Assertions.assertEquals(BigDecimal.valueOf(10.2), transactionDTO.getSum());
        int id = transactionDTO.getId();

        request = "12,2\ns\n\n ";
        in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());
        transactionDTO.setSum(BigDecimal.valueOf(12.2));
        transactionDTO.setCategoryId(0);
        transactionDTO.setDate(null);
        transactionDTO.setDescription(" ");

        Mockito.when(transactionService.add(transactionDTO)).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.addTransaction(BigDecimal.valueOf(12.2), 0, transactionDTO.getDate(), " ")).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.getTransactions()).thenReturn(List.of(transactionDTO));
        Mockito.when(httpRequestsClass.getBudget()).thenReturn(new MonthlyBudgetDTO.MonthlyBudgetBuilder(CurrentUser.currentUser.getId(), BigDecimal.valueOf(100)).build());

        transactionDTO = transaction.returnAddedTransaction();

        Assertions.assertNotEquals(BigDecimal.valueOf(10.2), transactionDTO.getSum());
        Assertions.assertEquals(BigDecimal.valueOf(12.2), transactionDTO.getSum());

        request = "10,2\ntt\n2021-02-10\ns";
        in = new ByteArrayInputStream(request.getBytes());
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

        transactionDTO = transaction.returnAddedTransaction();
        int id2 = transactionDTO.getId();

        Assertions.assertEquals(id2, id);
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

        Assertions.assertEquals(output.toString(), transaction.getTransactions());
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

        String outputReturned = transaction.returnFilteredTransactions();

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

        outputReturned = transaction.returnFilteredTransactions();

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

        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO(1, null);
        categoryDTO.setName("tt");

        TransactionCategoryDTO categoryDTO2 = new TransactionCategoryDTO(2, null);
        categoryDTO2.setName("tt2");

        String request = "10,2\ntt\n\ns";
        InputStream in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        TransactionDTO transactionDTO = new TransactionDTO(CurrentUser.currentUser.getId());

        transactionDTO.setSum(BigDecimal.valueOf(10.2));
        transactionDTO.setCategoryId(categoryDTO.getId());
        transactionDTO.setDate(null);
        transactionDTO.setDescription("s");

        Mockito.when(transactionService.add(transactionDTO)).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.getAllCommonCategoriesOrGoalsWithCurrentUser()).thenReturn(List.of(categoryDTO, categoryDTO2));
        Mockito.when(httpRequestsClass.getCategoryOrGoalWithName(categoryDTO.getName())).thenReturn(categoryDTO);
        Mockito.when(httpRequestsClass.getCategoryOrGoalWithName(categoryDTO2.getName())).thenReturn(categoryDTO2);
        Mockito.when(httpRequestsClass.addTransaction(BigDecimal.valueOf(10.2), categoryDTO.getId(), transactionDTO.getDate(), "s")).thenReturn(transactionDTO);

        transactionDTO = transaction.returnAddedTransaction();
        int id = transactionDTO.getId();

        request = id + "\n12,2\n\n2021-02-10\n ";
        in = new ByteArrayInputStream(request.getBytes());
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
        Mockito.when(transactionService.update(transactionDTO, id)).thenReturn(transactionDTO);
        Mockito.when(httpRequestsClass.editTransaction(id, BigDecimal.valueOf(12.2), 0, date, " ")).thenReturn(true);

        Assertions.assertTrue(transaction.transactionIsEdited());

        request = "50\n12,2\ntt2\n \n ";
        in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        id = 50;

        Mockito.when(transactionService.findById(id)).thenReturn(null);
        Mockito.when(httpRequestsClass.editTransaction(id, BigDecimal.valueOf(12.2), categoryDTO2.getId(), null, " ")).thenReturn(false);

        Assertions.assertFalse(transaction.transactionIsEdited());
    }

    @DisplayName("Test of the method for deleting transaction")
    @Test
    void deleteTransactionTest() {
        TransactionCategoryEntity category = new TransactionCategoryEntity();
        category.setName("tt");

        String request = "10,2\ntt\n\ns";
        InputStream in = new ByteArrayInputStream(request.getBytes());
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

        request = String.valueOf(1);
        in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        Assertions.assertTrue(transaction.transactionDeleted());

        request = String.valueOf(50);
        in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        Mockito.when(transactionService.findById(50)).thenReturn(null);
        Mockito.when(httpRequestsClass.deleteTransaction(50)).thenReturn(false);

        Assertions.assertFalse(transaction.transactionDeleted());
    }
}