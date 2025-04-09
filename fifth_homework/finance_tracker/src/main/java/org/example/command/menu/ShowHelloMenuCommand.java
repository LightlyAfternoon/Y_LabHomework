package org.example.command.menu;

import org.example.command.Command;

public class ShowHelloMenuCommand implements Command {
    private final Menu menu;

    public ShowHelloMenuCommand(Menu menu) {
        this.menu = menu;
    }

    @Override
    public void execute() {
        menu.showHelloMenu();
    }
}