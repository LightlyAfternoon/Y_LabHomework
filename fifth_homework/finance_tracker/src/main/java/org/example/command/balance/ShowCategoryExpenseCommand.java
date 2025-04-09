package org.example.command.balance;

import org.example.command.Command;

public class ShowCategoryExpenseCommand implements Command {
    private final Balance balance;

    public ShowCategoryExpenseCommand(Balance balance) {
        this.balance = balance;
    }

    @Override
    public void execute() {
        balance.showCategoryExpense();
    }
}