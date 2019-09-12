package Project1_NaiiveBayes;

/**
 * Created by pryor on 9/11/2019.
 */
public class Testing {
    public static void main(String[] args) {

        int[][] classifications =
                {{15, 2, 4},
                {3, 12, 1},
                {4, 2, 17}};
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

        precision += (double)TP / ((double)(TP + FP)); //Sums the precision
        recall += (double)TP / ((double)(TP + FN)); //Sums the recall

    }

        System.out.println(precision);
        System.out.println(recall);
}
}
