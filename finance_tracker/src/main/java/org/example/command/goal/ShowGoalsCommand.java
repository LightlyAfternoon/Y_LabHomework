package org.example.command.goal;

import org.example.command.Command;

public class ShowGoalsCommand implements Command {
    private final Goal goal;

    public ShowGoalsCommand(Goal goal) {
        this.goal = goal;
    }

    @Override
    public void execute() {
        goal.showGoals();
    }
}