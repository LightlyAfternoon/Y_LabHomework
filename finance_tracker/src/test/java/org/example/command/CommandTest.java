package org.example.command;

import org.example.CurrentUser;
import org.example.command.balance.*;
import org.example.command.budget.AddBudgetCommand;
import org.example.command.budget.Budget;
import org.example.command.budget.ShowBudgetCommand;
import org.example.command.goal.AddGoalCommand;
import org.example.command.goal.Goal;
import org.example.command.goal.ShowGoalsCommand;
import org.example.command.transaction.*;
import org.example.command.user.LogInCommand;
import org.example.command.user.RegisterCommand;
import org.example.command.user.ShowAllUsersCommand;
import org.example.command.user.User;
import org.example.controller.dto.MonthlyBudgetDTO;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;

@DisplayName("Tests of class with methods for console input")
class CommandTest {
    Invoker invoker;
    User user;
    Transaction transaction;
    Goal goal;
    Budget budget;
    Balance balance;
    HttpRequestsClass httpRequestsClass;

    @BeforeEach
    void setUp() {
        httpRequestsClass = Mockito.mock(HttpRequestsClass.class);

        UserEntity userEntity = new UserEntity(1);

        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setName("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        CurrentUser.currentUser = userEntity;

        invoker = new Invoker();
        user = new User(httpRequestsClass);
        transaction = new Transaction(httpRequestsClass);
        goal = new Goal(httpRequestsClass);
        budget = new Budget(httpRequestsClass);
        balance = new Balance(httpRequestsClass);
    }

    @DisplayName("Test of invoker and commands")
    @Test
    void invokerTest() {
        String request = "t\nt";
        InputStream in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        invoker.addCommand(new LogInCommand(user));
        Assertions.assertDoesNotThrow(() -> invoker.doCommands());

        request = "t\nt\nt";
        in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        invoker.addCommand(new RegisterCommand(user));
        Assertions.assertDoesNotThrow(() -> invoker.doCommands());

        Mockito.when(httpRequestsClass.getBudget()).thenReturn(new MonthlyBudgetDTO.MonthlyBudgetBuilder
                (CurrentUser.currentUser.getId(), BigDecimal.valueOf(10.2)).id(1).build());
        request = "10,2";
        in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        invoker.addCommand(new AddBudgetCommand(budget));
        Assertions.assertDoesNotThrow(() -> invoker.doCommands());

        request = "t\n10,2";
        in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        invoker.addCommand(new AddGoalCommand(goal));
        Assertions.assertDoesNotThrow(() -> invoker.doCommands());

        request = "10,2\ntt\n2021-02-10\ns";
        in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        invoker.addCommand(new AddTransactionCommand(transaction));
        Assertions.assertDoesNotThrow(() -> invoker.doCommands());

        request = "\ntt\nPos";
        in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        invoker.addCommand(new ShowFilteredTransactionsCommand(transaction));
        Assertions.assertDoesNotThrow(() -> invoker.doCommands());

        request = "1\n10,2\ntt\n2021-02-10\ns";
        in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        invoker.addCommand(new EditTransactionCommand(transaction));
        Assertions.assertDoesNotThrow(() -> invoker.doCommands());

        invoker.addCommand(new ShowGoalsCommand(goal));
        Assertions.assertDoesNotThrow(() -> invoker.doCommands());

        invoker.addCommand(new ShowBalanceCommand(balance));
        Assertions.assertDoesNotThrow(() -> invoker.doCommands());

        invoker.addCommand(new ShowIncomeForPeriodCommand(balance, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())));
        Assertions.assertDoesNotThrow(() -> invoker.doCommands());

        invoker.addCommand(new ShowExpenseForPeriodCommand(balance, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())));
        Assertions.assertDoesNotThrow(() -> invoker.doCommands());

        invoker.addCommand(new ShowCategoryExpenseCommand(balance));
        Assertions.assertDoesNotThrow(() -> invoker.doCommands());

        invoker.addCommand(new ShowAllUsersCommand(user));
        Assertions.assertDoesNotThrow(() -> invoker.doCommands());

        invoker.addCommand(new ShowTransactionsCommand(transaction));
        Assertions.assertDoesNotThrow(() -> invoker.doCommands());

        invoker.addCommand(new ShowBudgetCommand(budget));
        Assertions.assertDoesNotThrow(() -> invoker.doCommands());
    }
}