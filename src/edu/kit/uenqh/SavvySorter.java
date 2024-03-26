package edu.kit.uenqh;

import edu.kit.uenqh.model.SortingSystem;

import static edu.kit.uenqh.userinput.CommandConstants.ERROR_PREFIX;

/**
 * Main class for the SavvySorter application.
 *
 * @author uenqh
 */
public final class SavvySorter {

    private static final int LEGAL_ARGS_LENGTH = 0;
    private static final String ILLEGAL_INPUT_ARGUMENTS_MESSAGE = "the entered starting arguments are not allowed!";
    private static final String SUCCESSFUL_START_MESSAGE = "Use one of the following commands: ";
    private static final String COMMANDS_FORMAT_MESSAGE = "load <path>, run <id>, change <id> <file> <number>, quit";

    /**
     * Private constructor to prevent instantiation.
     */
    private SavvySorter() {
    }

    /**
     * Entry point of the SavvySorter application.
     *
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        if (!checkLegalArguments(args)) {
            System.err.println(ERROR_PREFIX + ILLEGAL_INPUT_ARGUMENTS_MESSAGE);
        } else {
            System.out.println(SUCCESSFUL_START_MESSAGE + COMMANDS_FORMAT_MESSAGE);
            final SortingSystem sortingSystem = new SortingSystem();
            sortingSystem.getCommandHandler().handleUserInput();
        }
    }

    /**
     * Checks if the provided arguments are legal.
     *
     * @param args The command-line arguments.
     * @return true if the arguments are legal; otherwise, returns false.
     */
    private static boolean checkLegalArguments(String[] args) {
        return args.length == LEGAL_ARGS_LENGTH;
    }

}
