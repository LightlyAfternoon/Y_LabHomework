package org.example.command.transaction;

import org.example.CurrentUser;
import org.example.command.HttpRequestsClass;
import org.example.command.Invoker;
import org.example.command.menu.Menu;
import org.example.command.menu.ShowTransactionMenuCommand;
import org.example.command.menu.ShowUserMenuCommand;
import org.example.controller.dto.MonthlyBudgetDTO;
import org.example.controller.dto.TransactionCategoryDTO;
import org.example.controller.dto.TransactionDTO;
import org.example.model.UserEntity;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Transaction {
    private final HttpRequestsClass httpRequestsClass;
    Scanner scanner;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Transaction(HttpRequestsClass httpRequestsClass) {
        this.httpRequestsClass = httpRequestsClass;
    }

    public void addTransaction() {
        returnAddedTransaction();
    }

    public void editTransaction() {
        if (!transactionIsEdited()) {
            System.out.println("Транзакция с указанным id не найдена");
        }
    }

    public void deleteTransaction() {
        Invoker invoker = new Invoker();
        Menu menu = new Menu(httpRequestsClass);

        System.out.println("Для подтверждения введите команду /confirm\n" +
                "Для возвращения в меню введите команду /menu:");

        String userInput = "";

        if (scanner.hasNext()) {
            userInput = scanner.next();
        }

        if (userInput.equals("/confirm")) {
            if (transactionDeleted()) {
                System.out.println("Транзакция удалена");
            } else {
                System.out.println("Транзакция с указанным id не найдена");
            }

            invoker.addCommand(new ShowTransactionMenuCommand(menu));
        } else if (userInput.equals("/menu")) {
            invoker.addCommand(new ShowUserMenuCommand(menu));
        } else {
            System.out.println("Команда не распознана\n");

            invoker.addCommand(new ShowUserMenuCommand(menu));
        }

        invoker.doCommands();
    }

    public void showFilteredTransactions() {
        System.out.println(returnFilteredTransactions());
    }

    public void showTransactions() {
        System.out.println(getTransactions());
    }

    public TransactionDTO returnAddedTransaction() {
        BigDecimal sum = sendTransactionSum();
        TransactionCategoryDTO categoryDTO = sendCategory();
        Date date = sendDate();
        String description = sendDescription();

        TransactionDTO transactionDTO = httpRequestsClass.addTransaction(sum, (categoryDTO != null ? categoryDTO.getId() : 0), date, description);

        checkIfSpendMoreThanBudget();

        return transactionDTO;
    }

    public boolean transactionIsEdited() {
        int id = sendTransactionId();
        BigDecimal sum = sendNewTransactionSum();
        TransactionCategoryDTO categoryDTO = sendNewTransactionCategory();
        Date date = sendNewTransactionDate();
        String description = sendNewTransactionDescription();

        if (httpRequestsClass.editTransaction(id, sum, (categoryDTO != null ? categoryDTO.getId() : 0), date, description)) {
            checkIfSpendMoreThanBudget();

            return true;
        }

        return false;
    }

    public boolean transactionDeleted() {
        return httpRequestsClass.deleteTransaction(sendTransactionId());
    }

    private BigDecimal sendTransactionSum() {
        System.out.println("Введите сумму (положительное число для дохода, отрицательное - для расхода):");

        scanner = new Scanner(System.in);

        return scanner.nextBigDecimal();
    }

    private TransactionCategoryDTO sendCategory() {
        System.out.println("Введите имя категории/цели из списка ниже или оставьте поле пустым:");

        for (TransactionCategoryDTO categoryDTO : httpRequestsClass.getAllCommonCategoriesOrGoalsWithCurrentUser()) {
            System.out.println("\n" + categoryDTO.getName() + "\n");
        }

        scanner.nextLine();
        String categoryName = scanner.nextLine();

        return httpRequestsClass.getCategoryOrGoalWithName(categoryName);
    }

    private Date sendDate() {
        System.out.println("Введите дату в формате 2000-12-21 или оставьте поле пустым (будет выбрана текущая дата):");
        String text = scanner.nextLine();

        if (!text.isBlank()) {
            try {
                return new Date(simpleDateFormat.parse(text).getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String sendDescription() {
        System.out.println("Введите описание или оставьте поле пустым:");
        return scanner.nextLine();
    }

    private BigDecimal sendNewTransactionSum() {
        System.out.println("Введите новую сумму (положительное число для дохода, отрицательное - для расхода):");

        return scanner.nextBigDecimal();
    }

    private TransactionCategoryDTO sendNewTransactionCategory() {
        System.out.println("Введите имя новой категории/цели из списка ниже или оставьте поле пустым:");

        for (TransactionCategoryDTO categoryDTO : httpRequestsClass.getAllCommonCategoriesOrGoalsWithCurrentUser()) {
            System.out.println("\n" + categoryDTO.getName() + "\n");
        }

        scanner.nextLine();
        String categoryName = scanner.nextLine();

        return httpRequestsClass.getCategoryOrGoalWithName(categoryName);
    }

    private Date sendNewTransactionDate() {
        System.out.println("Введите новую дату в формате 2000-12-21 или оставьте поле пустым (будет выбрана текущая дата):");
        String text = scanner.nextLine();
        Date date;
        if (!text.isBlank()) {
            try {
                date = new Date(simpleDateFormat.parse(text).getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                date = new Date(simpleDateFormat.parse(new Date(System.currentTimeMillis()).toString()).getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        return date;
    }

    private String sendNewTransactionDescription() {
        System.out.println("Введите новое описание или оставьте поле пустым:");

        return scanner.nextLine();
    }

    private int sendTransactionId() {
        System.out.println("Введите id транзакции:");

        scanner = new Scanner(System.in);

        return scanner.nextInt();
    }

    public String getTransactions() {
        StringBuilder output = new StringBuilder();

        for (TransactionDTO transactionDTO : httpRequestsClass.getTransactions()){
            output.append(transactionDTO).append("\n");
        }

        return output.toString();
    }

    private void checkIfSpendMoreThanBudget() {
        MonthlyBudgetDTO monthlyBudgetDTO = httpRequestsClass.getBudget();

        if (monthlyBudgetDTO != null) {
            BigDecimal totalSpentSum = BigDecimal.valueOf(0);

            for (TransactionDTO transactionDTO : httpRequestsClass.getTransactions()) {
                if (transactionDTO.getSum().compareTo(BigDecimal.valueOf(0)) > 0) {
                    totalSpentSum = totalSpentSum.add(transactionDTO.getSum());
                }
            }

            if (totalSpentSum.compareTo(monthlyBudgetDTO.getSum()) > 0) {
                System.out.println("!Вы превысили свой месячный бюджет!");

                sendNotificationEmail(CurrentUser.currentUser);
            }
        }
    }

    private void sendNotificationEmail(UserEntity user) {
        System.out.println("Уведомление будет отправлено на почту " + user.getEmail());
    }

    private Date sendFilterDate() {
        System.out.println("Введите дату в формате 2000-12-21 или оставьте поле пустым:");

        scanner = new Scanner(System.in);
        String text = scanner.nextLine();
        Date date = null;

        if (!text.isBlank()) {
            try {
                date = new Date(simpleDateFormat.parse(text).getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        return date;
    }

    private TransactionCategoryDTO sendFilterCategory() {
        System.out.println("Введите имя категории/цели из списка ниже или оставьте поле пустым:");

        for (TransactionCategoryDTO categoryDTO : httpRequestsClass.getAllCommonCategoriesOrGoalsWithCurrentUser()) {
            System.out.println("\n" + categoryDTO.getName() + "\n");
        }

        String categoryName = scanner.nextLine();

        return httpRequestsClass.getCategoryOrGoalWithName(categoryName);
    }

    private String sendFilterSumType() {
        System.out.println("Введите Pos для фильтрации доходов, Neg для фильтрации расходов или оставьте поле пустым:");

        return scanner.nextLine();
    }

    public String returnFilteredTransactions() {
        StringBuilder output = new StringBuilder();
        Date date = sendFilterDate();
        TransactionCategoryDTO categoryDTO = sendFilterCategory();
        String type = sendFilterSumType();

        for (TransactionDTO transactionDTO : httpRequestsClass.filterTransactions(date, categoryDTO != null ? categoryDTO.getId() : 0, type, CurrentUser.currentUser.getId())) {
            output.append(transactionDTO).append("\n");
        }

        return output.toString();
    }
}