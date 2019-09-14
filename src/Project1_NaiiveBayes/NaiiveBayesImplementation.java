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

    static double[] totalAcc1 = new double[10];
    static double[] totalAcc2 = new double[10];

    static double[] totalF1 = new double[10];
    static double[] totalF2 = new double[10];


    public static void main(String[] args) {
        ReadWrite r = new ReadWrite();

        String[] files = {"discrete_breastCancer.data", "discrete_glass.data", "house-votes.data",
                            "discrete_iris.data", "discrete_soybean-small.data"}; //List the files that will be opened.

        for(String path : files){
            for(int i = 0; i < totalAcc1.length; i++){ //Resets the totals
                totalAcc1[i] = 0;
                totalAcc2[i] = 0;
                totalF1[i] = 0;
                totalF2[i] = 0;
            }

            String file = r.readEntireFile(path); //Reads the entire file as a String
            System.out.println(path + '\n'); // Prints the filepath to the console so we can tell what the output corresponds to


            DataPoint.resetTypes();
            doEverything(file);


            System.out.println("Control Accuracy: " + Arrays.toString(totalAcc1));
            System.out.println("Avg: " + avg(totalAcc1));
            System.out.println("Var: " + variance(totalAcc1));

            System.out.println("Control F-Scores" + Arrays.toString(totalF1));
            System.out.println("Avg: " + avg(totalF1));
            System.out.println();

            System.out.println("Scrambled Accuracy: " + Arrays.toString(totalAcc2));
            System.out.println("Avg: " + avg(totalAcc2));

            System.out.println("Scrambled F-Scores" + Arrays.toString(totalF2));
            System.out.println("Avg: " + avg(totalF2));
            System.out.println();

            System.out.println("T Value Accuracy: " + studentT(totalAcc1, totalAcc2));
            System.out.println("T Value F-Score: " + studentT(totalF1, totalF2));


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

        double[][] temp = trainTest(data);
        double[] accuracy = temp[0];//Gets an array of the accuracy of each fold
        double[] fScores = temp[1];

        for(int i = 0; i < accuracy.length; i++){ //Adds the accuracy and f-score to the totals
            totalAcc1[i] += accuracy[i];
            totalF1[i] += fScores[i];
        }


        DataPoint[] rawCopy = rawData.clone();//Attempts to clone the data (Doesn't work as pointers still point to old DataPoint object)
        int numAttributesToShuffle = (int) (0.1 * attributes) + 1; //Shuffles 10% of the data

        //System.out.println("Shuffling: " + numAttributesToShuffle);
        ArrayList<Integer> index = new ArrayList<>();//Holds the columns that have been shuffled so they don't get shuffled again
        for(int i = 0; i < numAttributesToShuffle; i++) {
            int ran = rand.nextInt(attributes);
            while (index.contains(ran) || ran < 0 || ran >= attributes){ // Makes sure the random number is good to shuffle
                ran = rand.nextInt();
            }
            index.add(ran);
            rawCopy = shuffle(rawCopy, ran);//Shuffles the attributes
        }


        DataPoint[][] dataScrambled = fold(rawCopy); //Folds the scrambled data

        temp = trainTest(dataScrambled);
        double[] accuracyScrambled = temp[0]; // The accuracy of the scrrambled data
        double[] fScoresScrambled = temp[1];

        for(int i = 0; i < accuracyScrambled.length; i++){ //adds the accuracy and f-score to totals
            totalAcc2[i] += accuracyScrambled[i];
            totalF2[i] += fScoresScrambled[i];
        }

    }

    /**
     * Runs the training and testing of each fold
     * @param data The data to be trained on
     * @return the accuracy of each fold in an array with length 10
     */
    private static double[][] trainTest(DataPoint[][] data){
        double[] accuracy = new double[10];
        double[] fScore = new double[10];

        int testCol = 0;//The column used for testing the data
        for(int fold = 0; fold < 10; fold++) { //Loops for k-folds
            sums = new int[types][attributes + 1];
            for (int col = 0; col < data.length; col++) { //Loops over training
                if (col != testCol) {//If it is part of the training set
                    trainCol(data[col]);
                }
            }
            accuracy[fold] = testAcc(data[testCol]);
            fScore[fold] = testF(data[testCol]);

            testCol++;
        }
        return new double[][]{accuracy, fScore};
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
     * An implementation of the Durstenfeld shuffle algorithm (AKA Fisher-Yates).
     * @param x Array to shuffle
     * @param att Which column to shuffle
     * @return the scrambled array (not necessary as the algorithm modifies the DataPoint[] object)
     */
    private static DataPoint[] shuffle(DataPoint[] x, int att){
        Random rand = new Random();
        for(int i = x.length - 1; i > 0; i--){
            int j = rand.nextInt(i + 1); //Swaps with a random element less than i
            boolean temp = x[i].self[att];
            x[i].self[att] = x[j].self[att];
            x[j].self[att] = temp;
        }
        return x; //not necessary
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

    }

    /**
     * Tests the data on accuracy
     * @param testData The data to be tested
     * @return The accuracy of the model
     */
    private static double testAcc(DataPoint[] testData){
        int row = 0;
        DataPoint dataPoint = testData[row++]; //initialize the datapoint

        //counters
        int totalTest = 0;
        int correctClassification = 0;

        while (dataPoint != null) { //we know the last datapoint will be null (but this is not necessarily close to testData.length)
            totalTest++;//increment total
            if(classify(dataPoint) == dataPoint.type) //if we get it right add to the total correct
                correctClassification++;

            dataPoint = testData[row++]; //increment datapoint
        }
        return (double) correctClassification/(double) totalTest;
    }

    /**
     * This calculates the F1 score of the testing data
     * @param testData the data to be tested
     * @return the F1 score
     */
    private static double testF(DataPoint[] testData){
        int row = 0;

        //Makes a convolution matrix so that we can calculate the rate of true positives, false positives, and false negatives
        //First index will be true classification, Second index will be what the model classified
        int[][] classifications = new int[types][types];

        DataPoint dataPoint = testData[row++];
        while (dataPoint != null){ //we know the last datapoint will be null (but this is not necessarily close to testData.length)

            classifications[dataPoint.type][classify(dataPoint)]++;//add the value to the matrix at the spot where we classified it.

            dataPoint = testData[row++];//increment datapoint
        }

        double precision = 0;
        double recall = 0;
        for (int perspec = 0; perspec < classifications.length; perspec++) {//iterates through the perspective
            int TP = 0;  //True positive
            int FP = 0;  //False positive
            int FN = 0;  //False negative
            for (int compare = 0; compare < classifications.length; compare++) {
                if(compare != perspec){
                    FP += classifications[compare][perspec]; // Checks side to side for false positives
                    FN += classifications[perspec][compare]; // Checks up and down for false negatives
                } else{
                    TP += classifications[perspec][compare]; // Checks for the correct classifications
                }
            }

            if(TP + FP != 0 && TP + FN != 0) { // Make sure we have values to avoid NaN
                precision += (double) TP / ((double) (TP + FP)); //Sums the precision
                recall += (double) TP / ((double) (TP + FN)); //Sums the recall
            }

        }
        precision /= types;
        recall /= types;

        return 2 * (precision * recall) / (precision + recall);
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
        int[] counters = new int[10];//so the elements go into the array in order (ie. all the null values are at the end)
        Random rand = new Random();

        for(int i = 0; i < 10; i++){ //ensures all folds have at least one DataPoint
            data[i][counters[i]++] = points[i];
        }

        for (int i = 10; i < points.length; i++) {
            int random = rand.nextInt(10);
            data[random][counters[random]++] = points[i]; //places the points into the folds in order so as to avoid null values
        }

        return data;
    }

    /**
     * Runs the studentT testAcc to testAcc difference between two arrays
     * @param x the first array
     * @param y the second array
     * @return the student T testAcc
     */
    public static double studentT(double[] x, double[] y){
        double xMean = avg(x);
        double xVar = variance(x);

        double yMean = avg(y);
        double yVar = variance(y);

        double numerator = xMean - yMean;


        double denominator = Math.sqrt(xVar/(x.length) + yVar/(y.length));

        return numerator/denominator;
    }

    /**
     * Calculates the variance of an array
     * @param x the array
     * @return the variance
     */
    public static double variance(double[] x){

        double mean = avg(x);
        double sum = 0;

        for(int i = 0; i < x.length; i++){
            sum += (x[i] - mean) * (x[i] - mean);
        }
        double scalar = 1/(double)(x.length -1);
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
