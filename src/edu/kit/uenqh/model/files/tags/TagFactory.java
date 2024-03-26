package edu.kit.uenqh.model.files.tags;

import java.util.regex.Pattern;

import static edu.kit.uenqh.model.files.FileConstants.BINARY_TAG_NAME;
import static edu.kit.uenqh.model.files.FileConstants.MULTI_VALUE_TAG_NAME;
import static edu.kit.uenqh.model.files.FileConstants.NUMERIC_TAG_NAME;

/**
 * A factory class for creating different types of tags.
 *
 * @author uenqh
 */
public final class TagFactory {
    private static final String INVALID_TAG_TYPE_MESSAGE = "Invalid tag type: ";
    private static final String INVALID_TAG_NAME_FORMAT = "Invalid tag name: %s. Must have this format: %s";
    public static final String TAG_REGEX = "[a-zA-Z][0-9a-zA-Z]*";

    /**
     * Private constructor to prevent instantiation.
     */
    private TagFactory() {

    }

    /**
     * Creates a tag based on the given tag type, name, and value.
     *
     * @param tagType The type of the tag.
     * @param name    The name of the tag.
     * @param value   The value of the tag.
     * @return The created tag.
     * @throws IllegalArgumentException if the tag type is invalid.
     */
    public static Tag createTag(String tagType, String name, String value) {
        if (!Pattern.matches(TAG_REGEX, name)) {
            throw new IllegalArgumentException(String.format(INVALID_TAG_NAME_FORMAT, name, TAG_REGEX));
        }
        return switch (tagType) {
            case BINARY_TAG_NAME -> new BinaryTag(name, BinaryTagType.valueOf(value.toUpperCase()));
            case MULTI_VALUE_TAG_NAME -> new MultiValueTag(name, value);
            case NUMERIC_TAG_NAME -> new NumericTag(name, Integer.parseInt(value));
            default -> throw new IllegalArgumentException(INVALID_TAG_TYPE_MESSAGE + tagType);
        };
    }
}
