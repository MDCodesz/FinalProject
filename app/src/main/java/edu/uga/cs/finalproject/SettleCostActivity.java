package edu.uga.cs.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class SettleCostActivity extends AppCompatActivity {

    private TextView userOne;
    private TextView userTwo;
    private TextView totalCost;
    private TextView avgCost;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_cost);

        userOne = findViewById(R.id.userName1);
        userTwo = findViewById(R.id.userName2);
        totalCost = findViewById(R.id.totalCost);
        avgCost = findViewById(R.id.avgCost);

        database = FirebaseDatabase.getInstance();
        DatabaseReference purchasedListsRef = database.getReference()
                .child("purchasedlists");

        purchasedListsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    // Use 'key' as needed (e.g., store it in a list, print it, etc.)
                    Log.d("Key", key);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors while fetching data
            }
        });



    }
}