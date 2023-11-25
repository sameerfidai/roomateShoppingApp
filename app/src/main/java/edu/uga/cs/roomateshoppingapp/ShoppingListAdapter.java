package edu.uga.cs.roomateshoppingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private List<ShoppingItem> shoppingItemList;
    private OnDeleteClickListener onDeleteClickListener;

    public ShoppingListAdapter(List<ShoppingItem> shoppingItemList) {
        this.shoppingItemList = shoppingItemList;
    }

    public interface OnDeleteClickListener {
        void onDeleteClicked(int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public ShoppingListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListAdapter.ViewHolder holder, int position) {
        ShoppingItem item = shoppingItemList.get(position);
        holder.itemNameTextView.setText(item.getName());
        holder.itemPurchasedCheckbox.setChecked(item.isPurchased());
    }

    @Override
    public int getItemCount() {
        return shoppingItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView;
        CheckBox itemPurchasedCheckbox;
        Button deleteItemButton;

        public ViewHolder(View itemView) {
            super(itemView);
            deleteItemButton = itemView.findViewById(R.id.deleteItemButton);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemPurchasedCheckbox = itemView.findViewById(R.id.itemPurchasedCheckbox);

            deleteItemButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClicked(position);
                }
            });
        }
    }
}
