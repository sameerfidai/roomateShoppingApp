package edu.uga.cs.roomateshoppingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class PurchasedItemsAdapter extends RecyclerView.Adapter<PurchasedItemsAdapter.ViewHolder> {
    private List<PurchaseRecord> purchaseRecords;
    private Context context;
    private String listId;

    public PurchasedItemsAdapter(Context context, List<PurchaseRecord> purchaseRecords, String listId) {
        this.context = context;
        this.purchaseRecords = purchaseRecords;
        this.listId = listId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchased_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PurchaseRecord record = purchaseRecords.get(position);
        holder.purchaserName.setText(record.getPurchaserName());
        holder.totalPrice.setText(String.format("$%.2f", record.getTotalPrice()));

        StringBuilder itemDetailsDisplay = new StringBuilder();
        for (Map.Entry<String, Double> entry : record.getItemDetails().entrySet()) {
            itemDetailsDisplay.append(entry.getKey()).append(": $").append(String.format("%.2f", entry.getValue())).append("\n");
        }
        holder.itemNames.setText(itemDetailsDisplay.toString().trim());

        holder.totalPrice.setOnClickListener(view -> showUpdatePriceDialog(record));
        holder.removeItemButton.setOnClickListener(view -> showRemoveItemDialog(record));
    }

    private void showRemoveItemDialog(PurchaseRecord record) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select an item to remove");

        String[] items = record.getItemDetails().keySet().toArray(new String[0]);
        builder.setItems(items, (dialog, which) -> {
            String selectedItem = items[which];
            removeItemFromPurchase(record, selectedItem);
        });

        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeItemFromPurchase(PurchaseRecord record, String itemName) {
        if (record.getItemDetails().containsKey(itemName)) {
            double itemPrice = record.getItemDetails().get(itemName);
            record.setTotalPrice(record.getTotalPrice() - itemPrice);
            record.getItemDetails().remove(itemName);

            if (record.getItemDetails().isEmpty()) {
                deletePurchaseRecord(record);
            } else {
                DatabaseReference recordRef = FirebaseDatabase.getInstance().getReference("recentlyPurchased").child(record.getId());
                recordRef.setValue(record);
            }
            addItemBackToCart(itemName);
        } else {
            Toast.makeText(context, "Item not found", Toast.LENGTH_SHORT).show();
        }
        notifyDataSetChanged();
    }

    private void deletePurchaseRecord(PurchaseRecord record) {
        DatabaseReference recordRef = FirebaseDatabase.getInstance().getReference("recentlyPurchased").child(record.getId());
        recordRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Purchase record deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete purchase record", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addItemBackToCart(String itemName) {
        DatabaseReference shoppingListRef = FirebaseDatabase.getInstance().getReference("shoppingLists").child(listId).child("items");
        shoppingListRef.orderByChild("name").equalTo(itemName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    ShoppingItem item = childSnapshot.getValue(ShoppingItem.class);
                    if (item != null) {
                        item.setInCart(true);
                        shoppingListRef.child(childSnapshot.getKey()).setValue(item);
                        Toast.makeText(context, "Item added back to cart", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PurchasedItemsAdapter", "Failed to add item back to cart", databaseError.toException());
            }
        });
    }

    private void showUpdatePriceDialog(PurchaseRecord record) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Total Price");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            try {
                double newPrice = Double.parseDouble(input.getText().toString());
                record.setTotalPrice(newPrice);
                updatePurchaseRecordInFirebase(record);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid price", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updatePurchaseRecordInFirebase(PurchaseRecord record) {
        DatabaseReference recordRef = FirebaseDatabase.getInstance().getReference("recentlyPurchased").child(record.getId());
        recordRef.setValue(record).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                notifyDataSetChanged();
                Toast.makeText(context, "Price updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to update price", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return purchaseRecords.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView purchaserName;
        public TextView totalPrice;
        public TextView itemNames;
        public Button removeItemButton;

        public ViewHolder(View itemView) {
            super(itemView);
            purchaserName = itemView.findViewById(R.id.purchaserNameTextView);
            totalPrice = itemView.findViewById(R.id.totalPriceTextView);
            itemNames = itemView.findViewById(R.id.itemNamesTextView);
            removeItemButton = itemView.findViewById(R.id.removeItemButton);
        }
    }
}
