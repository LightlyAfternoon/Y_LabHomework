package org.example.command.transaction;

import org.example.command.Command;

public class ShowTransactionsCommand implements Command {
    private final Transaction transaction;

    public ShowTransactionsCommand(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void execute() {
        transaction.showTransactions();
    }
}