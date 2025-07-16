package com.demo;

import com.demo.util.InputParser;

import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static String filePath = "input/bounded_15_9.txt";
    public static String testFilePath = "input/test";

    public static void main(String[] args) {

        start(filePath);// start the application

    }

    public static void start (String filePath){

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
            TreeState targetState = TargetBST.createTargetBST(initialState);
            System.out.println(" * Target BST state: " + targetState);
            System.out.println();

            // Check if already sorted
            if (initialState.equals(targetState)) {
                System.out.println("* Tree is already a valid BST! No swaps needed.");
                return;
            }

            // Step 4: if not sorted Solve the problem
            System.out.println("Step 4: Finding Optimal Solution...\n");
            long startTime = System.currentTimeMillis();
            SearchResult result = TreeSolver.solve(initialState);
            long endTime = System.currentTimeMillis();

            // Step 5: Display results
            if (result != null) {
                System.out.println(" * Solution found!\n");
                System.out.println(result);
                System.out.println("Execution time: " + (endTime - startTime) + " ms\n");

                // Step 6: Verify solution
                System.out.println("Verification");
                if (verifySolution(initialState, targetState, result)) {
                    System.out.println("* Solution verified successfully!");
                } else {
                    System.out.println("* Solution verification failed!");
                }

                // Display performance metrics
                System.out.println("\n--- Performance  ---");
                System.out.printf("Tree size: %d nodes\n", treeData.length);
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
     * Verifies that the solution actually transforms initial state to target state
     */
    private static boolean verifySolution(TreeState initialState, TreeState targetState, SearchResult result) {

        TreeState currentState = initialState;
        System.out.println("Applying swap sequence step by step:");
        System.out.println("Step 0: " + currentState);

        // Apply each swap in the solution
        for (int i = 0; i < result.getSwapSequence().size(); i++) {
            String swapDescription = result.getSwapSequence().get(i);

            // Extract node index from the swap description
            int nodeIndex = extractNodeIndex(swapDescription);

            if (nodeIndex != -1) {
                TreeState newState = currentState.swap(nodeIndex);
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
