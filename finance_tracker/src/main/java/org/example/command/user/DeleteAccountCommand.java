package org.example.command.user;

import org.example.command.Command;

public class DeleteAccountCommand implements Command {
    private final User user;

    public DeleteAccountCommand(User user) {
        this.user = user;
    }

    @Override
    public void execute() {
        user.deleteAccount();
    }
}