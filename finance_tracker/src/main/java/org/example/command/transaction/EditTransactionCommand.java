package org.example.command.transaction;

import org.example.command.Command;

public class EditTransactionCommand implements Command {
    private final Transaction transaction;

    public EditTransactionCommand(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void execute() {
        transaction.editTransaction();
    }
}