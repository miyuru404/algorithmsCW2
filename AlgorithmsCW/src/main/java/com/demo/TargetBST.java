package com.demo;

/*
    this class is responsible for creating the target BST before run the actual algorithm
 */

import java.util.ArrayList;
import java.util.List;

public class TargetBST {

    /*
        Creates the target BST state from the initial tree
        The target has the same structure but values arranged as a BST
    */
    public static TreeState createTargetBST(TreeState initialState) {

        int n = initialState.getNumberOfNode();
        int[] targetValues = new int[n]; // creating array size of initial array size to hold new status

        // Get sorted values (1 to n)
        List<Integer> sortedValues = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            sortedValues.add(i);
        }

        // Fill the tree in BST order using in-order traversal
        fillBSTInOrder(targetValues, sortedValues, 0, 0, n - 1);

        return new TreeState(targetValues);
    }

    /*
        Recursively fills the tree array to create a BST structure
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

}
