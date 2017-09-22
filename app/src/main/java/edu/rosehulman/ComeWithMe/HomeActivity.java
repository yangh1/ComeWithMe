package edu.rosehulman.ComeWithMe;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.common.api.GoogleApiClient;

import edu.rosehulman.ComeWithMe.Fragments.CreateEventFragment;
import edu.rosehulman.ComeWithMe.Fragments.FriendsListFragment;
import edu.rosehulman.ComeWithMe.Fragments.PendingEventFragment;
import edu.rosehulman.ComeWithMe.Fragments.UpcomingEventFragment;
import edu.rosehulman.ComeWithMe.Model.User;
import edu.rosehulman.ComeWithMe.Utils.SharedPreferencesUtils;
import edu.rosehulman.ComeWithMe.Utils.Utils;


public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{
    private FloatingActionButton mFab;
    private Toolbar mToolbar;

    private Firebase mFirebaseRef;
    private static final int REQUEST_CODE_GOOGLE_LOGIN = 1;
    private GoogleApiClient mGoogleApiClient;
    private User user;
    //    private User user;
    private String uid;

    public Toolbar getToolbar() {
        return mToolbar;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_come_with_me);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Skipped during rotation, but Firebase settings should persist.
//        if (savedInstanceState == null) {
//            initializeFirebase();
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headView = navigationView.getHeaderView(0);

        TextView userUsername = (TextView) headView.findViewById(R.id.nav_header_username);
        TextView userEmail = (TextView)headView.findViewById(R.id.nav_header_email);
        String username = SharedPreferencesUtils.getUserUsername(this);
        String email = SharedPreferencesUtils.getUserEmail(this);
        userUsername.setText(username);
        userEmail.setText(email);

//        if (savedInstanceState == null) {
//            Firebase.setAndroidContext(this);
//        }
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
//
//        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);
//        if (mFirebaseRef.getAuth() == null) {
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.container, new LoginFragment());
//            ft.commit();
//        } else {
//            switchToPasswordFragment(Constants.USER_URL);
//        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new UpcomingEventFragment());
            ft.commit();
    }

    private void initializeFirebase() {
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);
        mFirebaseRef.keepSynced(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment switchTo = null;


// TODO: May be useful if I implement return to the chosen fragment after choosing a course.
        if (id == R.id.nav_sign_out) {
            Utils.signOut(this);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_create) {
            switchTo = new CreateEventFragment();
        } else if (id == R.id.nav_pend) {
            switchTo = new PendingEventFragment();
        } else if (id == R.id.nav_upcoming) {
            switchTo = new UpcomingEventFragment();
        } else if (id == R.id.nav_friend) {
            switchTo = new FriendsListFragment();
        }

        if (switchTo != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, switchTo);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void switchToPasswordFragment(String repoUrl) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        Fragment passwordFragment = new PasswordFragment();
        Fragment fragment = new UpcomingEventFragment();
        Bundle args = new Bundle();
        args.putString(Constants.FIREBASE, repoUrl);
//        passwordFragment.setArguments(args);
        fragment.setArguments(args);
        ft.replace(R.id.container, fragment, "upcoming");
        ft.commit();
    }

}
