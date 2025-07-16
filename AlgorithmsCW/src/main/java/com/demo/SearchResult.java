package com.demo;

import java.util.List;

/*
Result class to hold solution information
*/

public class SearchResult {

    private List<String> swapSequence;
    private int numberOfSwaps;
    private int nodesExplored;

    public SearchResult(List<String> swapSequence, int numberOfSwaps, int nodesExplored) {
        this.swapSequence = swapSequence;
        this.numberOfSwaps = numberOfSwaps;
        this.nodesExplored = nodesExplored;
    }

    public List<String> getSwapSequence() {
        return swapSequence;
    }

    public int getNumberOfSwaps() {
        return numberOfSwaps;
    }

    public int getNodesExplored() {
        return nodesExplored;
    }

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
