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
        adapter = new ShoppingListAdapter(shoppingItemList);
        recyclerView.setAdapter(adapter);

        String listId = getIntent().getStringExtra("listId");
        if (listId == null) {
            Toast.makeText(this, "List ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        shoppingListRef = database.getReference("shoppingLists").child(listId).child("items");

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

        addItemFab.setOnClickListener(v -> showAddItemDialog());
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
