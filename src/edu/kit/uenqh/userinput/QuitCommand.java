package edu.kit.uenqh.userinput;

import edu.kit.uenqh.model.SortingSystem;


/**
 * Represents a command to quit the program immediately.
 *
 * @author uenqh
 */
public class QuitCommand implements Command {
    private static final String QUIT_MESSAGE = null;
    private static final int NUMBER_OF_ARGUMENTS = 0;

    /**
     * Executes the command to quit the program.
     *
     * @param model            the SortingSystem instance to execute the command on.
     * @param commandArguments the arguments of the command (not used).
     * @return The result of the command execution.
     */
    @Override
    public CommandResult execute(SortingSystem model, String[] commandArguments) {
        model.getCommandHandler().quit();
        return new CommandResult(CommandResultType.SUCCESS, QUIT_MESSAGE);
    }

    /**
     * Retrieves the number of arguments required for the command.
     *
     * @return The number of arguments required for the command.
     */
    @Override
    public int getNumberOfArguments() {
        return NUMBER_OF_ARGUMENTS;
    }

}

