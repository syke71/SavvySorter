package edu.kit.uenqh.userinput;

import edu.kit.uenqh.model.SortingSystem;

/**
 * This interface represents an executable command.
 *
 * @author Programmieren-Team
 */
public interface Command {

    /**
     * Executes the command.
     *
     * @param model            the edu.kit.uenqh.model to execute the command on
     * @param commandArguments the arguments of the command
     * @return the result of the command
     */
    CommandResult execute(SortingSystem model, String[] commandArguments);

    /**
     * Returns the number of arguments that the command expects.
     *
     * @return the number of arguments that the command expects
     */
    int getNumberOfArguments();

}
