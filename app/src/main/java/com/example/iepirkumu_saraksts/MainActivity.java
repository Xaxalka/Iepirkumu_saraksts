package com.example.iepirkumu_saraksts;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.google.android.material.snackbar.Snackbar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import com.google.android.material.button.MaterialButton;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductAdapter.OnProductActionListener {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private AppDatabase db;
    private List<Product> allProducts = new ArrayList<>();
    private String currentCategory = "Visi";
    private String currentSearchQuery = "";
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(Color.parseColor("#1B5E20"));
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        emptyText = findViewById(R.id.emptyText);
        recyclerView = findViewById(R.id.itemsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Search Input
        EditText searchInput = findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                filterAndDisplay();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Category Buttons (highlight applied by updateCategory)
        findViewById(R.id.Visi_button).setOnClickListener(v -> updateCategory("Visi"));
        findViewById(R.id.Partika_button).setOnClickListener(v -> updateCategory(getString(R.string.partika)));
        findViewById(R.id.Dzerieni_button).setOnClickListener(v -> updateCategory(getString(R.string.dzerieni)));
        findViewById(R.id.Sadzive_button).setOnClickListener(v -> updateCategory(getString(R.string.sadzive)));
        findViewById(R.id.Citi_button).setOnClickListener(v -> updateCategory(getString(R.string.citi)));

        updateCategory("Visi");

        findViewById(R.id.fabAdd).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            startActivity(intent);
        });


        findViewById(R.id.menuIcon).setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(this, v);
            menu.getMenu().add(0, 1, 0, R.string.palidziba);
            menu.getMenu().add(0, 2, 1, R.string.admin_logout);
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    startActivity(new Intent(this, HelpActivity.class));
                } else {
                    Session.logout(this);
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                return true;
            });
            menu.show();
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int direction) {
                Product product = adapter.getItem(vh.getAdapterPosition());
                removeRow(product);
                Snackbar.make(recyclerView, R.string.product_deleted, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, v -> {
                            db.productDao().insert(product);
                            ReminderScheduler.schedule(MainActivity.this, product);
                            loadData();
                        })
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar bar, int event) {
                                // The photo is only removed once the deletion can no longer be undone
                                if (event != DISMISS_EVENT_ACTION) PhotoStore.delete(product.photoPath);
                            }
                        }).show();
            }
        }).attachToRecyclerView(recyclerView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        try {
            allProducts = db.productDao().getAll();
            if (allProducts == null) {
                allProducts = new ArrayList<>();
            }
            filterAndDisplay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCategory(String category) {
        currentCategory = category;
        int[] ids = {R.id.Visi_button, R.id.Partika_button, R.id.Dzerieni_button,
                R.id.Sadzive_button, R.id.Citi_button};
        String[] names = {"Visi", getString(R.string.partika), getString(R.string.dzerieni),
                getString(R.string.sadzive), getString(R.string.citi)};
        for (int i = 0; i < ids.length; i++) {
            MaterialButton b = findViewById(ids[i]);
            boolean selected = names[i].equals(category);
            b.setBackgroundTintList(ColorStateList.valueOf(selected
                    ? ContextCompat.getColor(this, R.color.secondary_green) : Color.WHITE));
            b.setTextColor(selected ? Color.WHITE : Color.BLACK);
        }
        filterAndDisplay();
    }

    private void filterAndDisplay() {
        List<Product> filteredList = new ArrayList<>();
        if (allProducts != null) {
            for (Product product : allProducts) {
                if (product == null || product.name == null || product.category == null) continue;
                
                boolean matchesCategory = currentCategory.equals("Visi") || product.category.equals(currentCategory);
                boolean matchesSearch = product.name.toLowerCase().contains(currentSearchQuery.toLowerCase());
                
                if (matchesCategory && matchesSearch) {
                    filteredList.add(product);
                }
            }
        }
        // Bought items sink to the bottom; the sort is stable so newest stay first.
        Collections.sort(filteredList, (a, b) -> Boolean.compare(a.isChecked, b.isChecked));
        adapter.setProducts(filteredList);
        emptyText.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onProductChecked(Product product, boolean isChecked) {
        product.isChecked = isChecked;
        db.productDao().update(product);
        ReminderScheduler.schedule(this, product);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onProductEdit(Product product) {
        Intent intent = new Intent(this, AddProductActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    @Override
    public void onProductDelete(Product product) {
        removeRow(product);
        PhotoStore.delete(product.photoPath);
    }

    private void removeRow(Product product) {
        ReminderScheduler.cancel(this, product);
        db.productDao().delete(product);
        loadData();
    }
}
