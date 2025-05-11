package org.example.command.menu;

import org.example.command.Command;

public class ShowTransactionMenuCommand implements Command {
    private final Menu menu;

    public ShowTransactionMenuCommand(Menu menu) {
        this.menu = menu;
    }

    @Override
    public void execute() {
        menu.showTransactionMenu();
    }
}