package org.example.command.balance;

import org.example.command.Command;

import java.sql.Date;

public class ShowExpenseForPeriodCommand implements Command {
    private final Balance balance;
    private final Date from;
    private final Date to;

    public ShowExpenseForPeriodCommand(Balance balance, Date from, Date to) {
        this.balance = balance;
        this.from = from;
        this.to = to;
    }

    @Override
    public void execute() {
        balance.showExpenseForPeriod(from, to);
    }
}