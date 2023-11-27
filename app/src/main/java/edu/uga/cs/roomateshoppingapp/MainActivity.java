package edu.uga.cs.roomateshoppingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Button logoutButton, viewShoppingListButton, createListButton, joinListButton, viewCartButton;
    private TextView userDetails;

    // References to Firebase Database nodes
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private DatabaseReference shoppingListsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth and Database references
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        shoppingListsRef = database.getReference("shoppingLists");

        // Initialize UI elements
        user = mAuth.getCurrentUser();
        logoutButton = findViewById(R.id.logoutBtn);
        userDetails = findViewById(R.id.user_details);
        viewShoppingListButton = findViewById(R.id.viewListBtn);
        viewCartButton = findViewById(R.id.viewCartBtn);
        createListButton = findViewById(R.id.createListBtn);
        joinListButton = findViewById(R.id.joinListBtn);

        // Initially hide both buttons until we know the user's list status
        createListButton.setVisibility(View.GONE);
        joinListButton.setVisibility(View.GONE);

        // Check if the user is logged in and display their details
        if (user == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            userDetails.setText(user.getEmail());
            // Check if the user already has a list
            usersRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User currentUser = dataSnapshot.getValue(User.class);
                    if (currentUser != null && currentUser.getListId() == null) {
                        // User does not have a list, show both buttons
                        createListButton.setVisibility(View.VISIBLE);
                        joinListButton.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                }
            });

            // Log the user out
            logoutButton.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });

            viewCartButton.setOnClickListener(v -> {
                usersRef.child(user.getUid()).child("listId").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String listId = dataSnapshot.getValue(String.class);
                        if (listId != null) {
                            Intent intent = new Intent(MainActivity.this, ShoppingCartActivity.class);
                            intent.putExtra("listId", listId);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "No shopping list found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });


            // View the shopping list
            viewShoppingListButton.setOnClickListener(v -> {
                if (user != null) {
                    usersRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User currentUser = dataSnapshot.getValue(User.class);
                            if (currentUser != null && currentUser.getListId() != null) {
                                Intent intent = new Intent(MainActivity.this, ShoppingListActivity.class);
                                intent.putExtra("listId", currentUser.getListId());
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "No shopping list found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(MainActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Handle case where user is null
                    Toast.makeText(MainActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                }
            });


            // Logic to handle creating a new shopping list
            createListButton.setOnClickListener(v -> {
                createNewShoppingList(user.getUid());
            });

            // Logic to handle joining an existing shopping list
            joinListButton.setOnClickListener(v -> {
                showJoinListDialog();
            });
        }
    }

    private void createNewShoppingList(String userId) {
        String listId = shoppingListsRef.push().getKey();
        if (listId != null) {
            String inviteCode = generateInviteCode();
            ShoppingList newShoppingList = new ShoppingList(listId, userId, inviteCode);
            shoppingListsRef.child(listId).setValue(newShoppingList).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    usersRef.child(userId).child("listId").setValue(listId);
                    Toast.makeText(MainActivity.this, "New Shopping List Created", Toast.LENGTH_SHORT).show();

                    // Redirect to the ShoppingListActivity with the listId
                    Intent intent = new Intent(MainActivity.this, ShoppingListActivity.class);
                    intent.putExtra("listId", listId); // Passing the listId to the ShoppingListActivity
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to create list: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Error creating list", Toast.LENGTH_SHORT).show();
        }
    }

    // Dialog to input invite code and attempt to join a list
    private void showJoinListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Join a Shopping List");

        // Set up the input field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inviteCode = input.getText().toString();
                joinShoppingList(user.getUid(), inviteCode);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }


    private void joinShoppingList(String userId, String inviteCode) {
        shoppingListsRef.orderByChild("inviteCode").equalTo(inviteCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String listId = snapshot.getKey();
                        usersRef.child(userId).child("listId").setValue(listId);

                        Toast.makeText(MainActivity.this, "Joined Shopping List", Toast.LENGTH_SHORT).show();

                        // Redirect to the ShoppingListActivity with the listId
                        Intent intent = new Intent(MainActivity.this, ShoppingListActivity.class);
                        intent.putExtra("listId", listId); // Passing the listId to the ShoppingListActivity
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Invalid Invite Code", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
