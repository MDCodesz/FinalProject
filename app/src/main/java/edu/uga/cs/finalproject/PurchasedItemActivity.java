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
 * This is an activity class for listing the currentlist items.
 * The currentlist items are listed as a RecyclerView.
 */

public class PurchasedItemActivity extends AppCompatActivity
        implements EditPurchasedItemDialogFragment.EditPurchasedItemDialogListener{

//    PurchasedItemRecyclerAdapter.OnItemClickListener

    public static final String DEBUG_TAG = "PurchasedItemActivity";

    private RecyclerView recyclerView;
    private PurchasedItemRecyclerAdapter recyclerAdapter;

    private List<PurchasedItem> purchasedItemsList;

    private FirebaseDatabase database;

    private String purchasedListKey;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        Log.d( DEBUG_TAG, "onCreate()" );

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_purchased_item );

        recyclerView = findViewById( R.id.recyclerView );

        // Retrieve the key passed from PurchasedListRecyclerAdapter
        purchasedListKey = getIntent().getStringExtra("purchasedListKey");
        if (purchasedListKey == null) {
            // Handle the situation where purchasedListKey is null
            Log.e(DEBUG_TAG, "PurchasedListKey is null");
            // You might want to finish the activity or handle this situation accordingly
            finish();
        } else {
            Log.e(DEBUG_TAG, "PurchasedListKey is present");
        }

        // initialize the Job Lead list
        purchasedItemsList = new ArrayList<PurchasedItem>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter withlist items is empty at first; it will be updated later
        recyclerAdapter = new PurchasedItemRecyclerAdapter( purchasedItemsList, PurchasedItemActivity.this );
        recyclerView.setAdapter( recyclerAdapter );

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();


        //DatabaseReference myRef = database.getReference("purchaseditems");
        // Assuming you have a reference to Firebase Database
        DatabaseReference myRef = database.getReference().child("purchasedlists").child(purchasedListKey).child("purchaseditems");

        // Set up a listener (event handler) to receive a value for the database reference.
        // This type of listener is called by Firebase once by immediately executing its onDataChange method
        // and then each time the value at Firebase changes.
        //
        // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
        // to maintainlist items.
        myRef.addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on ourlist item list.
                purchasedItemsList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    PurchasedItem PurchasedItem = postSnapshot.getValue(PurchasedItem.class);
                    PurchasedItem.setKey( postSnapshot.getKey() );
                    purchasedItemsList.add( PurchasedItem );
                    Log.d( DEBUG_TAG, "ValueEventListener: added: " + PurchasedItem );
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
    //tying out for add to basket button

    // This is our own callback for a DialogFragment which edits an existing PurchasedItem.
    // The edit may be an update or a deletion of this PurchasedItem.
    // It is called from the EditPurchasedItemDialogFragment.
    public void updatePurchasedItem( int position, PurchasedItem PurchasedItem, int action ) {
        if( action == EditPurchasedItemDialogFragment.DELETE ) {
            Log.d( DEBUG_TAG, "Deleting purchased item at: " + position + "(" + PurchasedItem.getItemName() + ")" );


            // remove the deletedlist item from the list (internal list in the App)
            purchasedItemsList.remove( position );

            // Update the recycler view to remove the deletedlist item from that view
            recyclerAdapter.notifyItemRemoved( position );

            // Delete thislist item in Firebase.
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database.getReference().child("purchasedlists").child(purchasedListKey).child("purchaseditems").child( PurchasedItem.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintainlist items.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "deleted purchased item at: " + position + "(" + PurchasedItem.getItemName() + ")" );
                            Toast.makeText(getApplicationContext(), "List item deleted for " + PurchasedItem.getItemName(),
                                    Toast.LENGTH_SHORT).show();                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to delete purchased item at: " + position + "(" + PurchasedItem.getItemName() + ")" );
                    Toast.makeText(getApplicationContext(), "Failed to delete " + PurchasedItem.getItemName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
