package edu.kit.uenqh.model.files;

import edu.kit.uenqh.model.files.tags.MultiValueTag;
import edu.kit.uenqh.model.files.tags.NumericTag;
import edu.kit.uenqh.model.files.tags.Tag;
import edu.kit.uenqh.model.files.tags.TagFactory;


import java.util.HashSet;
import java.util.List;
import static edu.kit.uenqh.model.files.FileConstants.MULTI_VALUE_TAG_NAME;

/**
 * Handles file-related operations such as formatting file records and tag conversions.
 *
 * @author uenqh
 */
public final class FileHandler {
    // Tag conversions
    private static final String NUMERIC_SIZE_TAG_NAME = "size";
    private static final String IMAGE_SIZE_TAG_NAME = "imagesize";
    private static final String NUMERIC_LENGTH_TAG_NAME = "length";
    private static final String AUDIO_LENGTH_TAG_NAME = "audiolength";
    private static final String VIDEO_LENGTH_TAG_NAME = "videolength";
    private static final String WORDS_TAG_NAME = "words";
    private static final String TEXT_LENGTH_TAG_NAME = "textlength";
    private static final String GENRE_TAG_NAME = "genre";
    private static final String AUDIO_GENRE_TAG_NAME = "audiogenre";
    private static final String VIDEO_GENRE_TAG_NAME = "videogenre";
    private static final String TEXT_GENRE_TAG_NAME = "textgenre";
    private static final String COLLIDING_TAGS_FORMAT = "the entered tags '%s' and '%s' are colliding with each other!";

    /**
     * Private constructor to prevent instantiation.
     */
    private FileHandler() {

    }

    /**
     * Formats a files.
     *
     * @param files the files to be formatted
     * @return the formatted file record
     */
    public static List<File> formattedFileRecord(List<File> files) {
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i) instanceof AudioFile) {
                // Handle audio file
                files.set(i, handleAudioFile(files.get(i)));
            } else if (files.get(i) instanceof ImageFile) {
                // Handle image file
                files.set(i, handleImageFile(files.get(i)));
            } else if (files.get(i) instanceof ProgramFile) {
                // Handle program file
                files.set(i, handleProgramFile(files.get(i)));
            } else if (files.get(i) instanceof TextFile) {
                // Handle text file
                files.set(i, handleTextFile(files.get(i)));
            } else if (files.get(i) instanceof VideoFile) {
                // Handle video file
                files.set(i, handleVideoFile(files.get(i)));
            }
        }
        return files;
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
            if (tagNames.contains(GENRE_TAG_NAME.toLowerCase()) && tagNames.contains(AUDIO_GENRE_TAG_NAME)) {
                message = String.format(COLLIDING_TAGS_FORMAT, GENRE_TAG_NAME, AUDIO_GENRE_TAG_NAME);
            }
            if (tagNames.contains(GENRE_TAG_NAME.toLowerCase()) && tagNames.contains(TEXT_GENRE_TAG_NAME)) {
                message = String.format(COLLIDING_TAGS_FORMAT, GENRE_TAG_NAME, TEXT_GENRE_TAG_NAME);
            }
            if (tagNames.contains(GENRE_TAG_NAME.toLowerCase()) && tagNames.contains(VIDEO_GENRE_TAG_NAME)) {
                message = String.format(COLLIDING_TAGS_FORMAT, GENRE_TAG_NAME, TEXT_GENRE_TAG_NAME);
            }
            if (tagNames.contains(NUMERIC_SIZE_TAG_NAME.toLowerCase()) && tagNames.contains(IMAGE_SIZE_TAG_NAME)) {
                message = String.format(COLLIDING_TAGS_FORMAT, NUMERIC_SIZE_TAG_NAME, IMAGE_SIZE_TAG_NAME);
            }
            if (tagNames.contains(NUMERIC_LENGTH_TAG_NAME.toLowerCase()) && tagNames.contains(AUDIO_LENGTH_TAG_NAME)) {
                message = String.format(COLLIDING_TAGS_FORMAT, NUMERIC_LENGTH_TAG_NAME, AUDIO_LENGTH_TAG_NAME);
            }
            if (tagNames.contains(NUMERIC_LENGTH_TAG_NAME.toLowerCase()) && tagNames.contains(VIDEO_LENGTH_TAG_NAME)) {
                message = String.format(COLLIDING_TAGS_FORMAT, NUMERIC_LENGTH_TAG_NAME, VIDEO_LENGTH_TAG_NAME);
            }
            if (tagNames.contains(WORDS_TAG_NAME.toLowerCase()) && tagNames.contains(TEXT_LENGTH_TAG_NAME)) {
                message = String.format(COLLIDING_TAGS_FORMAT, WORDS_TAG_NAME, TEXT_LENGTH_TAG_NAME);
            }
            if (!message.isEmpty()) {
                return message;
            }
        }
        return message;
    }
}
