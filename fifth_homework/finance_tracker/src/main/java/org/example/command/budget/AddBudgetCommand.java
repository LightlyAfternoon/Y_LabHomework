package org.example.command.budget;

import org.example.command.Command;

public class AddBudgetCommand implements Command {
    private final Budget budget;

    public AddBudgetCommand(Budget budget) {
        this.budget = budget;
    }

    @Override
    public void execute() {
        budget.addBudget();
    }
}