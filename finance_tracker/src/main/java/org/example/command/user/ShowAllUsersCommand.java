package org.example.command.user;

import org.example.command.Command;

public class ShowAllUsersCommand implements Command {
    private final User user;

    public ShowAllUsersCommand(User user) {
        this.user = user;
    }

    @Override
    public void execute() {
        user.showAllUsers();
    }
}