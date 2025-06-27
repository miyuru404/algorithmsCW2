package com.demo.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InputParser {

    /*
        This is the method "parser" that responsible for reading values from the file.
        This will read all the values in the file and validate the input using the helper function call "validateInput()".
        This method will use path of the input file as a string And return the original tree data if validated.
    */

    public static int[] parser(String filePath) throws IOException,IllegalArgumentException{

        // ArrayList that hold all the values from  file
        List<Integer> collectedNumbers = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){

            String line;

            while((line = reader.readLine()) != null){

                String[] tokens = line.trim().split("\\s+");//split by one or more whitespace characters

                for (String token : tokens) {
                    try {

                        collectedNumbers.add(Integer.parseInt(token));//convert string values to int and add to the array

                    } catch (NumberFormatException e) {
                        // If a token isn't an integer, throw an error
                        throw new IllegalArgumentException("File contains non-integer value: '" + token + "'", e);
                    }
                }
            }

        }
        catch(IOException e){
            throw new IOException("\"Error reading file: \" + filePath, e");
        }

        // Perform all validation
        validateInput(collectedNumbers);

        return collectedNumbers.stream().mapToInt(Integer::intValue).toArray();
    }







    /*
        helper function that validate the data
    */
    private static void validateInput(List<Integer> collectedNumbers) throws IllegalArgumentException{

        int n = collectedNumbers.size(); //original size of the list
        Set<Integer> numberSet = new HashSet<>(collectedNumbers);
        List<Integer> sortedValues = new ArrayList<>(collectedNumbers); // Create a copy to sort
        Collections.sort(sortedValues);

        // check for empty input file
        if(collectedNumbers.isEmpty()){
            throw new IllegalArgumentException("input file is empty");
        }

        // check for duplicate value
        if(numberSet.size() != n){
            throw new IllegalArgumentException("duplicate values found");
        }

        // check for correct order of values
        for(int i = 0 ; i < n ; i++){
            if(sortedValues.get(i) != (i+1)){
                throw new IllegalArgumentException("Numbers are not a complete sequence from 1 to " + n + "  Found ");
            }
        }

        System.out.println(" Validation successful ");

    }

}
