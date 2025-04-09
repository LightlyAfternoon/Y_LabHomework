package org.example.command.balance;

import org.example.command.Command;

public class ShowBalanceCommand implements Command {
    private final Balance balance;

    public ShowBalanceCommand(Balance balance) {
        this.balance = balance;
    }

    @Override
    public void execute() {
        balance.showBalance();
    }
}