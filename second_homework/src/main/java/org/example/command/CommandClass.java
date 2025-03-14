package org.example.command;

import org.example.CurrentUser;
import org.example.model.*;
import org.example.repository.MonthlyBudgetRepository;
import org.example.repository.TransactionCategoryRepository;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.UUID;

public class CommandClass {
    private static Scanner scanner;
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private CommandClass() {}

    public static UserRole getLoggedInUserRole() {
        scanner = new Scanner(System.in);

        System.out.println("Введите почту:");
        String email = scanner.next();

        System.out.println("Введите пароль:");
        String password = scanner.next();

        UserRepository userRepository = new UserRepository();
        UserEntity user = userRepository.findUserWithEmailAndPassword(email, password);

        if (user != null) {
            CurrentUser.currentUser = user;

            System.out.println("Вы успешно вошли в систему\n");

            return CurrentUser.currentUser.getRole();
        } else {
            System.out.println("Пользователь с такими почтой и паролем не найдены\n");

            return null;
        }
    }

    public static String getAllUsers() {
        StringBuilder output = new StringBuilder();

        for (UserEntity user : new UserRepository().findAll()){
            output.append(user).append("\n");
        }

        return output.toString();
    }

    public static String register() {
        scanner = new Scanner(System.in);

        System.out.println("Введите имя:");
        String name = scanner.next();

        System.out.println("Введите почту:");
        String email = scanner.next();

        System.out.println("Введите пароль:");
        String password = scanner.next();

        UserRepository userRepository = new UserRepository();
        UserEntity user = new UserEntity();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setBlocked(false);

        user = userRepository.add(user);

        if (user != null) {
            return "Вы успешно зарегистрировались\n";
        } else {
            return "Пользователь с такой почтой уже существует\n";
        }
    }

    public static MonthlyBudgetEntity addBudget() {
        System.out.println("Введите бюджет на данный месяц:");

        scanner = new Scanner(System.in);
        BigDecimal budget = scanner.nextBigDecimal();

        MonthlyBudgetRepository monthlyBudgetRepository = new MonthlyBudgetRepository();
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser);
        SimpleDateFormat yearAndMonthDateFormat = new SimpleDateFormat("yyyy-MM");
        try {
            monthlyBudgetEntity = monthlyBudgetRepository.findByDateAndUser(yearAndMonthDateFormat.parse(String.valueOf(monthlyBudgetEntity.getDate())), monthlyBudgetEntity.getUser());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if (monthlyBudgetEntity != null) {
            monthlyBudgetEntity.setSum(budget);

            monthlyBudgetRepository.update(monthlyBudgetEntity);
        } else {
            monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser);
            monthlyBudgetEntity.setSum(budget);

            monthlyBudgetEntity = monthlyBudgetRepository.add(monthlyBudgetEntity);
        }

        return monthlyBudgetEntity;
    }

    public static TransactionCategoryEntity addGoal() {
        System.out.println("Введите названия цели:");

        scanner = new Scanner(System.in);
        String name = scanner.nextLine();

        System.out.println("Введите необходимую для цели сумму:");
        BigDecimal sum = scanner.nextBigDecimal();

        TransactionCategoryRepository goalRepository = new TransactionCategoryRepository();
        TransactionCategoryEntity goal = new TransactionCategoryEntity(CurrentUser.currentUser);

        goal.setName(name);
        goal.setNeededSum(sum);

        return goalRepository.addGoal(goal);
    }

    public static TransactionEntity addTransaction() {
        System.out.println("Введите сумму (положительное число для дохода, отрицательное - для расхода):");

        scanner = new Scanner(System.in);
        BigDecimal sum = scanner.nextBigDecimal();

        System.out.println("Введите имя категории/цели из списка ниже или оставьте поле пустым:");
        for (TransactionCategoryEntity category : new TransactionCategoryRepository().findCommonCategoriesOrGoalsWithUser(CurrentUser.currentUser)) {
            System.out.println("\n" + category.getName() + "\n");
        }
        scanner.nextLine();
        String categoryName = scanner.nextLine();
        TransactionCategoryEntity category = new TransactionCategoryRepository().findByName(categoryName);

        System.out.println("Введите дату в формате 2000-12-21 или оставьте поле пустым (будет выбрана текущая дата):");
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

        System.out.println("Введите описание или оставьте поле пустым:");
        String description = scanner.nextLine();

        TransactionRepository transactionRepository = new TransactionRepository();
        TransactionEntity transaction = new TransactionEntity(CurrentUser.currentUser);

        transaction.setSum(sum);
        transaction.setCategory(category);
        transaction.setDate(date);
        transaction.setDescription(description);

        transaction = transactionRepository.add(transaction);

        checkIfSpendMoreThanBudget(transactionRepository);

        return transaction;
    }

    public static boolean deleteAccount() {
        UserEntity user = CurrentUser.currentUser;
        CurrentUser.currentUser = null;

        boolean isDeleted = new UserRepository().delete(user);

        if (isDeleted) {
            System.out.println("Аккаунт удалён");
        } else {
            System.out.println("Не удалось удалить аккаунт");
        }

        return isDeleted;
    }

    public static String getTransactions() {
        StringBuilder output = new StringBuilder();

        for (TransactionEntity transaction : new TransactionRepository().findAllWithUser(CurrentUser.currentUser)) {
            output.append(transaction).append("\n");
        }

        return output.toString();
    }

    public static String filterTransactions() {
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

        System.out.println("Введите имя категории/цели из списка ниже или оставьте поле пустым:");
        for (TransactionCategoryEntity category : new TransactionCategoryRepository().findCommonCategoriesOrGoalsWithUser(CurrentUser.currentUser)) {
            System.out.println("\n" + category.getName() + "\n");
        }
        String categoryName = scanner.nextLine();
        TransactionCategoryEntity category = new TransactionCategoryRepository().findByName(categoryName);

        System.out.println("Введите Pos для фильтрации доходов, Neg для фильтрации расходов или оставьте поле пустым:");
        String type = scanner.nextLine();

        StringBuilder output = new StringBuilder();

        for (TransactionEntity transaction : new TransactionRepository().findAllWithDateAndCategoryAndTypeAndUser(date, category, type, CurrentUser.currentUser)) {
            output.append(transaction.toString()).append("\n");
        }

        return output.toString();
    }

    public static boolean editTransaction() {
        System.out.println("Введите uuid транзакции, которую необходимо изменить:");

        scanner = new Scanner(System.in);
        String uuid = scanner.nextLine();
        TransactionEntity transaction = new TransactionRepository().findById(UUID.fromString(uuid));

        if (transaction != null) {
            TransactionRepository transactionRepository = new TransactionRepository();

            System.out.println("Введите новую сумму (положительное число для дохода, отрицательное - для расхода):");
            BigDecimal sum = scanner.nextBigDecimal();

            System.out.println("Введите имя новой категории/цели из списка ниже или оставьте поле пустым:");
            for (TransactionCategoryEntity category : new TransactionCategoryRepository().findCommonCategoriesOrGoalsWithUser(CurrentUser.currentUser)) {
                System.out.println("\n" + category.getName() + "\n");
            }
            String categoryName = scanner.nextLine();
            TransactionCategoryEntity category = new TransactionCategoryRepository().findByName(categoryName);

            System.out.println("Введите дату в формате 2000-12-21 или оставьте поле пустым (будет выбрана текущая дата):");
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

            System.out.println("Введите новое описание или оставьте поле пустым:");
            String description = scanner.nextLine();

            transaction.setSum(sum);
            transaction.setCategory(category);
            transaction.setDate(date);
            transaction.setDescription(description);

            transactionRepository.update(transaction);

            checkIfSpendMoreThanBudget(transactionRepository);

            return true;
        } else {
            System.out.println("Транзакция с указанным uuid не найдена");

            return false;
        }
    }

    public static boolean deleteTransaction() {
        System.out.println("Введите uuid транзакции, которую необходимо удалить");

        TransactionRepository transactionRepository = new TransactionRepository();
        scanner = new Scanner(System.in);
        TransactionEntity transaction = transactionRepository.findById(UUID.fromString(scanner.nextLine()));

        if (transaction != null) {
            System.out.println("Транзакция удалена");
        } else {
            System.out.println("Транзакция с указанным uuid не найдена");
        }

        return transactionRepository.delete(transaction);
    }

    public static String getAllUserGoals() {
        StringBuilder output = new StringBuilder();

        for (TransactionCategoryEntity goal : new TransactionCategoryRepository().findAllUserGoals(CurrentUser.currentUser)) {
            output.append(goal).append("\n");
        }

        return output.toString();
    }

    private static void checkIfSpendMoreThanBudget(TransactionRepository transactionRepository) {
        SimpleDateFormat yearAndMonthDateFormat = new SimpleDateFormat("yyyy-MM");
        MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser);
        try {
            monthlyBudgetEntity = new MonthlyBudgetRepository().findByDateAndUser(yearAndMonthDateFormat.parse(String.valueOf(monthlyBudgetEntity.getDate())), monthlyBudgetEntity.getUser());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if (monthlyBudgetEntity != null) {
            BigDecimal totalSpentSum = BigDecimal.valueOf(0);

            for (TransactionEntity transactionEntity : transactionRepository.findAllWithUser(CurrentUser.currentUser)) {
                if (transactionEntity.getSum().compareTo(BigDecimal.valueOf(0)) > 0) {
                    totalSpentSum = totalSpentSum.add(transactionEntity.getSum());
                }
            }

            if (totalSpentSum.compareTo(monthlyBudgetEntity.getSum()) > 0) {
                System.out.println("!Вы превысили свой месячный бюджет!");

                sendNotificationEmail(CurrentUser.currentUser);
            }
        }
    }

    private static void sendNotificationEmail(UserEntity user) {
        System.out.println("Уведомление будет отправлено на почту " + user.getEmail());
    }

    public static BigDecimal getCurrentBalance() {
        BigDecimal totalSum = BigDecimal.valueOf(0);

        for (TransactionEntity transactionEntity : new TransactionRepository().findAllWithUser(CurrentUser.currentUser)) {
            totalSum = totalSum.add(transactionEntity.getSum());
        }

        return totalSum;
    }

    public static BigDecimal getIncomeForPeriod(Date from, Date to) {
        BigDecimal totalSum = BigDecimal.valueOf(0);

        for (TransactionEntity transactionEntity : new TransactionRepository().findAllWithUser(CurrentUser.currentUser)) {
            if (transactionEntity.getSum().compareTo(BigDecimal.valueOf(0)) > 0 && transactionEntity.getDate().compareTo(from) >= 0 && transactionEntity.getDate().compareTo(to) <= 0) {
                totalSum = totalSum.add(transactionEntity.getSum());
            }
        }

        return totalSum;
    }

    public static BigDecimal getExpenseForPeriod(Date from, Date to) {
        BigDecimal totalSum = BigDecimal.valueOf(0);

        for (TransactionEntity transactionEntity : new TransactionRepository().findAllWithUser(CurrentUser.currentUser)) {
            if (transactionEntity.getSum().compareTo(BigDecimal.valueOf(0)) < 0 && transactionEntity.getDate().compareTo(from) >= 0 && transactionEntity.getDate().compareTo(to) <= 0) {
                totalSum = totalSum.add(transactionEntity.getSum());
            }
        }

        totalSum = totalSum.subtract(totalSum.add(totalSum));

        return totalSum;
    }

    public static String getCategoryExpenses() {
        TransactionCategoryRepository transactionCategoryRepository = new TransactionCategoryRepository();
        TransactionRepository transactionRepository = new TransactionRepository();
        BigDecimal totalSum = BigDecimal.valueOf(0);
        StringBuilder output = new StringBuilder();

        for (TransactionCategoryEntity category : transactionCategoryRepository.findAll()) {
            for (TransactionEntity transaction : transactionRepository.findAllWithDateAndCategoryAndTypeAndUser(null, category, "Neg", CurrentUser.currentUser)) {
                totalSum = totalSum.subtract(transaction.getSum());
            }
            output.append(category.getName()).append(": ").append(totalSum).append("\n");
        }

        return output.toString();
    }
}