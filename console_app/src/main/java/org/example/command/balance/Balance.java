package org.example.command.balance;

import org.example.CurrentUser;
import org.example.command.HttpRequestsClass;
import org.example.controller.dto.TransactionCategoryDTO;
import org.example.controller.dto.TransactionDTO;

import java.math.BigDecimal;
import java.sql.Date;

public class Balance {
    private final HttpRequestsClass httpRequestsClass;

    public Balance(HttpRequestsClass httpRequestsClass) {
        this.httpRequestsClass = httpRequestsClass;
    }

    public void showBalance() {
        System.out.println("Текущий баланс: " + getCurrentBalance());
    }

    public void showIncomeForPeriod(Date from, Date to) {
        System.out.println("Доход за выбранный период: " + getIncomeForPeriod(from, to));
    }

    public void showExpenseForPeriod(Date from, Date to) {
        System.out.println("Расход за выбранный период: " + getExpenseForPeriod(from, to) + "\n");
    }

    public void showCategoryExpense() {
        System.out.println(getCategoryExpenses());
    }

    public BigDecimal getCurrentBalance() {
        BigDecimal totalSum = BigDecimal.valueOf(0);

        for (TransactionDTO transactionDTO : httpRequestsClass.getTransactions()) {
            totalSum = totalSum.add(transactionDTO.getSum());
        }

        return totalSum;
    }

    public BigDecimal getIncomeForPeriod(Date from, Date to) {
        BigDecimal totalSum = BigDecimal.valueOf(0);

        for (TransactionDTO transactionDTO : httpRequestsClass.getTransactions()) {
            if (transactionDTO.getSum().compareTo(BigDecimal.valueOf(0)) > 0 && transactionDTO.getDate()
                    .compareTo(from) >= 0 && transactionDTO.getDate().compareTo(to) <= 0) {
                totalSum = totalSum.add(transactionDTO.getSum());
            }
        }

        return totalSum;
    }

    public BigDecimal getExpenseForPeriod(Date from, Date to) {
        BigDecimal totalSum = BigDecimal.valueOf(0);

        for (TransactionDTO transactionDTO : httpRequestsClass.getTransactions()) {
            if (transactionDTO.getSum().compareTo(BigDecimal.valueOf(0)) < 0 && transactionDTO.getDate()
                    .compareTo(from) >= 0 && transactionDTO.getDate().compareTo(to) <= 0) {
                totalSum = totalSum.add(transactionDTO.getSum());
            }
        }

        totalSum = totalSum.subtract(totalSum.add(totalSum));

        return totalSum;
    }

    public String getCategoryExpenses() {
        BigDecimal totalSum = BigDecimal.valueOf(0);
        StringBuilder output = new StringBuilder();

        for (TransactionCategoryDTO categoryDTO : httpRequestsClass.getAllCommonCategoriesOrGoalsWithCurrentUser()) {
            for (TransactionDTO transactionDTO : httpRequestsClass.filterTransactions(null, categoryDTO.getId(), "Neg", CurrentUser.currentUser.getId())) {
                totalSum = totalSum.subtract(transactionDTO.getSum());
            }
            output.append(categoryDTO.getName()).append(": ").append(totalSum).append("\n");
        }

        return output.toString();
    }
}