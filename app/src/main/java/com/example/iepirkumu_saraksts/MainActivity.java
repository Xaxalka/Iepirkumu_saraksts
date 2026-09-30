package com.example.iepirkumu_saraksts;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductAdapter.OnProductActionListener {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private AppDatabase db;
    private List<Product> allProducts = new ArrayList<>();
    private String currentCategory = "Visi";
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(Color.parseColor("#1B5E20"));
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

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

        // Category Buttons
        findViewById(R.id.Visi_button).setOnClickListener(v -> updateCategory("Visi"));
        findViewById(R.id.Partika_button).setOnClickListener(v -> updateCategory(getString(R.string.partika)));
        findViewById(R.id.Dzerieni_button).setOnClickListener(v -> updateCategory(getString(R.string.dzerieni)));
        findViewById(R.id.Sadzive_button).setOnClickListener(v -> updateCategory(getString(R.string.sadzive)));
        findViewById(R.id.Citi_button).setOnClickListener(v -> updateCategory(getString(R.string.citi)));

        findViewById(R.id.fabAdd).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            startActivity(intent);
        });


        findViewById(R.id.menuIcon).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        });

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
            Toast.makeText(this, "Loaded: " + allProducts.size(), Toast.LENGTH_SHORT).show();
            filterAndDisplay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCategory(String category) {
        currentCategory = category;
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
        adapter.setProducts(filteredList);
    }

    @Override
    public void onProductChecked(Product product, boolean isChecked) {
        product.isChecked = isChecked;
        db.productDao().update(product);
    }

    @Override
    public void onProductEdit(Product product) {
        Intent intent = new Intent(this, AddProductActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    @Override
    public void onProductDelete(Product product) {
        db.productDao().delete(product);
        loadData();
    }
}
