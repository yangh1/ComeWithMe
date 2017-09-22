package edu.rosehulman.ComeWithMe.Adapters;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

import edu.rosehulman.ComeWithMe.Constants;
import edu.rosehulman.ComeWithMe.Fragments.AttendeesDialog;
import edu.rosehulman.ComeWithMe.Fragments.CreateEventFragment;
import edu.rosehulman.ComeWithMe.Model.Attendee;
import edu.rosehulman.ComeWithMe.Model.Event;
import edu.rosehulman.ComeWithMe.Model.User;
import edu.rosehulman.ComeWithMe.R;


public class AttendeeAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Attendee> mAttendees = new ArrayList<>();
    private String uid;

    public AttendeeAdapter(AttendeesDialog attendeesDialog,Event event){
        mContext = attendeesDialog.getContext();
        Firebase attendeesRef = new Firebase(Constants.EVENT_URL+"/"+event.getKey()+"/attendees");
        attendeesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Attendee a = dataSnapshot.getValue(Attendee.class);
                mAttendees.add(a);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public ArrayList<Attendee> getmAttendees() {
        return mAttendees;
    }

    @Override
    public int getCount() {
        return mAttendees.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mAttendees.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;
        if(convertView == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.row_view_attendees, parent, false);

        }else{
            view = convertView;
        }
        TextView usernameView = (TextView) view.findViewById(R.id.attendee_username_text_view);
        TextView emailView = (TextView) view.findViewById(R.id.attendee_email_text_view);
        TextView statusView = (TextView) view.findViewById(R.id.attendee_status_check_view);
        usernameView.setText(mAttendees.get(position).getUsername());
        emailView.setText(mAttendees.get(position).getEmail());
        if(mAttendees.get(position).getStatus()){
            statusView.setText("Accepted");
            statusView.setTextColor(Color.GREEN);
        }else{
            statusView.setText("Unaccepted");
            statusView.setTextColor(Color.RED);
        }
        return view;
    }


}