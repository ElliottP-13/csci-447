package Project1_NaiiveBayes;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by pryor on 9/7/2019.
 */
public class DataPoint {
    static ArrayList<String> types = new ArrayList<>(); // List of all the different classes
    static int distinctAttributeVals = -1;

    int[] attributes; // The features of the DataPoint
    int index; // the identifier of the point
    int type; // The class of the point

    /**
     * Builds the DataPoint from a string input
     * @param data The line of data containing the attributes
     */
    public DataPoint(String data){

        String[] parse = data.split(",");
        attributes = new int[parse.length-2]; // cuts off the class and the index

        for(int i = 1; i < parse.length - 1; i++){
            attributes[i-1] = (int) Double.parseDouble(parse[i]);//stores the attribute value
            if(attributes[i-1] + 1 > distinctAttributeVals) //Assume we have attribute values being 0-n without gaps
                distinctAttributeVals = attributes[i-1] + 1;
        }

        index = 1; // index is never used so no purpose in maintaining it
        if (!types.contains(parse[parse.length-1])) // If it is a unique type add it to the list
            types.add(parse[parse.length-1]);

        type = types.indexOf(parse[parse.length - 1]); //makes the type a number


    }

    /**
     *
     * @return String representation of the DataPoint
     */
    public String toString(){
        return index + " : " + Arrays.toString(attributes);
    }

    /**
     * Tests whether two DataPoints are equivalent
     * @param b the DataPoint to compare to
     * @return true if they have the same features and class, false otherwise
     */
    public boolean equals(DataPoint b){
        if(b.type != type)
            return false;

        for(int i = 0; i < attributes.length; i++){
            if(b.attributes[i] != attributes[i]){
                return false;
            }
        }
        return true;
    }

    /**
     * Resets the types array, so it doesn't grow from training old models.
     */
    public static void reset(){
        types = new ArrayList<>();
        distinctAttributeVals = -1;
    }

}
