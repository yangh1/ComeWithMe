package edu.rosehulman.ComeWithMe.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rosehulman.ComeWithMe.Adapters.PendingEventAdapter;
import edu.rosehulman.ComeWithMe.Adapters.UpcomingEventAdapter;
import edu.rosehulman.ComeWithMe.HomeActivity;
import edu.rosehulman.ComeWithMe.Model.Event;
import edu.rosehulman.ComeWithMe.R;
import edu.rosehulman.ComeWithMe.Utils.SharedPreferencesUtils;


public class UpcomingEventFragment extends Fragment{
    private UpcomingEventAdapter mAdapter;
    private String uid;
    private Toolbar mToolbar;


    public UpcomingEventFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_upcoming, container, false);
        mToolbar = ((HomeActivity) getActivity()).getToolbar();
        mToolbar.setTitle("Upcoming Events");
//        getActivity().getMenuInflater().inflate(R.menu.main, mToolbar.getMenu());
//        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.upcoming_events_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        registerForContextMenu(recyclerView);
        uid = SharedPreferencesUtils.getCurrentUser(getContext());
        mAdapter = new UpcomingEventAdapter(this,uid);
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    public void showEditDialog(final Event event){
        DialogFragment df = new DialogFragment() {
            @Override
            @NonNull
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDeleteDialog(event);
                    }
                });
                builder.setNeutralButton("EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.editEvent(event);
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };
        df.show(getActivity().getSupportFragmentManager(), "EDIT");
    }

    public void showDeleteDialog(final Event event){
        DialogFragment df = new DialogFragment() {
            @Override
            @NonNull
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Do You Want To Delete This Event?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.deleteEvent(event);
                        dismiss();
                    }
                });
                builder.setNegativeButton("NO", null);
                return builder.create();
            }
        };
        df.show(getActivity().getSupportFragmentManager(), "confirm");
    }
}
