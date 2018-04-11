package com.cse442.friend_builder.model;

import android.os.Build;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by btack on 4/9/2018.
 */

public class BrianDictionary {

    public List<Pair<Double, String>> values = new ArrayList<>();
    public double length = 0;


    public BrianDictionary(){

    }

    public void setLength(double l)
    {
        length = l;
    }


    public void add(double distance, String info){

        values.add(new Pair<>(distance,info));
    }

    //@RequiresApi(api = Build.VERSION_CODES.N)


    public ArrayList<String> make(){

        Collections.sort(values,(new Closeness()));

        ArrayList<String> info = new ArrayList<>();

        int count = 0;

        while(count < length)
        {

            info.add(values.get(count).second);
            count = count + 1;
        }

        return info;
    }


}
