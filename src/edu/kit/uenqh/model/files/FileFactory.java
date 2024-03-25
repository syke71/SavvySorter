package edu.kit.uenqh.model.files;

import static edu.kit.uenqh.model.files.FileConstants.AUDIO_FILE_NAME;
import static edu.kit.uenqh.model.files.FileConstants.IMAGE_FILE_NAME;
import static edu.kit.uenqh.model.files.FileConstants.PROGRAM_FILE_NAME;
import static edu.kit.uenqh.model.files.FileConstants.TEXT_FILE_NAME;
import static edu.kit.uenqh.model.files.FileConstants.VIDEO_FILE_NAME;

/**
 * Factory class for creating files.
 *
 * @author uenqh
 */
public final class FileFactory {
    private static final String INVALID_FILE_TYPE_FORMAT = "invalid file type '%s'!";

    /**
     * Private constructor to prevent instantiation.
     */
    private FileFactory() {
    }

    /**
     * Creates a new file based on the provided file type, identifier, and access amount.
     *
     * @param fileType     the type of the file
     * @param identifier   the unique identifier of the file
     * @param accessAmount the access amount of the file
     * @return the created file
     * @throws InvalidFileTypeException if the provided file type is invalid
     */
    public static File createFile(String fileType, String identifier, int accessAmount) throws InvalidFileTypeException {
        return switch (fileType.toLowerCase()) {
            case AUDIO_FILE_NAME -> new AudioFile(identifier, accessAmount);
            case IMAGE_FILE_NAME -> new ImageFile(identifier, accessAmount);
            case PROGRAM_FILE_NAME -> new ProgramFile(identifier, accessAmount);
            case TEXT_FILE_NAME -> new TextFile(identifier, accessAmount);
            case VIDEO_FILE_NAME -> new VideoFile(identifier, accessAmount);
            default -> throw new InvalidFileTypeException(String.format(INVALID_FILE_TYPE_FORMAT, fileType));
        };
    }

}
