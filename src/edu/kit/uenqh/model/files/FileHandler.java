package edu.kit.uenqh.model.files;

import edu.kit.uenqh.model.SortingSystem;
import edu.kit.uenqh.model.files.tags.MultiValueTag;
import edu.kit.uenqh.model.files.tags.NumericTag;
import edu.kit.uenqh.model.files.tags.Tag;
import edu.kit.uenqh.model.files.tags.TagFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static edu.kit.uenqh.model.files.FileConstants.MULTI_VALUE_TAG_NAME;

/**
 * Handles file-related operations such as formatting file records and tag conversions.
 *
 * @author uenqh
 */
public class FileHandler {
    // Tag conversions
    private static final String NUMERIC_SIZE_TAG_NAME = "Size";
    private static final String IMAGE_SIZE_TAG_NAME = "ImageSize";
    private static final String NUMERIC_LENGTH_TAG_NAME = "Length";
    private static final String AUDIO_LENGTH_TAG_NAME = "AudioLength";
    private static final String VIDEO_LENGTH_TAG_NAME = "VideoLength";
    private static final String WORDS_TAG_NAME = "Words";
    private static final String TEXT_LENGTH_TAG_NAME = "TextLength";
    private static final String GENRE_TAG_NAME = "Genre";
    private static final String AUDIO_GENRE_TAG_NAME = "AudioGenre";
    private static final String VIDEO_GENRE_TAG_NAME = "VideoGenre";
    private static final String TEXT_GENRE_TAG_NAME = "TextGenre";
    private static final String COLLIDING_TAGS_FORMAT = "the entered tags '%s' and '%s' are colliding with each other!";


    private final SortingSystem sortingSystem;
    private final Map<String, Tag> tagMap;
    private final List<String> fileTypes;

    /**
     * Constructs a new FileHandler with the given sorting system.
     *
     * @param sortingSystem the sorting system to be used
     */
    public FileHandler(SortingSystem sortingSystem) {
        this.sortingSystem = sortingSystem;
        this.fileTypes = new ArrayList<>();
        this.tagMap = new HashMap<>();
    }

    /**
     * Formats a file record.
     *
     * @param fileRecord the file record to be formatted
     * @return the formatted file record
     */
    public static FileRecord formattedFileRecord(FileRecord fileRecord) {
        for (int i = 0; i < fileRecord.files().size(); i++) {
            if (fileRecord.files().get(i) instanceof AudioFile) {
                // Handle audio file
                fileRecord.files().set(i, handleAudioFile(fileRecord.files().get(i)));
            } else if (fileRecord.files().get(i) instanceof ImageFile) {
                // Handle image file
                fileRecord.files().set(i, handleImageFile(fileRecord.files().get(i)));
            } else if (fileRecord.files().get(i) instanceof ProgramFile) {
                // Handle program file
                fileRecord.files().set(i, handleProgramFile(fileRecord.files().get(i)));
            } else if (fileRecord.files().get(i) instanceof TextFile) {
                // Handle text file
                fileRecord.files().set(i, handleTextFile(fileRecord.files().get(i)));
            } else if (fileRecord.files().get(i) instanceof VideoFile) {
                // Handle video file
                fileRecord.files().set(i, handleVideoFile(fileRecord.files().get(i)));
            }
        }
        return fileRecord;
    }

    private static File handleImageFile(File file) {
        for (int i = 0; i < file.getTags().size(); i++) {
            Tag tag = file.getTags().get(i);
            if (tag instanceof NumericTag && tag.getName().equalsIgnoreCase(NUMERIC_SIZE_TAG_NAME)) {
                int size = Integer.parseInt(tag.getValue());
                Tag newTag = TagFactory.createTag(MULTI_VALUE_TAG_NAME, IMAGE_SIZE_TAG_NAME, ImageFile.imageSizeConverter(size));
                file.getTags().set(i, newTag);
            }
        }
        return file;
    }

    private static File handleAudioFile(File file) {
        for (int i = 0; i < file.getTags().size(); i++) {
            Tag tag = file.getTags().get(i);
            if (tag instanceof NumericTag && tag.getName().equalsIgnoreCase(NUMERIC_LENGTH_TAG_NAME)) {
                int length = Integer.parseInt(tag.getValue());
                Tag newTag = TagFactory.createTag(MULTI_VALUE_TAG_NAME, AUDIO_LENGTH_TAG_NAME, AudioFile.audioLengthConverter(length));
                file.getTags().set(i, newTag);
            } else if (tag instanceof MultiValueTag && tag.getName().equalsIgnoreCase(GENRE_TAG_NAME)) {
                tag.setName(AUDIO_GENRE_TAG_NAME);
            }
        }
        return file;
    }

    private static File handleVideoFile(File file) {
        for (int i = 0; i < file.getTags().size(); i++) {
            Tag tag = file.getTags().get(i);
            if (tag instanceof NumericTag && tag.getName().equalsIgnoreCase(NUMERIC_LENGTH_TAG_NAME)) {
                int length = Integer.parseInt(tag.getValue());
                Tag newTag = TagFactory.createTag(MULTI_VALUE_TAG_NAME, VIDEO_LENGTH_TAG_NAME, VideoFile.videoLengthConverter(length));
                file.getTags().set(i, newTag);
            } else if (tag instanceof MultiValueTag && tag.getName().equalsIgnoreCase(GENRE_TAG_NAME)) {
                tag.setName(VIDEO_GENRE_TAG_NAME);
            }
        }
        return file;
    }

    private static File handleTextFile(File file) {
        for (int i = 0; i < file.getTags().size(); i++) {
            Tag tag = file.getTags().get(i);
            if (tag instanceof NumericTag && tag.getName().equalsIgnoreCase(WORDS_TAG_NAME)) {
                int length = Integer.parseInt(tag.getValue());
                Tag newTag = TagFactory.createTag(MULTI_VALUE_TAG_NAME, TEXT_LENGTH_TAG_NAME, TextFile.textLengthConverter(length));
                file.getTags().set(i, newTag);
            } else if (tag instanceof MultiValueTag && tag.getName().equalsIgnoreCase(GENRE_TAG_NAME)) {
                tag.setName(TEXT_GENRE_TAG_NAME);
            }
        }
        return file;
    }

    private static File handleProgramFile(File file) {
        // currently no handling necessary
        return file;
    }

    /**
     * Checks for colliding tag names within a list of files and returns a message if collision is detected.
     * A collision occurs when certain tags appear together, indicating potential conflicts or inconsistencies.
     *
     * @param files A list of files to check for colliding tag names.
     * @return A message describing the colliding tags, or an empty string if no collisions are found.
     */
    public static String checkCollidingTagNames(List<File> files) {
        String message = "";
        for (File file : files) {
            HashSet<String> tagNames = new HashSet<>();
            for (Tag tag : file.getTags()) {
                tagNames.add(tag.getName().toLowerCase());
            }
            if (tagNames.contains(GENRE_TAG_NAME.toLowerCase()) && tagNames.contains(AUDIO_GENRE_TAG_NAME.toLowerCase())) {
                message = String.format(COLLIDING_TAGS_FORMAT, GENRE_TAG_NAME, AUDIO_GENRE_TAG_NAME);
            }
            if (tagNames.contains(GENRE_TAG_NAME.toLowerCase()) && tagNames.contains(TEXT_GENRE_TAG_NAME.toLowerCase())) {
                message = String.format(COLLIDING_TAGS_FORMAT, GENRE_TAG_NAME, TEXT_GENRE_TAG_NAME);
            }
            if (tagNames.contains(GENRE_TAG_NAME.toLowerCase()) && tagNames.contains(VIDEO_GENRE_TAG_NAME.toLowerCase())) {
                message = String.format(COLLIDING_TAGS_FORMAT, GENRE_TAG_NAME, TEXT_GENRE_TAG_NAME);
            }
            if (tagNames.contains(NUMERIC_SIZE_TAG_NAME.toLowerCase()) && tagNames.contains(IMAGE_SIZE_TAG_NAME.toLowerCase())) {
                message = String.format(COLLIDING_TAGS_FORMAT, NUMERIC_SIZE_TAG_NAME, IMAGE_SIZE_TAG_NAME);
            }
            if (tagNames.contains(NUMERIC_LENGTH_TAG_NAME.toLowerCase()) && tagNames.contains(AUDIO_LENGTH_TAG_NAME.toLowerCase())) {
                message = String.format(COLLIDING_TAGS_FORMAT, NUMERIC_LENGTH_TAG_NAME, AUDIO_LENGTH_TAG_NAME);
            }
            if (tagNames.contains(NUMERIC_LENGTH_TAG_NAME.toLowerCase()) && tagNames.contains(VIDEO_LENGTH_TAG_NAME)) {
                message = String.format(COLLIDING_TAGS_FORMAT, NUMERIC_LENGTH_TAG_NAME, VIDEO_LENGTH_TAG_NAME);
            }
            if (tagNames.contains(NUMERIC_LENGTH_TAG_NAME.toLowerCase()) && tagNames.contains(TEXT_LENGTH_TAG_NAME)) {
                message = String.format(COLLIDING_TAGS_FORMAT, NUMERIC_LENGTH_TAG_NAME, TEXT_LENGTH_TAG_NAME);
            }
            if (!message.isEmpty()) {
                return message;
            }
        }
        return message;
    }




}
