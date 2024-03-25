package edu.kit.uenqh.model.files;

/**
 * Represents a program file.
 *
 * @author uenqh
 */
public class ProgramFile extends File {

    /**
     * Constructs a ProgramFile with the specified identifier and access amount.
     *
     * @param identifier   The unique identifier of the program file.
     * @param accessAmount The number of accesses to the program file.
     */
    public ProgramFile(String identifier, int accessAmount) {
        super(identifier, accessAmount);
    }
}
