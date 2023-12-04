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

//        FloatingActionButton floatingButton = findViewById(R.id.floatingActionButton);
//        floatingButton.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogFragment newFragment = new AddListItemDialogFragment();
//                newFragment.show( getSupportFragmentManager(), null);
//            }
//        });

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
        DatabaseReference myRef = database.getReference("purchasedlist");

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

    // this is our own callback for a AddListItemDialogFragment which adds a new purchase list.
//    public void addListItem(PurchasedList PurchasedList) {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("purchasedlist");
//        myRef.push().setValue( PurchasedList )
//                .addOnSuccessListener( new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        recyclerView.post( new Runnable() {
//                            @Override
//                            public void run() {
//                                recyclerView.smoothScrollToPosition( purchasedList.size()-1 );
//                            }
//                        } );
//                        Log.d( DEBUG_TAG, "Purchase list saved: " + PurchasedList );
//                        // Show a quick confirmation
//                        Toast.makeText(getApplicationContext(), "Purchase list created for " + PurchasedList.getName(),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener( new OnFailureListener() {
//                    @Override
//                    public void onFailure( @NonNull Exception e ) {
//                        Toast.makeText( getApplicationContext(), "Failed to create a Purchase list for " + PurchasedList.getName(),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

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
                    .child( "purchasedlist" )
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
        else if( action == EditPurchasedListDialogFragment.DELETE ) {
            Log.d( DEBUG_TAG, "Deleting purchase list at: " + position + "(" + PurchasedList.getName() + ")" );

            // remove the deleted purchase list from the list (internal list in the App)
            purchasedList.remove( position );

            // Update the recycler view to remove the deleted purchase list from that view
            recyclerAdapter.notifyItemRemoved( position );

            // Delete this purchase list in Firebase.
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "purchasedlist" )
                    .child( PurchasedList.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain purchase lists.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "deleted purchase list at: " + position + "(" + PurchasedList.getName() + ")" );
                            Toast.makeText(getApplicationContext(), "Purchase list deleted for " + PurchasedList.getName(),
                                    Toast.LENGTH_SHORT).show();                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to delete purchase list at: " + position + "(" + PurchasedList.getName() + ")" );
                    Toast.makeText(getApplicationContext(), "Failed to delete " + PurchasedList.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
