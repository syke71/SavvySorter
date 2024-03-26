package edu.kit.uenqh.userinput;

/**
 * Constants related to commands.
 *
 * @author uenqh
 */
public final class CommandConstants {

    /**
     * The minimum access amount allowed for a file.
     */
    public static final int MIN_ACCESS_AMOUNT = 0;

    // Command names
    /**
     * Represents the name of the load command.
     */
    public static final String LOAD_COMMAND_NAME = "load";
    /**
     * Represents the name of the run command.
     */
    public static final String RUN_COMMAND_NAME = "run";
    /**
     * Represents the name of the change command.
     */
    public static final String CHANGE_COMMAND_NAME = "change";
    /**
     * Represents the name of the quit command.
     */
    public static final String QUIT_COMMAND_NAME = "quit";

    // Command messages
    /**
     * Represents the prefix used for error messages.
     */
    public static final String ERROR_PREFIX = "ERROR: ";
    /**
     * Represents the format for an error message when the wrong number of arguments is provided for a command.
     */
    public static final String WRONG_ARGUMENTS_COUNT_FORMAT = "wrong number of arguments for command '%s'!";
    /**
     * Represents the message indicating that a file must be loaded before it can be changed.
     */
    public static final String EMPTY_FILE_RECORD_MESSAGE = "you must first load a file before changing it!";
    /**
     * Represents the string for a new line.
     */
    public static final String NEXT_LINE = "\n";


    /**
     * Private constructor to prevent instantiation.
     */
    private CommandConstants() {

    }
}
