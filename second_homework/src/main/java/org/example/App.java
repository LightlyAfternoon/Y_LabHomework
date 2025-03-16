package org.example;

import liquibase.exception.LiquibaseException;
import org.example.command.CommandClass;
import org.example.db.ConnectionClass;
import org.example.model.*;
import org.example.repository.UserRepository;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class App 
{
    /**
     * Login menu - users start point
     */
    public static void main( String[] args ) {
        Scanner scanner = new Scanner(System.in);
        String command = "";

        while (true) {
            System.out.println("Здравствуйте! Хотите зарегистрироваться или войти в аккаунт? \n" +
                    "/login" + " - войти в аккаунт\n" +
                    "/register - зарегистрироваться\n" +
                    "/exit - выход из приложения");

            if (scanner.hasNext()) {
                command = scanner.next();
            }

            switch (command) {
                case "/login" -> {
                    UserRole role;
                    try {
                        role = CommandClass.getLoggedInUserRole();
                    } catch (SQLException | LiquibaseException e) {
                        throw new RuntimeException(e);
                    }

                    if (role == UserRole.USER) {
                        menuForUser();

                        return;
                    } else if (role == UserRole.ADMIN) {
                        menuForAdmin();

                        return;
                    }
                }
                case "/register" -> System.out.println(CommandClass.register());
                case "/exit" -> {
                    return;
                }
                default -> System.out.println("Команда не распознана\n");
            }
        }
    }

    /**
     * Users menu after login
     */
    static void menuForUser() {
        Scanner scanner = new Scanner(System.in);
        String command = "";

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
                    MonthlyBudgetEntity monthlyBudgetEntity = CommandClass.addBudget();

                    System.out.println("Бюджет на " + monthlyBudgetEntity.getDate() + " теперь составляет " + monthlyBudgetEntity.getSum() + "\n");
                }
                case "/goal" -> CommandClass.addGoal();
                case "/add_transaction" -> CommandClass.addTransaction();
                case "/delete_account" -> {
                    System.out.println("Для подтверждения введите команду /confirm\n" +
                            "Для возвращения в меню введите команду /menu:");

                    if (scanner.hasNext()) {
                        command = scanner.next();
                    }

                    if (command.equals("/confirm")) {
                        CommandClass.deleteAccount();

                        return;
                    } else if (command.equals("/menu")) {
                        menuForUser();

                        return;
                    } else {
                        System.out.println("Команда не распознана\n");
                    }
                }
                case "/show_transactions" -> {
                    System.out.println(CommandClass.getTransactions());

                    transactionsMenu();

                    return;
                }
                case "/show_goals" -> System.out.println(CommandClass.getAllUserGoals());
                case "/balance" -> System.out.println("Текущий баланс: " + CommandClass.getCurrentBalance());
                case "/balance_for_period" -> {
                    System.out.println("Введите начальную дату в формате 2000-12-21:");
                    scanner = new Scanner(System.in);
                    Date from = getDate(scanner);

                    System.out.println("Введите конечную дату в формате 2000-12-21:");
                    Date to = getDate(scanner);

                    System.out.println("Доход за выбранный период: " + CommandClass.getIncomeForPeriod(from, to));
                    System.out.println("Расход за выбранный период: " + CommandClass.getExpenseForPeriod(from, to) + "\n");
                }
                case "/category_expenses" -> System.out.println(CommandClass.getCategoryExpenses());
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
                    System.out.println(CommandClass.filterTransactions());

                    transactionsMenu();
                }
                case "/edit_transaction" -> CommandClass.editTransaction();
                case "/delete_transaction" -> {
                    System.out.println("Для подтверждения введите команду /confirm\n" +
                            "Для возвращения в меню введите команду /menu:");

                    if (scanner.hasNext()) {
                        command = scanner.next();
                    }

                    if (command.equals("/confirm")) {
                        CommandClass.deleteTransaction();
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

        while (true) {
            System.out.println("Введите желаемое действие:\n" +
                    "/users - вывести список всех пользователей приложения\n" +
                    "/exit - выйти из приложения\n");

            if (scanner.hasNext()) {
                command = scanner.next();
            }

            switch (command) {
                case "/users" -> System.out.println(CommandClass.getAllUsers());
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