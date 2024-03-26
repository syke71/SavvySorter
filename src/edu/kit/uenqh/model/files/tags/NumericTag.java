package edu.kit.uenqh.model.files.tags;

import java.util.Objects;

/**
 * Represents a numeric tag associated with a file.
 *
 * @author uenqh
 */
public class NumericTag extends Tag {
    private final int numericValue;

    /**
     * Constructs a NumericTag with the specified name and numeric value.
     *
     * @param name         The name of the tag.
     * @param numericValue The numeric value associated with the tag.
     */
    public NumericTag(String name, int numericValue) {
        super(name);
        this.numericValue = numericValue;
    }

    /**
     * Retrieves the string representation of the numeric value.
     *
     * @return The string representation of the numeric value.
     */
    @Override
    public String getValue() {
        return String.valueOf(this.numericValue);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o The reference object with which to compare.
     * @return True if this object is the same as the o argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NumericTag tag = (NumericTag) o;
        return this.hashCode() == tag.hashCode();
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.numericValue, this.getName());
    }
}
