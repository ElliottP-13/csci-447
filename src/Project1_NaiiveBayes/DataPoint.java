package Project1_NaiiveBayes;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by pryor on 9/7/2019.
 */
public class DataPoint {
    static ArrayList<String> types = new ArrayList<>();

    boolean[] self;
    int index;
    int type;

    public DataPoint(String data){

        String[] parse = data.split(",");
        self = new boolean[parse.length-2];

        for(int i = 1; i < parse.length - 1; i++){
            self[i-1] = Double.parseDouble(parse[i]) == 1;
        }

        //index = (int) Double.parseDouble(parse[0]);
        index = 1;
        if (!types.contains(parse[parse.length-1]))
            types.add(parse[parse.length-1]);

        type = types.indexOf(parse[parse.length - 1]);


    }

    public String toString(){
        return index + " : " + Arrays.toString(self);
    }

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

}
