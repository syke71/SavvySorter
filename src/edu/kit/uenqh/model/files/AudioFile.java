package edu.kit.uenqh.model.files;

/**
 * Represents an audio file.
 * Extends the {@link File} class.
 *
 * @author uenqh
 */
public class AudioFile extends File {
    private static final String ILLEGAL_LENGTH = null;
    private static final int SAMPLE_MIN_LENGTH = 0;
    private static final String SAMPLE_NAME = "sample";
    private static final int SHORT_MIN_LENGTH = 10;
    private static final String SHORT_NAME = "short";
    private static final int NORMAL_MIN_LENGTH = 60;
    private static final String NORMAL_NAME = "normal";
    private static final int LONG_MIN_LENGTH = 300;
    private static final String LONG_NAME = "long";

    /**
     * Creates an AudioFile object with the given identifier and access amount.
     *
     * @param identifier   the identifier of the audio file
     * @param accessAmount the access amount of the audio file
     */
    public AudioFile(String identifier, int accessAmount) {
        super(identifier, accessAmount);
    }

    /**
     * Converts the given length of an audio file into a descriptive name based on its duration.
     *
     * @param length the length of the audio file in seconds
     * @return a descriptive name indicating the duration of the audio file
     */
    public static String audioLengthConverter(int length) {
        if (length >= LONG_MIN_LENGTH) {
            return LONG_NAME;
        } else if (length >= NORMAL_MIN_LENGTH) {
            return NORMAL_NAME;
        } else if (length >= SHORT_MIN_LENGTH) {
            return SHORT_NAME;
        } else if (length >= SAMPLE_MIN_LENGTH) {
            return SAMPLE_NAME;
        }
        return ILLEGAL_LENGTH;
    }
}
