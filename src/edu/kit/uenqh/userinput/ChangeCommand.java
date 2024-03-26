package edu.kit.uenqh.userinput;

import edu.kit.uenqh.model.SortingSystem;
import edu.kit.uenqh.model.files.File;
import edu.kit.uenqh.model.files.FileRecord;

import static edu.kit.uenqh.userinput.CommandConstants.EMPTY_FILE_RECORD_MESSAGE;

/**
 * Represents a command to change the access amount of a file.
 * Implements the {@link Command} interface.
 *
 * @author uenqh
 */
public class ChangeCommand implements Command {

    private static final int NUMBER_OF_ARGUMENTS = 3;
    private static final int ID_INDEX = 0;
    private static final int FILE_IDENTIFIER_INDEX = 1;
    private static final int TOTAL_ACCESS_AMOUNT_INDEX = 2;
    private static final int MIN_CHANGE_ACCESS_AMOUNT = 0;

    // return messages
    private static final String INVALID_COMMAND_MESSAGE = "the entered command is invalid! This commands format is: ";
    private static final String VALID_COMMAND_MESSAGE = "<id> <file> <number>";
    private static final String INVALID_ID_FORMAT = "the entered id (%s) does not exist!";
    private static final String INVALID_ACCESS_AMOUNT_FORMAT = "the entered number (%s) must be at least %s!";
    private static final String INVALID_FILE_FORMAT = "the entered file name (%s) does not exist!";
    private static final String SUCCESSFUL_CHANGE_FORMAT = "Change %s to %s for %s";

    /**
     * Executes the command to change the access amount of a file.
     *
     * @param model            the sorting system model
     * @param commandArguments the arguments provided with the command
     * @return the result of executing the command
     */
    @Override
    public CommandResult execute(SortingSystem model, String[] commandArguments) {
        if (model.getFileRecords().isEmpty()) {
            return new CommandResult(CommandResultType.FAILURE, EMPTY_FILE_RECORD_MESSAGE);
        }
        if (!checkLegalFormat(commandArguments)) {
            return new CommandResult(CommandResultType.FAILURE, INVALID_COMMAND_MESSAGE + VALID_COMMAND_MESSAGE);
        }
        int id = Integer.parseInt(commandArguments[ID_INDEX]);
        if (!checkIdExists(model, id)) {
            return new CommandResult(CommandResultType.FAILURE, String.format(INVALID_ID_FORMAT, id));
        }
        int accessAmount = Integer.parseInt(commandArguments[TOTAL_ACCESS_AMOUNT_INDEX]);
        if (!checkLegalAccessAmount(accessAmount)) {
            return new CommandResult(CommandResultType.FAILURE,
                INVALID_ACCESS_AMOUNT_FORMAT.formatted(accessAmount, MIN_CHANGE_ACCESS_AMOUNT));
        }
        FileRecord fileRecord = model.getFileRecordById(id);
        String identifier = commandArguments[FILE_IDENTIFIER_INDEX];
        if (!checkFileExists(fileRecord, identifier)) {
            return new CommandResult(CommandResultType.FAILURE, String.format(INVALID_FILE_FORMAT, identifier));
        }
        int fileId = 0;
        for (int i = 0; i < fileRecord.files().size(); i++) {
            if (fileRecord.files().get(i).getIdentifier().equals(identifier)) {
                fileId = i;
            }
        }
        int oldAccessAmount = fileRecord.files().get(fileId).getAccessAmount();
        model.getFileRecordById(id).files().get(fileId).setAccessAmount(accessAmount);
        String message = SUCCESSFUL_CHANGE_FORMAT.formatted(oldAccessAmount, accessAmount, identifier);
        return new CommandResult(CommandResultType.SUCCESS, message);
    }


    /**
     * Retrieves the number of arguments required for the command.
     *
     * @return the number of arguments required for the command
     */
    @Override
    public int getNumberOfArguments() {
        return NUMBER_OF_ARGUMENTS;
    }

    private boolean checkLegalFormat(String[] commandArguments) {
        try {
            Integer.parseInt(commandArguments[ID_INDEX]);
            Integer.parseInt(commandArguments[TOTAL_ACCESS_AMOUNT_INDEX]);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    private boolean checkIdExists(SortingSystem model, int id) {
        return model.getFileRecordById(id) != null;
    }

    private boolean checkLegalAccessAmount(int accessAmount) {
        return accessAmount >= MIN_CHANGE_ACCESS_AMOUNT;
    }

    private boolean checkFileExists(FileRecord fileRecord, String identifier) {
        for (File file : fileRecord.files()) {
            if (file.getIdentifier().equals(identifier)) {
                return true;
            }
        }
        return false;
    }

}
