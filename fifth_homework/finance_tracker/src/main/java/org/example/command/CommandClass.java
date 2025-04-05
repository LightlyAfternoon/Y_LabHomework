package org.example.command;

import org.example.CurrentUser;
import org.example.controller.dto.MonthlyBudgetDTO;
import org.example.controller.dto.TransactionCategoryDTO;
import org.example.controller.dto.TransactionDTO;
import org.example.controller.dto.UserDTO;
import org.example.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

/**
 * This class contains methods for user input in console commands.
 */
public class CommandClass {
    private Scanner scanner;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final HttpRequestsClass httpRequestsClass;

    @Autowired
    public CommandClass(HttpRequestsClass httpRequestsClass) {
        this.httpRequestsClass = httpRequestsClass;
    }

    private String sendEmail() {
        scanner = new Scanner(System.in);

        System.out.println("Введите почту:");

        return scanner.next();
    }

    private String sendPassword() {
        System.out.println("Введите пароль:");

        return scanner.next();
    }

    private String sendUserName() {
        System.out.println("Введите имя:");

        return scanner.next();
    }

    public UserRole getLoggedInUserRole() {
        UserDTO userDTO = httpRequestsClass.getLoggedInUser(sendEmail(), sendPassword());

        if (userDTO != null) {
            CurrentUser.currentUser = new UserEntity.UserBuilder(userDTO.getEmail(), userDTO.getPassword(), userDTO.getName()).
                    id(userDTO.getId()).role(userDTO.getRole()).isBlocked(userDTO.isBlocked()).build();

            return CurrentUser.currentUser.getRole();
        }

        return null;
    }

    public String getAllUsers() {
        StringBuilder output = new StringBuilder();

            for (UserDTO userDTO : httpRequestsClass.getAllUsers()){
                output.append(userDTO).append("\n");
            }

        return output.toString();
    }

    public UserDTO getRegisteredUser() {
        return httpRequestsClass.getRegisteredUser(sendEmail(), sendPassword(), sendUserName());
    }

    private BigDecimal sendBudgetSum() {
        System.out.println("Введите бюджет на данный месяц:");

        scanner = new Scanner(System.in);

        return scanner.nextBigDecimal();
    }

    public MonthlyBudgetDTO addBudget() {
        return httpRequestsClass.addBudget(sendBudgetSum());
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

    public TransactionCategoryDTO addGoal() {
        return httpRequestsClass.addGoal(sendGoalName(), sendGoalSum());
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

    private String sendDescription() {
        System.out.println("Введите описание или оставьте поле пустым:");

        return scanner.nextLine();
    }

    public TransactionDTO addTransaction() {
        BigDecimal sum = sendTransactionSum();
        TransactionCategoryDTO categoryDTO = sendCategory();
        Date date = sendDate();
        String description = sendDescription();

        TransactionDTO transactionDTO = httpRequestsClass.addTransaction(sum, (categoryDTO != null ? categoryDTO.getId() : 0), date, description);

        checkIfSpendMoreThanBudget();

        return transactionDTO;
    }

    public boolean deleteAccount() {
        return httpRequestsClass.deleteAccount(CurrentUser.currentUser.getId());
    }

    public String getTransactions() {
        StringBuilder output = new StringBuilder();

        for (TransactionDTO transactionDTO : httpRequestsClass.getTransactions()){
            output.append(transactionDTO).append("\n");
        }

        return output.toString();
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

    public String filterTransactions() {
        StringBuilder output = new StringBuilder();
        Date date = sendFilterDate();
        TransactionCategoryDTO categoryDTO = sendFilterCategory();
        String type = sendFilterSumType();

        for (TransactionDTO transactionDTO : httpRequestsClass.filterTransactions(date, categoryDTO != null ? categoryDTO.getId() : 0, type, CurrentUser.currentUser.getId())){
            output.append(transactionDTO).append("\n");
        }

        return output.toString();
    }

    private int sendTransactionId() {
        System.out.println("Введите id транзакции:");

        scanner = new Scanner(System.in);

        return scanner.nextInt();
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

    private String sendNewTransactionDescription() {
        System.out.println("Введите новое описание или оставьте поле пустым:");

        return scanner.nextLine();
    }

    public boolean editTransaction() {
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

    public boolean deleteTransaction() {
        return httpRequestsClass.deleteTransaction(sendTransactionId());
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
                if (transactionDTO.getSum().compareTo(BigDecimal.valueOf(0)) > 0 && transactionDTO.getDate().compareTo(from) >= 0 && transactionDTO.getDate().compareTo(to) <= 0) {
                    totalSum = totalSum.add(transactionDTO.getSum());
                }
            }

        return totalSum;
    }

    public BigDecimal getExpenseForPeriod(Date from, Date to) {
        BigDecimal totalSum = BigDecimal.valueOf(0);

        for (TransactionDTO transactionDTO : httpRequestsClass.getTransactions()) {
            if (transactionDTO.getSum().compareTo(BigDecimal.valueOf(0)) < 0 && transactionDTO.getDate().compareTo(from) >= 0 && transactionDTO.getDate().compareTo(to) <= 0) {
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

    public void showMenu(UserRole role) {
        if (role == UserRole.USER) {
            menuForUser();
        } else if (role == UserRole.ADMIN) {
            menuForAdmin();
        }
    }

    /**
     * Users menu after login
     */
    void menuForUser() {
        Scanner scanner = new Scanner(System.in);
        String command = "";

        while (true) {
            System.out.println("""
                    Введите желаемое действие:
                    /budget - установить месячный бюджет
                    /goal - установить цель
                    /add_transaction - создать транзакцию
                    /delete_account - удалить аккаунт
                    /show_transactions - вывести все транзакции
                    /show_goals - вывести все цели
                    /balance - вывести текущий баланс
                    /balance_for_period - вывести доход и расход за период
                    /category_expenses - вывести расходы по категориям
                    /exit - выйти из приложения
                    """);

            if (scanner.hasNext()) {
                command = scanner.next();
            }

            switch (command) {
                case "/budget" -> {
                    MonthlyBudgetDTO monthlyBudgetEntity = addBudget();

                    System.out.println("Бюджет на " + monthlyBudgetEntity.getDate() + " теперь составляет " + monthlyBudgetEntity.getSum() + "\n");
                }
                case "/goal" -> addGoal();
                case "/add_transaction" -> addTransaction();
                case "/delete_account" -> {
                    System.out.println("Для подтверждения введите команду /confirm\n" +
                            "Для возвращения в меню введите команду /menu:");

                    if (scanner.hasNext()) {
                        command = scanner.next();
                    }

                    if (command.equals("/confirm")) {
                        if (deleteAccount()) {
                            System.out.println("Аккаунт удалён");
                        } else {
                            System.out.println("Не удалось удалить аккаунт");
                        }

                        return;
                    } else if (command.equals("/menu")) {
                        menuForUser();

                        return;
                    } else {
                        System.out.println("Команда не распознана\n");
                    }
                }
                case "/show_transactions" -> {
                    System.out.println(getTransactions());

                    transactionsMenu();

                    return;
                }
                case "/show_goals" -> System.out.println(getAllUserGoals());
                case "/balance" -> System.out.println("Текущий баланс: " + getCurrentBalance());
                case "/balance_for_period" -> {
                    System.out.println("Введите начальную дату в формате 2000-12-21:");
                    scanner = new Scanner(System.in);
                    Date from = getDate(scanner);

                    System.out.println("Введите конечную дату в формате 2000-12-21:");
                    Date to = getDate(scanner);

                    System.out.println("Доход за выбранный период: " + getIncomeForPeriod(from, to));
                    System.out.println("Расход за выбранный период: " + getExpenseForPeriod(from, to) + "\n");
                }
                case "/category_expenses" -> System.out.println(getCategoryExpenses());
                case "/exit" -> {
                    return;
                }
                default -> System.out.println("Команда не распознана\n");
            }
        }
    }

    private void transactionsMenu() {
        Scanner scanner = new Scanner(System.in);
        String command = "";

        while (true) {
            System.out.println("""
                    Введите желаемое действие:
                    /filter_transactions - отфильтровать список транзакций
                    /edit_transaction - изменить транзакцию
                    /delete_transaction - удалить транзакцию
                    /menu - вернуться в меню
                    """);

            if (scanner.hasNext()) {
                command = scanner.next();
            }

            switch (command) {
                case "/filter_transactions" -> {
                    System.out.println(filterTransactions());

                    transactionsMenu();
                }
                case "/edit_transaction" -> {
                    if (!editTransaction()) {
                        System.out.println("Транзакция с указанным id не найдена");
                    }
                }
                case "/delete_transaction" -> {
                    System.out.println("Для подтверждения введите команду /confirm\n" +
                            "Для возвращения в меню введите команду /menu:");

                    if (scanner.hasNext()) {
                        command = scanner.next();
                    }

                    if (command.equals("/confirm")) {
                        if (deleteTransaction()) {
                            System.out.println("Транзакция удалена");
                        } else {
                            System.out.println("Транзакция с указанным id не найдена");
                        }
                    } else if (command.equals("/menu")) {
                        menuForUser();

                        return;
                    } else {
                        System.out.println("Команда не распознана\n");
                    }
                }
                case "/menu" -> {
                    menuForUser();

                    return;
                }
                default -> System.out.println("Команда не распознана\n");
            }
        }
    }

    /**
     * Admins menu after login
     */
    void menuForAdmin() {
        Scanner scanner = new Scanner(System.in);
        String command = "";

        while (true) {
            System.out.println("""
                    Введите желаемое действие:
                    /users - вывести список всех пользователей приложения
                    /exit - выйти из приложения
                    """);

            if (scanner.hasNext()) {
                command = scanner.next();
            }

            switch (command) {
                case "/users" -> System.out.println(getAllUsers());
                case "/exit" -> {
                    return;
                }
                default -> System.out.println("Команда не распознана\n");
            }
        }
    }

    private static Date getDate(Scanner scanner) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String text = scanner.nextLine();

        try {
            return new Date(simpleDateFormat.parse(text).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}