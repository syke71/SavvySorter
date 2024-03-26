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
import java.util.regex.Pattern;

import static edu.kit.uenqh.model.files.FileConstants.BINARY_TAG_NAME;
import static edu.kit.uenqh.model.files.FileConstants.MULTI_VALUE_TAG_NAME;
import static edu.kit.uenqh.model.files.FileConstants.NUMERIC_TAG_NAME;
import static edu.kit.uenqh.model.files.FileConstants.PROGRAM_FILE_NAME;
import static edu.kit.uenqh.model.files.FileConstants.TAG_REGEX;
import static edu.kit.uenqh.userinput.CommandConstants.MIN_ACCESS_AMOUNT;
import static edu.kit.uenqh.userinput.CommandConstants.NEXT_LINE;

/**
 * A command implementation for loading files into the system.
 *
 * @author uenqh
 */
public class LoadCommand implements Command {
    private static final int PATH_INDEX = 0;
    private static final int NUMBER_OF_ARGUMENTS = 1;
    private static final String ENTRY_SEPARATOR_REGEX = ",";
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
    private static final String INVALID_FILE_FORMAT_MESSAGE = "entries within the loaded file are not formatted correctly!";
    private static final String NOT_UNIQUE_FILE_IDENTIFIER_MESSAGE = "the loaded file contains reoccurring file identifiers!";
    private static final String NOT_UNIQUE_TAG_NAMES_MESSAGE = "the loaded file contains reoccurring tags!";
    private static final String MULTIPLE_TAG_ASSIGNMENT_MESSAGE = "you cannot assign the same tag multiple times to the same file!";
    private static final String IDENTIFIER_CONTAINS_ILLEGAL_CHARACTER_MESSAGE = "the loaded file contains illegal characters!";
    private static final String INVALID_ACCESS_AMOUNT_FORMAT = "the loaded file contains an invalid access amount of %s in line %s!";
    private static final String ILLEGAL_TAG_NAME_FORMAT = "the entered tag (%s) name contains illegal characters!";
    private static final String INVALID_TAG_TYPE_MESSAGE = "the entered tag contains an invalid tag type!";
    private static final String EMPTY_STRING = null;

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
        String path = commandArguments[PATH_INDEX];
        // Check arguments
        if (checkLegalArguments(path).getType().equals(CommandResultType.FAILURE)) {
            return checkLegalArguments(path);
        }

        // Check loaded file
        if (checkLegalFiles(path).getType().equals(CommandResultType.FAILURE)) {
            return checkLegalFiles(path);
        }

        List<String> entries = readFile(path);
        ArrayList<File> files = new ArrayList<>();
        try {
            files = createFiles(entries);
        } catch (InvalidFileTypeException e) {
            return new CommandResult(CommandResultType.FAILURE, e.getMessage());
        }
        HashSet<Tag> tags = createUniqueTagSet(files);
        int id = model.getFileRecords().size();

        FileRecord fileRecord = FileHandler.formattedFileRecord(new FileRecord(files, tags, id));
        model.getFileRecords().add(fileRecord);
        String message = String.format(LOADED_SUCCESSFULLY_FORMAT, commandArguments[PATH_INDEX], id);
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

    private CommandResult checkLegalArguments(String path) {
        if (!Files.exists(Paths.get(path))) {
            return new CommandResult(CommandResultType.FAILURE, String.format(FILE_DOES_NOT_EXIST_FORMAT, path));
        }
        return new CommandResult(CommandResultType.SUCCESS, EMPTY_STRING);
    }
    private CommandResult checkLegalFiles(String path) {
        List<String> entries = readFile(path);

        if (entries == null || entries.isEmpty()) {
            return new CommandResult(CommandResultType.FAILURE, EMPTY_FILE_MESSAGE);
        }
        if (!checkLegalFileFormat(entries)) {
            return new CommandResult(CommandResultType.FAILURE, INVALID_FILE_FORMAT_MESSAGE);
        }
        if (checkLegalAccessAmount(entries).getType().equals(CommandResultType.FAILURE)) {
            return checkLegalAccessAmount(entries);
        }
        if (checkLegalTagFormat(entries).getType().equals(CommandResultType.FAILURE)) {
            return checkLegalTagFormat(entries);
        }
        if (!checkLegalFileIdentifiers(entries)) {
            return new CommandResult(CommandResultType.FAILURE, IDENTIFIER_CONTAINS_ILLEGAL_CHARACTER_MESSAGE);
        }
        if (!checkUniqueFileIdentifiers(entries)) {
            return new CommandResult(CommandResultType.FAILURE, NOT_UNIQUE_FILE_IDENTIFIER_MESSAGE);
        }
        List<File> files;
        try {
            files = createFiles(entries);
        } catch (InvalidFileTypeException e) {
            return new CommandResult(CommandResultType.SUCCESS, e.getMessage());
        }
        if (!checkUniqueTagPerFiles(files)) {
            return new CommandResult(CommandResultType.FAILURE, NOT_UNIQUE_TAG_NAMES_MESSAGE);
        }
        return new CommandResult(CommandResultType.SUCCESS, EMPTY_STRING);
    }
    // File checks
    private boolean checkLegalFileFormat(List<String> entries) {
        for (String s : entries) {
            if (!checkLegalFileFormat(s)) {
                return false;
            }
        }
        return true;
    }
    private boolean checkLegalFileFormat(String entry) {
        String[] splitEntry = entry.split(ENTRY_SEPARATOR_REGEX);
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
    private int[] createAccessAmounts(List<String> entries) {
        int[] accessAmounts = new int[entries.size()];
        for (int i = 0; i < accessAmounts.length; i++) {
            String[] splitEntry = entries.get(i).trim().split(ENTRY_SEPARATOR_REGEX);
            accessAmounts[i] = Integer.parseInt(splitEntry[FILE_ACCESS_AMOUNT_INDEX]);
        }
        return accessAmounts;
    }
    private CommandResult checkLegalAccessAmount(List<String> entries) {
        int[] accessAmount = createAccessAmounts(entries);
        for (int i = 0; i < accessAmount.length; i++) {
            if (accessAmount[i] < MIN_ACCESS_AMOUNT) {
                return new CommandResult(CommandResultType.FAILURE, String.format(INVALID_ACCESS_AMOUNT_FORMAT, accessAmount[i], i + 1));
            }
        }
        return new CommandResult(CommandResultType.SUCCESS, EMPTY_STRING);
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
    private ArrayList<File> createFiles(List<String> entries) throws InvalidFileTypeException {
        ArrayList<File> files = new ArrayList<>();
        String[] fileIdentifiers = new String[entries.size()];
        String[] fileTypes = new String[entries.size()];
        int[] accessAmounts = new int[entries.size()];
        Map<String, List<String>> tagMap = new HashMap<>();

        int step = 0;
        for (String s : entries) {
            String[] splitEntry = s.trim().split(ENTRY_SEPARATOR_REGEX);
            fileIdentifiers[step] = splitEntry[UNIQUE_FILE_IDENTIFIER_INDEX];
            fileTypes[step] = splitEntry[FILE_TYPE_INDEX];
            accessAmounts[step] = Integer.parseInt(splitEntry[FILE_ACCESS_AMOUNT_INDEX]);
            List<String> list = new ArrayList<>(Arrays.asList(splitEntry).subList(TAG_START_INDEX, splitEntry.length));
            tagMap.put(fileIdentifiers[step], list);
            step++;
        }
        for (int i = 0; i < fileIdentifiers.length; i++) {
            File file;
            file = FileFactory.createFile(fileTypes[i], fileIdentifiers[i], accessAmounts[i]);
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
    private String[] createFileIdentifiers(List<String> entries) {
        String[] fileIdentifiers = new String[entries.size()];
        for (int i = 0; i < fileIdentifiers.length; i++) {
            String[] splitEntry = entries.get(i).trim().split(ENTRY_SEPARATOR_REGEX);
            fileIdentifiers[i] = splitEntry[UNIQUE_FILE_IDENTIFIER_INDEX];
        }
        return fileIdentifiers;
    }
    private boolean checkLegalFileIdentifiers(List<String> entries) {
        String[] fileIdentifiers = createFileIdentifiers(entries);
        for (String s : fileIdentifiers) {
            if (s.contains(ILLEGAL_IDENTIFIER_CHARACTER)) {
                return false;
            }
        }
        return true;
    }
    private boolean checkUniqueFileIdentifiers(List<String> entries) {
        String[] fileIdentifiers = createFileIdentifiers(entries);
        Set<String> uniqueIdentifiers = new HashSet<>();
        for (String s : fileIdentifiers) {
            if (!uniqueIdentifiers.add(s)) {
                return false;
            }
        }
        return true;
    }
    // Tag checks
    private CommandResult checkLegalTagFormat(List<String> entries) {
        for (String s : entries) {
            String[] splitEntry = s.split(ENTRY_SEPARATOR_REGEX);
            String[] tags = Arrays.copyOfRange(splitEntry, TAG_START_INDEX, splitEntry.length);
            String[] tagNames = createTagNameArray(tags);
            if (checkLegalTagName(tagNames).getType().equals(CommandResultType.FAILURE)) {
                return checkLegalTagName(tagNames);
            }
        }
        List<Tag> tags = createTags(entries);
        if (!checkTagCreation(tags)) {
            return new CommandResult(CommandResultType.FAILURE, INVALID_TAG_TYPE_MESSAGE);
        }
        if (!checkUniqueTags(tags)) {
            return new CommandResult(CommandResultType.FAILURE, MULTIPLE_TAG_ASSIGNMENT_MESSAGE);
        }
        return new CommandResult(CommandResultType.SUCCESS, EMPTY_STRING);
    }
    private CommandResult checkLegalTagName(String[] tags) {
        for (String s : tags) {
            if (!Pattern.matches(TAG_REGEX, s)) {
                return new CommandResult(CommandResultType.FAILURE, String.format(ILLEGAL_TAG_NAME_FORMAT, s));
            }
        }
        return new CommandResult(CommandResultType.SUCCESS, EMPTY_STRING);
    }
    private String[] createTagNameArray(String[] tags) {
        String[] tagNames = new String[tags.length];
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].contains(MULTI_VALUE_TAG_SPLIT_SYMBOL)) {
                tagNames[i] = tags[i].split(MULTI_VALUE_TAG_SPLIT_SYMBOL)[MULTI_VALUE_TAG_NAME_INDEX];
            } else {
                tagNames[i] = tags[i];
            }
        }
        return tagNames;
    }
    private List<Tag> createTags(List<String> entries) {
        List<Tag> tags = new ArrayList<>();
        for (String s : entries) {
            String[] splitEntry = s.split(ENTRY_SEPARATOR_REGEX);
            String[] tagArray = Arrays.copyOfRange(splitEntry, TAG_START_INDEX, splitEntry.length);
            for (String tag : tagArray) {
                tags.add(createTag(tag));
            }
        }
        return tags;
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
    private boolean checkTagCreation(List<Tag> tags) {
        for (Tag tag : tags) {
            if (tag == null) {
                return false;
            }
        }
        return true;
    }
    private boolean checkUniqueTags(List<Tag> tags) {
        HashMap<String, String> tagNames = new HashMap<>();
        for (Tag tag : tags) {
            if (!tagNames.containsKey(tag.getName())) {
                tagNames.put(tag.getName(), tag.getClass().toString());
            } else {
                if (!tag.getClass().toString().equals(tagNames.get(tag.getName()))) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean checkUniqueTagPerFiles(List<File> files) {
        for (File file : files) {
            if (!checkUniqueTagPerFile(file.getTags())) {
                return false;
            }
        }
        return true;
    }
    private boolean checkUniqueTagPerFile(List<Tag> tags) {
        HashSet<String> tagSet = new HashSet<>();
        for (Tag tag : tags) {
            if (!tagSet.add(tag.getName().toLowerCase())) {
                return false;
            }
        }
        return true;
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
