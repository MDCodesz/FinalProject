package edu.uga.cs.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;


/**
 * This class is an activity to create a new job lead.
 */
public class NewListItemActivity extends AppCompatActivity {

    public static final String DEBUG_TAG = "NewListItemActivity";

    private EditText itemNameView;
    private EditText priceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list_item);

        itemNameView = findViewById( R.id.editText1 );
        priceView = findViewById( R.id.editTextNumber );
        Button saveButton = findViewById(R.id.button);

        saveButton.setOnClickListener( new ButtonClickListener()) ;
    }

    private class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String itemName = itemNameView.getText().toString();
            String priceText = priceView.getText().toString();

            // Converting the String to a double
            double price = 0.0;
            if (!priceText.isEmpty()) {
                try {
                    price = Double.parseDouble(priceText);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    //display an error message to the user here
                }
            }
            final ListItem listItem = new ListItem( itemName, price);

            // Add a new element (ListItem) to the list of shopping lists in Firebase.
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("itemlists");

            Log.d( DEBUG_TAG, "Pushing new items" );

            // First, a call to push() appends a new node to the existing list (one is created
            // if this is done for the first time).  Then, we set the value in the newly created
            // list node to store the new job lead.
            // This listener will be invoked asynchronously, as no need for an AsyncTask, as in
            // the previous apps to maintain shopping lists.
            myRef.push().setValue( listItem )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d( DEBUG_TAG, "Successfully pushed" );
                        // Show a quick confirmation
                        Toast.makeText(getApplicationContext(), "List Item created for " + listItem.getItemName(),
                                Toast.LENGTH_SHORT).show();

                        // Clear the EditTexts for next use.
                        itemNameView.setText("");
                        priceView.setText(""); // remove?
                    }
                })
                .addOnFailureListener( new OnFailureListener() {

                    @Override
                    public void onFailure( @NonNull Exception e ) {
                        Log.d( DEBUG_TAG, "failed pushed" );
                        Toast.makeText( getApplicationContext(), "Failed to create a List Item for " + listItem.getItemName(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    }

    @Override
    protected void onResume() {
        Log.d( DEBUG_TAG, "NewListItemActivity.onResume()" );
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d( DEBUG_TAG, "NewListItemActivity.onPause()" );
        super.onPause();
    }

    // The following activity callback methods are not needed and are for educational purposes only
    @Override
    protected void onStart() {
        Log.d( DEBUG_TAG, "NewListItemActivity.onStart()" );
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d( DEBUG_TAG, "NewListItemActivity.onStop()" );
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d( DEBUG_TAG, "NewListItemActivity.onDestroy()" );
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d( DEBUG_TAG, "NewListItemActivity.onRestart()" );
        super.onRestart();
    }
}
