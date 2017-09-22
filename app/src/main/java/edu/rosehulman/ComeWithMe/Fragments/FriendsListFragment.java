package edu.rosehulman.ComeWithMe.Fragments;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

import edu.rosehulman.ComeWithMe.Adapters.FriendAdapter;
import edu.rosehulman.ComeWithMe.Constants;
import edu.rosehulman.ComeWithMe.HomeActivity;
import edu.rosehulman.ComeWithMe.Model.User;
import edu.rosehulman.ComeWithMe.R;
import edu.rosehulman.ComeWithMe.Utils.SharedPreferencesUtils;


public class FriendsListFragment extends Fragment {
    private FriendAdapter mAdapter;
    private String uid;
    private Toolbar mToolbar;
    boolean x;

    private final boolean[] result = {true};
    public FriendsListFragment() {


    }

    public static FriendsListFragment newInstance() {
        FriendsListFragment fragment = new FriendsListFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_CURRENT_COURSE_KEY, currentCourseKey);
//        fragment.setArguments(args);
        return fragment;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);
        mToolbar = ((HomeActivity) getActivity()).getToolbar();
        mToolbar.setTitle("Friends List");
//        getActivity().getMenuInflater().inflate(R.menu.main, mToolbar.getMenu());
//        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriendDialog(null);
            }
        });
        fab.setVisibility(View.VISIBLE);

//        mToolbar = ((GradeRecorderActivity) getActivity()).getToolbar();
//        Utils.getCurrentCourseNameForToolbar(this);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.friends_list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        registerForContextMenu(recyclerView);
        uid = SharedPreferencesUtils.getCurrentUser(getContext());
        mAdapter = new FriendAdapter(this, uid);
        recyclerView.setAdapter(mAdapter);


        return rootView;
    }

    private void addFriendDialog(Object o) {
        DialogFragment df = new DialogFragment() {
            @Override
            @NonNull
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add New Friend");
                View view = getActivity().getLayoutInflater().inflate(R.layout.add_friend, null, false);
                builder.setView(view);
                final EditText emailEditText = (EditText) view.findViewById(R.id.fri_email);
//                if (assignment != null) {
//                    assignmentNameEditText.setText(assignment.getName());
//                    assignmentMaxGradeEditText.setText(String.valueOf(assignment.getMaxGrade()));
//                }
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String emailText = emailEditText.getText().toString();
                        Log.d("Bool", emailText);

                        final String[] name = {""};
                        final Firebase firebase = new Firebase(Constants.USER_URL);
                        firebase.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                User person = dataSnapshot.getValue(User.class);
                                if (person.getEmail().equals(emailText)) {
                                    x = true;
                                    result[0] = true;
                                    Log.d("123", result[0] + "");
                                    for (User u : mAdapter.getFriends()) {
                                        if (u.getEmail().equals(emailText)) {
                                            x = false;

                                        }
                                    }
                                    if (x) {
                                        Firebase friendsRef = new Firebase(Constants.USER_URL + "/" + uid + "/friends");
                                        System.out.println(Constants.USER_URL + "/" + uid + "/friends");
                                        friendsRef.push().setValue(person);
                                        result[0] = true;

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



//                        ResultDialog(result[0], x, emailText);
                    }

                });
                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };
        df.show(getActivity().getSupportFragmentManager(), "add_edit_assignment");
    }

    public void showDeleteDialog(final User friend) {
        DialogFragment df = new DialogFragment() {
            @Override
            @NonNull
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Do you want to delete " + friend.getUsername() + " from your friends list?");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.removeFriend(friend);
                        dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };
        df.show(getActivity().getSupportFragmentManager(), "confirm");
    }

    public void ResultDialog(final boolean result, final boolean x, final String name) {
        Log.d("Bool", result + "");
        Log.d("BOol", x+"");
        DialogFragment df = new DialogFragment() {
            @Override
            @NonNull
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                if (!(result ^ x)) {
                    builder.setTitle(name + " Has Been Your Friend !");
                } else {
                    builder.setTitle(name + " is Not Found !");
                }
                return builder.create();
            }
        };
        df.show(getActivity().getSupportFragmentManager(), "result");
    }
}
