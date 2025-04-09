package org.example.command.menu;

import org.example.command.Command;

public class ShowAdminMenuCommand implements Command {
    private final Menu userMenu;

    public ShowAdminMenuCommand(Menu userMenu) {
        this.userMenu = userMenu;
    }

    @Override
    public void execute() {
        userMenu.showAdminMenu();
    }
}