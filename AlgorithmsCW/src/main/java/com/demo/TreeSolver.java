package com.demo;

import java.util.*;

import static com.demo.TargetBST.createTargetBST;

public class TreeSolver {

    // Heuristic #1: Count how many nodes are not in their target positions.
    // This gives a rough estimate of how far we are from the solution.
    private static int calculateHeuristic(TreeState current, TreeState target) {
        int misplaced = 0;
        for (int i = 0; i < current.getNumberOfNode(); i++) {
            if (current.getValue(i) != target.getValue(i)) misplaced++;
        }
        return misplaced;
    }

    // Heuristic #2: A more refined estimate using index distance.
    // Measures how far each value is from its goal position, on average.
    private static int calculateManhattanHeuristic(TreeState current, TreeState target) {
        int total = 0, n = current.getNumberOfNode();
        Map<Integer, Integer> currentPos = new HashMap<>();
        Map<Integer, Integer> targetPos = new HashMap<>();

        // Store positions of each value in both current and target trees
        for (int i = 0; i < n; i++) {
            currentPos.put(current.getValue(i), i);
            targetPos.put(target.getValue(i), i);
        }

        // Add up the absolute position differences
        for (int val = 1; val <= n; val++) {
            total += Math.abs(currentPos.get(val) - targetPos.get(val));
        }

        return total / 2; // Approximate number of swaps (since each swap affects two values)
    }

    // Combined heuristic: Use the more pessimistic (higher) value to stay admissible for A*
    private static int combinedHeuristic(TreeState current, TreeState target) {
        return Math.max(
                calculateHeuristic(current, target),
                calculateManhattanHeuristic(current, target)
        );
    }

    // Main method to solve the tree using the A* search algorithm
    public static SearchResult solve(TreeState initialState) {
        TreeState targetState = createTargetBST(initialState); // Build the ideal target BST

        // If the current state is already sorted, return
        if (initialState.equals(targetState)) {
            return new SearchResult(new ArrayList<>(), 0, 1);
        }

        // Open set stores nodes to explore
        PriorityQueue<SearchNode> openSet = new PriorityQueue<>();
        Set<TreeState> closedSet = new HashSet<>(); // To avoid re-exploring visited states
        Map<TreeState, Integer> gScoreMap = new HashMap<>(); // Cost from start to a given state

        // Start from the initial state
        int initialH = combinedHeuristic(initialState, targetState);
        openSet.offer(new SearchNode(initialState, new ArrayList<>(), 0, initialH));
        gScoreMap.put(initialState, 0);

        int nodesExplored = 0;


        while (!openSet.isEmpty()) {
            SearchNode current = openSet.poll();
            nodesExplored++;

            // If we've already explored this state, skip it
            if (closedSet.contains(current.state)) continue;
            closedSet.add(current.state);

            // if we’ve reached the target tree configuration
            if (current.state.equals(targetState)) {
                return new SearchResult(current.path, current.gScore, nodesExplored);
            }

            int n = current.state.getNumberOfNode();

            // Try swapping each child with its parent
            for (int child = 1; child < n; child++) {
                TreeState newState = current.state.swap(child);

                // Skip invalid or already explored states
                if (newState == null || closedSet.contains(newState)) continue;

                int tentativeG = current.gScore + 1; // One swap done

                // If this path is better (shorter), update the scores
                if (tentativeG < gScoreMap.getOrDefault(newState, Integer.MAX_VALUE)) {
                    gScoreMap.put(newState, tentativeG);

                    // Record the swap that was made
                    List<String> newPath = new ArrayList<>(current.path);
                    int parent = current.state.getParentIndex(child);
                    newPath.add(String.format(
                            "Swap node %d (value %d) with node %d (value %d)",
                            child, current.state.getValue(child),
                            parent, current.state.getValue(parent))
                    );

                    // Calculate estimated cost to goal
                    int hScore = combinedHeuristic(newState, targetState);
                    openSet.offer(new SearchNode(newState, newPath, tentativeG, hScore));
                }
            }
        }
        return null;
    }

    // Internal helper class to represent a node in the search tree for A* algorithm
    private static class SearchNode implements Comparable<SearchNode> {

        TreeState state;
        List<String> path; // The swap steps taken to reach this state
        int gScore; // Actual cost from start to this node
        int hScore; // Estimated cost from this node to goal
        int fScore; // Total estimated cost (g + h)

        public SearchNode(TreeState state, List<String> path, int gScore, int hScore) {
            this.state = state;
            this.path = path;
            this.gScore = gScore;
            this.hScore = hScore;
            this.fScore = gScore + hScore;
        }

        // Nodes with lower fScore get higher priority in the queue
        @Override
        public int compareTo(SearchNode other) {
            if (this.fScore != other.fScore) return Integer.compare(this.fScore, other.fScore);
            if (this.hScore != other.hScore) return Integer.compare(this.hScore, other.hScore);
            return Integer.compare(other.gScore, this.gScore); // Prefer deeper paths if tie
        }
    }
}
