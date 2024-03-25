package edu.kit.uenqh.model.files;

/**
 * An exception indicating that an invalid file type was encountered.
 *
 * @author uenqh
 */
public class InvalidFileTypeException extends Exception {

    /**
     * Constructs a new InvalidFileTypeException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidFileTypeException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidFileTypeException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public InvalidFileTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidFileTypeException with the specified cause.
     *
     * @param cause the cause
     */
    public InvalidFileTypeException(Throwable cause) {
        super(cause);
    }
}
