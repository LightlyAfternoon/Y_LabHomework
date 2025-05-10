package org.example;

import org.example.command.HttpRequestsClass;
import org.example.command.Invoker;
import org.example.command.menu.Menu;
import org.example.command.menu.ShowHelloMenuCommand;

public class ConsoleApp
{
    /**
     * Login menu - users start point
     */
    public static void main( String[] args ) {
        Invoker invoker = new Invoker();

        invoker.addCommand(new ShowHelloMenuCommand(new Menu(new HttpRequestsClass())));

        invoker.doCommands();
    }
}