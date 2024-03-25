package edu.kit.uenqh.model;

import edu.kit.uenqh.model.files.FileRecord;
import edu.kit.uenqh.userinput.CommandHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the sorting system, which manages file records and handles user commands.
 *
 * @author uenqh
 */
public class SortingSystem {

    private final CommandHandler commandHandler;
    private final List<FileRecord> fileRecords;

    /**
     * Constructs a new SortingSystem object.
     */
    public SortingSystem() {
        this.commandHandler = new CommandHandler(this);
        this.fileRecords = new ArrayList<>();
    }

    /**
     * Gets the command handler associated with this sorting system.
     *
     * @return The command handler.
     */
    public CommandHandler getCommandHandler() {
        return this.commandHandler;
    }

    /**
     * Gets the list of file records managed by this sorting system.
     *
     * @return The list of file records.
     */
    public List<FileRecord> getFileRecords() {
        return this.fileRecords;
    }

    /**
     * Retrieves a file record by its ID.
     *
     * @param id The ID of the file record to retrieve.
     * @return The file record with the specified ID, or null if not found.
     */
    public FileRecord getFileRecordById(int id) {
        for (FileRecord fileRecord : this.fileRecords) {
            if (fileRecord.id() == id) {
                return fileRecord;
            }
        }
        return null;
    }
}
