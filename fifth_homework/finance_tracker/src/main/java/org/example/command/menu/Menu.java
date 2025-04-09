package org.example.command.menu;

import org.example.command.HttpRequestsClass;
import org.example.command.Invoker;
import org.example.command.balance.*;
import org.example.command.budget.AddBudgetCommand;
import org.example.command.budget.Budget;
import org.example.command.budget.ShowBudgetCommand;
import org.example.command.goal.AddGoalCommand;
import org.example.command.goal.Goal;
import org.example.command.goal.ShowGoalsCommand;
import org.example.command.transaction.*;
import org.example.command.user.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Menu {
    private final HttpRequestsClass httpRequestsClass;

    public Menu(HttpRequestsClass httpRequestsClass) {
        this.httpRequestsClass = httpRequestsClass;
    }

    public void showHelloMenu() {
        Scanner scanner = new Scanner(System.in);
        String userInput = "";
        User user = new User(httpRequestsClass);
        Invoker invoker = new Invoker();

        while (true) {
            System.out.println("""
                    Здравствуйте! Хотите зарегистрироваться или войти в аккаунт?\s
                    /login - войти в аккаунт
                    /register - зарегистрироваться
                    /exit - выход из приложения""");

            if (scanner.hasNext()) {
                userInput = scanner.next();
            }

            switch (userInput) {
                case "/login" -> invoker.addCommand(new LogInCommand(user));
                case "/register" -> invoker.addCommand(new RegisterCommand(user));
                case "/exit" -> {
                    return;
                }
                default -> System.out.println("Команда не распознана\n");
            }

            invoker.doCommands();
        }
    }

    /**
     * Users menu after login
     */
    public void showUserMenu() {
        Scanner scanner = new Scanner(System.in);
        String userInput = "";
        Invoker invoker = new Invoker();
        User user = new User(httpRequestsClass);
        Budget budget = new Budget(httpRequestsClass);
        Balance balance = new Balance(httpRequestsClass);
        Transaction transaction = new Transaction(httpRequestsClass);
        Goal goal = new Goal(httpRequestsClass);

        while (true) {
            System.out.println("""
                    Введите желаемое действие:
                    /budget - установить месячный бюджет
                    /show_budget - вывести текущий бюджет на месяц
                    /goal - установить цель
                    /add_transaction - создать транзакцию
                    /delete_account - удалить аккаунт
                    /show_transactions - вывести все транзакции
                    /show_goals - вывести все цели
                    /balance - вывести текущий баланс
                    /balance_for_period - вывести доход и расход за период
                    /category_expense - вывести расходы по категориям
                    /logout - выйти из приложения
                    """);

            if (scanner.hasNext()) {
                userInput = scanner.next();
            }

            switch (userInput) {
                case "/budget" -> invoker.addCommand(new AddBudgetCommand(budget));
                case "/show_budget" -> invoker.addCommand(new ShowBudgetCommand(budget));
                case "/goal" -> invoker.addCommand(new AddGoalCommand(goal));
                case "/add_transaction" -> invoker.addCommand(new AddTransactionCommand(transaction));
                case "/delete_account" -> {
                    invoker.addCommand(new DeleteAccountCommand(user));
                    invoker.doCommands();

                    return;
                }
                case "/show_transactions" -> {
                    invoker.addCommand(new ShowTransactionsCommand(transaction));
                    invoker.doCommands();

                    showTransactionMenu();

                    return;
                }
                case "/show_goals" -> invoker.addCommand(new ShowGoalsCommand(goal));
                case "/balance" -> invoker.addCommand(new ShowBalanceCommand(balance));
                case "/balance_for_period" -> {
                    System.out.println("Введите начальную дату в формате 2000-12-21:");
                    scanner = new Scanner(System.in);
                    Date from = getDate(scanner);

                    System.out.println("Введите конечную дату в формате 2000-12-21:");
                    Date to = getDate(scanner);

                    invoker.addCommand(new ShowIncomeForPeriodCommand(balance, from, to));
                    invoker.addCommand(new ShowExpenseForPeriodCommand(balance, from, to));
                }
                case "/category_expense" -> invoker.addCommand(new ShowCategoryExpenseCommand(balance));
                case "/logout" -> {
                    return;
                }
                default -> System.out.println("Команда не распознана\n");
            }

            invoker.doCommands();
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

    /**
     * Admins menu after login
     */
    public void showAdminMenu() {
        Scanner scanner = new Scanner(System.in);
        String userInput = "";
        Invoker invoker = new Invoker();
        User user = new User(httpRequestsClass);

        while (true) {
            System.out.println("""
                    Введите желаемое действие:
                    /users - вывести список всех пользователей приложения
                    /logout - выйти из приложения
                    """);

            if (scanner.hasNext()) {
                userInput = scanner.next();
            }

            switch (userInput) {
                case "/users" -> invoker.addCommand(new ShowAllUsersCommand(user));
                case "/logout" -> {
                    return;
                }
                default -> System.out.println("Команда не распознана\n");
            }

            invoker.doCommands();
        }
    }

    public void showTransactionMenu() {
        Scanner scanner = new Scanner(System.in);
        String userInput = "";
        Invoker invoker = new Invoker();
        Transaction transaction = new Transaction(httpRequestsClass);

        while (true) {
            System.out.println("""
                    Введите желаемое действие:
                    /filter_transactions - отфильтровать список транзакций
                    /edit_transaction - изменить транзакцию
                    /delete_transaction - удалить транзакцию
                    /menu - вернуться в меню
                    """);

            if (scanner.hasNext()) {
                userInput = scanner.next();
            }

            switch (userInput) {
                case "/filter_transactions" -> invoker.addCommand(new ShowFilteredTransactionsCommand(transaction));
                case "/edit_transaction" -> invoker.addCommand(new EditTransactionCommand(transaction));
                case "/delete_transaction" -> invoker.addCommand(new DeleteTransactionCommand(transaction));
                case "/menu" -> {
                    showUserMenu();

                    return;
                }
                default -> System.out.println("Команда не распознана\n");
            }

            invoker.doCommands();
        }
    }
}