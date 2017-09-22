package edu.rosehulman.ComeWithMe.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import edu.rosehulman.ComeWithMe.Adapters.InvitedAttendeeAdapter;
import edu.rosehulman.ComeWithMe.Constants;
import edu.rosehulman.ComeWithMe.HomeActivity;
import edu.rosehulman.ComeWithMe.Model.Attendee;
import edu.rosehulman.ComeWithMe.Model.Event;
import edu.rosehulman.ComeWithMe.R;
import edu.rosehulman.ComeWithMe.Utils.RandomKeyGenerator;
import edu.rosehulman.ComeWithMe.Utils.SharedPreferencesUtils;


public class CreateEventFragment extends Fragment{
    private EditText titleEdit;
    private TextView dateView;
    private TextView timeView;
    private EditText locationEdit;
    private InvitedAttendeeAdapter mAdapter;
    private Toolbar mToolbar;
    private String uid;

    private final static int oneHourDuration = 60*60*1000;
    public int[] dateArr = new int[5];

    public CreateEventFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_create, container, false);
        mToolbar = ((HomeActivity) getActivity()).getToolbar();
        mToolbar.setTitle("Create Event");
        titleEdit = (EditText) v.findViewById(R.id.TitleText);
        dateView = (TextView) v.findViewById(R.id.DateView);
        ImageButton dataButton = (ImageButton) v.findViewById(R.id.DateSetButton);
        timeView = (TextView) v.findViewById(R.id.TimeView);
        ImageButton timeButton = (ImageButton) v.findViewById(R.id.TimeButton);
        locationEdit = (EditText) v.findViewById(R.id.LocationText);
        ListView attendeeView = (ListView) v.findViewById(R.id.attendee_select_View);
        uid = SharedPreferencesUtils.getCurrentUser(getContext());
        mAdapter = new InvitedAttendeeAdapter(this, uid);
        attendeeView.setAdapter(mAdapter);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.create_event_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    eventCreate();
                    addToCalendar(dateArr, mAdapter.getmAttendees());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        fab.setBackgroundColor(Color.RED);
        fab.setBackgroundResource(R.drawable.ic_action_add);
        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog();
            }
        });

        return v;
    }

    private void eventCreate() throws Exception {
        Firebase eventRef = new Firebase(Constants.EVENT_URL);
        String title = titleEdit.getText().toString();
        String host = SharedPreferencesUtils.getUserUsername(getContext())+" "+SharedPreferencesUtils.getUserEmail(getContext());
        String date = dateView.getText().toString();
        String time = timeView.getText().toString();
        String location = locationEdit.getText().toString();
        final String eventKey = RandomKeyGenerator.generateRandomKey(15,RandomKeyGenerator.Mode.ALPHANUMERIC);
        Event event = new Event(title,host,date,time,location,eventKey);
        eventRef.push().setValue(event);
        eventRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event e = dataSnapshot.getValue(Event.class);
                if (e.getEventKey().equals(eventKey)) {
                    Firebase attendeesRef = new Firebase(Constants.EVENT_URL + "/" + dataSnapshot.getKey() + "/attendees");
                    for (Attendee attendee : mAdapter.getmAttendees()) {
                        System.out.println(attendee.getUsername() + " " + attendee.getEmail() + " " + attendee.getStatus());
                        if (attendee.getStatus()) {
                            attendee.setStatus(false);
                            attendeesRef.push().setValue(attendee);
                        }
                    }
//                    String username = SharedPreferencesUtils.getUserUsername(CreateEventFragment.this.getActivity());
//                    String email = SharedPreferencesUtils.getUserEmail(CreateEventFragment.this.getActivity());
//                    attendeesRef.push().setValue(new Attendee(username, email, true));
                    try {
                        String username = SharedPreferencesUtils.getUserUsername(getContext());
                        String email = SharedPreferencesUtils.getUserEmail(getContext());
                        attendeesRef.push().setValue(new Attendee(username, email, true));

                    } catch (Exception x){

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
        FragmentTransaction ft = this.getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = new SuccessfulFragment();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    private void showDateDialog() {
        DialogFragment df = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = getActivity().getLayoutInflater().inflate(R.layout.date_select_dialog, null);

                builder.setView(view);
                //Capture
                final String[] selYear = {""};
                final int[] selMouth = {0};
                final String[] selDay = {""};
                final TextView dataText = (TextView) view.findViewById(R.id.DateSelectView);
                final CalendarView setDateView = (CalendarView) view.findViewById(R.id.calendar_view);
                setDateView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                        selYear[0] = String.valueOf(year);
                        selMouth[0] = month+1;
                        selDay[0] = String.valueOf(dayOfMonth);
                        dateArr[0] = year;
                        dateArr[1] = month;
                        dateArr[2] = dayOfMonth;

                    }
                });;


                //Buttons
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long d = setDateView.getDate();
                        Date date = new Date(d);
                        String[] dataArray = date.toString().split(" ");
//                        dateView.setText(dataArray[0]+" "+dataArray[1]+" "+dataArray[2]);
                        String mouth = "";
                        if(selMouth[0] == 1) mouth = "Jan";
                        else if(selMouth[0] == 2) mouth="Feb";
                        else if(selMouth[0]==3) mouth = "Mar";
                        else if(selMouth[0]==4) mouth = "Apr";
                        else if(selMouth[0]==5) mouth = "May";
                        else if(selMouth[0]==6) mouth = "Jun";
                        else if(selMouth[0]==7) mouth = "Jul";
                        else if(selMouth[0]==8) mouth = "Aug";
                        else if(selMouth[0]==9) mouth = "Sep";
                        else if(selMouth[0]==10) mouth = "Oct";
                        else if(selMouth[0]==11) mouth = "Nov";
                        else if(selMouth[0]==12) mouth = "Dec";
                        dateView.setText(mouth + " / " + selDay[0] + " / " + selYear[0]);
                    }
                });
                return builder.create();
            }
        };
        df.show(getFragmentManager(), "add");
    }

    private void showTimeDialog() {
        DialogFragment df = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = getActivity().getLayoutInflater().inflate(R.layout.time_select_dialog, null);

                builder.setView(view);
                //Capture
                final String[] hour = {""};
                final int[] min = {0};
                final TextView timeText = (TextView) view.findViewById(R.id.TimeSelectView);
                final TimePicker timePicker = (TimePicker) view.findViewById(R.id.dialog_set_time_picker);
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        hour[0] = String.valueOf(hourOfDay);
                        min[0] = minute;
                        dateArr[3] = hourOfDay;
                        dateArr[4] = minute;
                    }
                });
                //Buttons
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        long t = timePicker.getDrawingTime();
//                        Time time = new Time(t);
                        timeView.setText(hour[0]+" : "+min[0]);

                    }
                });
                return builder.create();
            }
        };
        df.show(getFragmentManager(), "add");
    }

    private void resultDialog() {
        DialogFragment df = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Event Create Successful!");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((HomeActivity) CreateEventFragment.this.getActivity()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new UpcomingEventFragment()).commit();

                    }
                });
                return builder.create();
            }
        };
        df.show(getFragmentManager(), "create");
    }


    private void addToCalendar(int[] intArr, ArrayList<Attendee> attList){
        attList = mAdapter.getmAttendees();
        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setType("vnd.android.cursor.item/event");
        calIntent.putExtra(CalendarContract.Events.TITLE, String.valueOf(titleEdit.getText()));
        Log.d("Cal", String.valueOf(titleEdit.getText()));
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, String.valueOf(locationEdit.getText()));

        GregorianCalendar calDate = new GregorianCalendar(intArr[0],intArr[1],intArr[2],intArr[3],intArr[4]);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                calDate.getTimeInMillis());
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                calDate.getTimeInMillis() + oneHourDuration);
//        for(Attendee a : attList){
//            calIntent.putExtra(Intent.EXTRA_EMAIL, a.getEmail());
//        }

        int i = 0;
        int size = mAdapter.getCount();
        String[] emailArr = new String[size];
        for(Attendee attendee : attList){
            emailArr[i] = attendee.getEmail();
            i++;

        }

        calIntent.putExtra(Intent.EXTRA_EMAIL, emailArr);

        startActivity(calIntent);

    }

}
