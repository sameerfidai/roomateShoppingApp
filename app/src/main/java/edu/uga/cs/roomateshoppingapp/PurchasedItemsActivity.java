package edu.uga.cs.roomateshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PurchasedItemsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PurchasedItemsAdapter adapter;
    private DatabaseReference recentlyPurchasedRef;
    private List<PurchaseRecord> purchasedRecordsList = new ArrayList<>(); // List of PurchaseRecords
    private String listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased_items);

        listId = getIntent().getStringExtra("listId");
        if (listId == null) {
            Toast.makeText(this, "List ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.purchasedItemsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PurchasedItemsAdapter(this, purchasedRecordsList, listId);
        recyclerView.setAdapter(adapter);

        // Replace with the correct reference to your Firebase database
        recentlyPurchasedRef = FirebaseDatabase.getInstance().getReference("recentlyPurchased");

        loadPurchasedItems();
    }

    private void loadPurchasedItems() {
        recentlyPurchasedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                purchasedRecordsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PurchaseRecord record = snapshot.getValue(PurchaseRecord.class);
                    if (record != null) {
                        purchasedRecordsList.add(record);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PurchasedItemsActivity", "Database error: " + databaseError.getMessage());
                Toast.makeText(PurchasedItemsActivity.this, "Failed to load purchased records.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
