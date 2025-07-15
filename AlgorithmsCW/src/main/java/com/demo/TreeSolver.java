package com.demo;

import java.util.*;

public class TreeSolver {

    // Class to represent a search node containing state and path information
    private static class SearchNode {
        private TreeState state;
        private List<String> path;
        private int depth;

        public SearchNode(TreeState state, List<String> path, int depth) {
            this.state = state;
            this.path = new ArrayList<>(path);
            this.depth = depth;
        }

        public TreeState getState() { return state; }
        public List<String> getPath() { return path; }
        public int getDepth() { return depth; }
    }

    /**
     * Creates the target BST state from the initial tree
     * The target has the same structure but values arranged as a BST
     */
    public static TreeState createTargetBST(TreeState initialState) {
        int n = initialState.getNumberOfNode();
        int[] targetValues = new int[n];

        // Get sorted values (1 to n)
        List<Integer> sortedValues = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            sortedValues.add(i);
        }

        // Fill the tree in BST order using in-order traversal
        fillBSTInOrder(targetValues, sortedValues, 0, 0, n - 1);

        return new TreeState(targetValues);
    }

    /**
     * Recursively fills the tree array to create a BST structure
     */
    private static void fillBSTInOrder(int[] tree, List<Integer> sortedValues,
                                       int nodeIndex, int start, int end) {
        if (start > end || nodeIndex >= tree.length) {
            return;
        }

        // Calculate middle element for this subtree
        int mid = start + (end - start) / 2;
        tree[nodeIndex] = sortedValues.get(mid);

        // Recursively fill left and right subtrees
        int leftChild = 2 * nodeIndex + 1;
        int rightChild = 2 * nodeIndex + 2;

        fillBSTInOrder(tree, sortedValues, leftChild, start, mid - 1);
        fillBSTInOrder(tree, sortedValues, rightChild, mid + 1, end);
    }

    /**
     * Solves the tree sorting problem using BFS to find shortest path
     */
    public static SearchResult solve(TreeState initialState) {
        TreeState targetState = createTargetBST(initialState);

        // If already at target, return empty solution
        if (initialState.equals(targetState)) {
            return new SearchResult(new ArrayList<>(), 0, 1);
        }

        // BFS data structures
        Queue<SearchNode> queue = new LinkedList<>();
        Set<TreeState> visited = new HashSet<>();

        // Initialize search
        queue.offer(new SearchNode(initialState, new ArrayList<>(), 0));
        visited.add(initialState);

        int nodesExplored = 0;

        while (!queue.isEmpty()) {
            SearchNode current = queue.poll();
            nodesExplored++;

            // Try all possible swaps (swap each non-root node with its parent)
            for (int childIndex = 1; childIndex < current.getState().getNumberOfNode(); childIndex++) {
                TreeState newState = current.getState().swapWithParent(childIndex);

                if (newState != null && !visited.contains(newState)) {
                    visited.add(newState);

                    // Create new path with this swap
                    List<String> newPath = new ArrayList<>(current.getPath());
                    int parentIndex = current.getState().getParentIndex(childIndex);
                    String swapDescription = String.format("Swap node %d (value %d) with node %d (value %d)",
                            childIndex, current.getState().getValue(childIndex),
                            parentIndex, current.getState().getValue(parentIndex));
                    newPath.add(swapDescription);

                    // Check if we reached the target
                    if (newState.equals(targetState)) {
                        return new SearchResult(newPath, current.getDepth() + 1, nodesExplored);
                    }

                    // Add to queue for further exploration
                    queue.offer(new SearchNode(newState, newPath, current.getDepth() + 1));
                }
            }
        }

        return null;
    }

    /**
     * Result class to hold solution information
     */
    public static class SearchResult {
        private List<String> swapSequence;
        private int numberOfSwaps;
        private int nodesExplored;

        public SearchResult(List<String> swapSequence, int numberOfSwaps, int nodesExplored) {
            this.swapSequence = swapSequence;
            this.numberOfSwaps = numberOfSwaps;
            this.nodesExplored = nodesExplored;
        }

        public List<String> getSwapSequence() { return swapSequence; }
        public int getNumberOfSwaps() { return numberOfSwaps; }
        public int getNodesExplored() { return nodesExplored; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Solution found in ").append(numberOfSwaps).append(" swaps:\n");
            sb.append("Nodes explored: ").append(nodesExplored).append("\n");
            sb.append("Swap sequence:\n");
            for (int i = 0; i < swapSequence.size(); i++) {
                sb.append((i + 1)).append(". ").append(swapSequence.get(i)).append("\n");
            }
            return sb.toString();
      7/*    }
    }
}
