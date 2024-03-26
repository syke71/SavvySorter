package edu.kit.uenqh.model;

import edu.kit.uenqh.model.files.File;
import edu.kit.uenqh.model.files.tags.BinaryTagType;
import edu.kit.uenqh.model.files.tags.Tag;
import edu.kit.uenqh.utility.TreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides methods for calculating information theory metrics and creating file trees.
 *
 * @author uenqh
 */
public final class InformationTheory {

    /**
     * The minimum value for recursion.
     */
    public static final double MIN_VALUE_FOR_RECURSION = 0.001;

    /**
     * Private constructor to prevent instantiation.
     */
    private InformationTheory() {

    }

    /**
     * Calculates the entropy H(D) for a given list of files.
     *
     * @param files the list of files
     * @return the entropy
     */
    private static double calculateEntropy(ArrayList<File> files) {
        double entropy = 0;
        for (File file : files) {
            double probability = (double) file.getAccessAmount() / (double) calculateTotalAccesses(files);
            entropy -= probability * log2(probability);
        }
        return entropy;
    }

    /**
     * Calculates the logarithm base 2 of the given value.
     *
     * @param x the value
     * @return the logarithm base 2
     */
    private static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    /**
     * Calculates the total access amount of all documents in the given list of files.
     *
     * @param files the list of files
     * @return the total access amount
     */
    private static int calculateTotalAccesses(ArrayList<File> files) {
        int totalAccesses = 0;
        for (File file : files) {
            totalAccesses += file.getAccessAmount();
        }
        return totalAccesses;
    }

    /**
     * Calculates the conditional entropy H(D|t) for a list of tags and files.
     *
     * @param tags  the list of tags
     * @param files the list of files
     * @return the conditional entropy
     */
    private static double calculateConditionalEntropy(ArrayList<Tag> tags, ArrayList<File> files) {
        double conditionalEntropy = 0;
        ArrayList<File> undefinedTagFiles = new ArrayList<>(files);
        for (Tag tag : tags) {
            ArrayList<File> filteredFiles = filterFilesByTag(tag, files);
            undefinedTagFiles.removeAll(filteredFiles);
            double probability = calculateProbability(filteredFiles, files);
            conditionalEntropy += probability * calculateEntropy(filteredFiles);
        }
        conditionalEntropy += calculateProbability(undefinedTagFiles, files) * calculateEntropy(undefinedTagFiles);
        return conditionalEntropy;
    }

    /**
     * Calculates the probability of the filtered files among all files.
     *
     * @param filteredFiles the list of filtered files
     * @param files         the list of all files
     * @return the probability
     */
    public static double calculateProbability(ArrayList<File> filteredFiles, ArrayList<File> files) {
        double probability = 0;
        for (File file : filteredFiles) {
            probability += (double) file.getAccessAmount() / (double) calculateTotalAccesses(files);
        }
        return probability;
    }

    /**
     * Filters the files by the given tag.
     *
     * @param tag   the tag to filter by
     * @param files the list of files
     * @return the filtered list of files
     */
    private static ArrayList<File> filterFilesByTag(Tag tag, ArrayList<File> files) {
        ArrayList<File> filteredFiles = new ArrayList<>();
        for (File file : files) {
            for (Tag t : file.getTags()) {
                if (tag.equals(t)) {
                    filteredFiles.add(file);
                }
            }
        }
        return filteredFiles;
    }

    /**
     * Calculates the information gain IG(D, t) for a list of tags and files.
     *
     * @param tags  the list of tags
     * @param files the list of files
     * @return the information gain
     */
    private static double calculateInformationGain(ArrayList<Tag> tags, ArrayList<File> files) {
        double entropyD = calculateEntropy(files);
        double conditionalEntropy = calculateConditionalEntropy(tags, files);
        return entropyD - conditionalEntropy;
    }

    /**
     * Calculates the information gain for each tag and creates a map of tag to information gain.
     *
     * @param tagByName the map of tag names to list of tags
     * @param files     the list of files
     * @return the map of tag name to information gain
     */
    private static Map<String, Double> calculateInformationGain(Map<String, ArrayList<Tag>> tagByName, ArrayList<File> files) {
        Map<String, Double> informationGain = new HashMap<>();
        for (String s : tagByName.keySet()) {
            informationGain.put(s, calculateInformationGain(tagByName.get(s), files));
        }
        return informationGain;
    }

    /**
     * Creates a file tree based on the tag by name map and the list of files.
     *
     * @param tagByName the map of tag names to list of tags
     * @param files     the list of files
     * @return the root of the file tree
     */
    public static TreeNode createFileTree(Map<String, ArrayList<Tag>> tagByName, ArrayList<File> files) {
        Map<String, Double> informationGain = calculateInformationGain(tagByName, files);
        double max;
        if (informationGain.isEmpty()) {
            max = 0;
        } else {
            max = Collections.max(informationGain.values());
        }

        String filterTag = "";
        for (String s : informationGain.keySet()) {
            if (informationGain.get(s) == max) {
                filterTag = s;
            }
        }

        ArrayList<File> filteredFiles = new ArrayList<>(files);
        ArrayList<TreeNode> children = new ArrayList<>();

        Map<String, ArrayList<Tag>> filteredTagByName = new HashMap<>();
        for (String s : tagByName.keySet()) {
            filteredTagByName.put(s, tagByName.get(s));
        }
        filteredTagByName.remove(filterTag);
        // continue recursion
        if (max >= MIN_VALUE_FOR_RECURSION && max != 1) {
            // create new children using filtered lists
            for (Tag t : tagByName.get(filterTag)) {
                filteredFiles.removeAll(filterFilesByTag(t, files));
                TreeNode child = createFileTree(filteredTagByName, filterFilesByTag(t, files));
                child.setConnectingEdge(t.getValue());
                child.setProbability(calculateProbability(filterFilesByTag(t, files), files));
                children.add(child);
            }
            // create new child for "undefined" tag
            if (!filteredFiles.isEmpty()) {
                TreeNode child = createFileTree(filteredTagByName, filteredFiles);
                child.setConnectingEdge(String.valueOf(BinaryTagType.UNDEFINED).toLowerCase());
                child.setProbability(calculateProbability(filteredFiles, files));
                children.add(child);
            }
        } else {
            // create TreeNode<String> using a file
            return new TreeNode(filteredFiles);
        }
        TreeNode parent = new TreeNode(filterTag);
        for (TreeNode child : children) {
            parent.addChild(child);
        }
        parent.getInformationGain().putAll(informationGain);
        return parent;
    }
}
