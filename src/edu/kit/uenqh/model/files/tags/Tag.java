package edu.kit.uenqh.model.files.tags;

/**
 * Represents a generic tag associated with a file.
 *
 * @author uenqh
 */
public abstract class Tag {
    private String name;

    /**
     * Constructs a new tag with the specified name.
     *
     * @param name The name of the tag.
     */
    public Tag(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the tag.
     *
     * @return The name of the tag.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the tag.
     *
     * @param name The new name of the tag.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the value associated with the tag.
     *
     * @return The value of the tag.
     */
    public abstract String getValue();
}
