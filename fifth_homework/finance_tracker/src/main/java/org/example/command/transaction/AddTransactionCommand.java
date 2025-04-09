package org.example.command.transaction;

import org.example.command.Command;

public class AddTransactionCommand implements Command {
    private final Transaction transaction;

    public AddTransactionCommand(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void execute() {
        transaction.addTransaction();
    }
}