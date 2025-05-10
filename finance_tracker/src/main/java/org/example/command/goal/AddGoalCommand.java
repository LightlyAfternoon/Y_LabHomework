package org.example.command.goal;

import org.example.command.Command;

public class AddGoalCommand implements Command {
    private final Goal goal;

    public AddGoalCommand(Goal goal) {
        this.goal = goal;
    }

    @Override
    public void execute() {
        goal.addGoal();
    }
}