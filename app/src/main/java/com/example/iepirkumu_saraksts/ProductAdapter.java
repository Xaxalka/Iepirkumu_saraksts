package com.example.iepirkumu_saraksts;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.util.LruCache;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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

    private final LruCache<String, Bitmap> photoCache = new LruCache<>(30);

    private Bitmap photoFor(String path) {
        Bitmap cached = photoCache.get(path);
        if (cached == null) {
            cached = PhotoStore.load(path);
            if (cached != null) photoCache.put(path, cached);
        }
        return cached;
    }

    public Product getItem(int position) {
        return products.get(position);
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
        holder.imageText.setText(ProductIcons.forProduct(product));
        Bitmap photo = product.photoPath == null ? null : photoFor(product.photoPath);
        holder.photoView.setClipToOutline(true);
        holder.photoView.setImageBitmap(photo);
        holder.photoView.setVisibility(photo != null ? View.VISIBLE : View.GONE);
        holder.imageText.setVisibility(photo != null ? View.INVISIBLE : View.VISIBLE);
        holder.amountText.setText(holder.itemView.getContext().getString(R.string.daudzums, product.amount));
        holder.categoryText.setText(holder.itemView.getContext().getString(R.string.kategorija, product.category));
        
        if (product.reminderTime > 0) {
            holder.reminderText.setVisibility(View.VISIBLE);
            holder.reminderText.setText("⏰ " + new SimpleDateFormat(
                    holder.itemView.getContext().getString(R.string.reminder_format),
                    Locale.getDefault()).format(product.reminderTime));
        } else {
            holder.reminderText.setVisibility(View.GONE);
        }
        if (product.isChecked) {
            holder.nameText.setPaintFlags(holder.nameText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.nameText.setPaintFlags(holder.nameText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        holder.itemView.setAlpha(product.isChecked ? 0.5f : 1f);

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
        TextView nameText, amountText, categoryText, imageText, reminderText;
        ImageView photoView;
        CheckBox checkBox;
        View editButton, deleteButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageText = itemView.findViewById(R.id.productImage);
            photoView = itemView.findViewById(R.id.productPhoto);
            reminderText = itemView.findViewById(R.id.productReminder);
            nameText = itemView.findViewById(R.id.productName);
            amountText = itemView.findViewById(R.id.productAmount);
            categoryText = itemView.findViewById(R.id.productCategory);
            checkBox = itemView.findViewById(R.id.productCheckbox);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
