package edu.kit.uenqh.model.files;

import edu.kit.uenqh.model.files.tags.Tag;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Represents a record containing files and associated tags.
 *
 * @author uenqh
 *
 * @param files the list of files in the record
 * @param tags  the set of unique tags associated with the files
 * @param id    the identifier of the record
 */
public record FileRecord(ArrayList<File> files, HashSet<Tag> tags, int id) {

}
