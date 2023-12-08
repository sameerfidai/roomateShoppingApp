package edu.uga.cs.roomateshoppingapp;

import android.content.DialogInterface;
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

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {

    private List<ShoppingItem> cartItems;
    private DatabaseReference shoppingListRef;

    public ShoppingCartAdapter(List<ShoppingItem> cartItems, DatabaseReference shoppingListRef) {
        this.cartItems = cartItems;
        this.shoppingListRef = shoppingListRef;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingItem item = cartItems.get(position);
        holder.itemNameTextView.setText(item.getName());

        if (item.getPrice() == 0) {
            holder.itemPriceTextView.setText("Set Price");
        } else {
            holder.itemPriceTextView.setText(String.format("$%.2f", item.getPrice()));
        }
        holder.itemPriceTextView.setOnClickListener(view -> showPriceDialog(holder, item, position));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView, itemPriceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemPriceTextView = itemView.findViewById(R.id.itemPriceTextView);
        }
    }

    private void showPriceDialog(ViewHolder holder, ShoppingItem item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setTitle("Set Price");

        final EditText input = new EditText(holder.itemView.getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setPositiveButton("Set", (dialog, which) -> {
            try {
                double price = Double.parseDouble(input.getText().toString());
                item.setPrice(price);
                shoppingListRef.child(item.getId()).child("price").setValue(price);
                notifyItemChanged(position);
            } catch (NumberFormatException e) {
                Toast.makeText(holder.itemView.getContext(), "Invalid price", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
