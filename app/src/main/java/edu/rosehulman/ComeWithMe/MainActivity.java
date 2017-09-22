package edu.rosehulman.ComeWithMe;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;

import edu.rosehulman.ComeWithMe.Model.User;
import edu.rosehulman.ComeWithMe.Utils.SharedPreferencesUtils;


public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_CODE_GOOGLE_LOGIN = 1;
    private GoogleApiClient mGoogleApiClient;
    private User user;
    private String uid;
    private boolean isExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            Firebase.setAndroidContext(this);
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        Firebase firebase = new Firebase(Constants.FIREBASE_URL);
        if(firebase.getAuth()==null||isExpired(firebase.getAuth())){
            switchToLoginFragment();
        }else {
            switchToPasswordFragment(Constants.USER_URL);
        }
    }

    private boolean isExpired(AuthData authData) {
        return (System.currentTimeMillis() / 1000) >= authData.getExpires();
    }

    @Override
    public void onGoogleLogin() {
        Intent intend = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intend, REQUEST_CODE_GOOGLE_LOGIN);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == REQUEST_CODE_GOOGLE_LOGIN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount acct = result.getSignInAccount();
                String emailAddress = acct.getEmail();
                this.user = new User(acct.getDisplayName(),acct.getEmail());
                getGoogleOAuthToken(emailAddress);
            }
        }
    }

    private void getGoogleOAuthToken(final String emailAddress) {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            String errorMessage = null;

            @Override
            protected String doInBackground(Void... params) {
                String token = null;
                try {
                    String scope = "oauth2:profile email";
                    token = GoogleAuthUtil.getToken(MainActivity.this, emailAddress, scope);
                } catch (IOException transientEx) {
                /* Network or server error */
                    errorMessage = "Network error: " + transientEx.getMessage();
                } catch (UserRecoverableAuthException e) {
                /* We probably need to ask for permissions, so start the intent if there is none pending */
                    Intent recover = e.getIntent();
                    startActivityForResult(recover, MainActivity.REQUEST_CODE_GOOGLE_LOGIN);
                } catch (GoogleAuthException authEx) {
                    errorMessage = "Error authenticating with Google: " + authEx.getMessage();
                }
                return token;
            }
            @Override
            protected void onPostExecute(String token) {
                Log.d("FPK", "onPostExecute");
                if(token!=null){
                    onGoogleLoginWithToken(token);
                }else{
                    showLoginError(errorMessage);
                }
            }
        };
        task.execute();
    }

    private void onGoogleLoginWithToken(String oAuthToken) {
        Firebase firebase = new Firebase(Constants.FIREBASE_URL);
        firebase.authWithOAuthToken("google", oAuthToken, new MyAuthResultHandler());
    }

    class MyAuthResultHandler implements Firebase.AuthResultHandler{

        @Override
        public void onAuthenticated(AuthData authData) {
            uid = authData.getUid();
            Firebase xx = new Firebase(Constants.USER_URL+"/"+uid);
            SharedPreferencesUtils.setCurrentUser(MainActivity.this, uid);
            SharedPreferencesUtils.settUserUsername(MainActivity.this, user.getUsername());
            SharedPreferencesUtils.settUserEmail(MainActivity.this,user.getEmail());
            xx.child("email").setValue(user.getEmail());
            xx.child("username").setValue(user.getUsername());
            switchToPasswordFragment(Constants.USER_URL);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            showLoginError(firebaseError.getMessage());
        }
    }

    // MARK: Provided Helper Methods
    private void switchToLoginFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new LoginFragment(), "Login");
        ft.commit();
    }

    private void switchToPasswordFragment(String repoUrl) {
        Intent homeIntent = new Intent(this,HomeActivity.class);
        startActivity(homeIntent);
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
////        Fragment passwordFragment = new PasswordFragment();
//        Fragment homePageFragment = new HomePageFragment();
//        Bundle args = new Bundle();
//        args.putString(Constants.FIREBASE, repoUrl);
////        passwordFragment.setArguments(args);
//        homePageFragment.setArguments(args);
//        ft.replace(R.id.fragment_container, homePageFragment, "Passwords");
//        ft.commit();
    }

    private void showLoginError(String message) {
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("Login");
        loginFragment.onLoginError(message);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(Constants.TAG,"onConnectionFailed: "+connectionResult.getErrorMessage());
    }
}
