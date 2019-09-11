package Project1_NaiiveBayes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by pryor on 9/7/2019.
 */
public class NaiiveBayesImplementation {

    static int[][] sums;
    static int attributes;
    static int types;
    static int total;

    public static void main(String[] args) {
        ReadWrite r = new ReadWrite();
        String[] files = {"discrete_breastCancer.data", "discrete_glass.data", "house-votes.data",
                            "discrete_iris.data", "discrete_soybean-small.data"}; //List the files that will be opened.
        for(String path : files){
            String file = r.readEntireFile(path); //Reads the entire file as a String
            System.out.println(path); // Prints the filepath to the console so we can tell what the output corresponds to

            doEverything(file);
            DataPoint.resetTypes();
            System.out.println("*******************************************");

        }

    }

    /**
     * This function is the driver for all of the training and testing for each dataset
     * @param file The file to be analyzed
     */
    private static void doEverything(String file){
        Random rand = new Random();
        String[] lines = file.split("\n"); //Reads each line of the file

        DataPoint[] rawData = new DataPoint[lines.length];//The data
        DataPoint[] pureData = new DataPoint[lines.length]; //Store a pure copy that is never modified

        //Parses the data
        for(int i = 0; i < lines.length; i++){
            rawData[i] = new DataPoint(lines[i]);
            pureData[i] = new DataPoint(lines[i]);
        }

        total = lines.length; //total number of datapoints

        attributes = rawData[0].self.length;//Number of different attributes
        types = DataPoint.types.size();//Number of different classes

        DataPoint[][] data = fold(rawData);//Folds the data randomly into 10 folds

        double[] accuracy = trainTest(data);//Gets an array of the accuracy of each fold
        System.out.println("Original Accuracy: " + Arrays.toString(accuracy)); //Un-scrambled accuracy
        System.out.println("Avg: " + avg(accuracy));

        DataPoint[] rawCopy = rawData.clone();//Attempts to clone the data (Doesn't work as pointers still point to old DataPoint object)
        int numAttributesToShuffle = (int) (0.1 * attributes) + 1; //Shuffles 10% of the data

        System.out.println("Shuffling: " + numAttributesToShuffle);
        ArrayList<Integer> index = new ArrayList<>();//Holds the columns that have been shuffled so they don't get shuffled again
        for(int i = 0; i < numAttributesToShuffle; i++) {
            int ran = rand.nextInt(attributes);
            while (index.contains(ran) || ran < 0 || ran > attributes){ // Makes sure the random number is good to shuffle
                ran = rand.nextInt();
            }
            index.add(ran);
            //System.out.println(ran);
            rawCopy = fisherYatesShuffle(rawCopy, ran);//Shuffles the attributes
        }


        DataPoint[][] dataCopy = fold(rawCopy); //Folds the scrambled data

        double[] accuracyCopy = trainTest(dataCopy); // The accuracy of the scrrambled data

        System.out.println("Scrambled Accuracy: " + Arrays.toString(accuracyCopy));
        System.out.println("Avg: " + avg(accuracyCopy));

        System.out.println("T Value: " + studentT(accuracy, accuracyCopy));
    }

    /**
     * Runs the training and testing of each fold
     * @param data The data to be trained on
     * @return the accuracy of each fold in an array with length 10
     */
    private static double[] trainTest(DataPoint[][] data){
        double[] accuracy = new double[10];

        int testCol = 0;//The column used for testing the data
        for(int fold = 0; fold < 10; fold++) { //Loops for k-folds
            sums = new int[types][attributes + 1];
            for (int col = 0; col < data.length; col++) { //Loops over training
                if (col != testCol) {//If it is part of the training set
                    trainCol(data[col]);
                }
            }
            accuracy[fold] = test(data[testCol]);
            testCol++;
        }
        return accuracy;
    }

    /**
     * Tests if two lists of data are different.
     * Was used to verify the scramble method
     * @param x first list of data
     * @param y second list of data
     * @return true if the lists are different
     */
    private static boolean areDiff(DataPoint[] x, DataPoint[] y){
        for(int i = 0; i < x.length; i++){
            if(!x[i].equals(y[i]))
                return true;
        }
        return false;
    }

    /**
     * An implementation of the Fisher-Yates shuffle algorithm.
     * @param x Array to shuffle
     * @param att Which column to shuffle
     * @return the scrambled array (not necessary as the algorithm modifies the DataPoint[] object)
     */
    private static DataPoint[] fisherYatesShuffle(DataPoint[] x, int att){
        Random rand = new Random();
        for(int i = x.length - 1; i > 0; i--){
            int j = rand.nextInt(i + 1); //Swaps with a random element less than i
            boolean temp = x[i].self[att];
            x[i].self[att] = x[j].self[att];
            x[j].self[att] = temp;
        }
        return x;
    }

    /**
     * Trains a specific column of data by calculating its sums
     * @param trainData The data to be trained on
     */
    private static void trainCol(DataPoint[] trainData){
        int row = 0;
        DataPoint dataPoint = trainData[row++];//Start at the 0th row
        while (dataPoint != null) {//Because there are null values at the bottom of the fold. (fold width too big, and not perfectly even)
            for (int k = 0; k < attributes; k++) {//Add up all of the true attributes
                if (dataPoint.self[k]) {
                    sums[dataPoint.type][k]++;//increment total for specific attribute
                }
            }
            sums[dataPoint.type][attributes] ++;//increment total
            dataPoint = trainData[row++];//increment datapoint
        }
        if(row < 2){//Due to not perfectly even folding (randomized), small datasets are possible to have empty folds
            System.out.println("OOPS, one fold is empty");
        }
    }

    /**
     * Tests the data on accuracy
     * @param testData The data to be tested
     * @return The accuracy of the model
     */
    private static double test(DataPoint[] testData){
        int row = 0;
        DataPoint dataPoint = testData[row++];
        int totalTest = 0;
        int correctClassification = 0;
        while (dataPoint != null) {
            totalTest += 1;
            if(classify(dataPoint) == dataPoint.type)
                correctClassification++;

            dataPoint = testData[row++];
        }
        return (double) correctClassification/(double) totalTest;
    }

    /**
     * Uses the trained model to classify an unknown point
     * By: Thomas Herndon
     * @param x The datapoint to classify
     * @return the predicted class
     */
    private static int classify(DataPoint x){
        double max=0;
        int maxarg=0;
        for(int c=0; c<types; c++){
            if (C(x,c) > max) { // If it has a higher probability of being the new class
                max = C(x, c);
                maxarg=c;
            }
        }
        return maxarg;
    }

    /**
     * Returns the probability that the datapoint is of a given class
     * By: Thomas Herndon
     * @param x The datapoint to be tested
     * @param c The class to which the datapoint might belong
     * @return The probability X is part of C
     */
    private static double C(DataPoint x, int c){
        double p=1;
        for(int a=0;a<attributes;a++) {
            if (x.self[a]) // If the attribute is true
                p *= (double) (sums[c][a] + 1) / (double) (sums[c][attributes] + types);
            else // If the attribute is false, use the 1 minus the fraction
                p *= 1 - (double) (sums[c][a] + 1) / (double) (sums[c][attributes] + types);
        }
        return (double)(sums[c][attributes])/(double)(total)*p;
    }

    /**
     * Folds the data into 10 fairly equal folds
     * @param points The data to be folded
     * @return a folded list
     */
    public static DataPoint[][] fold(DataPoint[] points){
        DataPoint[][] data = new DataPoint[10][points.length];
        int[] counters = new int[10];
        Random rand = new Random();

        for (int i = 0; i < points.length; i++) {
            int random = rand.nextInt(10);
            data[random][counters[random]++] = points[i];
        }

        return data;
    }

    /**
     * Runs the studentT test to test difference between two arrays
     * @param x the first array
     * @param y the second array
     * @return the student T test
     */
    public static double studentT(double[] x, double[] y){
        double xMean = avg(x);
        double xVar = variance(x);

        double yMean = avg(y);
        double yVar = variance(y);

        double numerator = xMean - yMean;
        double denominator = Math.sqrt(xVar/x.length + yVar/y.length);

        return numerator/denominator;


    }

    /**
     * Calculates the variance of an array
     * @param x the array
     * @return the variance
     */
    public static double variance(double[] x){
        double scalar = 1/(double)(x.length -1);
        double mean = avg(x);
        double sum = 0;
        for(int i = 0; i < x.length; i++){
            sum += (x[i] - mean)*(x[i] - mean);
        }
        return scalar * sum;
    }

    /**
     * Calculates the average of an array
     * @param x the array
     * @return the average
     */
    public static double avg(double[] x){
        double sum = 0;
        double total = 0;
        for(double y : x){
            sum += y;
            total++;
        }
        return sum/total;
    }

}
