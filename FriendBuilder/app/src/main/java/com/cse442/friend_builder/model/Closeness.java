package com.cse442.friend_builder.model;

import android.util.Pair;

import java.util.Comparator;

/**
 * Created by btack on 4/9/2018.
 */

public class Closeness implements Comparator {


    @Override
    public int compare(Object o, Object t1) {

        o = (Pair<Double,String>) o;
        t1 = (Pair<Double,String>) t1;

        return (int) (((Pair<Double, String>) o).first * 100 - ((Pair<Double, String>) t1).first * 100);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
