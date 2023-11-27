package edu.uga.cs.roomateshoppingapp;

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

public class ShoppingCartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ShoppingCartAdapter adapter;
    private DatabaseReference shoppingListRef;
    private List<ShoppingItem> shoppingCartItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        recyclerView = findViewById(R.id.recyclerViewShoppingCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the listId from the intent
        String listId = getIntent().getStringExtra("listId");
        if (listId == null) {
            Toast.makeText(this, "List ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Replace with the correct reference to your Firebase database
        shoppingListRef = FirebaseDatabase.getInstance().getReference("shoppingLists").child(listId).child("items");
        adapter = new ShoppingCartAdapter(shoppingCartItemList, shoppingListRef);
        recyclerView.setAdapter(adapter);

        // Query the database for items where inCart is true
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
}

