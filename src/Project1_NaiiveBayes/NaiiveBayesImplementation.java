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
                            "discrete_iris.data", "discrete_soybean-small.data"};
        for(String path : files){
            String file = r.readEntireFile(path);
            System.out.println(path);

            doEverything(file);
            System.out.println("*******************************************");

        }

    }

    private static void doEverything(String file){
        Random rand = new Random();
        String[] lines = file.split("\n");

        DataPoint[] rawData = new DataPoint[lines.length];
        DataPoint[] pureData = new DataPoint[lines.length];

        for(int i = 0; i < lines.length; i++){
            rawData[i] = new DataPoint(lines[i]);
            pureData[i] = new DataPoint(lines[i]);
        }

        total = lines.length;

        attributes = rawData[0].self.length;
        types = DataPoint.types.size();

        DataPoint[][] data = fold(rawData);

        double[] accuracy = trainTest(data);
        System.out.println("Original Accuracy: " + Arrays.toString(accuracy));
        System.out.println("Avg: " + avg(accuracy));

        DataPoint[] rawCopy = rawData.clone();
        int numAttributesToShuffle = (int) (0.1 * attributes) + 1;

        System.out.println("Shuffling: " + numAttributesToShuffle);
        ArrayList<Integer> index = new ArrayList<>();
        for(int i = 0; i < numAttributesToShuffle; i++) {
            int ran = rand.nextInt(attributes);
            while (index.contains(ran) || ran < 0 || ran > attributes){
                ran = rand.nextInt();
            }
            index.add(ran);
            //System.out.println(ran);
            rawCopy = fisherYatesShuffle(rawCopy, ran);
        }


        DataPoint[][] dataCopy = fold(rawCopy);

        double[] accuracyCopy = trainTest(dataCopy);

        System.out.println("Copy Accuracy: " + Arrays.toString(accuracyCopy));
        System.out.println("Avg: " + avg(accuracyCopy));

        System.out.println("T Value: " + studentT(accuracy, accuracyCopy));
    }

    private static double[] trainTest(DataPoint[][] data){
        double[] accuracy = new double[10];

        int testCol = 0;
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

    private static boolean areDiff(DataPoint[] x, DataPoint[] y){
        for(int i = 0; i < x.length; i++){
            if(!x[i].equals(y[i]))
                return true;
        }
        return false;
    }

    private static DataPoint[] fisherYatesShuffle(DataPoint[] x, int att){
        Random rand = new Random();
        for(int i = x.length - 1; i > 0; i--){
            int j = rand.nextInt(i + 1);
            boolean temp = x[i].self[att];
            x[i].self[att] = x[j].self[att];
            x[j].self[att] = temp;
        }
        return x;
    }

    private static void trainCol(DataPoint[] trainData){
        int row = 0;
        DataPoint dataPoint = trainData[row++];
        while (dataPoint != null) {
            for (int k = 0; k < attributes; k++) {
                if (dataPoint.self[k]) {
                    sums[dataPoint.type][k]++;//increment total for specific attribute
                }
            }
            sums[dataPoint.type][attributes] ++;//increment total
            dataPoint = trainData[row++];
        }
        if(row < 2){
            System.out.println("OOPS, one fold is empty");
        }
    }

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

    private static int classify(DataPoint x){
        double max=0;
        int maxarg=0;
        for(int c=0;c<types;c++){
            if (C(x,c)>max) {
                max = C(x, c);
                maxarg=c;
            }
        }
        return maxarg;
    }

    private static double C(DataPoint x, int c){
        double p=1;
        for(int a=0;a<attributes;a++)
            if(x.self[a])
                p*= (double)(sums[c][a]+1) / (double)(sums[c][attributes]+types);
            else
                p*=1-(double)(sums[c][a]+1) / (double)(sums[c][attributes]+types);
        return (double)(sums[c][attributes])/(double)(total)*p;
    }

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

    public static double studentT(double[] x, double[] y){
        double xMean = avg(x);
        double xVar = variance(x);

        double yMean = avg(y);
        double yVar = variance(y);

        double numerator = xMean - yMean;
        double denominator = Math.sqrt(xVar/x.length + yVar/y.length);

        return numerator/denominator;


    }

    public static double variance(double[] x){
        double scalar = 1/(double)(x.length -1);
        double mean = avg(x);
        double sum = 0;
        for(int i = 0; i < x.length; i++){
            sum += (x[i] - mean)*(x[i] - mean);
        }
        return scalar * sum;
    }

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
