package org.example.command;

import java.util.ArrayList;
import java.util.List;

public class Invoker {
    List<Command> commands = new ArrayList<>();

    public void addCommand(Command command) {
        commands.add(command);
    }

    public void doCommands() {
        for (Command command : commands) {
            command.execute();
        }

        commands = new ArrayList<>();
    }
}