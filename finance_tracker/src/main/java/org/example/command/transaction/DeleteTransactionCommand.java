package org.example.command.transaction;

import org.example.command.Command;

public class DeleteTransactionCommand implements Command {
    private final Transaction transaction;

    public DeleteTransactionCommand(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void execute() {
        transaction.deleteTransaction();
    }
}