package org.example.command.user;

import org.example.command.Command;

public class RegisterCommand implements Command {
    private final User user;

    public RegisterCommand(User user) {
        this.user = user;
    }

    @Override
    public void execute() {
        user.register();
    }
}