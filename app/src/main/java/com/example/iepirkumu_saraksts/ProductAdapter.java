package com.example.iepirkumu_saraksts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private final OnProductActionListener actionListener;

    public interface OnProductActionListener {
        void onProductChecked(Product product, boolean isChecked);
        void onProductEdit(Product product);
        void onProductDelete(Product product);
    }

    public ProductAdapter(List<Product> products, OnProductActionListener actionListener) {
        this.products = products;
        this.actionListener = actionListener;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.nameText.setText(product.name);
        holder.amountText.setText(holder.itemView.getContext().getString(R.string.daudzums, product.amount));
        holder.categoryText.setText(holder.itemView.getContext().getString(R.string.kategorija, product.category));
        
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(product.isChecked);
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            actionListener.onProductChecked(product, isChecked);
        });

        holder.editButton.setOnClickListener(v -> actionListener.onProductEdit(product));
        holder.deleteButton.setOnClickListener(v -> actionListener.onProductDelete(product));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, amountText, categoryText;
        CheckBox checkBox;
        View editButton, deleteButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.productName);
            amountText = itemView.findViewById(R.id.productAmount);
            categoryText = itemView.findViewById(R.id.productCategory);
            checkBox = itemView.findViewById(R.id.productCheckbox);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
