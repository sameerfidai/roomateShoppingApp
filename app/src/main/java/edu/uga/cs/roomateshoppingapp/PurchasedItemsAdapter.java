package edu.uga.cs.roomateshoppingapp;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PurchasedItemsAdapter extends RecyclerView.Adapter<PurchasedItemsAdapter.ViewHolder> {
    private List<PurchaseRecord> purchaseRecords;
    private Context context; // Added to access context in the adapter

    public PurchasedItemsAdapter(Context context, List<PurchaseRecord> purchaseRecords) {
        this.context = context;
        this.purchaseRecords = purchaseRecords;
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
        holder.itemNames.setText(String.join(", ", record.getItemNames()));

        holder.totalPrice.setOnClickListener(view -> showUpdatePriceDialog(record));
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
        DatabaseReference recordRef = FirebaseDatabase.getInstance().getReference("recentlyPurchased").child(record.getId()); // Ensure you have a way to get the record's ID
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
        public TextView itemNames; // TextView to display item names

        public ViewHolder(View itemView) {
            super(itemView);
            purchaserName = itemView.findViewById(R.id.purchaserNameTextView);
            totalPrice = itemView.findViewById(R.id.totalPriceTextView);
            itemNames = itemView.findViewById(R.id.itemNamesTextView);
        }
    }
}
