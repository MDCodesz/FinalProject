package edu.uga.cs.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ShoppingListManagementActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "ManagementActivity";

    private TextView signedInTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_management);

        Log.d( DEBUG_TAG, "ShoppingListManagementActivity.onCreate()" );



        Button newItemButton = findViewById(R.id.button1);
        Button viewShoppingListButton = findViewById(R.id.button2);
        Button reviewCart = findViewById(R.id.button3);
        Button viewPurchasedListsButton = findViewById(R.id.button4);
        Button logoutButton = findViewById(R.id.button6);
        signedInTextView = findViewById( R.id.textView3 );

        newItemButton.setOnClickListener( new NewListItemButtonClickListener() );
        viewShoppingListButton.setOnClickListener( new ViewShoppingListButtonClickListener() );
        viewPurchasedListsButton.setOnClickListener( new ViewPurchasedListsButtonClickListener() );
        reviewCart.setOnClickListener(new ReviewCartButtonClickListener() );
        logoutButton.setOnClickListener(new LogoutButtonClickListener());
        // Setup a listener for a change in the sign in status (authentication status change)
        // when it is invoked, check if a user is signed in and update the UI text view string,
        // as needed.
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if( currentUser != null ) {
                    // User is signed in
                    Log.d(DEBUG_TAG, "onAuthStateChanged:signed_in:" + currentUser.getUid());
                    String userEmail = currentUser.getEmail();
                    signedInTextView.setText( "Signed in as: " + userEmail );
                } else {
                    // User is signed out
                    Log.d( DEBUG_TAG, "onAuthStateChanged:signed_out" );
                    signedInTextView.setText( "Signed in as: not signed in" );
                }
            }
        });
    }



    private class NewListItemButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), NewListItemActivity.class);
            view.getContext().startActivity( intent );
        }
    }

    private class ViewShoppingListButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), ReviewListActivity.class);
            view.getContext().startActivity(intent);
        }
    }

    private class ViewPurchasedListsButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), PurchasedListActivity.class);
            view.getContext().startActivity(intent);
        }
    }

    private class ReviewCartButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), BasketActivity.class);
            view.getContext().startActivity(intent);
        }
    }

    private class LogoutButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            FirebaseAuth.getInstance().signOut();
            view.getContext().startActivity(intent);
        }
    }

    // These activity callback methods are not needed and are for edational purposes only
    @Override
    protected void onStart() {
        Log.d( DEBUG_TAG, "ShoppingList: ManagementActivity.onStart()" );
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d( DEBUG_TAG, "ShoppingList: ManagementActivity.onResume()" );
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d( DEBUG_TAG, "ShoppingList: ManagementActivity.onPause()" );
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d( DEBUG_TAG, "ShoppingList: ManagementActivity.onStop()" );
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d( DEBUG_TAG, "ShoppingList: ManagementActivity.onDestroy()" );
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d( DEBUG_TAG, "ShoppingList: ManagementActivity.onRestart()" );
        super.onRestart();
    }
}