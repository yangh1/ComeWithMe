package edu.rosehulman.ComeWithMe.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.rosehulman.ComeWithMe.Adapters.UpcomingEventAdapter;
import edu.rosehulman.ComeWithMe.HomeActivity;
import edu.rosehulman.ComeWithMe.R;
import edu.rosehulman.ComeWithMe.Utils.SharedPreferencesUtils;


public class SuccessfulFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_successful, container, false);
        Button okB = (Button) rootView.findViewById(R.id.SuccessfulButton);
        okB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuccessfulFragment.this.getContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
