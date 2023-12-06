package edu.uga.cs.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class BasketActivity extends AppCompatActivity implements AddListItemDialogFragment.AddListItemDialogListener,
        EditListItemDialogFragment.EditListItemDialogListener {

    public static final String DEBUG_TAG = "BasketActivity";

    private RecyclerView recyclerView;
    private BasketRecyclerAdapter recyclerAdapter;

    private List<ListItem> itemsList;

    private FirebaseDatabase database;
    private double price;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        recyclerView = findViewById(R.id.recyclerView2);
        Button checkoutButton = findViewById(R.id.button5);
        FloatingActionButton floatingButton = findViewById(R.id.floatingActionButton2);
        floatingButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AddListItemDialogFragment();
                newFragment.show( getSupportFragmentManager(), null);
            }
        });
        //to get user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userId = currentUser.getUid();
        //String userId =" ";
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        Log.d(DEBUG_TAG, "Basket onCreate user: " + userRef);
        // initialize the Job Lead list
        itemsList = new ArrayList<ListItem>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with job leads is empty at first; it will be updated later
        recyclerAdapter = new BasketRecyclerAdapter(itemsList, BasketActivity.this);
        recyclerView.setAdapter(recyclerAdapter);

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(userId).child("ShoppingBasket");
        // Set up a listener (event handler) to receive a value for the database reference.
        // This type of listener is called by Firebase once by immediately executing its onDataChange method
        // and then each time the value at Firebase changes.
        //
        // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
        // to maintain job leads.
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our job lead list.
                itemsList.clear(); // clear the current content; this is inefficient!
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ListItem ListItem = postSnapshot.getValue(ListItem.class);
                    ListItem.setKey(postSnapshot.getKey());
                    itemsList.add(ListItem);
                    Log.d(DEBUG_TAG, "Basket ValueEventListener: added: " + ListItem);
                    Log.d(DEBUG_TAG, "Basket ValueEventListener: key: " + postSnapshot.getKey());
                }

                Log.d(DEBUG_TAG, "ValueEventListener: notifying recyclerAdapter");
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("ValueEventListener: reading failed: " + databaseError.getMessage());
            }
        });
    }

    // this is our own callback for a AddListItemDialogFragment which adds a new job lead.
    public void addListItem(ListItem ListItem) {
        // add the new job lead
        // Add a new element (ListItem) to the list of job leads in Firebase.
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userId = currentUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(userId).child("ShoppingBasket");

        // First, a call to push() appends a new node to the existing list (one is created
        // if this is done for the first time).  Then, we set the value in the newly created
        // list node to store the new job lead.
        // This listener will be invoked asynchronously, as no need for an AsyncTask, as in
        // the previous apps to maintain job leads.
        myRef.push().setValue(ListItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // Reposition the RecyclerView to show the ListItem most recently added (as the last item on the list).
                        // Use of the post method is needed to wait until the RecyclerView is rendered, and only then
                        // reposition the item into view (show the last item on the list).
                        // the post method adds the argument (Runnable) to the message queue to be executed
                        // by Android on the main UI thread.  It will be done *after* the setAdapter call
                        // updates the list items, so the repositioning to the last item will take place
                        // on the complete list of items.
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(itemsList.size() - 1);
                            }
                        });

                        Log.d(DEBUG_TAG, "Job lead saved: " + ListItem);
                        // Show a quick confirmation
                        Toast.makeText(getApplicationContext(), "Job lead created for " + ListItem.getItemName(),
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to create a Job lead for " + ListItem.getItemName(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // This is our own callback for a DialogFragment which edits an existing ListItem.
    // The edit may be an update or a deletion of this ListItem.
    // It is called from the EditListItemDialogFragment.
    public void updateListItem(int position, ListItem ListItem, int action) {
        if (action == EditListItemDialogFragment.SAVE) {
            Log.d(DEBUG_TAG, "Updating job lead at: " + position + "(" + ListItem.getItemName() + ")");

            // Update the recycler view to show the changes in the updated job lead in that view
            recyclerAdapter.notifyItemChanged(position);

            // Update this job lead in Firebase
            // Note that we are using a specific key (one child in the list)
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String userId = currentUser.getUid();
            DatabaseReference ref = database
                    .getReference()
                    .child("Users")
                    .child(userId)
                    .child("ShoppingBasket")
                    .child(ListItem.getKey());

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain job leads.
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().setValue(ListItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(DEBUG_TAG, "updated job lead at: " + position + "(" + ListItem.getItemName() + ")");
                            Toast.makeText(getApplicationContext(), "Job lead updated for " + ListItem.getItemName(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(DEBUG_TAG, "failed to update job lead at: " + position + "(" + ListItem.getItemName() + ")");
                    Toast.makeText(getApplicationContext(), "Failed to update " + ListItem.getItemName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else if (action == EditListItemDialogFragment.DELETE) {
            Log.d(DEBUG_TAG, "Deleting item at: " + position + "(" + ListItem.getItemName() + ")");

            // remove the deleted job lead from the list (internal list in the App)
            itemsList.remove(position);

            // Update the recycler view to remove the deleted job lead from that view
            recyclerAdapter.notifyItemRemoved(position);

            // Delete this job lead in Firebase.
            // Note that we are using a specific key (one child in the list)
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String userId = currentUser.getUid();
            DatabaseReference ref = database
                    .getReference()
                    .child("Users")
                    .child(userId)
                    .child("ShoppingBasket")
                    .child(ListItem.getKey());

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain job leads.
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(DEBUG_TAG, "deleted list item at: " + position + "(" + ListItem.getItemName() + ")");
                            Toast.makeText(getApplicationContext(), "Item deleted for " + ListItem.getItemName(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(DEBUG_TAG, "failed to delete job lead at: " + position + "(" + ListItem.getItemName() + ")");
                    Toast.makeText(getApplicationContext(), "Failed to delete " + ListItem.getItemName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

//    private void addToBasket(ListItem listItem) {
//        // Add a new element (ListItem) to the list of job leads in Firebase.
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("ShoppingBasket");
//
//        // Remove from shopping list
//        myRef.child("ShoppingList").child(listItem.getKey()).removeValue();
//        // Add to shopping basket
//        myRef.child("ShoppingBasket").child(listItem.getKey()).setValue(listItem);
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (currentUser != null) {
//            // User is logged in, show the shopping basket
//            showShoppingBasket();
//        } else {
//            // User is not logged in, hide the shopping basket
//            hideShoppingBasket();
//        }
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d( DEBUG_TAG, "onResume()" );
//        userRef.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Boolean basketVisible = dataSnapshot.child("basketVisible").getValue(Boolean.class);
//
//                if (basketVisible != null && basketVisible) {
//                    // Basket is visible, show the shopping basket
//                    showShoppingBasket();
//                } else {
//                    // Basket is not visible, hide the shopping basket
//                    hideShoppingBasket();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle errors if necessary
//            }
//        });
//    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d( DEBUG_TAG, "onStart()" );
//        // Check and update basket visibility when the authentication state changes
//        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//                String username = currentUser.getEmail();
//                //userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
//                //Log.d(DEBUG_TAG,"onStart() ", "current user: "+ currentUser);
//                if (username != null) {
//                    // User is logged in, check and update basket visibility
//                    showShoppingBasket();
//                } else {
//                    Log.d(DEBUG_TAG, "Current user: " +String.valueOf(currentUser));
//                    // User is not logged in, hide the shopping basket
//                    hideShoppingBasket();
//                }
//            }
//        });
//    }
//
//
//    private void showShoppingBasket() {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Users");
//
//
//        // Show the shopping basket UI elements
//        //findViewById(R.id.recyclerView2).setVisibility(View.VISIBLE);
//
//        // Update the database to indicate that the basket is visiblem
//        myRef.child("basketVisible").setValue(true);
//    }
//
//    private void hideShoppingBasket() {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Users");
//
//
//        // Hide the shopping basket UI elements
//       // findViewById(R.id.recyclerView2).setVisibility(View.GONE);
//
//        // Update the database to indicate that the basket is not visible
//        myRef.child("basketVisible").setValue(false);
//    }
    private class checkoutListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String username = currentUser.getEmail();
            System.out.println(username);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("purchasedlists");
            for (int i = itemsList.size() - 1; i >= 0; i--) {

                ListItem item = itemsList.get(i);
                item.setUser(username);
                String key = item.getKey();

                myRef.push().setValue( item )
                        .addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure( @NonNull Exception e ) {
                                // do nothing
                            }
                        });


                item.setKey(key);
                updateListItem(i, item, EditListItemDialogFragment.DELETE );

            } // end for



        }
    }
    }