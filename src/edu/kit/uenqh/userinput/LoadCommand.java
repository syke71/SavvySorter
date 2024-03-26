package edu.kit.uenqh.userinput;

import edu.kit.uenqh.model.SortingSystem;
import edu.kit.uenqh.model.files.File;
import edu.kit.uenqh.model.files.FileFactory;
import edu.kit.uenqh.model.files.FileHandler;
import edu.kit.uenqh.model.files.FileRecord;
import edu.kit.uenqh.model.files.InvalidFileTypeException;
import edu.kit.uenqh.model.files.tags.BinaryTag;
import edu.kit.uenqh.model.files.tags.BinaryTagType;
import edu.kit.uenqh.model.files.tags.Tag;
import edu.kit.uenqh.model.files.tags.TagFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static edu.kit.uenqh.model.files.FileConstants.BINARY_TAG_NAME;
import static edu.kit.uenqh.model.files.FileConstants.MULTI_VALUE_TAG_NAME;
import static edu.kit.uenqh.model.files.FileConstants.NUMERIC_TAG_NAME;
import static edu.kit.uenqh.model.files.FileConstants.PROGRAM_FILE_NAME;
import static edu.kit.uenqh.userinput.CommandConstants.MIN_ACCESS_AMOUNT;
import static edu.kit.uenqh.userinput.CommandConstants.NEXT_LINE;

/**
 * A command implementation for loading files into the system.
 *
 * @author uenqh
 */
public class LoadCommand implements Command {

    private static final int ARGUMENT_INDEX = 0;
    private static final int NUMBER_OF_ARGUMENTS = 1;
    private static final String COMMAND_SEPARATOR_REGEX = ",";
    private static final int UNIQUE_FILE_IDENTIFIER_INDEX = 0;
    private static final int FILE_TYPE_INDEX = 1;
    private static final int FILE_ACCESS_AMOUNT_INDEX = 2;
    private static final int TAG_START_INDEX = 3;
    private static final int MIN_ENTRY_ARRAY_LENGTH = 3;
    private static final String MULTI_VALUE_TAG_SPLIT_SYMBOL = "=";
    private static final int MULTI_VALUE_TAG_NAME_INDEX = 0;
    private static final int MULTI_VALUE_TAG_VALUE_INDEX = 1;
    private static final String ILLEGAL_IDENTIFIER_CHARACTER = " ";
    private static final String EXECUTABLE_TAG_NAME = "executable";
    private static final String LOADED_SUCCESSFULLY_FORMAT = "Loaded %s with id: %s";
    private static final String FILE_DOES_NOT_EXIST_FORMAT = "there is no file at '%s'";
    private static final String EMPTY_FILE_MESSAGE = "loaded file was empty!";
    private static final String WRONG_FILE_FORMAT = "entries within the loaded file are not formatted correctly!";
    private static final String NOT_UNIQUE_FILE_IDENTIFIER_MESSAGE = "the loaded file contains reoccurring file identifiers!";
    private static final String NOT_UNIQUE_TAG_NAMES_MESSAGE = "the loaded file contains reoccurring tags!";

    private static final String IDENTIFIER_CONTAINS_ILLEGAL_CHARACTER_MESSAGE = "the loaded file contains illegal characters!";
    private static final String INVALID_ACCESS_AMOUNT_FORMAT = "the loaded file contains an invalid access amount of %s in line %s!";


    /**
     * Executes the load command, which reads a file containing information about files to be loaded into the system,
     * creates corresponding File objects, and adds them to the system.
     *
     * @param model            The SortingSystem object representing the system where files will be loaded.
     * @param commandArguments An array of String representing the arguments passed with the load command.
     * @return A CommandResult indicating the outcome of the command execution.
     */
    @Override
    public CommandResult execute(SortingSystem model, String[] commandArguments) {
        String path = commandArguments[ARGUMENT_INDEX];
        if (!Files.exists(Paths.get(path))) {
            return new CommandResult(CommandResultType.FAILURE, String.format(FILE_DOES_NOT_EXIST_FORMAT, path));
        }
        List<String> entries = readFile(path);
        if (entries == null || entries.isEmpty()) {
            return new CommandResult(CommandResultType.FAILURE, EMPTY_FILE_MESSAGE);
        }
        if (!checkLegalFileFormat(model, entries)) {
            return new CommandResult(CommandResultType.FAILURE, WRONG_FILE_FORMAT);
        }
        String[] fileIdentifiers = new String[entries.size()];
        String[] fileTypes = new String[entries.size()];
        int[] accessAmounts = new int[entries.size()];
        Map<String, List<String>> tagMap = new HashMap<>();
        int step = 0;
        for (String s : entries) {
            String[] splitEntry = s.trim().split(COMMAND_SEPARATOR_REGEX);
            fileIdentifiers[step] = splitEntry[UNIQUE_FILE_IDENTIFIER_INDEX];
            fileTypes[step] = splitEntry[FILE_TYPE_INDEX];
            accessAmounts[step] = Integer.parseInt(splitEntry[FILE_ACCESS_AMOUNT_INDEX]);
            List<String> list = new ArrayList<>(Arrays.asList(splitEntry).subList(TAG_START_INDEX, splitEntry.length));
            tagMap.put(fileIdentifiers[step], list);
            step++;
        }
        for (int i = 0; i < entries.size(); i++) {
            if (accessAmounts[i] < MIN_ACCESS_AMOUNT) {
                return new CommandResult(CommandResultType.FAILURE, INVALID_ACCESS_AMOUNT_FORMAT.formatted(accessAmounts[i], i + 1));
            }
        }
        if (!this.checkUniqueFileIdentifiers(fileIdentifiers)) {
            return new CommandResult(CommandResultType.FAILURE, NOT_UNIQUE_FILE_IDENTIFIER_MESSAGE);
        }
        if (!this.checkLegalFileIdentifiers(fileIdentifiers)) {
            return new CommandResult(CommandResultType.FAILURE, IDENTIFIER_CONTAINS_ILLEGAL_CHARACTER_MESSAGE);
        }
        if (!this.checkUniqueTags(tagMap)) {
            return new CommandResult(CommandResultType.FAILURE, NOT_UNIQUE_TAG_NAMES_MESSAGE);
        }
        // load all files with their respective tags
        ArrayList<File> files = new ArrayList<>();
        try {
            files = createFiles(fileIdentifiers, fileTypes, accessAmounts, tagMap);
        } catch (InvalidFileTypeException e) {
            return new CommandResult(CommandResultType.FAILURE, e.getMessage());
        }
        HashSet<Tag> tags = new HashSet<>(createUniqueTagSet(files));
        int id = model.getFileRecords().size();
        model.getFileRecords().add(FileHandler.formattedFileRecord(new FileRecord(files, tags, id)));
        String message = String.format(LOADED_SUCCESSFULLY_FORMAT, commandArguments[ARGUMENT_INDEX], id);
        return new CommandResult(CommandResultType.SUCCESS, appendEntries(message, entries));
    }

    /**
     * Retrieves the number of arguments required for the load command.
     *
     * @return The number of arguments required for the load command.
     */
    @Override
    public int getNumberOfArguments() {
        return NUMBER_OF_ARGUMENTS;
    }

    private List<String> readFile(String path) {
        List<String> entries;
        try {
            entries = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
        return entries;
    }

    private boolean checkLegalFileFormat(SortingSystem model, List<String> entries) {
        for (String s : entries) {
            if (!checkLegalFileFormat(model, s)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkLegalFileFormat(SortingSystem model, String entry) {
        String[] splitEntry = entry.trim().split(COMMAND_SEPARATOR_REGEX);
        if (splitEntry.length < MIN_ENTRY_ARRAY_LENGTH) {
            return false;
        }
        try {
            Integer.parseInt(splitEntry[FILE_ACCESS_AMOUNT_INDEX]);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean checkLegalFileIdentifiers(String[] entries) {
        for (String s : entries) {
            if (s.contains(ILLEGAL_IDENTIFIER_CHARACTER)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkUniqueFileIdentifiers(String[] entries) {
        Set<String> uniqueIdentifiers = new HashSet<>();
        for (String s : entries) {
            if (!uniqueIdentifiers.add(s)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkUniqueTags(Map<String, List<String>> tagMap) {
        ArrayList<String> tags = new ArrayList<>();
        for (String s : tagMap.keySet()) {
            tags.addAll(tagMap.get(s));
        }
        Set<String> uniqueTags = new HashSet<>();
        for (String s : tags) {
            if (!uniqueTags.add(s)) {
                return false;
            }
        }
        return true;
    }

    private Tag createTag(String tag) {
        Tag newTag;
        String name;
        String value;
        if (tag.contains(MULTI_VALUE_TAG_SPLIT_SYMBOL)) {
            String[] splitTag = tag.trim().split(MULTI_VALUE_TAG_SPLIT_SYMBOL);
            name = splitTag[MULTI_VALUE_TAG_NAME_INDEX];
            value = splitTag[MULTI_VALUE_TAG_VALUE_INDEX];
            try {
                Integer.parseInt(splitTag[MULTI_VALUE_TAG_VALUE_INDEX]);
                newTag = TagFactory.createTag(NUMERIC_TAG_NAME, name, value);
            } catch (NumberFormatException ignored) {
                newTag = TagFactory.createTag(MULTI_VALUE_TAG_NAME, name, value);
            }
        } else {
            name = tag;
            newTag = TagFactory.createTag(BINARY_TAG_NAME, name, String.valueOf(BinaryTagType.DEFINED));
        }
        return newTag;
    }

    private ArrayList<File> createFiles(String[] fileIdentifiers, String[] fileTypes, int[] accessAmounts, Map<String, List<String>> tagMap)
        throws InvalidFileTypeException {
        ArrayList<File> files = new ArrayList<>();
        for (int i = 0; i < fileIdentifiers.length; i++) {
            File file;
            try {
                file = FileFactory.createFile(fileTypes[i], fileIdentifiers[i], accessAmounts[i]);
            } catch (InvalidFileTypeException e) {
                throw new InvalidFileTypeException(e.getMessage());
            }
            for (String tag : tagMap.get(fileIdentifiers[i])) {
                file.getTags().add(createTag(tag));
            }
            if (fileTypes[i].equals(PROGRAM_FILE_NAME)) {
                file.getTags().add(createExecutableTag());
            }
            files.add(file);
        }
        return files;
    }

    private HashSet<Tag> createUniqueTagSet(List<File> files) {
        HashSet<Tag> tags = new HashSet<>();
        for (File file : files) {
            tags.addAll(file.getTags());
        }
        return tags;
    }


    private String appendEntries(String message, List<String> entries) {
        StringBuilder output = new StringBuilder();
        output.append(message);
        for (String s : entries) {
            output.append(NEXT_LINE);
            output.append(s);
        }
        return output.toString();
    }

    private Tag createExecutableTag() {
        return new BinaryTag(EXECUTABLE_TAG_NAME, BinaryTagType.DEFINED);
    }
}
