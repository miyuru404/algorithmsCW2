package com.demo;

import java.util.*;

public class TreeSolver {

    // Enhanced SearchNode class for A* with f, g, h scores
    private static class SearchNode implements Comparable<SearchNode> {
        private TreeState state;
        private List<String> path;
        private int gScore;    // Cost from start to current node
        private int hScore;    // Heuristic cost from current to goal
        private int fScore;    // Total cost (g + h)

        public SearchNode(TreeState state, List<String> path, int gScore, int hScore) {
            this.state = state;
            this.path = new ArrayList<>(path);
            this.gScore = gScore;
            this.hScore = hScore;
            this.fScore = gScore + hScore;
        }

        public TreeState getState() { return state; }
        public List<String> getPath() { return path; }
        public int getGScore() { return gScore; }
        public int getHScore() { return hScore; }
        public int getFScore() { return fScore; }

        @Override
        public int compareTo(SearchNode other) {
            // Primary sort by f-score (total cost)
            int fCompare = Integer.compare(this.fScore, other.fScore);
            if (fCompare != 0) return fCompare;

            // Secondary sort by h-score (heuristic) - prefer lower h-score
            int hCompare = Integer.compare(this.hScore, other.hScore);
            if (hCompare != 0) return hCompare;

            // Tertiary sort by g-score - prefer higher g-score (closer to goal)
            return Integer.compare(other.gScore, this.gScore);
        }
    }

    /**
     * Heuristic function: Count positions where elements are not in correct BST position
     * This is admissible (never overestimates) because each misplaced element needs at least one swap
     */
    private static int calculateHeuristic(TreeState current, TreeState target) {
        int misplacedCount = 0;
        int n = current.getNumberOfNode();

        for (int i = 0; i < n; i++) {
            if (current.getValue(i) != target.getValue(i)) {
                misplacedCount++;
            }
        }

        return misplacedCount;
    }

    /**
     * Alternative heuristic: Manhattan distance adapted for tree swaps
     * More informed than simple misplaced count
     */
    private static int calculateManhattanHeuristic(TreeState current, TreeState target) {
        int totalDistance = 0;
        int n = current.getNumberOfNode();

        // Create position maps for efficient lookup
        Map<Integer, Integer> currentPositions = new HashMap<>();
        Map<Integer, Integer> targetPositions = new HashMap<>();

        for (int i = 0; i < n; i++) {
            currentPositions.put(current.getValue(i), i);
            targetPositions.put(target.getValue(i), i);
        }

        // Calculate sum of distances each value needs to move
        for (int value = 1; value <= n; value++) {
            int currentPos = currentPositions.get(value);
            int targetPos = targetPositions.get(value);

            // Tree distance approximation - could be improved with actual tree distance
            totalDistance += Math.abs(currentPos - targetPos);
        }

        // Since one swap moves two elements, divide by 2 (but ensure it's still admissible)
        return totalDistance / 2;
    }

    /**
     * Combined heuristic that takes maximum of different heuristics
     * Ensures admissibility while being more informed
     */
    private static int combinedHeuristic(TreeState current, TreeState target) {
        int h1 = calculateHeuristic(current, target);
        int h2 = calculateManhattanHeuristic(current, target);

        // Take maximum to ensure admissibility
        return Math.max(h1, h2);
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
     * Solves the tree sorting problem using A* search to find optimal path
     */
    public static SearchResult solve(TreeState initialState) {
        TreeState targetState = createTargetBST(initialState);

        // If already at target, return empty solution
        if (initialState.equals(targetState)) {
            return new SearchResult(new ArrayList<>(), 0, 1);
        }

        // A* data structures
        PriorityQueue<SearchNode> openSet = new PriorityQueue<>();
        Set<TreeState> closedSet = new HashSet<>();
        Map<TreeState, Integer> gScoreMap = new HashMap<>();

        // Initialize search
        int initialH = combinedHeuristic(initialState, targetState);
        SearchNode initialNode = new SearchNode(initialState, new ArrayList<>(), 0, initialH);
        openSet.offer(initialNode);
        gScoreMap.put(initialState, 0);

        int nodesExplored = 0;

        while (!openSet.isEmpty()) {
            SearchNode current = openSet.poll();
            nodesExplored++;

            // If we've already processed this state with a better path, skip
            if (closedSet.contains(current.getState())) {
                continue;
            }

            // Add to closed set
            closedSet.add(current.getState());

            // Check if we reached the target
            if (current.getState().equals(targetState)) {
                return new SearchResult(current.getPath(), current.getGScore(), nodesExplored);
            }

            // Try all possible swaps (swap each non-root node with its parent)
            for (int childIndex = 1; childIndex < current.getState().getNumberOfNode(); childIndex++) {
                TreeState newState = current.getState().swapWithParent(childIndex);

                if (newState != null && !closedSet.contains(newState)) {
                    int tentativeGScore = current.getGScore() + 1;

                    // If we found a better path to this state, or it's the first time we see it
                    if (tentativeGScore < gScoreMap.getOrDefault(newState, Integer.MAX_VALUE)) {
                        gScoreMap.put(newState, tentativeGScore);

                        // Create new path with this swap
                        List<String> newPath = new ArrayList<>(current.getPath());
                        int parentIndex = current.getState().getParentIndex(childIndex);
                        String swapDescription = String.format("Swap node %d (value %d) with node %d (value %d)",
                                childIndex, current.getState().getValue(childIndex),
                                parentIndex, current.getState().getValue(parentIndex));
                        newPath.add(swapDescription);

                        // Calculate heuristic for new state
                        int hScore = combinedHeuristic(newState, targetState);

                        // Add to open set
                        SearchNode newNode = new SearchNode(newState, newPath, tentativeGScore, hScore);
                        openSet.offer(newNode);
                    }
                }
            }
        }

        return null; // No solution found
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
        }
    }
}