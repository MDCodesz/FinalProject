package edu.uga.cs.finalproject;


import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * This is an activity class for listing the current purchase lists.
 * The current purchase lists are listed as a RecyclerView.
 */
public class PurchasedListActivity extends AppCompatActivity
        implements EditPurchasedListDialogFragment.EditPurchasedListDialogListener {
    //AddListItemDialogFragment.AddListItemDialogListener,

    public static final String DEBUG_TAG = "PurchasedListActivity";

    private RecyclerView recyclerView;
    private PurchasedListRecyclerAdapter recyclerAdapter;

    private List<PurchasedList> purchasedList;

    private FirebaseDatabase database;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        Log.d( DEBUG_TAG, "onCreate()" );

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_purchased_list );

        recyclerView = findViewById( R.id.recyclerView );

        // initialize the Job Lead list
        purchasedList = new ArrayList<PurchasedList>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with purchase lists is empty at first; it will be updated later
        recyclerAdapter = new PurchasedListRecyclerAdapter( purchasedList, PurchasedListActivity.this );
        recyclerView.setAdapter( recyclerAdapter );

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("purchasedlists");

        // Set up a listener (event handler) to receive a value for the database reference.
        // This type of listener is called by Firebase once by immediately executing its onDataChange method
        // and then each time the value at Firebase changes.
        //
        // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
        // to maintain purchase lists.
        myRef.addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our purchase list list.
                purchasedList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    PurchasedList PurchasedList = postSnapshot.getValue(PurchasedList.class);
                    PurchasedList.setKey( postSnapshot.getKey() );
                    purchasedList.add( PurchasedList );
                    Log.d( DEBUG_TAG, "ValueEventListener: added: " + PurchasedList );
                    Log.d( DEBUG_TAG, "ValueEventListener: key: " + postSnapshot.getKey() );
                }

                Log.d( DEBUG_TAG, "ValueEventListener: notifying recyclerAdapter" );
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {
                System.out.println( "ValueEventListener: reading failed: " + databaseError.getMessage() );
            }
        } );
    }


    public void updatePurchasedListPrice(String purchasedListKey, String name, double newPrice) {
        DatabaseReference purchasedListRef = database.getReference()
                .child("purchasedlists")
                .child(purchasedListKey)
                .child("price"); // Reference to the price field under the purchased list

        purchasedListRef.setValue(newPrice)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(DEBUG_TAG, "Purchased list price updated successfully");
                        // Notify user or perform further actions upon successful update
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(DEBUG_TAG, "Failed to update purchased list price: " + e.getMessage());
                        // Handle the failure scenario, if needed
                    }
                });


        DatabaseReference purchasedListNameRef = database.getReference()
                .child("purchasedlists")
                .child(purchasedListKey)
                .child("name"); // Reference to the price field under the purchased list

        purchasedListNameRef.setValue(name)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(DEBUG_TAG, "Purchased list name updated successfully");
                        // Notify user or perform further actions upon successful update
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(DEBUG_TAG, "Failed to update purchased list name: " + e.getMessage());
                        // Handle the failure scenario, if needed
                    }
                });
    }


    // This is our own callback for a DialogFragment which edits an existing PurchasedList.
    // The edit may be an update or a deletion of this PurchasedList.
    // It is called from the EditPurchasedListDialogFragment.
    public void updatePurchasedList( int position, PurchasedList PurchasedList, int action ) {
        if( action == EditPurchasedListDialogFragment.SAVE ) {
            Log.d( DEBUG_TAG, "Updating purchase list at: " + position + "(" + PurchasedList.getName() + ")" );

            // Update the recycler view to show the changes in the updated purchase list in that view
            recyclerAdapter.notifyItemChanged( position );

            // Update this purchase list in Firebase
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "purchasedlists" )
                    .child( PurchasedList.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain purchase lists.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().setValue( PurchasedList ).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "updated purchase list at: " + position + "(" + PurchasedList.getName() + ")" );
                            Toast.makeText(getApplicationContext(), "Purchase list updated for " + PurchasedList.getName(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to update purchase list at: " + position + "(" + PurchasedList.getName() + ")" );
                    Toast.makeText(getApplicationContext(), "Failed to update " + PurchasedList.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
