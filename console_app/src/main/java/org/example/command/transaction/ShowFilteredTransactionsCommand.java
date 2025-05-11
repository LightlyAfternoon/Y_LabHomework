package org.example.command.transaction;

import org.example.command.Command;

public class ShowFilteredTransactionsCommand implements Command {
    private final Transaction transaction;

    public ShowFilteredTransactionsCommand(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void execute() {
        transaction.showFilteredTransactions();
    }
}