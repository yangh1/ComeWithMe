package edu.rosehulman.ComeWithMe.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import edu.rosehulman.ComeWithMe.Adapters.AttendeeAdapter;
import edu.rosehulman.ComeWithMe.HomeActivity;
import edu.rosehulman.ComeWithMe.MainActivity;
import edu.rosehulman.ComeWithMe.Model.Attendee;
import edu.rosehulman.ComeWithMe.Model.Event;
import edu.rosehulman.ComeWithMe.R;


@SuppressLint("ValidFragment")
public class AttendeesDialog extends DialogFragment{

    private AttendeeAdapter mAdapter;
    private ListView mylist;
    private Event event;

    public AttendeesDialog(Event event){
        this.event = event;
    }


    private HomeActivity mFragment;


    @SuppressLint("InflateParams")
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Attendees List");
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.attendees_dialog, null);
        builder.setView(view);
        mAdapter = new AttendeeAdapter(this,event);
        mylist = (ListView) view.findViewById(R.id.event_attendee_View);
        mylist.setAdapter(mAdapter);

        return builder.create();
    }


}
