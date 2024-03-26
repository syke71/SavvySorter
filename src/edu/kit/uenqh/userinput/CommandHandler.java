package edu.kit.uenqh.userinput;

import edu.kit.uenqh.model.SortingSystem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import static edu.kit.uenqh.userinput.CommandConstants.CHANGE_COMMAND_NAME;
import static edu.kit.uenqh.userinput.CommandConstants.ERROR_PREFIX;
import static edu.kit.uenqh.userinput.CommandConstants.LOAD_COMMAND_NAME;
import static edu.kit.uenqh.userinput.CommandConstants.NEXT_LINE;
import static edu.kit.uenqh.userinput.CommandConstants.QUIT_COMMAND_NAME;
import static edu.kit.uenqh.userinput.CommandConstants.RUN_COMMAND_NAME;
import static edu.kit.uenqh.userinput.CommandConstants.INVALID_ARGUMENTS_COUNT_FORMAT;

/**
 * This class handles the user input and executes the commands.
 *
 * @author Programmieren-Team
 * @author uenqh
 */
public class CommandHandler {
    private static final String COMMAND_SEPARATOR_REGEX = " ";
    private static final String COMMAND_NOT_FOUND_FORMAT = "command '%s' not found!";
    private static final String UNEXPECTED_VALUE_MESSAGE = "Unexpected value: ";

    private final SortingSystem sortingSystem;
    private final Map<String, Command> commands;
    private boolean running = false;

    /**
     * Constructs a new CommandHandler.
     *
     * @param sortingSystem the sorting system that this instance manages
     */
    public CommandHandler(SortingSystem sortingSystem) {
        this.sortingSystem = Objects.requireNonNull(sortingSystem);
        this.commands = new HashMap<>();
        this.initCommands();
    }

    /**
     * Starts the interaction with the user.
     */
    public void handleUserInput() {
        this.running = true;

        try (Scanner scanner = new Scanner(System.in)) {
            while (running && scanner.hasNextLine()) {
                executeCommand(scanner.nextLine());
            }
        }
    }

    /**
     * Quits the interaction with the user.
     */
    public void quit() {
        this.running = false;
    }

    private void executeCommand(String commandWithArguments) {
        String[] splitCommand = commandWithArguments.trim().split(COMMAND_SEPARATOR_REGEX);
        String commandName = splitCommand[0];
        String[] commandArguments = Arrays.copyOfRange(splitCommand, 1, splitCommand.length);

        executeCommand(commandName, commandArguments);
    }

    private void executeCommand(String commandName, String[] commandArguments) {
        if (!commands.containsKey(commandName)) {
            System.err.printf(ERROR_PREFIX + COMMAND_NOT_FOUND_FORMAT + NEXT_LINE, commandName);
        } else if (commands.get(commandName).getNumberOfArguments() != commandArguments.length) {
            System.err.printf(ERROR_PREFIX + INVALID_ARGUMENTS_COUNT_FORMAT + NEXT_LINE, commandName);
        } else {
            CommandResult result = commands.get(commandName).execute(sortingSystem, commandArguments);
            String output = switch (result.getType()) {
                case SUCCESS -> result.getMessage();
                case FAILURE -> ERROR_PREFIX + result.getMessage();
            };
            if (output != null) {
                switch (result.getType()) {
                    case SUCCESS -> System.out.println(output);
                    case FAILURE -> System.err.println(output);
                    default -> throw new IllegalStateException(UNEXPECTED_VALUE_MESSAGE + result.getType());
                }
            }
        }
    }

    private void initCommands() {
        this.addCommand(LOAD_COMMAND_NAME, new LoadCommand());
        this.addCommand(RUN_COMMAND_NAME, new RunCommand());
        this.addCommand(CHANGE_COMMAND_NAME, new ChangeCommand());
        this.addCommand(QUIT_COMMAND_NAME, new QuitCommand());
    }

    private void addCommand(String commandName, Command command) {
        this.commands.put(commandName, command);
    }
}
