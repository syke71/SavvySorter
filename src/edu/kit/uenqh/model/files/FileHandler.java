package edu.kit.uenqh.model.files;

import edu.kit.uenqh.model.SortingSystem;
import edu.kit.uenqh.model.files.tags.CategoricalTag;
import edu.kit.uenqh.model.files.tags.NumericTag;
import edu.kit.uenqh.model.files.tags.Tag;
import edu.kit.uenqh.model.files.tags.TagFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.kit.uenqh.model.files.FileConstants.CATEGORICAL_TAG_NAME;
import static edu.kit.uenqh.model.files.FileConstants.TEXT_FILE_NAME;

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

    private static final String GENRE_TAG_NAME = "Genre";
    private static final String AUDIO_GENRE_TAG_NAME = "AudioGenre";
    private static final String VIDEO_GENRE_TAG_NAME = "VideoGenre";
    private static final String TEXT_GENRE_TAG_NAME = "TextGenre";

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
                Tag newTag = TagFactory.createTag(CATEGORICAL_TAG_NAME, IMAGE_SIZE_TAG_NAME, ImageFile.imageSizeConverter(size));
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
                Tag newTag = TagFactory.createTag(CATEGORICAL_TAG_NAME, AUDIO_LENGTH_TAG_NAME, AudioFile.audioLengthConverter(length));
                file.getTags().set(i, newTag);
            } else if (tag instanceof CategoricalTag && tag.getName().equalsIgnoreCase(GENRE_TAG_NAME)) {
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
                Tag newTag = TagFactory.createTag(CATEGORICAL_TAG_NAME, VIDEO_LENGTH_TAG_NAME, VideoFile.videoLengthConverter(length));
                file.getTags().set(i, newTag);
            } else if (tag instanceof CategoricalTag && tag.getName().equalsIgnoreCase(GENRE_TAG_NAME)) {
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
                Tag newTag = TagFactory.createTag(CATEGORICAL_TAG_NAME, TEXT_FILE_NAME, TextFile.textLengthConverter(length));
                file.getTags().set(i, newTag);
            } else if (tag instanceof CategoricalTag && tag.getName().equalsIgnoreCase(GENRE_TAG_NAME)) {
                tag.setName(TEXT_GENRE_TAG_NAME);
            }
        }
        return file;
    }

    private static File handleProgramFile(File file) {
        // currently no handling necessary
        return file;
    }
}
