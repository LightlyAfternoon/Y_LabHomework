package org.example.command;

import liquibase.exception.LiquibaseException;
import org.example.CurrentUser;
import org.example.model.*;
import org.example.repository.MonthlyBudgetRepository;
import org.example.repository.TransactionCategoryRepository;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class CommandClass {
    private Scanner scanner;
    private final SimpleDateFormat simpleDateFormat;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionCategoryRepository categoryRepository;
    private final MonthlyBudgetRepository budgetRepository;

    public CommandClass() {
        userRepository = new UserRepository();
        transactionRepository = new TransactionRepository();
        categoryRepository = new TransactionCategoryRepository();
        budgetRepository = new MonthlyBudgetRepository();

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public CommandClass(UserRepository newUserRepository, TransactionRepository newTransactionRepository, TransactionCategoryRepository newCategoryRepository, MonthlyBudgetRepository newBudgetRepository) {
        userRepository = newUserRepository;
        transactionRepository = newTransactionRepository;
        categoryRepository = newCategoryRepository;
        budgetRepository = newBudgetRepository;

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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

    public UserRole getLoggedInUserRole() {
        UserEntity user;
        try {
            user = userRepository.findUserWithEmailAndPassword(sendEmail(), sendPassword());
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        if (user != null) {
            CurrentUser.currentUser = user;

            return CurrentUser.currentUser.getRole();
        }

        return null;
    }

    public String getAllUsers() {
        StringBuilder output = new StringBuilder();

        try {
            for (UserEntity user : userRepository.findAll()){
                output.append(user).append("\n");
            }
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        return output.toString();
    }

    private String sendUserName() {
        System.out.println("Введите имя:");

        return scanner.next();
    }

    public UserEntity getRegisteredUser() {
        UserEntity user = new UserEntity();

        user.setEmail(sendEmail());
        user.setPassword(sendPassword());
        user.setName(sendUserName());
        user.setBlocked(false);

        try {
            return userRepository.add(user);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    private BigDecimal sendBudgetSum() {
        System.out.println("Введите бюджет на данный месяц:");

        scanner = new Scanner(System.in);

        return scanner.nextBigDecimal();
    }

    public MonthlyBudgetEntity addBudget() {
        BigDecimal budget = sendBudgetSum();

        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser);
        SimpleDateFormat yearAndMonthDateFormat = new SimpleDateFormat("yyyy-MM");
        try {
            monthlyBudgetEntity = budgetRepository.findByDateAndUser(new Date(yearAndMonthDateFormat.parse(String.valueOf(monthlyBudgetEntity.getDate())).getTime()), monthlyBudgetEntity.getUser());
        } catch (ParseException | SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        try {
            if (monthlyBudgetEntity != null) {
                monthlyBudgetEntity.setSum(budget);
                budgetRepository.update(monthlyBudgetEntity);
            } else {
                monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser);
                monthlyBudgetEntity.setSum(budget);

                monthlyBudgetEntity = budgetRepository.add(monthlyBudgetEntity);
            }
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        return monthlyBudgetEntity;
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

    public TransactionCategoryEntity addGoal() {
        TransactionCategoryEntity goal = new TransactionCategoryEntity(CurrentUser.currentUser);

        goal.setName(sendGoalName());
        goal.setNeededSum(sendGoalSum());

        try {
            return categoryRepository.add(goal);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    private BigDecimal sendTransactionSum() {
        System.out.println("Введите сумму (положительное число для дохода, отрицательное - для расхода):");

        scanner = new Scanner(System.in);

        return scanner.nextBigDecimal();
    }

    private TransactionCategoryEntity sendCategory() {
        System.out.println("Введите имя категории/цели из списка ниже или оставьте поле пустым:");
        try {
            for (TransactionCategoryEntity category : categoryRepository.findCommonCategoriesOrGoalsWithUser(CurrentUser.currentUser)) {
                System.out.println("\n" + category.getName() + "\n");
            }
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
        scanner.nextLine();
        String categoryName = scanner.nextLine();

        try {
            return categoryRepository.findByName(categoryName);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
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

    public TransactionEntity addTransaction() {
        TransactionEntity transaction = new TransactionEntity(CurrentUser.currentUser);

        transaction.setSum(sendTransactionSum());
        transaction.setCategory(sendCategory());
        transaction.setDate(sendDate());
        transaction.setDescription(sendDescription());

        try {
            transaction = transactionRepository.add(transaction);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        checkIfSpendMoreThanBudget(transactionRepository);

        return transaction;
    }

    public boolean deleteAccount() {
        UserEntity user = CurrentUser.currentUser;
        CurrentUser.currentUser = null;

        boolean isDeleted;
        try {
            isDeleted = userRepository.delete(user);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        return isDeleted;
    }

    public String getTransactions() {
        StringBuilder output = new StringBuilder();

        try {
            for (TransactionEntity transaction : transactionRepository.findAllWithUser(CurrentUser.currentUser)) {
                output.append(transaction).append("\n");
            }
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
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

    private TransactionCategoryEntity sendFilterCategory() {
        System.out.println("Введите имя категории/цели из списка ниже или оставьте поле пустым:");
        try {
            for (TransactionCategoryEntity category : categoryRepository.findCommonCategoriesOrGoalsWithUser(CurrentUser.currentUser)) {
                System.out.println("\n" + category.getName() + "\n");
            }
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
        String categoryName = scanner.nextLine();
        TransactionCategoryEntity category;
        try {
            category = categoryRepository.findByName(categoryName);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        return category;
    }

    private String sendFilterSumType() {
        System.out.println("Введите Pos для фильтрации доходов, Neg для фильтрации расходов или оставьте поле пустым:");

        return scanner.nextLine();
    }

    public String filterTransactions() {
        StringBuilder output = new StringBuilder();

        try {
            for (TransactionEntity transaction : transactionRepository.findAllWithDateAndCategoryAndTypeAndUser(sendFilterDate(), sendFilterCategory(), sendFilterSumType(), CurrentUser.currentUser)) {
                output.append(transaction.toString()).append("\n");
            }
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
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

    private TransactionCategoryEntity sendNewTransactionCategory() {
        System.out.println("Введите имя новой категории/цели из списка ниже или оставьте поле пустым:");
        try {
            for (TransactionCategoryEntity category : categoryRepository.findCommonCategoriesOrGoalsWithUser(CurrentUser.currentUser)) {
                System.out.println("\n" + category.getName() + "\n");
            }
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
        String categoryName = scanner.nextLine();
        TransactionCategoryEntity category;
        try {
            category = categoryRepository.findByName(categoryName);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        return category;
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

    public boolean editTransaction() {
        TransactionEntity transaction;
        try {
            transaction = transactionRepository.findById(sendTransactionId());
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        if (transaction != null) {
            transaction.setSum(sendNewTransactionSum());
            transaction.setCategory(sendNewTransactionCategory());
            transaction.setDate(sendNewTransactionDate());
            transaction.setDescription(sendNewTransactionDescription());

            try {
                transactionRepository.update(transaction);
            } catch (SQLException | LiquibaseException e) {
                throw new RuntimeException(e);
            }

            checkIfSpendMoreThanBudget(transactionRepository);

            return true;
        } else {
            return false;
        }
    }

    public boolean deleteTransaction() {
        TransactionEntity transaction;
        try {
            transaction = transactionRepository.findById(sendTransactionId());
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        try {
            return transactionRepository.delete(transaction);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAllUserGoals() {
        StringBuilder output = new StringBuilder();

        try {
            for (TransactionCategoryEntity goal : categoryRepository.findAllUserGoals(CurrentUser.currentUser)) {
                BigDecimal totalSum = BigDecimal.valueOf(0);
                String goalString = goal.toString();

                if (goal.getNeededSum() != null) {
                    try {
                        for (TransactionEntity transaction : transactionRepository.findAllWithUser(CurrentUser.currentUser)) {
                            if (transaction.getCategory() != null && transaction.getCategory().equals(goal) && transaction.getCategory().getNeededSum() != null) {
                                totalSum = totalSum.add(transaction.getSum());
                            }
                        }
                    } catch (SQLException | LiquibaseException e) {
                        throw new RuntimeException(e);
                    }

                    goalString += " Необходимая сумма = " + totalSum + "/" + goal.getNeededSum();
                }

                output.append(goalString).append("\n");
            }
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        return output.toString();
    }

    private void checkIfSpendMoreThanBudget(TransactionRepository transactionRepository) {
        SimpleDateFormat yearAndMonthDateFormat = new SimpleDateFormat("yyyy-MM");
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser);
        try {
            monthlyBudgetEntity = budgetRepository.findByDateAndUser(new Date(yearAndMonthDateFormat.parse(String.valueOf(monthlyBudgetEntity.getDate())).getTime()), monthlyBudgetEntity.getUser());
        } catch (ParseException | SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        if (monthlyBudgetEntity != null) {
            BigDecimal totalSpentSum = BigDecimal.valueOf(0);

            try {
                for (TransactionEntity transactionEntity : transactionRepository.findAllWithUser(CurrentUser.currentUser)) {
                    if (transactionEntity.getSum().compareTo(BigDecimal.valueOf(0)) > 0) {
                        totalSpentSum = totalSpentSum.add(transactionEntity.getSum());
                    }
                }
            } catch (SQLException | LiquibaseException e) {
                throw new RuntimeException(e);
            }

            if (totalSpentSum.compareTo(monthlyBudgetEntity.getSum()) > 0) {
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

        try {
            for (TransactionEntity transactionEntity : transactionRepository.findAllWithUser(CurrentUser.currentUser)) {
                totalSum = totalSum.add(transactionEntity.getSum());
            }
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        return totalSum;
    }

    public BigDecimal getIncomeForPeriod(Date from, Date to) {
        BigDecimal totalSum = BigDecimal.valueOf(0);

        try {
            for (TransactionEntity transactionEntity : transactionRepository.findAllWithUser(CurrentUser.currentUser)) {
                if (transactionEntity.getSum().compareTo(BigDecimal.valueOf(0)) > 0 && transactionEntity.getDate().compareTo(from) >= 0 && transactionEntity.getDate().compareTo(to) <= 0) {
                    totalSum = totalSum.add(transactionEntity.getSum());
                }
            }
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        return totalSum;
    }

    public BigDecimal getExpenseForPeriod(Date from, Date to) {
        BigDecimal totalSum = BigDecimal.valueOf(0);

        try {
            for (TransactionEntity transactionEntity : transactionRepository.findAllWithUser(CurrentUser.currentUser)) {
                if (transactionEntity.getSum().compareTo(BigDecimal.valueOf(0)) < 0 && transactionEntity.getDate().compareTo(from) >= 0 && transactionEntity.getDate().compareTo(to) <= 0) {
                    totalSum = totalSum.add(transactionEntity.getSum());
                }
            }
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }

        totalSum = totalSum.subtract(totalSum.add(totalSum));

        return totalSum;
    }

    public String getCategoryExpenses() {
        BigDecimal totalSum = BigDecimal.valueOf(0);
        StringBuilder output = new StringBuilder();

        try {
            for (TransactionCategoryEntity category : categoryRepository.findAll()) {
                for (TransactionEntity transaction : transactionRepository.findAllWithDateAndCategoryAndTypeAndUser(null, category, "Neg", CurrentUser.currentUser)) {
                    totalSum = totalSum.subtract(transaction.getSum());
                }
                output.append(category.getName()).append(": ").append(totalSum).append("\n");
            }
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
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
    static void menuForUser() {
        Scanner scanner = new Scanner(System.in);
        String command = "";
        CommandClass commandClass = new CommandClass();

        while (true) {
            System.out.println("Введите желаемое действие:\n" +
                    "/budget - установить месячный бюджет\n" +
                    "/goal - установить цель\n" +
                    "/add_transaction - создать транзакцию\n" +
                    "/delete_account - удалить аккаунт\n" +
                    "/show_transactions - вывести все транзакции\n" +
                    "/show_goals - вывести все цели\n" +
                    "/balance - вывести текущий баланс\n" +
                    "/balance_for_period - вывести доход и расход за период\n" +
                    "/category_expenses - вывести расходы по категориям\n" +
                    "/exit - выйти из приложения\n");

            if (scanner.hasNext()) {
                command = scanner.next();
            }

            switch (command) {
                case "/budget" -> {
                    MonthlyBudgetEntity monthlyBudgetEntity = commandClass.addBudget();

                    System.out.println("Бюджет на " + monthlyBudgetEntity.getDate() + " теперь составляет " + monthlyBudgetEntity.getSum() + "\n");
                }
                case "/goal" -> commandClass.addGoal();
                case "/add_transaction" -> commandClass.addTransaction();
                case "/delete_account" -> {
                    System.out.println("Для подтверждения введите команду /confirm\n" +
                            "Для возвращения в меню введите команду /menu:");

                    if (scanner.hasNext()) {
                        command = scanner.next();
                    }

                    if (command.equals("/confirm")) {
                        if (commandClass.deleteAccount()) {
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
                    System.out.println(commandClass.getTransactions());

                    transactionsMenu();

                    return;
                }
                case "/show_goals" -> System.out.println(commandClass.getAllUserGoals());
                case "/balance" -> System.out.println("Текущий баланс: " + commandClass.getCurrentBalance());
                case "/balance_for_period" -> {
                    System.out.println("Введите начальную дату в формате 2000-12-21:");
                    scanner = new Scanner(System.in);
                    Date from = getDate(scanner);

                    System.out.println("Введите конечную дату в формате 2000-12-21:");
                    Date to = getDate(scanner);

                    System.out.println("Доход за выбранный период: " + commandClass.getIncomeForPeriod(from, to));
                    System.out.println("Расход за выбранный период: " + commandClass.getExpenseForPeriod(from, to) + "\n");
                }
                case "/category_expenses" -> System.out.println(commandClass.getCategoryExpenses());
                case "/exit" -> {
                    return;
                }
                default -> System.out.println("Команда не распознана\n");
            }
        }
    }

    private static void transactionsMenu() {
        Scanner scanner = new Scanner(System.in);
        String command = "";
        CommandClass commandClass = new CommandClass();

        while (true) {
            System.out.println("Введите желаемое действие:\n" +
                    "/filter_transactions - отфильтровать список транзакций\n" +
                    "/edit_transaction - изменить транзакцию\n" +
                    "/delete_transaction - удалить транзакцию\n" +
                    "/menu - вернуться в меню\n");

            if (scanner.hasNext()) {
                command = scanner.next();
            }

            switch (command) {
                case "/filter_transactions" -> {
                    System.out.println(commandClass.filterTransactions());

                    transactionsMenu();
                }
                case "/edit_transaction" -> {
                    if (!commandClass.editTransaction()) {
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
                        if (commandClass.deleteTransaction()) {
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
    static void menuForAdmin() {
        Scanner scanner = new Scanner(System.in);
        String command = "";
        CommandClass commandClass = new CommandClass();

        while (true) {
            System.out.println("Введите желаемое действие:\n" +
                    "/users - вывести список всех пользователей приложения\n" +
                    "/exit - выйти из приложения\n");

            if (scanner.hasNext()) {
                command = scanner.next();
            }

            switch (command) {
                case "/users" -> System.out.println(commandClass.getAllUsers());
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