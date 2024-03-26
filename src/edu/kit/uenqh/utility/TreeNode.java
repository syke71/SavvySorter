package edu.kit.uenqh.utility;

import edu.kit.uenqh.model.files.File;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a node in a tree structure used for data processing.
 *
 * @author uenqh
 */
public class TreeNode {
    private static final String FINAL_CHILD_OUTPUT_FORMAT = "\"%s\"";
    private static final String PREFIX = "/";
    private static final String OUTPUT_SEPARATOR = "---";
    private static final String NEXT_LINE = "\n";
    private static final String EMPTY_STRING = "";
    private static final String TAG_CONNECTOR = "=";
    private static final String DECIMAL_FORMAT = "%.2f";
    private final String identifier;
    private final HashMap<String, Double> informationGain;
    private String connectingEdge;
    private double probability;
    private final List<TreeNode> children;
    private final List<File> files;

    /**
     * Constructs a TreeNode with the given identifier.
     *
     * @param identifier The identifier of the TreeNode.
     */
    public TreeNode(String identifier) {
        this.identifier = identifier;
        this.informationGain = new HashMap<>();
        this.children = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    /**
     * Constructs a TreeNode with the given list of files.
     *
     * @param files The list of files associated with the TreeNode.
     */
    public TreeNode(List<File> files) {
        this.files = new ArrayList<>(files);
        this.identifier = "";
        this.informationGain = new HashMap<>();
        this.children = new ArrayList<>();
    }

    /**
     * Retrieves the identifier of the TreeNode.
     *
     * @return The identifier of the TreeNode.
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Retrieves the connecting edge of the TreeNode.
     *
     * @return The connecting edge of the TreeNode.
     */
    public String getConnectingEdge() {
        return this.connectingEdge;
    }

    /**
     * Sets the connecting edge of the TreeNode.
     *
     * @param s The connecting edge to set.
     */
    public void setConnectingEdge(String s) {
        this.connectingEdge = s;
    }

    /**
     * Retrieves the information gain of the TreeNode.
     *
     * @return The information gain of the TreeNode.
     */
    public HashMap<String, Double> getInformationGain() {
        return this.informationGain;
    }

    /**
     * Retrieves the children of the TreeNode.
     *
     * @return The children of the TreeNode.
     */
    public List<TreeNode> getChildren() {
        return children;
    }

    /**
     * Adds a child TreeNode to the TreeNode.
     *
     * @param child The child TreeNode to add.
     */
    public void addChild(TreeNode child) {
        children.add(child);
    }

    /**
     * Sorts the children of the TreeNode based on probability (descending) and identifier (lexicographically).
     */
    private void sortChildren() {
        this.children.sort(Comparator.comparingDouble(TreeNode::getProbability).thenComparing(TreeNode::getIdentifier).reversed());
    }

    /**
     * Retrieves the probability of the TreeNode.
     *
     * @return The probability of the TreeNode.
     */
    public double getProbability() {
        return probability;
    }

    /**
     * Sets the probability of the TreeNode.
     *
     * @param probability The probability to set.
     */
    public void setProbability(double probability) {
        this.probability = probability;
    }

    /**
     * Generates a string representation of the TreeNode.
     *
     * @return The string representation of the TreeNode.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(printDataRecursive(this, new StringBuilder(), EMPTY_STRING));
        builder.append(OUTPUT_SEPARATOR + NEXT_LINE);
        builder.append(printTreeRecursive(this, new StringBuilder(), EMPTY_STRING));
        builder.replace(builder.length() - NEXT_LINE.length(), builder.length(), "");
        return builder.toString();
    }

    private StringBuilder printDataRecursive(TreeNode node, StringBuilder builder, String path) {
        for (String s : sortHashMapByValue(node.getInformationGain()).keySet()) {
            if (node.getInformationGain().get(s) != 0) {
                builder.append(path.toLowerCase());
                builder.append(PREFIX);
                builder.append(s.toLowerCase());
                builder.append(TAG_CONNECTOR);
                builder.append(String.format(DECIMAL_FORMAT, node.getInformationGain().get(s)));
                builder.append(NEXT_LINE);
            }
        }
        for (TreeNode child : node.getChildren()) {
            if (!child.getChildren().isEmpty()) {
                StringBuilder newPath = new StringBuilder();
                newPath.append(path.toLowerCase());
                newPath.append(PREFIX);
                newPath.append(node.identifier);
                newPath.append(TAG_CONNECTOR);
                newPath.append(child.getConnectingEdge());
                builder.append(printDataRecursive(child, new StringBuilder(), newPath.toString()));
            }
        }
        return builder;
    }

    private StringBuilder printTreeRecursive(TreeNode node, StringBuilder builder, String path) {
        if (node.children.isEmpty()) {
            builder.append(PREFIX);
            for (File file : node.files) {
                builder.append(String.format(FINAL_CHILD_OUTPUT_FORMAT, file.getIdentifier()));
                builder.append(NEXT_LINE);
                builder.append(path).append(PREFIX);
            }
            builder.replace(builder.length() - PREFIX.length() - path.length(), builder.length(), "");
        } else {
            node.sortChildren();
            for (TreeNode child : node.children) {
                StringBuilder newPath = new StringBuilder();
                newPath.append(path);
                newPath.append(PREFIX);
                newPath.append(node.identifier);
                newPath.append(TAG_CONNECTOR);
                newPath.append(child.getConnectingEdge());
                if (child.getChildren().isEmpty()) {
                    builder.append(newPath);
                }
                builder.append(printTreeRecursive(child, new StringBuilder(), newPath.toString()));
            }
        }
        return builder;
    }

    private static HashMap<String, Double> sortHashMapByValue(HashMap<String, Double> map) {
        // Convert HashMap to List of Map Entries
        List<Map.Entry<String, Double>> list = new ArrayList<>(map.entrySet());

        // Sort the list based on double values
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Create a new LinkedHashMap to preserve the insertion order
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}
