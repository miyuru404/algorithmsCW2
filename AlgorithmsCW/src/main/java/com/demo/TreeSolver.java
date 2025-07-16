package com.demo;

import java.util.*;

import static com.demo.TargetBST.createTargetBST;

public class TreeSolver {

    // Heuristic: count how many nodes are out of place
    private static int calculateHeuristic(TreeState current, TreeState target) {
        int misplaced = 0;
        for (int i = 0; i < current.getNumberOfNode(); i++) {
            if (current.getValue(i) != target.getValue(i)) misplaced++;
        }
        return misplaced;
    }

    // Heuristic: sum of index differences for each value (tree-aware approximation)
    private static int calculateManhattanHeuristic(TreeState current, TreeState target) {
        int total = 0, n = current.getNumberOfNode();
        Map<Integer, Integer> currentPos = new HashMap<>();
        Map<Integer, Integer> targetPos = new HashMap<>();

        for (int i = 0; i < n; i++) {
            currentPos.put(current.getValue(i), i);
            targetPos.put(target.getValue(i), i);
        }

        for (int val = 1; val <= n; val++) {
            total += Math.abs(currentPos.get(val) - targetPos.get(val));
        }

        return total / 2; // Approximation: each swap moves 2 elements
    }

    // Combined heuristic: max of both to stay admissible
    private static int combinedHeuristic(TreeState current, TreeState target) {
        return Math.max(
                calculateHeuristic(current, target),
                calculateManhattanHeuristic(current, target)
        );
    }

    // Solve using A* search
    public static SearchResult solve(TreeState initialState) {
        TreeState targetState = createTargetBST(initialState);

        if (initialState.equals(targetState)) {
            return new SearchResult(new ArrayList<>(), 0, 1);
        }

        PriorityQueue<SearchNode> openSet = new PriorityQueue<>();
        Set<TreeState> closedSet = new HashSet<>();
        Map<TreeState, Integer> gScoreMap = new HashMap<>();

        int initialH = combinedHeuristic(initialState, targetState);
        openSet.offer(new SearchNode(initialState, new ArrayList<>(), 0, initialH));
        gScoreMap.put(initialState, 0);

        int nodesExplored = 0;

        while (!openSet.isEmpty()) {
            SearchNode current = openSet.poll();
            nodesExplored++;

            if (closedSet.contains(current.state)) continue;
            closedSet.add(current.state);

            if (current.state.equals(targetState)) {
                return new SearchResult(current.path, current.gScore, nodesExplored);
            }

            int n = current.state.getNumberOfNode();
            for (int child = 1; child < n; child++) {
                TreeState newState = current.state.swap(child);

                if (newState == null || closedSet.contains(newState)) continue;

                int tentativeG = current.gScore + 1;
                if (tentativeG < gScoreMap.getOrDefault(newState, Integer.MAX_VALUE)) {
                    gScoreMap.put(newState, tentativeG);

                    List<String> newPath = new ArrayList<>(current.path);
                    int parent = current.state.getParentIndex(child);
                    newPath.add(String.format(
                            "Swap node %d (value %d) with node %d (value %d)",
                            child, current.state.getValue(child),
                            parent, current.state.getValue(parent))
                    );

                    int hScore = combinedHeuristic(newState, targetState);
                    openSet.offer(new SearchNode(newState, newPath, tentativeG, hScore));
                }
            }
        }

        return null; // No solution
    }

    // Represents a node in the A* search
    private static class SearchNode implements Comparable<SearchNode> {
        TreeState state;
        List<String> path;
        int gScore; // steps taken so far
        int hScore; // estimated steps to target
        int fScore; // total estimated cost

        public SearchNode(TreeState state, List<String> path, int gScore, int hScore) {
            this.state = state;
            this.path = path;
            this.gScore = gScore;
            this.hScore = hScore;
            this.fScore = gScore + hScore;
        }

        @Override
        public int compareTo(SearchNode other) {
            if (this.fScore != other.fScore) return Integer.compare(this.fScore, other.fScore);
            if (this.hScore != other.hScore) return Integer.compare(this.hScore, other.hScore);
            return Integer.compare(other.gScore, this.gScore); // prefer deeper if tied
        }
    }
}
