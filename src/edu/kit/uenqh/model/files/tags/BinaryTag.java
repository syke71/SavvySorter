package edu.kit.uenqh.model.files.tags;

import java.util.Objects;

/**
 * Represents a binary tag.
 * Extends the {@link Tag} class.
 *
 * @author uenqh
 */
public class BinaryTag extends Tag {
    private final BinaryTagType label;

    /**
     * Constructs a new BinaryTag object with the specified name and label.
     *
     * @param name  the name of the binary tag
     * @param label the label of the binary tag
     */
    public BinaryTag(String name, BinaryTagType label) {
        super(name);
        this.label = label;

    }

    /**
     * Retrieves the value of the binary tag.
     *
     * @return the string representation of the label of the binary tag
     */
    @Override
    public String getValue() {
        return String.valueOf(this.label).toLowerCase();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o the reference object with which to compare
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BinaryTag tag = (BinaryTag) o;
        return this.hashCode() == tag.hashCode();
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.label, this.getName());
    }
}
