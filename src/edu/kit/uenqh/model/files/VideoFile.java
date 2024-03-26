package edu.kit.uenqh.model.files;

/**
 * Represents a video file.
 *
 * @author uenqh
 */
public class VideoFile extends File {
    private static final String ILLEGAL_LENGTH = null;
    private static final int CLIP_MIN_LENGTH = 0;
    private static final String CLIP_NAME = "clip";
    private static final int SHORT_MIN_LENGTH = 300;
    private static final String SHORT_NAME = "short";
    private static final int MOVIE_MIN_LENGTH = 3600;
    private static final String MOVIE_NAME = "movie";
    private static final int LONG_MIN_LENGTH = 7200;
    private static final String LONG_NAME = "long";

    /**
     * Constructs a new VideoFile object with the given identifier and access amount.
     *
     * @param identifier   The unique identifier of the video file.
     * @param accessAmount The number of times the video file has been accessed.
     */
    public VideoFile(String identifier, int accessAmount) {
        super(identifier, accessAmount);
    }

    /**
     * Converts the length of the video into a descriptive category.
     *
     * @param length The length of the video in seconds.
     * @return A string representing the category of the video based on its length.
     */
    public static String videoLengthConverter(int length) {
        if (length >= LONG_MIN_LENGTH) {
            return LONG_NAME;
        } else if (length >= MOVIE_MIN_LENGTH) {
            return MOVIE_NAME;
        } else if (length >= SHORT_MIN_LENGTH) {
            return SHORT_NAME;
        } else if (length >= CLIP_MIN_LENGTH) {
            return CLIP_NAME;
        }
        return ILLEGAL_LENGTH;
    }
}
