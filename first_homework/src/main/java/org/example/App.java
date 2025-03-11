package org.example;

import org.example.command.CommandClass;
import org.example.model.*;
import org.example.repository.UserRepository;

import java.util.Scanner;

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
                    "/login" + " - войти в аккаунт\n" +
                    "/register - зарегистрироваться\n" +
                    "/exit - выход из приложения");

            if (scanner.hasNext()) {
                command = scanner.next();
            }

            if (command.equals("/login")) {
                UserRole role = CommandClass.getLoggedInUserRole();
                if (role == UserRole.USER){
                    menuForUser();

                    return;
                } else if (role == UserRole.ADMIN) {
                    menuForAdmin();

                    return;
                }
            } else if (command.equals("/register")) {
                System.out.println(CommandClass.register());
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
                    "/show_goals - вывести все цели\n" +
                    "/exit - выйти из приложения\n");

            if (scanner.hasNext()) {
                command = scanner.next();
            }

            if (command.equals("/budget")) {
                MonthlyBudgetEntity monthlyBudgetEntity = CommandClass.addBudget();

                System.out.println("Бюджет на " + monthlyBudgetEntity.getDate() + " теперь составляет " + monthlyBudgetEntity.getSum() + "\n");
            } else if (command.equals("/goal")) {
                CommandClass.addGoal();
            } else if (command.equals("/add_transaction")) {
                CommandClass.addTransaction();
            } else if (command.equals("/delete_account")) {
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
            } else if (command.equals("/show_transactions")) {
                System.out.println(CommandClass.getTransactions());

                transactionsMenu();

                return;
            } else if (command.equals("/show_goals")) {
                System.out.println(CommandClass.getAllUserGoals());


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
                System.out.println(CommandClass.filterTransactions());

                transactionsMenu();
            } else if (command.equals("/edit_transaction")) {
                CommandClass.editTransaction();
            } else if (command.equals("/delete_transaction")) {
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
                System.out.println(CommandClass.getAllUsers());
            } else if (command.equals("/exit")) {
                return;
            } else {
                System.out.println("Команда не распознана\n");
            }
        }
    }
}