package com.demo;

import java.util.Arrays;

public class TreeState {

    private final int[] treeValues;// store value in an array
    private final int computedHashCode; // pre-calculated hashcode because initial array is immutable

    public TreeState(int[] values) {

        treeValues = Arrays.copyOf(values, values.length);// creating for new copy from initial arrays
        this.computedHashCode = Arrays.hashCode(treeValues); // hash is calculated only one time and return it whenever call it

    }


    public int getNumberOfNode() {
        return treeValues.length;
    }

    public int getValue(int index) {
        if (index < 0 || index >= treeValues.length) {
            throw new IndexOutOfBoundsException("Node index " + index + " is out of bounds for tree size " + treeValues.length);
        }
        return treeValues[index];
    }

    public int getParentIndex(int childIndex) {
        if (childIndex <= 0 || childIndex >= treeValues.length) {
            // Root (index 0) has no parent
            return -1;
        }
        return (childIndex - 1) / 2; // Integer division for parent index
    }

    public int getLeftChildIndex(int parentIndex) {
        int leftChild = 2 * parentIndex + 1;
        return (leftChild < treeValues.length) ? leftChild : -1; // -1 if no left child (out of bounds)
    }

    public int getRightChildIndex(int parentIndex) {
        int rightChild = 2 * parentIndex + 2;
        return (rightChild < treeValues.length) ? rightChild : -1; // -1 if no right child (out of bounds)
    }

     /*
     most important method in this class
     this method is hold the logic of how swapping two value must happen
     */

    public TreeState swap(int childIndex) {

        int parentIndex = getParentIndex(childIndex);

        //  if childIndex is 0 (root), it has no parent to swap with.
        if (parentIndex == -1) {
            return null;
        }

        // Create a new array for the new state
        int[] newTreeValues = Arrays.copyOf(this.treeValues, this.treeValues.length);

        //  swap on the new array
        int temp = newTreeValues[childIndex];
        newTreeValues[childIndex] = newTreeValues[parentIndex];
        newTreeValues[parentIndex] = temp;

        // Return a new TreeState object
        return new TreeState(newTreeValues);
    }

    @Override
    public boolean equals(Object o) {
        //  check If it's the exact same object in memory
        if (this == o) return true;

        //  check the other object is null or not of the same class
        if (o == null || getClass() != o.getClass()) return false;

        //  cast the object to TreeState.
        TreeState other = (TreeState) o;


        //    Arrays.equals() comparing array contents element by element.
        return Arrays.equals(this.treeValues, other.treeValues);
    }


    /* hash code */

    //@Override
    //public int hashCode() {
    //  numberOfHashCodeCalled++;
    // this ensures  Arrays.equals().
    // return Arrays.hashCode(treeValues);
    //}


    @Override
    public int hashCode() {
        return computedHashCode; // return the saved value
    }


    @Override
    public String toString() {
        return Arrays.toString(treeValues);
    }


}
