package edu.uga.cs.roomateshoppingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PurchasedItemsAdapter extends RecyclerView.Adapter<PurchasedItemsAdapter.ViewHolder> {
    private List<PurchaseRecord> purchaseRecords;

    public PurchasedItemsAdapter(List<PurchaseRecord> purchaseRecords) {
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
