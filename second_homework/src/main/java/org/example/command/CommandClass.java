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
}