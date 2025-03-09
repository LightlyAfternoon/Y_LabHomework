package org.example;

import org.example.model.MonthlyBudgetEntity;
import org.example.model.TransactionCategoryEntity;
import org.example.model.UserEntity;
import org.example.model.UserRole;
import org.example.repository.MonthlyBudgetRepository;
import org.example.repository.TransactionCategoryRepository;
import org.example.repository.UserRepository;

import java.math.BigDecimal;
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
                    "/transaction - создать транзакцию\n" +
                    "/exit - выйти из приложения\n");

            if (scanner.hasNext()) {
                command = scanner.next();
            }

            if (command.equals("/budget")) {
                System.out.println("Введите бюджет на данный месяц:");
                BigDecimal budget = scanner.nextBigDecimal();

                System.out.println("Введите категорию из списка ниже или оставьте пустое поле:\n");

                for (TransactionCategoryEntity category : new TransactionCategoryRepository().findAll()){
                    System.out.println(category.getName() + "\n");
                }

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
            } else if (command.equals("/exit")) {
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