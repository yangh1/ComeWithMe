package edu.rosehulman.ComeWithMe.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rosehulman.ComeWithMe.Adapters.PendingEventAdapter;
import edu.rosehulman.ComeWithMe.HomeActivity;
import edu.rosehulman.ComeWithMe.Model.Event;
import edu.rosehulman.ComeWithMe.Model.User;
import edu.rosehulman.ComeWithMe.R;
import edu.rosehulman.ComeWithMe.Utils.SharedPreferencesUtils;


public class PendingEventFragment extends Fragment{
    private PendingEventAdapter mAdapter;
    private String uid;
    private Toolbar mToolbar;

    private final static String mapString = "geo:0,0?q=";


    public PendingEventFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_pending, container, false);
        mToolbar = ((HomeActivity) getActivity()).getToolbar();
        mToolbar.setTitle("Pending Events");
//        getActivity().getMenuInflater().inflate(R.menu.main, mToolbar.getMenu());
//        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.pending_events_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        registerForContextMenu(recyclerView);
        uid = SharedPreferencesUtils.getCurrentUser(getContext());
        mAdapter = new PendingEventAdapter(this, uid);
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    public void showEditDialog(final Event event){
        final String location = event.getLocation();
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
                builder.setNeutralButton("MAP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switchToGoogleMap(location);
                    }
                });
//                builder.setNeutralButton("EDIT", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mAdapter.editEvent(event);
//                    }
//                });
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

    private void switchToGoogleMap(String placeName){
        String q = mapString+placeName;
        Uri gmmIntentUri = Uri.parse(q);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
