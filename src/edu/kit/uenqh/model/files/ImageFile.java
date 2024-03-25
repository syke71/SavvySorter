package edu.kit.uenqh.model.files;

/**
 * Represents an image file.
 *
 * @author uenqh
 */
public class ImageFile extends File {
    private static final String ILLEGAL_SIZE = null;
    private static final int ICON_MIN_SIZE = 0;
    private static final String ICON_NAME = "Icon";
    private static final int SMALL_MIN_SIZE = 10000;
    private static final String SMALL_NAME = "Small";
    private static final int MEDIUM_MIN_SIZE = 40000;
    private static final String MEDIUM_NAME = "Medium";
    private static final int LARGE_MIN_SIZE = 800000;
    private static final String LARGE_NAME = "Large";

    /**
     * Constructs an ImageFile with the given identifier and access amount.
     *
     * @param identifier   the identifier of the image file
     * @param accessAmount the access amount of the image file
     */
    public ImageFile(String identifier, int accessAmount) {
        super(identifier, accessAmount);
    }

    /**
     * Converts the size of the image to a descriptive string representing its size category.
     *
     * @param size the size of the image
     * @return a string representing the size category of the image
     */
    public static String imageSizeConverter(int size) {
        if (size >= LARGE_MIN_SIZE) {
            return LARGE_NAME;
        } else if (size >= MEDIUM_MIN_SIZE) {
            return MEDIUM_NAME;
        } else if (size >= SMALL_MIN_SIZE) {
            return SMALL_NAME;
        } else if (size >= ICON_MIN_SIZE) {
            return ICON_NAME;
        }
        return ILLEGAL_SIZE;
    }
}
