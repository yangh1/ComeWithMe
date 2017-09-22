package edu.rosehulman.ComeWithMe.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;

import edu.rosehulman.ComeWithMe.Constants;
import edu.rosehulman.ComeWithMe.Fragments.FriendsListFragment;
import edu.rosehulman.ComeWithMe.Model.User;
import edu.rosehulman.ComeWithMe.R;
import edu.rosehulman.ComeWithMe.Utils.SharedPreferencesUtils;


public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder>{

    private Firebase mFriendsRef;
    private ArrayList<User> mFriends = new ArrayList<>();
    private FriendsListFragment mFriendsListFragment;
    private String uid;

    public FriendAdapter(FriendsListFragment friendsListFragment, String uid){
        mFriendsRef = new Firebase(Constants.USER_URL+"/"+uid+"/friends");
        mFriendsRef.addChildEventListener(new FriendsChildEventListener());
        mFriendsListFragment = friendsListFragment;
        this.uid =uid;
    }

    public ArrayList<User> getFriends(){
        return this.mFriends;
    }

    public void removeFriend(User friend){
        Firebase delete = new Firebase(Constants.USER_URL+"/"+uid+"/friends/"+friend.getKey());
        delete.removeValue();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_friends, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.usernameView.setText(mFriends.get(position).getUsername());
        holder.emailView.setText(mFriends.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView usernameView;
        private TextView emailView;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameView = (TextView) itemView.findViewById(R.id.username_text_view);
            emailView = (TextView) itemView.findViewById(R.id.email_text_view);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onLongClick(View v) {
            User friend = mFriends.get(getAdapterPosition());
            mFriendsListFragment.showDeleteDialog(friend);
            return true;
        }
    }

    private class FriendsChildEventListener implements ChildEventListener {
        private void add(DataSnapshot dataSnapshot) {
            User friend = dataSnapshot.getValue(User.class);
            friend.setKey(dataSnapshot.getKey());
            mFriends.add(friend);
        }

        private int remove(String key) {
            for (User a : mFriends) {
                if (a.getKey().equals(key)) {
                    int foundPos = mFriends.indexOf(a);
                    mFriends.remove(a);
                    return foundPos;
                }
            }
            return -1;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            User friend = dataSnapshot.getValue(User.class);
            add(dataSnapshot);
            // We think using notifyItemInserted can cause crashes due to animation race condition.
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            remove(dataSnapshot.getKey());
            add(dataSnapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            int position = remove(dataSnapshot.getKey());
            if (position >= 0) {
                notifyItemRemoved(position);
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }
}
