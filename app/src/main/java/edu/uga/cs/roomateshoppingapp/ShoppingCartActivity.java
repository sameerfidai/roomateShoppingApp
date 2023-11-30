package edu.uga.cs.roomateshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShoppingCartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ShoppingCartAdapter adapter;
    private DatabaseReference shoppingListRef, recentlyPurchasedRef;
    private List<ShoppingItem> shoppingCartItemList = new ArrayList<>();
    private String listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        Button btnCheckout = findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(v -> checkoutItems());

        recyclerView = findViewById(R.id.recyclerViewShoppingCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the listId from the intent
        listId = getIntent().getStringExtra("listId");
        if (listId == null) {
            Toast.makeText(this, "List ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        shoppingListRef = FirebaseDatabase.getInstance().getReference("shoppingLists").child(listId).child("items");
        recentlyPurchasedRef = FirebaseDatabase.getInstance().getReference("recentlyPurchased").push(); // Create a new node for this purchase
        adapter = new ShoppingCartAdapter(shoppingCartItemList, shoppingListRef);
        recyclerView.setAdapter(adapter);

        loadShoppingCartItems();
    }

    private void loadShoppingCartItems() {
        shoppingListRef.orderByChild("inCart").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                shoppingCartItemList.clear();
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    ShoppingItem item = itemSnapshot.getValue(ShoppingItem.class);
                    if (item != null && item.isInCart()) {
                        shoppingCartItemList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ShoppingCartActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void checkoutItems() {
        final double[] totalPrice = {0};
        List<String> itemNames = new ArrayList<>();
        String purchaserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(purchaserId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null) {
                    String purchaserName = currentUser.getName();

                    for (ShoppingItem item : shoppingCartItemList) {
                        if (item.isInCart()) {
                            totalPrice[0] += item.getPrice();
                            itemNames.add(item.getName()); // Collect names of all purchased items

                            // Update the item's status in the shopping list
                            item.setInCart(false);
                            shoppingListRef.child(item.getId()).setValue(item);
                        }
                    }

                    // Create a single purchase record for all items
                    DatabaseReference purchaseRef = FirebaseDatabase.getInstance().getReference("recentlyPurchased").push();
                    PurchaseRecord purchaseRecord = new PurchaseRecord(totalPrice[0], purchaserName, itemNames);
                    String purchaseRecordId = purchaseRef.getKey(); // Get the Firebase-generated key
                    purchaseRecord.setId(purchaseRecordId); // Set the ID of the PurchaseRecord
                    purchaseRef.setValue(purchaseRecord);

                    // Show checkout confirmation
                    showCheckoutConfirmation(totalPrice[0], purchaserName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void showCheckoutConfirmation(double totalPrice, String purchaserId) {
        // Format the price to two decimal places
        String formattedPrice = String.format(Locale.getDefault(), "$%.2f", totalPrice);

        // Use an AlertDialog for confirmation
        new AlertDialog.Builder(this)
                .setTitle("Checkout Complete")
                .setMessage("Total Price: " + formattedPrice)
                .setPositiveButton("Go to Main", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Go to MainActivity
                        Intent intent = new Intent(ShoppingCartActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("View Purchases", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Go to PurchasedItemsActivity
                        Intent intent = new Intent(ShoppingCartActivity.this, PurchasedItemsActivity.class);
                        startActivity(intent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}
