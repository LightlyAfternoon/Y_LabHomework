package org.example.command;

import org.example.CurrentUser;
import org.example.command.goal.Goal;
import org.example.controller.dto.TransactionCategoryDTO;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.example.service.TransactionCategoryService;
import org.example.service.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DisplayName("Tests of class with methods for console input")
class GoalTest {
    TransactionCategoryService transactionCategoryService;
    TransactionService transactionService;
    Goal goal;
    HttpRequestsClass httpRequestsClass;

    @BeforeEach
    void setUp() {
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

        goal = new Goal(httpRequestsClass);
    }

    @DisplayName("Test of the method for adding goal")
    @Test
    void addGoalTest() {
        String request = "t\n10,2";
        InputStream in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        TransactionCategoryDTO categoryDTO = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());
        categoryDTO.setName("t");
        categoryDTO.setNeededSum(BigDecimal.valueOf(10.2));

        Mockito.when(transactionCategoryService.add(categoryDTO)).thenReturn(categoryDTO);
        Mockito.when(httpRequestsClass.addGoal("t", BigDecimal.valueOf(10.2))).thenReturn(categoryDTO);

        categoryDTO = goal.getAddedGoal();

        Assertions.assertEquals(BigDecimal.valueOf(10.2), categoryDTO.getNeededSum());
        int id = categoryDTO.getId();

        request = "t\n312,5";
        in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        categoryDTO.setNeededSum(BigDecimal.valueOf(312.5));

        Mockito.when(httpRequestsClass.addGoal("t", BigDecimal.valueOf(312.5))).thenReturn(categoryDTO);

        categoryDTO = goal.getAddedGoal();

        Assertions.assertNotEquals(BigDecimal.valueOf(10.2), categoryDTO.getNeededSum());
        Assertions.assertEquals(BigDecimal.valueOf(312.5), categoryDTO.getNeededSum());

        request = "t\n10,2";
        in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        categoryDTO.setNeededSum(BigDecimal.valueOf(10.2));

        Mockito.when(httpRequestsClass.addGoal("t", BigDecimal.valueOf(10.2))).thenReturn(categoryDTO);

        categoryDTO = goal.getAddedGoal();

        int id2 = categoryDTO.getId();

        Assertions.assertEquals(id2, id);
    }

    @DisplayName("Test of the method for getting all goals of current user")
    @Test
    void getAllUserGoalsTest() {
        String request = "t\n10,2";
        InputStream in = new ByteArrayInputStream(request.getBytes());
        System.setIn(in);

        TransactionCategoryDTO goalDTO = new TransactionCategoryDTO(0, CurrentUser.currentUser.getId());
        goalDTO.setName("t");
        goalDTO.setNeededSum(BigDecimal.valueOf(10.2));

        Mockito.when(transactionCategoryService.add(goalDTO)).thenReturn(goalDTO);
        Mockito.when(httpRequestsClass.addGoal("t", BigDecimal.valueOf(10.2))).thenReturn(goalDTO);

        request = "t2\n312,5";
        in = new ByteArrayInputStream(request.getBytes());
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

        String outputReturned = goal.getAllUserGoals();

        StringBuilder output = new StringBuilder();

        for (TransactionCategoryDTO dto : categoryEntities) {
            output.append(dto).append(" Необходимая сумма = ").append(0).append("/").append(dto.getNeededSum()).append("\n");
        }

        Assertions.assertEquals(output.toString(), outputReturned);
    }
}