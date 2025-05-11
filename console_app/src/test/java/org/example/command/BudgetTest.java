package org.example.command;

import org.example.CurrentUser;
import org.example.command.budget.Budget;
import org.example.controller.dto.MonthlyBudgetDTO;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.example.service.MonthlyBudgetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;

@DisplayName("Tests of class with methods for console input")
class BudgetTest {
    MonthlyBudgetService monthlyBudgetService;
    Budget budget;
    HttpRequestsClass httpRequestsClass;

    @BeforeEach
    void setUp() {
        monthlyBudgetService = Mockito.mock(MonthlyBudgetService.class);
        httpRequestsClass = Mockito.mock(HttpRequestsClass.class);

        UserEntity userEntity = new UserEntity(1);

        userEntity.setEmail("t");
        userEntity.setPassword("t");
        userEntity.setName("t");
        userEntity.setRole(UserRole.USER);
        userEntity.setBlocked(false);

        CurrentUser.currentUser = userEntity;

        budget = new Budget(httpRequestsClass);
    }

    @DisplayName("Test of the method for adding monthly budget")
    @Test
    void addBudgetTest() {
        String reguest = "10,2";
        InputStream in = new ByteArrayInputStream(reguest.getBytes());
        System.setIn(in);

        MonthlyBudgetDTO monthlyBudgetDTO = new MonthlyBudgetDTO(CurrentUser.currentUser.getId());
        monthlyBudgetDTO.setSum(BigDecimal.valueOf(10.2));

        Mockito.when(monthlyBudgetService.findByDateAndUserId(monthlyBudgetDTO.getDate(), CurrentUser.currentUser.getId())).thenReturn(monthlyBudgetDTO);
        Mockito.when(httpRequestsClass.getBudget()).thenReturn(monthlyBudgetDTO);
        Mockito.when(httpRequestsClass.addBudget(BigDecimal.valueOf(10.2))).thenReturn(monthlyBudgetDTO);

        monthlyBudgetDTO = budget.getAddedBudget();

        Assertions.assertEquals(BigDecimal.valueOf(10.2), monthlyBudgetDTO.getSum());
        int id = monthlyBudgetDTO.getId();

        reguest = "312,5";
        in = new ByteArrayInputStream(reguest.getBytes());
        System.setIn(in);

        monthlyBudgetDTO.setSum(BigDecimal.valueOf(312.5));

        Mockito.when(monthlyBudgetService.findByDateAndUserId(monthlyBudgetDTO.getDate(), CurrentUser.currentUser.getId())).thenReturn(monthlyBudgetDTO);
        Mockito.when(httpRequestsClass.getBudget()).thenReturn(monthlyBudgetDTO);
        Mockito.when(httpRequestsClass.addBudget(BigDecimal.valueOf(312.5))).thenReturn(monthlyBudgetDTO);

        monthlyBudgetDTO = budget.getAddedBudget();
        int id2 = monthlyBudgetDTO.getId();

        Assertions.assertNotEquals(BigDecimal.valueOf(10.2), monthlyBudgetDTO.getSum());
        Assertions.assertEquals(BigDecimal.valueOf(312.5), monthlyBudgetDTO.getSum());
        Assertions.assertEquals(id2, id);
    }
}