package Project1_NaiiveBayes;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by pryor on 9/7/2019.
 */
public class DataPoint {
    static ArrayList<String> types = new ArrayList<>(); // List of all the different classes

    boolean[] self; // The features of the DataPoint
    int index; // the identifier of the point
    int type; // The class of the point

    /**
     * Builds the DataPoint from a string input
     * @param data The line of data containing the attributes
     */
    public DataPoint(String data){

        String[] parse = data.split(",");
        self = new boolean[parse.length-2]; // cuts off the class and the index

        for(int i = 1; i < parse.length - 1; i++){
            self[i-1] = Double.parseDouble(parse[i]) == 1;//True if it is a 1, false if it isn't
        }

        //index = (int) Double.parseDouble(parse[0]);
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
        return index + " : " + Arrays.toString(self);
    }

    /**
     * Tests whether two DataPoints are equivalent
     * @param b the DataPoint to compare to
     * @return true if they have the same features and class, false otherwise
     */
    public boolean equals(DataPoint b){
        if(b.type != type)
            return false;

        for(int i = 0; i < self.length; i++){
            if(b.self[i] != self[i]){
                return false;
            }
        }
        return true;
    }

    /**
     * Resets the types array, so it doesn't grow from training old models.
     */
    public static void resetTypes(){
        types = new ArrayList<>();
    }

}
