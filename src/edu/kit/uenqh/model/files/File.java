package edu.kit.uenqh.model.files;

import edu.kit.uenqh.model.files.tags.Tag;

import java.util.ArrayList;

/**
 * Represents a file in the system.
 *
 * @author uenqh
 */
public abstract class File {
    private final ArrayList<Tag> tags;
    private final String identifier;
    private int accessAmount;

    /**
     * Constructs a new File with the specified identifier and access amount.
     *
     * @param identifier   the unique identifier of the file
     * @param accessAmount the access amount of the file
     */
    public File(String identifier, int accessAmount) {
        this.tags = new ArrayList<>();
        this.identifier = identifier;
        this.accessAmount = accessAmount;
    }

    /**
     * Retrieves the list of tags associated with the file.
     *
     * @return the list of tags associated with the file
     */
    public ArrayList<Tag> getTags() {
        return this.tags;
    }

    /**
     * Retrieves the unique identifier of the file.
     *
     * @return the unique identifier of the file
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Retrieves the access amount of the file.
     *
     * @return the access amount of the file
     */
    public int getAccessAmount() {
        return this.accessAmount;
    }

    /**
     * Sets the access amount of the file.
     *
     * @param accessAmount the access amount to set
     */
    public void setAccessAmount(int accessAmount) {
        this.accessAmount = accessAmount;
    }
}
