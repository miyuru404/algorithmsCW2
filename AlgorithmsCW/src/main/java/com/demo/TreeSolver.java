package com.demo;

import java.util.*;

public class TreeSolver {

    // ✅ Compact record to store swap steps
    private record Swap(int childIndex, int parentIndex) {}

    // ✅ Node used in BFS
    private static class SearchNode {
        private TreeState state;
        private SearchNode parent;
        private Swap move;
        private int depth;

        public SearchNode(TreeState state, SearchNode parent, Swap move, int depth) {
            this.state = state;
            this.parent = parent;
            this.move = move;
            this.depth = depth;
        }

        public TreeState getState() { return state; }
        public SearchNode getParent() { return parent; }
        public Swap getMove() { return move; }
        public int getDepth() { return depth; }
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

        Queue<SearchNode> queue = new LinkedList<>();
        Set<TreeState> visited = new HashSet<>();

        queue.offer(new SearchNode(initialState, null, null, 0));
        visited.add(initialState);

        int nodesExplored = 0;

        while (!queue.isEmpty()) {
            SearchNode current = queue.poll();
            nodesExplored++;

            for (int childIndex = 1; childIndex < current.getState().getNumberOfNode(); childIndex++) {
                TreeState newState = current.getState().swapWithParent(childIndex);

                if (newState != null && !visited.contains(newState)) {
                    visited.add(newState);

                    int parentIndex = current.getState().getParentIndex(childIndex);
                    Swap move = new Swap(childIndex, parentIndex);
                    SearchNode newNode = new SearchNode(newState, current, move, current.getDepth() + 1);

                    if (newState.equals(targetState)) {
                        List<Swap> swapPath = reconstructPath(newNode);
                        List<String> readablePath = convertToReadableSwapList(swapPath);
                        return new SearchResult(readablePath, newNode.getDepth(), nodesExplored);
                    }

                    queue.offer(newNode);
                }
            }
        }

        return null;
    }

    // ✅ Reconstruct path from goal node to root
    private static List<Swap> reconstructPath(SearchNode node) {
        List<Swap> path = new ArrayList<>();
        while (node.getParent() != null) {
            path.add(node.getMove());
            node = node.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    // ✅ Convert Swap objects to readable strings
    private static List<String> convertToReadableSwapList(List<Swap> swaps) {
        List<String> readable = new ArrayList<>();
        for (Swap swap : swaps) {
            readable.add("Swap node " + swap.childIndex() + " with node " + swap.parentIndex());
        }
        return readable;
    }

    /**
     * Builds the target BST layout with sorted values (structure preserved)
     */
    public static TreeState createTargetBST(TreeState initialState) {
        int n = initialState.getNumberOfNode();
        int[] targetValues = new int[n];
        List<Integer> sortedValues = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            sortedValues.add(i);
        }

        fillBSTInOrder(targetValues, sortedValues, 0, 0, n - 1);
        return new TreeState(targetValues);
    }

    // ✅ Helper to fill BST using in-order traversal
    private static void fillBSTInOrder(int[] tree, List<Integer> sortedValues,
                                       int nodeIndex, int start, int end) {
        if (start > end || nodeIndex >= tree.length) return;

        int mid = start + (end - start) / 2;
        tree[nodeIndex] = sortedValues.get(mid);

        int left = 2 * nodeIndex + 1;
        int right = 2 * nodeIndex + 2;

        fillBSTInOrder(tree, sortedValues, left, start, mid - 1);
        fillBSTInOrder(tree, sortedValues, right, mid + 1, end);
    }

    // ✅ Result class to return info to Main
    public static class SearchResult {
        private final List<String> swapSequence;
        private final int numberOfSwaps;
        private final int nodesExplored;

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
