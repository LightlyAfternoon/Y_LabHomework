package org.example;

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

public class App 
{
    static Scanner scanner = new Scanner(System.in);
    static String command = "";

    /**
     * Login menu - users start point
     */
    public static void main( String[] args )
    {
        UserEntity admin = new UserEntity();

        admin.setName("Admin");
        admin.setEmail("Admin");
        admin.setPassword("1");
        admin.setRole(UserRole.ADMIN);
        admin.setBlocked(false);

        new UserRepository().add(admin);

        while (true) {
            System.out.println("Здравствуйте! Хотите зарегистрироваться или войти в аккаунт? \n" +
                    "/login - войти в аккаунт\n" +
                    "/register - зарегистрироваться\n" +
                    "/exit - выход из приложения");

            if (scanner.hasNext()) {
                command = scanner.next();
            }

            if (command.equals("/login")) {
                System.out.println("Введите почту:");
                String email = scanner.next();

                System.out.println("Введите пароль:");
                String password = scanner.next();

                UserRepository userRepository = new UserRepository();
                UserEntity user = userRepository.findUserWithEmailAndPassword(email, password);

                if (user != null) {
                    CurrentUser.currentUser = user;

                    System.out.println("Вы успешно вошли в систему\n");

                    if (CurrentUser.currentUser.getRole() == UserRole.USER) {
                        menuForUser();
                    } else {
                        menuForAdmin();
                    }

                    return;
                } else {
                    System.out.println("Пользователь с такими почтой и паролем не найдены\n");
                }
            } else if (command.equals("/register")) {
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
                    System.out.println("Вы успешно зарегистрировались");
                } else {
                    System.out.println("Пользователь с такой почтой уже существует\n");
                }
            } else if (command.equals("/exit")) {
                return;
            } else {
                System.out.println("Команда не распознана\n");
            }
        }
    }

    /**
     * Users menu after login
     */
    static void menuForUser() {
        while (true) {
            System.out.println("Введите желаемое действие:\n" +
                    "/budget - установить месячный бюджет\n" +
                    "/goal - установить цель\n" +
                    "/add_transaction - создать транзакцию\n" +
                    "/delete_account - удалить аккаунт\n" +
                    "/show_transactions - вывести все транзакции\n" +
                    "/exit - выйти из приложения\n");

            if (scanner.hasNext()) {
                command = scanner.next();
            }

            if (command.equals("/budget")) {
                System.out.println("Введите бюджет на данный месяц:");
                BigDecimal budget = scanner.nextBigDecimal();

                MonthlyBudgetRepository monthlyBudgetRepository = new MonthlyBudgetRepository();
                MonthlyBudgetEntity monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser);
                monthlyBudgetEntity = monthlyBudgetRepository.findByDateAndUser(monthlyBudgetEntity.getDate(), monthlyBudgetEntity.getUser());

                if (monthlyBudgetEntity != null) {
                    monthlyBudgetEntity.setSum(budget);

                    monthlyBudgetRepository.update(monthlyBudgetEntity);
                } else {
                    monthlyBudgetEntity = new MonthlyBudgetEntity(CurrentUser.currentUser);
                    monthlyBudgetEntity.setSum(budget);

                    monthlyBudgetRepository.add(monthlyBudgetEntity);
                }

                System.out.println("Бюджет на " + monthlyBudgetEntity.getDate() + " теперь составляет " + monthlyBudgetEntity.getSum() + "\n");
            } else if (command.equals("/goal")) {
                System.out.println("Введите названия цели:");
                String name = scanner.nextLine();

                System.out.println("Введите необходимую для цели сумму:");
                BigDecimal sum = scanner.nextBigDecimal();

                TransactionCategoryRepository goalRepository = new TransactionCategoryRepository();
                TransactionCategoryEntity goal = new TransactionCategoryEntity(CurrentUser.currentUser);

                goal.setName(name);
                goal.setNeededSum(sum);

                goalRepository.addGoal(goal);
            } else if (command.equals("/add_transaction")) {
                System.out.println("Введите сумму (положительное число для дохода, отрицательное - для расхода):");
                BigDecimal sum = scanner.nextBigDecimal();

                System.out.println("Введите имя категории/цели из списка ниже или оставьте поле пустым:");
                for (TransactionCategoryEntity category : new TransactionCategoryRepository().findCommonCategoriesOrGoalsWithUser(CurrentUser.currentUser)) {
                    System.out.println("\n" + category.getName() + "\n");
                }
                scanner = new Scanner(System.in);
                String categoryName = scanner.nextLine();
                TransactionCategoryEntity category = new TransactionCategoryRepository().findByName(categoryName);

                System.out.println("Введите дату в формате 2000-12-21 или оставьте поле пустым (будет выбрана текущая дата):");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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

                transactionRepository.add(transaction);
            } else if (command.equals("/delete_account")) {
                System.out.println("Для подтверждения введите команду /confirm\n" +
                        "Для возвращения в меню введите команду /menu:");

                if (scanner.hasNext()) {
                    command = scanner.next();
                }
                
                if (command.equals("/confirm")) {
                    UserEntity user = CurrentUser.currentUser;
                    CurrentUser.currentUser = null;

                    new UserRepository().delete(user);

                    System.out.println("Аккаунт удалён");

                    return;
                } else if (command.equals("/menu")) {
                    menuForUser();

                    return;
                } else {
                    System.out.println("Команда не распознана\n");
                }
            } else if (command.equals("/show_transactions")) {
                for (TransactionEntity transaction : new TransactionRepository().findAllWithUser(CurrentUser.currentUser)) {
                    System.out.println(transaction.toString() + "\n");
                }

                transactionsMenu();

                return;
            } else if (command.equals("/exit")) {
                return;
            } else {
                System.out.println("Команда не распознана\n");
            }
        }
    }

    private static void transactionsMenu() {
        while (true) {
            System.out.println("Введите желаемое действие:\n" +
                    "/filter_transactions - отфильтровать список транзакций\n" +
                    "/edit_transaction - изменить транзакцию\n" +
                    "/delete_transaction - удалить транзакцию\n" +
                    "/menu - вернуться в меню\n");

            if (scanner.hasNext()) {
                command = scanner.next();
            }

            if (command.equals("/filter_transactions")) {
                System.out.println("Введите дату в формате 2000-12-21 или оставьте поле пустым:");
                scanner = new Scanner(System.in);
                String text = scanner.nextLine();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
                scanner = new Scanner(System.in);
                String categoryName = scanner.nextLine();
                TransactionCategoryEntity category = new TransactionCategoryRepository().findByName(categoryName);

                System.out.println("Введите Pos для фильтрации доходов, Neg для фильтрации расходов или оставьте поле пустым:");
                String type = scanner.nextLine();

                for (TransactionEntity transaction : new TransactionRepository().findAllWithDateAndCategoryAndTypeAndUser(date, category, type, CurrentUser.currentUser)) {
                    System.out.println(transaction.toString() + "\n");
                }

                transactionsMenu();
            } else if (command.equals("/edit_transaction")) {
                System.out.println("Введите uuid транзакции, которую необходимо изменить:");
                scanner = new Scanner(System.in);
                String uuid = scanner.nextLine();
                TransactionEntity transaction = new TransactionRepository().findById(UUID.fromString(uuid));

                if (transaction != null) {
                    TransactionRepository transactionRepository = new TransactionRepository();

                    System.out.println("Введите новую сумму (положительное число для дохода, отрицательное - для расхода):");
                    scanner = new Scanner(System.in);
                    BigDecimal sum = scanner.nextBigDecimal();

                    System.out.println("Введите имя новой категории/цели из списка ниже или оставьте поле пустым:");
                    for (TransactionCategoryEntity category : new TransactionCategoryRepository().findCommonCategoriesOrGoalsWithUser(CurrentUser.currentUser)) {
                        System.out.println("\n" + category.getName() + "\n");
                    }
                    scanner = new Scanner(System.in);
                    String categoryName = scanner.nextLine();
                    TransactionCategoryEntity category = new TransactionCategoryRepository().findByName(categoryName);

                    System.out.println("Введите дату в формате 2000-12-21 или оставьте поле пустым (будет выбрана текущая дата):");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
                } else {
                    System.out.println("Транзакция с указанным uuid не найдена");
                }
            } else if (command.equals("/delete_transaction")) {
                System.out.println("Введите uuid транзакции, которую необходимо удалить");
                TransactionRepository transactionRepository = new TransactionRepository();
                scanner = new Scanner(System.in);
                TransactionEntity transaction = transactionRepository.findById(UUID.fromString(scanner.nextLine()));

                if (transaction != null) {
                    System.out.println("Для подтверждения введите команду /confirm\n" +
                            "Для возвращения в меню введите команду /menu:");

                    if (scanner.hasNext()) {
                        command = scanner.next();
                    }

                    if (command.equals("/confirm")) {
                        transactionRepository.delete(transaction);

                        System.out.println("Транзакция удалена");
                    } else if (command.equals("/menu")) {
                        menuForUser();

                        return;
                    } else {
                        System.out.println("Команда не распознана\n");
                    }
                } else {
                    System.out.println("Транзакция с указанным uuid не найдена");
                }
            } else if (command.equals("/menu")) {
                menuForUser();

                return;
            } else {
                System.out.println("Команда не распознана\n");
            }
        }
    }

    /**
     * Admins menu after login
     */
    static void menuForAdmin() {
        while (true) {
            System.out.println("Введите желаемое действие:\n" +
                    "/users - вывести список всех пользователей приложения\n" +
                    "/exit - выйти из приложения\n");

            if (scanner.hasNext()) {
                command = scanner.next();
            }

            if (command.equals("/users")) {
                for (UserEntity user : new UserRepository().findAll()){
                    System.out.println(user.getUuid() + " " + user.getName() + user.getEmail() + user.getRole());
                }
            } else if (command.equals("/exit")) {
                return;
            } else {
                System.out.println("Команда не распознана\n");
            }
        }
    }
}