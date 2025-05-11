package org.example.command.menu;

import org.example.command.Command;

public class ShowUserMenuCommand implements Command {
    private final Menu userMenu;

    public ShowUserMenuCommand(Menu userMenu) {
        this.userMenu = userMenu;
    }

    @Override
    public void execute() {
        userMenu.showUserMenu();
    }
}