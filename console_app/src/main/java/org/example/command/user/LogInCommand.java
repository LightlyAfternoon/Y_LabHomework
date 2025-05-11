package org.example.command.user;

import org.example.command.Command;

public class LogInCommand implements Command {
    private final User user;

    public LogInCommand(User user) {
        this.user = user;
    }

    @Override
    public void execute() {
        user.logIn();
    }
}