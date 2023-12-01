package edu.uga.cs.roomateshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Button settleCostsButton = findViewById(R.id.settleCostsButton);
        settleCostsButton.setOnClickListener(v -> settleCosts());

        loadPurchasedItems();
    }

    private void settleCosts() {
        // Step 1: Calculate money spent by each roommate
        Map<String, Double> amountSpentByEachRoommate = new HashMap<>();
        for (PurchaseRecord record : purchasedRecordsList) {
            String purchaser = record.getPurchaserName();
            amountSpentByEachRoommate.put(purchaser, amountSpentByEachRoommate.getOrDefault(purchaser, 0.0) + record.getTotalPrice());
        }

        // Step 2: Calculate the average amount spent
        double totalSpent = 0;
        for (double amount : amountSpentByEachRoommate.values()) {
            totalSpent += amount;
        }
        double averageSpent = totalSpent / amountSpentByEachRoommate.size();

        // Step 3: Display the calculated amounts
        displaySettlementDetails(amountSpentByEachRoommate, averageSpent);

        // Step 4: Clear the purchased items list
        clearPurchasedItems();
    }

    private void displaySettlementDetails(Map<String, Double> amountSpentByEachRoommate, double averageSpent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Settlement Details");

        StringBuilder message = new StringBuilder();
        for (Map.Entry<String, Double> entry : amountSpentByEachRoommate.entrySet()) {
            message.append(entry.getKey()).append(" spent: $").append(String.format("%.2f", entry.getValue())).append("\n");
        }
        message.append("\nTotal Spent: $").append(String.format("%.2f", amountSpentByEachRoommate.values().stream().mapToDouble(Double::doubleValue).sum()));
        message.append("\nAverage Spent: $").append(String.format("%.2f", averageSpent));

        builder.setMessage(message.toString());

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void clearPurchasedItems() {
        // Delete all items from Firebase
        recentlyPurchasedRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(PurchasedItemsActivity.this, "All purchased items have been cleared.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PurchasedItemsActivity.this, "Failed to clear purchased items.", Toast.LENGTH_SHORT).show();
            }
        });

        // Clear the local list and update the adapter
        purchasedRecordsList.clear();
        adapter.notifyDataSetChanged();
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
