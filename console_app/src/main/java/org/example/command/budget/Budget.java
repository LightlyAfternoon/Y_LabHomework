package org.example.command.budget;

import org.example.CurrentUser;
import org.example.command.HttpRequestsClass;
import org.example.controller.dto.MonthlyBudgetDTO;

import java.math.BigDecimal;
import java.util.Scanner;

public class Budget {
    private final HttpRequestsClass httpRequestsClass;
    MonthlyBudgetDTO monthlyBudgetDTO;
    Scanner scanner;

    public Budget(HttpRequestsClass httpRequestsClass) {
        this.httpRequestsClass = httpRequestsClass;
        this.monthlyBudgetDTO = new MonthlyBudgetDTO(CurrentUser.currentUser.getId());
    }

    public void addBudget() {
        getAddedBudget();

        showBudget();
    }

    public MonthlyBudgetDTO getAddedBudget() {
        return httpRequestsClass.addBudget(sendBudgetSum());
    }

    private BigDecimal sendBudgetSum() {
        System.out.println("Введите бюджет на данный месяц:");

        scanner = new Scanner(System.in);

        return scanner.nextBigDecimal();
    }

    public void showBudget() {
        if (monthlyBudgetDTO.getSum() == null) {
            monthlyBudgetDTO = httpRequestsClass.getBudget();
        }

        System.out.println("Бюджет на " + monthlyBudgetDTO.getDate() + " составляет " + monthlyBudgetDTO.getSum() + "\n");
    }
}