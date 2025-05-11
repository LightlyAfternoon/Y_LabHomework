package org.example.command.user;

import org.example.CurrentUser;
import org.example.command.HttpRequestsClass;
import org.example.command.Invoker;
import org.example.command.menu.Menu;
import org.example.command.menu.ShowAdminMenuCommand;
import org.example.command.menu.ShowUserMenuCommand;
import org.example.controller.dto.UserDTO;
import org.example.model.UserEntity;
import org.example.model.UserRole;

import java.util.Scanner;

public class User {
    private final HttpRequestsClass httpRequestsClass;
    Scanner scanner;
    Menu menu;

    public User(HttpRequestsClass httpRequestsClass) {
        this.httpRequestsClass = httpRequestsClass;
        this.menu = new Menu(httpRequestsClass);
    }

    public void logIn() {
        UserRole role;
        Invoker invoker = new Invoker();

        if ((role = getLoggedInUserRole()) != null) {
            System.out.println("Вы успешно вошли в систему\n");

            if (role == UserRole.USER) {
                invoker.addCommand(new ShowUserMenuCommand(menu));
            } else if (role == UserRole.ADMIN) {
                invoker.addCommand(new ShowAdminMenuCommand(menu));
            }
        } else {
            System.out.println("Пользователь с такими почтой и паролем не найдены\n");
        }

        invoker.doCommands();
    }

    public void register() {
        if (getRegisteredUser() != null) {
            System.out.println("Вы успешно зарегистрировались\n");
        } else {
            System.out.println("Пользователь с такой почтой уже существует\n");
        }
    }

    public void deleteAccount() {
        Invoker invoker = new Invoker();

        System.out.println("Для подтверждения введите команду /confirm\n" +
                "Для возвращения в меню введите команду /menu:");

        String userInput = "";
        scanner = new Scanner(System.in);

        if (scanner.hasNext()) {
            userInput = scanner.next();
        }

        if (userInput.equals("/confirm")) {
            if (accountDeleted()) {
                System.out.println("Аккаунт удалён");
            } else {
                System.out.println("Не удалось удалить аккаунт");
            }
        } else if (userInput.equals("/menu")) {
            invoker.addCommand(new ShowUserMenuCommand(menu));
        } else {
            System.out.println("Команда не распознана\n");

            if (CurrentUser.currentUser.getRole() == UserRole.USER) {
                invoker.addCommand(new ShowUserMenuCommand(menu));
            } else {
                invoker.addCommand(new ShowAdminMenuCommand(menu));
            }
        }

        invoker.doCommands();
    }

    public void showAllUsers() {
        System.out.println(getAllUsers());
    }

    public UserDTO getRegisteredUser() {
        return httpRequestsClass.getRegisteredUser(sendEmail(), sendPassword(), sendUserName());
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

    public boolean accountDeleted() {
        return httpRequestsClass.deleteAccount(CurrentUser.currentUser.getId());
    }

    public String getAllUsers() {
        StringBuilder output = new StringBuilder();

        for (UserDTO userDTO : httpRequestsClass.getAllUsers()){
            output.append(userDTO).append("\n");
        }

        return output.toString();
    }
}