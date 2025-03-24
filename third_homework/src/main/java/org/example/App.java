package org.example;

import org.example.command.CommandClass;
import org.example.command.HttpRequestsClass;
import org.example.model.*;
import org.example.repository.MonthlyBudgetRepository;
import org.example.repository.TransactionCategoryRepository;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;

import java.util.Scanner;

public class App 
{
    /**
     * Login menu - users start point
     */
    public static void main( String[] args ) {
        Scanner scanner = new Scanner(System.in);
        String command = "";
        CommandClass commandClass = new CommandClass(new HttpRequestsClass(), new UserRepository(), new TransactionRepository(),
                new TransactionCategoryRepository(), new MonthlyBudgetRepository());

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

                    if ((role = commandClass.getLoggedInUserRole()) != null) {
                        System.out.println("Вы успешно вошли в систему\n");

                        commandClass.showMenu(role);

                        return;
                    } else {
                        System.out.println("Пользователь с такими почтой и паролем не найдены\n");
                    }
                }
                case "/register" -> {
                    if (commandClass.getRegisteredUser() != null) {
                        System.out.println("Вы успешно зарегистрировались\n");
                    } else {
                        System.out.println("Пользователь с такой почтой уже существует\n");
                    }
                }
                case "/exit" -> {
                    return;
                }
                default -> System.out.println("Команда не распознана\n");
            }
        }
    }
}