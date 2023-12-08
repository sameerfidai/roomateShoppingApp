package edu.uga.cs.roomateshoppingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

public class ShoppingListActivity extends AppCompatActivity {

    private List<ShoppingItem> shoppingItemList;
    private ShoppingListAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton addItemFab;
    private DatabaseReference shoppingListRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        recyclerView = findViewById(R.id.shoppingListRecyclerView);
        addItemFab = findViewById(R.id.addItemFab);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        shoppingItemList = new ArrayList<>();

        String listId = getIntent().getStringExtra("listId");
        if (listId == null) {
            Toast.makeText(this, "List ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        shoppingListRef = database.getReference("shoppingLists").child(listId).child("items");

        adapter = new ShoppingListAdapter(shoppingItemList, shoppingListRef);
        recyclerView.setAdapter(adapter);

        shoppingListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                shoppingItemList.clear();
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    ShoppingItem item = itemSnapshot.getValue(ShoppingItem.class);
                    shoppingItemList.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ShoppingListActivity", "Database error: " + databaseError.getMessage());
            }
        });

        // on click for add to list button
        addItemFab.setOnClickListener(v -> showAddItemDialog());

        // on click for updating an item
        adapter.setOnItemClickListener(item -> showUpdateItemDialog(item));

        // on click for deleting an item
        adapter.setOnDeleteClickListener(position -> {
            ShoppingItem item = shoppingItemList.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteItem(item))
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void showUpdateItemDialog(ShoppingItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Item");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(item.getName());
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String updatedName = input.getText().toString().trim();
            if (!updatedName.isEmpty()) {
                updateItem(item.getId(), updatedName);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateItem(String itemId, String updatedName) {
        shoppingListRef.child(itemId).child("name").setValue(updatedName).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ShoppingListActivity.this, "Item updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ShoppingListActivity.this, "Failed to update item: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteItem(ShoppingItem item) {
        if (item != null && item.getId() != null) {
            Log.d("ShoppingListActivity", "Deleting item: " + item.getId());

            shoppingListRef.child(item.getId()).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ShoppingListActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShoppingListActivity.this, "Failed to delete item: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ShoppingListActivity.this, "Error: Item or Item ID is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Item");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String itemName = input.getText().toString().trim();
                if (!itemName.isEmpty()) {
                    addItemToList(itemName);
                } else {
                    Toast.makeText(ShoppingListActivity.this, "Item name cannot be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addItemToList(String itemName) {
        String itemId = shoppingListRef.push().getKey();
        if (itemId != null) {
            ShoppingItem newItem = new ShoppingItem(itemId, itemName, false, ""); // Passing "" for purchaserId
            shoppingListRef.child(itemId).setValue(newItem).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ShoppingListActivity.this, "Item added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShoppingListActivity.this, "Failed to add item: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ShoppingListActivity.this, "Error creating item", Toast.LENGTH_SHORT).show();
        }
    }
}
