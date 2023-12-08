package edu.uga.cs.roomateshoppingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private List<ShoppingItem> shoppingItemList;
    private OnDeleteClickListener onDeleteClickListener;
    private OnItemClickListener onItemClickListener;
    private DatabaseReference shoppingListRef;


    public ShoppingListAdapter(List<ShoppingItem> shoppingItemList, DatabaseReference shoppingListRef) {
        this.shoppingItemList = shoppingItemList;
        this.shoppingListRef = shoppingListRef;
    }

    public interface OnItemClickListener {
        void onItemClick(ShoppingItem item);
    }

    public interface OnDeleteClickListener {
        void onDeleteClicked(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the current ShoppingItem
        ShoppingItem item = shoppingItemList.get(position);

        // Set the text and checkbox state
        holder.itemNameTextView.setText(item.getName());
        holder.itemPurchasedCheckbox.setChecked(item.isInCart());

        // Remove previous listeners
        holder.itemPurchasedCheckbox.setOnCheckedChangeListener(null);

        // Set the checkbox listener
        holder.itemPurchasedCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the item's inCart status in Firebase
            shoppingListRef.child(item.getId()).child("inCart").setValue(isChecked);
        });
    }


    @Override
    public int getItemCount() {
        return shoppingItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView;
        CheckBox itemPurchasedCheckbox;
        Button deleteItemButton;
        Button updateItemButton;

        public ViewHolder(View itemView) {
            super(itemView);
            deleteItemButton = itemView.findViewById(R.id.deleteItemButton);
            updateItemButton = itemView.findViewById(R.id.updateItemButton);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemPurchasedCheckbox = itemView.findViewById(R.id.itemPurchasedCheckbox);

            deleteItemButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClicked(position);
                }
            });

            updateItemButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    onItemClickListener.onItemClick(shoppingItemList.get(position));
                }
            });
        }
    }
}
