package org.example.command.goal;

import org.example.CurrentUser;
import org.example.command.HttpRequestsClass;
import org.example.controller.dto.TransactionCategoryDTO;
import org.example.controller.dto.TransactionDTO;

import java.math.BigDecimal;
import java.util.Scanner;

public class Goal {
    private final HttpRequestsClass httpRequestsClass;
    Scanner scanner;

    public Goal(HttpRequestsClass httpRequestsClass) {
        this.httpRequestsClass = httpRequestsClass;
    }

    public void addGoal() {
        getAddedGoal();
    }

    public TransactionCategoryDTO getAddedGoal() {
        return httpRequestsClass.addGoal(sendGoalName(), sendGoalSum());
    }

    public void showGoals() {
        System.out.println(getAllUserGoals());
    }

    private String sendGoalName() {
        System.out.println("Введите названия цели:");

        scanner = new Scanner(System.in);

        return scanner.nextLine();
    }

    private BigDecimal sendGoalSum() {
        System.out.println("Введите необходимую для цели сумму:");

        return scanner.nextBigDecimal();
    }

    public String getAllUserGoals() {
        StringBuilder output = new StringBuilder();

        for (TransactionCategoryDTO goalDTO : httpRequestsClass.getAllUserGoals(CurrentUser.currentUser.getId())) {
            BigDecimal totalSum = BigDecimal.valueOf(0);
            String goalString = goalDTO.toString();

            if (goalDTO.getNeededSum() != null) {
                for (TransactionDTO transactionDTO : httpRequestsClass.getTransactions()) {
                    if (transactionDTO.getCategoryId() != 0 && transactionDTO.getCategoryId() == goalDTO.getId() && httpRequestsClass.getCategoryOrGoalWithId(transactionDTO.getCategoryId()).getNeededSum() != null) {
                        totalSum = totalSum.add(transactionDTO.getSum());
                    }
                }

                goalString += " Необходимая сумма = " + totalSum + "/" + goalDTO.getNeededSum();
            }

            output.append(goalString).append("\n");
        }

        return output.toString();
    }
}