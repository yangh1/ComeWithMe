package edu.rosehulman.ComeWithMe.Utils;

import android.content.Context;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

import edu.rosehulman.ComeWithMe.Constants;
import edu.rosehulman.ComeWithMe.Model.Attendee;
import edu.rosehulman.ComeWithMe.Model.Event;

public class Utils {
    public static void signOut(Context context) {
        SharedPreferencesUtils.removeCurrentUser(context);

        // Log out
        Firebase ref = new Firebase(Constants.FIREBASE_URL);
        ref.unauth();
    }

    public static void AttendeeStatus(final ArrayList<Attendee> mAttendees, final Event event){
        Firebase attendeeRef = new Firebase(Constants.EVENT_URL+"/"+event.getKey()+"/attendees");
        attendeeRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Attendee a = dataSnapshot.getValue(Attendee.class);
                for(Attendee attendee : mAttendees){
                    if(a.getEmail().equals(attendee.getEmail())){
                        attendee.setStatus(a.getStatus());
                    }
                }
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
}
