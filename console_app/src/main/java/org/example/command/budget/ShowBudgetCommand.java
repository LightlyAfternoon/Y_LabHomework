package org.example.command.budget;

import org.example.command.Command;

public class ShowBudgetCommand implements Command {
    private final Budget budget;

    public ShowBudgetCommand(Budget budget) {
        this.budget = budget;
    }

    @Override
    public void execute() {
        budget.showBudget();
    }
}