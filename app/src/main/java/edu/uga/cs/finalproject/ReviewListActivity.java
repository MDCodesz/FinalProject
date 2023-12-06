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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
public class ReviewListActivity
        extends AppCompatActivity
        implements AddListItemDialogFragment.AddListItemDialogListener,
        EditListItemDialogFragment.EditListItemDialogListener, ListItemRecyclerAdapter.OnItemClickListener {

    public static final String DEBUG_TAG = "ReviewListActivity";

    private RecyclerView recyclerView;
    private ListItemRecyclerAdapter recyclerAdapter;

    private List<ListItem> itemsList;
    private List<Integer> selectedPositions = new ArrayList<>();

    private FirebaseDatabase database;
    private double price;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        Log.d( DEBUG_TAG, "onCreate()" );

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_review_list );

        recyclerView = findViewById( R.id.recyclerView );

        FloatingActionButton floatingButton = findViewById(R.id.floatingActionButton);
        floatingButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AddListItemDialogFragment();
                newFragment.show( getSupportFragmentManager(), null);
            }
        });

        // initialize the Job Lead list
        itemsList = new ArrayList<ListItem>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter withlist items is empty at first; it will be updated later
        recyclerAdapter = new ListItemRecyclerAdapter( itemsList, ReviewListActivity.this );
        recyclerView.setAdapter( recyclerAdapter );

        //testing for add to basket purposees
        recyclerAdapter.setOnItemClickListener(this);

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("itemlists");

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
                itemsList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    ListItem ListItem = postSnapshot.getValue(ListItem.class);
                    ListItem.setKey( postSnapshot.getKey() );
                    itemsList.add( ListItem );
                    Log.d( DEBUG_TAG, "ValueEventListener: added: " + ListItem );
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
    @Override
    public void onAddToBasketClick(int position) {
        //ListItem item = itemsList.get(position);
        //to get item IDs
        //String itemName =
        //get the item from the adapter based on the position ;
        //addToBasket(item);
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(Integer.valueOf(position));
        } else {
            selectedPositions.add(position);
            addSelectedItemsToBasket();
        }

    }
    private void addSelectedItemsToBasket() {
        List<ListItem> selectedItems = getSelectedItems();

        if (!selectedItems.isEmpty()) {
            List<String> itemIds = getItemIds(selectedItems);
            Log.d("ShoppingListActivity", "ItemIds: " + itemIds);

            addToBasket(itemIds, selectedItems);
        }
    }
    private List<String> getItemIds(List<ListItem> items) {
        List<String> itemIds = new ArrayList<>();

        for (ListItem item : items) {
            String itemId = item.getKey(); // Replace with the actual method to get the item ID
            itemIds.add(itemId);
        }

        return itemIds;
    }
    // Function to add an item to the basket
    private void addToBasket(List<String> itemIds, List<ListItem> items) {
        // Assuming userUid and itemId are known
        //DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userUid);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userId = currentUser.getUid();
        DatabaseReference myRef = database.getReference("Users").child(userId);
        for(int i = 0; i < itemIds.size(); i++ ) {
            String itemId = itemIds.get(i);
            ListItem item = items.get(i);


            // Remove from shopping list
            myRef.child("itemlists").child(itemId).removeValue();
            Log.d("ShoppingListActivity", "ItemIds Removed: " + itemIds);

            // Add to shopping basket
            myRef.child("ShoppingBasket").child(itemId).setValue(item);
        }
    }



    private List<ListItem> getSelectedItems() {
        List<ListItem> selectedItems = new ArrayList<>();

        for (int position : selectedPositions) {
            if (position >= 0 && position < recyclerAdapter.getItemCount()) {
                ListItem item = itemsList.get(position);
                selectedItems.add(item);
            }
        }

        return selectedItems;
    }

    // this is our own callback for a AddListItemDialogFragment which adds a newlist item.
    public void addListItem(ListItem ListItem) {
        // add the newlist item
        // Add a new element (ListItem) to the list oflist items in Firebase.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("itemlists");

        // First, a call to push() appends a new node to the existing list (one is created
        // if this is done for the first time).  Then, we set the value in the newly created
        // list node to store the newlist item.
        // This listener will be invoked asynchronously, as no need for an AsyncTask, as in
        // the previous apps to maintainlist items.
        myRef.push().setValue( ListItem )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // Reposition the RecyclerView to show the ListItem most recently added (as the last item on the list).
                        // Use of the post method is needed to wait until the RecyclerView is rendered, and only then
                        // reposition the item into view (show the last item on the list).
                        // the post method adds the argument (Runnable) to the message queue to be executed
                        // by Android on the main UI thread.  It will be done *after* the setAdapter call
                        // updates the list items, so the repositioning to the last item will take place
                        // on the complete list of items.
                        recyclerView.post( new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition( itemsList.size()-1 );
                            }
                        } );

                        Log.d( DEBUG_TAG, "List item saved: " + ListItem );
                        // Show a quick confirmation
                        Toast.makeText(getApplicationContext(), "List item created for " + ListItem.getItemName(),
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure( @NonNull Exception e ) {
                        Toast.makeText( getApplicationContext(), "Failed to create a List item for " + ListItem.getItemName(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // This is our own callback for a DialogFragment which edits an existing ListItem.
    // The edit may be an update or a deletion of this ListItem.
    // It is called from the EditListItemDialogFragment.
    public void updateListItem( int position, ListItem ListItem, int action ) {
        if( action == EditListItemDialogFragment.SAVE ) {
            Log.d( DEBUG_TAG, "Updating list item at: " + position + "(" + ListItem.getItemName() + ")" );

            // Update the recycler view to show the changes in the updatedlist item in that view
            recyclerAdapter.notifyItemChanged( position );

            // Update thislist item in Firebase
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "itemlists" )
                    .child( ListItem.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintainlist items.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().setValue( ListItem ).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "updatedlist item at: " + position + "(" + ListItem.getItemName() + ")" );
                            Toast.makeText(getApplicationContext(), "List item updated for " + ListItem.getItemName(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to updatelist item at: " + position + "(" + ListItem.getItemName() + ")" );
                    Toast.makeText(getApplicationContext(), "Failed to update " + ListItem.getItemName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if( action == EditListItemDialogFragment.DELETE ) {
            Log.d( DEBUG_TAG, "Deletinglist item at: " + position + "(" + ListItem.getItemName() + ")" );

            // remove the deletedlist item from the list (internal list in the App)
            itemsList.remove( position );

            // Update the recycler view to remove the deletedlist item from that view
            recyclerAdapter.notifyItemRemoved( position );

            // Delete thislist item in Firebase.
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "itemlists" )
                    .child( ListItem.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintainlist items.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "deleted list item at: " + position + "(" + ListItem.getItemName() + ")" );
                            Toast.makeText(getApplicationContext(), "List item deleted for " + ListItem.getItemName(),
                                    Toast.LENGTH_SHORT).show();                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to delete list item at: " + position + "(" + ListItem.getItemName() + ")" );
                    Toast.makeText(getApplicationContext(), "Failed to delete " + ListItem.getItemName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
