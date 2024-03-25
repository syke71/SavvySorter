package edu.kit.uenqh.model.files;

/**
 * Represents a text file, a type of file with different lengths categorized as short, medium, or long.
 *
 * @author uenqh
 */
public class TextFile extends File {
    private static final String ILLEGAL_LENGTH = null;
    private static final int SHORT_MIN_LENGTH = 0;
    private static final String SHORT_NAME = "Short";
    private static final int MEDIUM_MIN_LENGTH = 100;
    private static final String MEDIUM_NAME = "Medium";
    private static final int LONG_MIN_LENGTH = 1000;
    private static final String LONG_NAME = "Long";

    /**
     * Constructs a new TextFile with the given identifier and access amount.
     *
     * @param identifier   The identifier of the text file.
     * @param accessAmount The access amount of the text file.
     */
    public TextFile(String identifier, int accessAmount) {
        super(identifier, accessAmount);
    }

    /**
     * Converts the length of a text file into a descriptive length category.
     *
     * @param length The length of the text file.
     * @return The descriptive length category (Short, Medium, Long) based on the length.
     */
    public static String textLengthConverter(int length) {
        if (length >= LONG_MIN_LENGTH) {
            return LONG_NAME;
        } else if (length >= MEDIUM_MIN_LENGTH) {
            return MEDIUM_NAME;
        } else if (length >= SHORT_MIN_LENGTH) {
            return SHORT_NAME;
        }
        return ILLEGAL_LENGTH;
    }

}
