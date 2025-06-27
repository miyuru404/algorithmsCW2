package com.demo;

//import static com.demo.util.InputParser.parser;

import com.demo.util.InputParser;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        String filePath = "input/test";

        System.out.println("--- Testing InputParser ---");

        try {

            int[] treeData = InputParser.parser(filePath);

            System.out.println("Successfully parsed tree data: " + Arrays.toString(treeData));
            System.out.println("Validation successful.");

        } catch (IOException e) {
            System.err.println("File I/O Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Input Data Validation Error: " + e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected errors
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("--- Test Finished ---");
    }
}