package com.example.matala3.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.matala3.R;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Product> cartList;
    private OnItemClickListener itemClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION; // Track selected item

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    // Constructor with item click listener
    public CartAdapter(List<Product> cartList, OnItemClickListener listener) {
        this.cartList = cartList;
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartList.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText("Price: $" + product.getPrice());
        holder.tvProductQuantity.setText("x" + product.getQuantity());

        // Highlight selected item
        holder.itemView.setBackgroundColor(position == selectedPosition ? 0xFFE0E0E0 : 0xFFFFFFFF);

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
            if (itemClickListener != null) {
                itemClickListener.onItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList != null ? cartList.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
        }
    }

    // Method to update the cart list dynamically
    public void updateCartList(List<Product> newCartList) {
        if (newCartList != null) {
            this.cartList = newCartList;
            notifyDataSetChanged();
        }
    }
}
