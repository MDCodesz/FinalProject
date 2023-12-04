package edu.uga.cs.finalproject;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_NO );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d( DEBUG_TAG, "JobLead: MainActivity.onCreate()" );
        // assigning ID of the toolbar to a variable
        toolbar = findViewById( R.id.toolbar );
        // using toolbar as ActionBar
        //setSupportActionBar( toolbar );
        // Find our drawer view
        drawerLayout = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawerToggle = setupDrawerToggle();
        drawerToggle.setDrawerIndicatorEnabled( true );
        drawerToggle.syncState();

        // Connect DrawerLayout events to the ActionBarToggle
        drawerLayout.addDrawerListener( drawerToggle );
        // Find the drawer view
        navigationView = findViewById( R.id.nvView );
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem( menuItem );
                    return true;
                });

        Button signInButton = findViewById( R.id.button1 );
        Button registerButton = findViewById( R.id.button2 );

        signInButton.setOnClickListener( new SignInButtonClickListener() );
        registerButton.setOnClickListener( new RegisterButtonClickListener() );
    }
    public void selectDrawerItem( MenuItem menuItem ) {
        Intent intent = null;

        // Create a new fragment based on the used selection in the nav drawer
        int itemId = menuItem.getItemId();
        if (itemId == R.id.menu_list) {
            intent = new Intent(this, ReviewListActivity.class);
        } else if(itemId == R.id.menu_register) {
            intent = new Intent(this, RegisterActivity.class);
        }else if(itemId == R.id.menu_basket) {
            intent = new Intent(this, BasketActivity.class);
        }else {
            return;
        }

        startActivity(intent);

        /*
        // this is included here as a possible future modification
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked( true );
        // Set action bar title
        setTitle( menuItem.getTitle());
         */

        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }
    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open,  R.string.nav_close);
    }
    @Override
    protected void onPostCreate( Bundle savedInstanceState ) {
        super.onPostCreate( savedInstanceState );
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged( @NonNull Configuration newConfig ) {
        super.onConfigurationChanged( newConfig );
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged( newConfig );
    }
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        if( drawerToggle.onOptionsItemSelected( item ) ) {
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    // A button listener class to start a Firebase sign-in process
    private class SignInButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick( View v ) {
            // This is an example of how to use the AuthUI activity for signing in to Firebase.
            // Here, we are just using email/password sign in.
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    // removes name setRequireName(false)
                    new AuthUI.IdpConfig.EmailBuilder().setRequireName(false).build()
            );

            Log.d( DEBUG_TAG, "MainActivity.SignInButtonClickListener: Signing in started" );

            // Create an Intent to singin to Firebese.
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build();
            signInLauncher.launch(signInIntent);
        }
    }

    // The ActivityResultLauncher class provides a new way to invoke an activity
    // for some result.  It is a replacement for the deprecated method startActivityForResult.
    //
    // The signInLauncher variable is a launcher to start the AuthUI's logging in process that
    // should return to the MainActivity when completed.  The overridden onActivityResult
    // is then called when the Firebase logging-in process is finished.
    private ActivityResultLauncher<Intent> signInLauncher =
            registerForActivityResult(
                    new FirebaseAuthUIActivityResultContract(),
                    new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                        @Override
                        public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                            onSignInResult(result);
                        }
                    }
            );

    // This method is called once the Firebase sign-in activity (lqunched above) returns (completes).
    // Then, the current (logged-in) Firebase user can be obtained.
    // Subsequently, there is a transition to the JobLeadManagementActivity.
    private void onSignInResult( FirebaseAuthUIAuthenticationResult result ) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            if( response != null ) {
                Log.d( DEBUG_TAG, "MainActivity.onSignInResult: response.getEmail(): " + response.getEmail() );
            }

            //Log.d( DEBUG_TAG, "MainActivity.onSignInResult: Signed in as: " + user.getEmail() );

            // after a successful sign in, start the job leads management activity
            Intent intent = new Intent( this, ShoppingListManagementActivity.class );
            startActivity( intent );
        }
        else {
            Log.d( DEBUG_TAG, "MainActivity.onSignInResult: Failed to sign in" );
            // Sign in failed. If response is null the user canceled the
            Toast.makeText( getApplicationContext(),
                    "Sign in failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private class RegisterButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // start the user registration activity
            Intent intent = new Intent(view.getContext(), RegisterActivity.class);
            view.getContext().startActivity(intent);
        }
    }

    // These activity callback methods are not needed and are for edational purposes only
    @Override
    protected void onStart() {
        Log.d( DEBUG_TAG, "JobLead: MainActivity.onStart()" );
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d( DEBUG_TAG, "JobLead: MainActivity.onResume()" );
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d( DEBUG_TAG, "JobLead: MainActivity.onPause()" );
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d( DEBUG_TAG, "JobLead: MainActivity.onStop()" );
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d( DEBUG_TAG, "JobLead: MainActivity.onDestroy()" );
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d( DEBUG_TAG, "JobLead: MainActivity.onRestart()" );
        super.onRestart();
    }
}
