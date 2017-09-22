package edu.rosehulman.ComeWithMe.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;

import edu.rosehulman.ComeWithMe.Constants;
import edu.rosehulman.ComeWithMe.Fragments.AttendeesDialog;
import edu.rosehulman.ComeWithMe.Fragments.EditEventFragment;
import edu.rosehulman.ComeWithMe.Fragments.PendingEventFragment;
import edu.rosehulman.ComeWithMe.Fragments.UpcomingEventFragment;
import edu.rosehulman.ComeWithMe.Model.Attendee;
import edu.rosehulman.ComeWithMe.Model.Event;
import edu.rosehulman.ComeWithMe.R;
import edu.rosehulman.ComeWithMe.Utils.SharedPreferencesUtils;

public class UpcomingEventAdapter extends RecyclerView.Adapter<UpcomingEventAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<Event> mEvents = new ArrayList<>();
    private UpcomingEventFragment mUpcomingEventFragment;
    private String uid;
    private String username;
    private String email;
    private Firebase mEventRef;

    public UpcomingEventAdapter(UpcomingEventFragment upcomingEventFragment, String uid){
        mContext = upcomingEventFragment.getContext();
        mEventRef = new Firebase(Constants.EVENT_URL);
        mEventRef.addChildEventListener(new AttendeeChildEventListener());
        mUpcomingEventFragment = upcomingEventFragment;
        username = SharedPreferencesUtils.getUserUsername(upcomingEventFragment.getContext());
        email = SharedPreferencesUtils.getUserEmail(upcomingEventFragment.getContext());
        this.uid =uid;
    }

    public ArrayList<Event> getmEventss() {
        return mEvents;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_upcoming_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.eventTitle.setText(mEvents.get(position).getTitle());
        String hostUsername = "";
        String[] hn = mEvents.get(position).getHost().split(" ");
        for(int i=0;i<hn.length-1;i++){
            hostUsername=hostUsername+hn[i]+" ";
        }
        holder.eventHost.setText(hostUsername);
        holder.evenTime.setText(mEvents.get(position).getDate()+" "+mEvents.get(position).getTime());
        holder.eventLocation.setText(mEvents.get(position).getLocation());

    }

    public void deleteEvent(Event event){
        int index = 0;
        for(int i=0;i<mEvents.size();i++){
            if(mEvents.get(i).getEventKey().equals(event.getEventKey())){
                index = i;
            }
        }
        Event re = mEvents.remove(index);
        Firebase firebase = new Firebase(Constants.EVENT_URL+"/"+re.getKey());
        firebase.removeValue();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private TextView eventTitle;
        private TextView eventHost;
        private TextView evenTime;
        private TextView eventLocation;
        private Button statusButton;

        public ViewHolder(View itemView) {
            super(itemView);
            eventTitle = (TextView) itemView.findViewById(R.id.upcoming_event_title_text_view);
            eventHost= (TextView) itemView.findViewById(R.id.upcoming_event_host_text_view);
            evenTime = (TextView) itemView.findViewById(R.id.upcoming_event_data_text_view);
            eventLocation = (TextView) itemView.findViewById(R.id.upcoming_event_location_text_view);
            statusButton = (Button) itemView.findViewById(R.id.upcoming_user_status_button);
            statusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Event e = mEvents.get(getAdapterPosition());
                    Attendee at = null;
                    for (String a : e.getAttendees().keySet()) {
                        if (e.getAttendees().get(a).getEmail().equals(SharedPreferencesUtils.getUserEmail(mUpcomingEventFragment.getContext()))) {
                            if (e.getAttendees().get(a).getStatus()) {
                                e.getAttendees().get(a).setStatus(false);
                            } else {
                                e.getAttendees().get(a).setStatus(true);
                            }
                            at = e.getAttendees().get(a);
                            at.setKey(a);
                        }
                    }
                    if (at.getStatus()) {
                        statusButton.setText(R.string.status_Accepted);
                        statusButton.setTextColor(Color.GREEN);
                    } else {
                        statusButton.setText(R.string.status_Unaccepted);
                        statusButton.setTextColor(Color.RED);
                    }
                    Firebase attendeeRef = new Firebase(Constants.EVENT_URL + "/" + e.getKey() + "/attendees");
                    attendeeRef.child(at.getKey()).setValue(at);
//                    mEvents.remove(e);
//                    notifyDataSetChanged();
                }
            });
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            AttendeesDialog df = new AttendeesDialog(mEvents.get(getAdapterPosition()));
            df.show(mUpcomingEventFragment.getFragmentManager(), "show attendees");
        }

        @Override
        public boolean onLongClick(View v) {
            mUpcomingEventFragment.showEditDialog(mEvents.get(getAdapterPosition()));
            return true;
        }
    }

    private class AttendeeChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final Event event = dataSnapshot.getValue(Event.class);
            event.setKey(dataSnapshot.getKey());
            event.setAttendees(new HashMap<String, Attendee>());
            Firebase userStatus = new Firebase(Constants.EVENT_URL+"/"+dataSnapshot.getKey()+"/attendees");
            userStatus.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Attendee a = dataSnapshot.getValue(Attendee.class);
                    if(a.getEmail().equals(email)&& a.getStatus()){
                        event.addAttendee(dataSnapshot.getKey(), a);
                        mEvents.add(event);
                        notifyDataSetChanged();
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

    public void editEvent(Event event){
        String host = SharedPreferencesUtils.getUserUsername(mUpcomingEventFragment.getContext())+" "
                +SharedPreferencesUtils.getUserEmail(mUpcomingEventFragment.getContext());
        SharedPreferencesUtils.setEventKey(mContext, event.getKey());
        SharedPreferencesUtils.setEvent(mContext,event);
        if(event.getHost().equals(host)){
            FragmentTransaction ft = mUpcomingEventFragment.getActivity().getSupportFragmentManager().beginTransaction();
            Fragment editFragment = new EditEventFragment();
            ft.replace(R.id.container, editFragment);
            ft.commit();
        }
    }
}
