package com.example.iepirkumu_saraksts;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddProductActivity extends AppCompatActivity {
    private EditText nameInput, amountInput;
    private Spinner categorySpinner;
    private Product existingProduct;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = AppDatabase.getInstance(this);

        nameInput = findViewById(R.id.productNameInput);
        amountInput = findViewById(R.id.productAmountInput);
        categorySpinner = findViewById(R.id.categorySpinner);

        // Back button functionality
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Category Spinner setup
        String[] categories = {
            getString(R.string.partika),
            getString(R.string.dzerieni),
            getString(R.string.sadzive),
            getString(R.string.citi)
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Check  editing
        existingProduct = (Product) getIntent().getSerializableExtra("product");
        if (existingProduct != null) {
            ((TextView) findViewById(R.id.addTitle)).setText(R.string.rediget);
            nameInput.setText(existingProduct.name);
            amountInput.setText(existingProduct.amount);
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(existingProduct.category)) {
                    categorySpinner.setSelection(i);
                    break;
                }
            }
        }

        // Save button functionality
        findViewById(R.id.saveButton).setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String amount = amountInput.getText().toString().trim();
            String category = categorySpinner.getSelectedItem().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, "Lūdzu ievadiet nosaukumu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount.isEmpty()) {
                Toast.makeText(this, "Lūdzu ievadiet daudzumu", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Integer.parseInt(amount);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Daudzumam jābūt skaitlim", Toast.LENGTH_SHORT).show();
                return;
            }

            if (existingProduct == null) {
                Product newProduct = new Product(name, amount, category);
                db.productDao().insert(newProduct);
            } else {
                existingProduct.name = name;
                existingProduct.amount = amount;
                existingProduct.category = category;
                db.productDao().update(existingProduct);
            }
            finish();
        });
    }
}
