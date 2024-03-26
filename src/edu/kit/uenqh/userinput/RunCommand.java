package edu.kit.uenqh.userinput;

import edu.kit.uenqh.model.InformationTheory;
import edu.kit.uenqh.model.SortingSystem;
import edu.kit.uenqh.model.files.FileRecord;
import edu.kit.uenqh.model.files.tags.Tag;
import edu.kit.uenqh.utility.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static edu.kit.uenqh.userinput.CommandConstants.EMPTY_FILE_RECORD_MESSAGE;

/**
 * Represents a command to run a file record with a specified ID.
 *
 * @author uenqh
 */
public class RunCommand implements Command {
    private static final int NUMBER_OF_ARGUMENTS = 1;
    private static final int ARGUMENT_INDEX = 0;
    private static final String INVALID_ARGUMENT_TYPE = "he entered ID must be a number!";
    private static final String UNKNOWN_ID = "the entered ID could not be found!";
    private static final String SUCCESSFUL_RUN_MESSAGE = null;

    /**
     * Executes the command to run a file record with a specified ID.
     *
     * @param model The sorting system model.
     * @param commandArguments The arguments for the command, where commandArguments[0] is the ID of the file record to run.
     * @return The result of the command execution, indicating success or failure.
     */
    @Override
    public CommandResult execute(SortingSystem model, String[] commandArguments) {
        if (model.getFileRecords().isEmpty()) {
            return new CommandResult(CommandResultType.FAILURE, EMPTY_FILE_RECORD_MESSAGE);
        }
        if (!checkValidArgumentType(commandArguments[ARGUMENT_INDEX])) {
            return new CommandResult(CommandResultType.FAILURE, INVALID_ARGUMENT_TYPE);
        }
        int id = Integer.parseInt(commandArguments[ARGUMENT_INDEX]);
        if (!checkIfIdExists(model, id)) {
            return new CommandResult(CommandResultType.FAILURE, UNKNOWN_ID);
        }

        // run record with ID
        FileRecord fileRecord = model.getFileRecordById(id);
        Map<String, ArrayList<Tag>> tagByName = new HashMap<>();
        for (Tag t : fileRecord.tags()) {
            tagByName.put(t.getName(), new ArrayList<>());
        }
        for (Tag t : fileRecord.tags()) {
            if (!tagByName.get(t.getName()).contains(t)) {
                tagByName.get(t.getName()).add(t);
            }
        }

        TreeNode root = InformationTheory.createFileTree(tagByName, fileRecord.files());
        System.out.println(root.toString());

        return new CommandResult(CommandResultType.SUCCESS, SUCCESSFUL_RUN_MESSAGE);
    }

    /**
     * Retrieves the number of arguments required for this command.
     * @return The number of arguments required.
     */
    @Override
    public int getNumberOfArguments() {
        return NUMBER_OF_ARGUMENTS;
    }
    private boolean checkValidArgumentType(String commandArgument) {
        try {
            Integer.parseInt(commandArgument);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean checkIfIdExists(SortingSystem model, int id) {
        return model.getFileRecordById(id) != null;
    }

}
