package com.cse442.friend_builder;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class Hosted extends Fragment {

    public LinearLayout hostedEventsList;

    public Hosted() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_hosted, container, false);
        hostedEventsList = v.findViewById(R.id.hostedEventsList);

        return v;
    }

}
