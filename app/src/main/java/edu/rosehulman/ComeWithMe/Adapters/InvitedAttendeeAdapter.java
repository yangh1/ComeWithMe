package edu.rosehulman.ComeWithMe.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
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
import edu.rosehulman.ComeWithMe.Fragments.CreateEventFragment;
import edu.rosehulman.ComeWithMe.Model.Attendee;
import edu.rosehulman.ComeWithMe.Model.Event;
import edu.rosehulman.ComeWithMe.Model.User;
import edu.rosehulman.ComeWithMe.R;
import edu.rosehulman.ComeWithMe.Utils.Utils;

public class InvitedAttendeeAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Attendee> mAttendees = new ArrayList<>();
    private Fragment mFragment;
    private String uid;
    private Firebase mFriendsRef;

    public InvitedAttendeeAdapter(Fragment fragment, String uid){
        mContext = fragment.getContext();
        mFriendsRef = new Firebase(Constants.USER_URL+"/"+uid+"/friends");
        mFriendsRef.addChildEventListener(new AttendeeChildEventListener());
        mFragment = fragment;
        this.uid =uid;
    }

    public void setAttendeesStatus(Event event){
        System.out.println(mAttendees.size());
        Utils.AttendeeStatus(mAttendees, event);
        for (Attendee a:mAttendees){
            System.out.println(a.getStatus()+" "+a.getUsername());
        }
        notifyDataSetChanged();
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
            view = LayoutInflater.from(mContext).inflate(R.layout.row_view_friends_select, parent, false);

        }else{
            view = convertView;
        }
        TextView usernameView = (TextView) view.findViewById(R.id.select_username_text_view);
        TextView emailView = (TextView) view.findViewById(R.id.select_email_text_view);
        CheckBox statusBox = (CheckBox) view.findViewById(R.id.select_status_check_view);
        usernameView.setText(mAttendees.get(position).getUsername());
        emailView.setText(mAttendees.get(position).getEmail());
        statusBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAttendees.get(position).setStatus(isChecked);
            }
        });
        return view;
    }

    private class AttendeeChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            User person = dataSnapshot.getValue(User.class);
            Attendee attendee = new Attendee(person.getUsername(),person.getEmail(),false);
            mAttendees.add(attendee);
            notifyDataSetChanged();
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
    }

}
