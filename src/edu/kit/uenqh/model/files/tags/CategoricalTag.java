package edu.kit.uenqh.model.files.tags;

import java.util.Objects;

/**
 * Represents a categorical tag.
 * Extends the {@link Tag} class.
 *
 * @author uenqh
 */
public class CategoricalTag extends Tag {
    private final String value;

    /**
     * Constructs a new CategoricalTag object with the specified name and value.
     *
     * @param name  the name of the categorical tag
     * @param value the value of the categorical tag
     */
    public CategoricalTag(String name, String value) {
        super(name);
        this.value = value;
    }

    /**
     * Retrieves the value of the categorical tag.
     *
     * @return the value of the categorical tag
     */
    @Override
    public String getValue() {
        return this.value;
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
        CategoricalTag tag = (CategoricalTag) o;
        return this.hashCode() == tag.hashCode();
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.value, this.getName());
    }
}
