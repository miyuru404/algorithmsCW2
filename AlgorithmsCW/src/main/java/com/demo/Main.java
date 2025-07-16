package com.demo;
import com.demo.util.InputParser;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        // Use command line argument if provided, otherwise use default
        String filePath =  "input/bounded_15_2.txt";

        System.out.println("\n=== TREE SORTING ALGORITHM ===");
        System.out.println("Input file: " + filePath);
        System.out.println();

        try {
            // Step 1: Parse input file
            System.out.println("Step 01: Parsing Input...\n ");
            int[] treeData = InputParser.parser(filePath);
            System.out.println(" * parsed tree data: " + Arrays.toString(treeData));
            System.out.println(" * Number of nodes: " + treeData.length);
            System.out.println();

            // Step 2: Create initial tree state
            System.out.println("Step 2: Creating Tree State...\n");
            TreeState initialState = new TreeState(treeData);
            System.out.println(" * Initial tree state: " + initialState);
            System.out.println();


            // Step 3: Generate target BST
            System.out.println("Step 3: Generating Target BST...\n");
            TreeState targetState = TreeSolver.createTargetBST(initialState);
            System.out.println(" * Target BST state: " + targetState);
            System.out.println();

            // Check if already sorted
            if (initialState.equals(targetState)) {
                System.out.println("* Tree is already a valid BST! No swaps needed.");
                return;
            }

            // Step 4: Solve the problem
            System.out.println("Step 4: Finding Optimal Solution...\n");
            //System.out.println("Searching for minimum swap sequence...");

            long startTime = System.currentTimeMillis();
            TreeSolver.SearchResult result = TreeSolver.solve(initialState);
            long endTime = System.currentTimeMillis();

            // Step 5: Display results
            //System.out.println("--- Step 5: Results ---");
            if (result != null) {
                System.out.println(" * Solution found!\n");
                System.out.println("Minimum swaps required: " + result.getNumberOfSwaps());
                System.out.println("Nodes explored: " + result.getNodesExplored());
                System.out.println("Execution time: " + (endTime - startTime) + " ms");
                System.out.println();

                // Display swap sequence
                System.out.println("--- Swap Sequence ---");
                if (result.getSwapSequence().isEmpty()) {
                    System.out.println("No swaps needed - tree is already sorted!");
                } else {
                    for (int i = 0; i < result.getSwapSequence().size(); i++) {
                        System.out.println((i + 1) + ". " + result.getSwapSequence().get(i));
                    }
                }
                System.out.println();

                // Step 6: Verify solution
                System.out.println("Verification");
                if (verifySolution(initialState, targetState, result)) {
                    System.out.println("* Solution verified successfully!");
                } else {
                    System.out.println("* Solution verification failed!");
                }

                // Display performance metrics
                System.out.println();
                System.out.println("--- Performance Metrics ---");
                System.out.printf("Tree size: %d nodes\n", treeData.length);
                System.out.printf("Optimal solution length: %d swaps\n", result.getNumberOfSwaps());
                System.out.printf("Search space explored: %d states\n", result.getNodesExplored());
                System.out.printf("Time complexity: O(N! × N) where N = %d\n", treeData.length);
                System.out.printf("Execution time: %d ms\n", (endTime - startTime));

            } else {
                System.out.println("✗ No solution found!");
                System.out.println("This should not happen for valid inputs.");
            }

        } catch (IOException e) {
            System.err.println("File I/O Error: " + e.getMessage());
            System.err.println("Please check that the input file exists and is readable.");
        } catch (IllegalArgumentException e) {
            System.err.println("Input Data Validation Error: " + e.getMessage());
            System.err.println("Please ensure the input file contains valid tree data.");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== PROGRAM FINISHED ===");
    }



    /**
     * Helper method to print specified number of spaces
     */
    private static void printSpaces(int count) {
        for (int i = 0; i < count; i++) {
            System.out.print(" ");
        }
    }

    /**
     * Verifies that the solution actually transforms initial state to target state
     */
    private static boolean verifySolution(TreeState initialState, TreeState targetState,
                                          TreeSolver.SearchResult result) {
        TreeState currentState = initialState;

        System.out.println("Applying swap sequence step by step:");
        System.out.println("Step 0: " + currentState);

        // Apply each swap in the solution
        for (int i = 0; i < result.getSwapSequence().size(); i++) {
            String swapDescription = result.getSwapSequence().get(i);

            // Extract node index from the swap description
            int nodeIndex = extractNodeIndex(swapDescription);

            if (nodeIndex != -1) {
                TreeState newState = currentState.swapWithParent(nodeIndex);
                if (newState != null) {
                    currentState = newState;
                    System.out.println("Step " + (i + 1) + ": " + currentState + " ← " + swapDescription);
                } else {
                    System.out.println("✗ Invalid swap at step " + (i + 1));
                    return false;
                }
            } else {
                System.out.println("✗ Could not parse swap description: " + swapDescription);
                return false;
            }
        }

        boolean isValid = currentState.equals(targetState);
        System.out.println("Final state matches target: " + isValid);
        return isValid;
    }

    /**
     * Extracts the first node index from a swap description
     */
    private static int extractNodeIndex(String swapDescription) {
        try {
            // Look for "Swap node X" pattern
            int startIndex = swapDescription.indexOf("Swap node ") + "Swap node ".length();
            int endIndex = swapDescription.indexOf(" ", startIndex);
            if (endIndex == -1) endIndex = swapDescription.length();

            String nodeIndexStr = swapDescription.substring(startIndex, endIndex);
            return Integer.parseInt(nodeIndexStr);
        } catch (Exception e) {
            return -1;
        }
    }
}