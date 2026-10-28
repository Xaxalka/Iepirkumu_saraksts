package com.example.iepirkumu_saraksts;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.content.ActivityNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.text.TextWatcher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
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
    private long reminderTime = 0;
    private boolean categoryChosenByUser = false;
    private int autoSelectedPosition = 0;
    private String photoPath;
    private String originalPhotoPath;
    private boolean saved = false;
    private Uri cameraUri;
    private ImageView photoPreview;
    private View photoPlaceholder, photoRemoveButton;

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onPhotoPicked);
    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), ok -> {
                if (ok) onPhotoPicked(cameraUri);
            });
    private TextView reminderText, reminderAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = AppDatabase.getInstance(this);

        nameInput = findViewById(R.id.productNameInput);
        amountInput = findViewById(R.id.productAmountInput);
        categorySpinner = findViewById(R.id.categorySpinner);

        photoPreview = findViewById(R.id.photoPreview);
        photoPlaceholder = findViewById(R.id.photoPlaceholder);
        photoRemoveButton = findViewById(R.id.photoRemoveButton);
        findViewById(R.id.photoGalleryButton).setOnClickListener(v -> galleryLauncher.launch("image/*"));
        findViewById(R.id.photoCameraButton).setOnClickListener(v -> launchCamera());
        photoRemoveButton.setOnClickListener(v -> setPhoto(null));

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

        reminderText = findViewById(R.id.reminderText);
        reminderAction = findViewById(R.id.reminderAction);
        findViewById(R.id.reminderPicker).setOnClickListener(v -> pickReminder());
        reminderAction.setOnClickListener(v -> {
            if (reminderTime != 0) {
                reminderTime = 0;
                updateReminderUi();
            } else {
                pickReminder();
            }
        });

        // Auto-pick the category for well-known products until the user picks one themselves
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Any selection we did not make ourselves means the user chose a category
                if (position != autoSelectedPosition) categoryChosenByUser = true;
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        nameInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (categoryChosenByUser) return;
                String guess = ProductIcons.categoryFor(s.toString());
                if (guess == null) return;
                for (int i = 0; i < categories.length; i++) {
                    if (categories[i].equals(guess)) {
                        autoSelectedPosition = i;
                        categorySpinner.setSelection(i);
                        break;
                    }
                }
            }
        });

        // Check  editing
        existingProduct = (Product) getIntent().getSerializableExtra("product");
        if (existingProduct != null) {
            ((TextView) findViewById(R.id.addTitle)).setText(R.string.rediget);
            originalPhotoPath = existingProduct.photoPath;
            photoPath = existingProduct.photoPath;
            categoryChosenByUser = true;
            nameInput.setText(existingProduct.name);
            reminderTime = existingProduct.reminderTime;
            amountInput.setText(existingProduct.amount);
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(existingProduct.category)) {
                    categorySpinner.setSelection(i);
                    break;
                }
            }
        }

        updateReminderUi();
        updatePhotoUi();

        // Save button functionality
        findViewById(R.id.saveButton).setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String amount = amountInput.getText().toString().trim();
            String category = categorySpinner.getSelectedItem().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, R.string.enter_name, Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount.isEmpty()) {
                Toast.makeText(this, R.string.enter_amount, Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Integer.parseInt(amount);
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.amount_must_be_number, Toast.LENGTH_SHORT).show();
                return;
            }

            if (existingProduct == null) {
                Product newProduct = new Product(name, amount, category);
                newProduct.reminderTime = reminderTime;
                newProduct.photoPath = photoPath;
                newProduct.id = (int) db.productDao().insert(newProduct);
                ReminderScheduler.schedule(this, newProduct);
            } else {
                existingProduct.name = name;
                existingProduct.amount = amount;
                existingProduct.category = category;
                existingProduct.reminderTime = reminderTime;
                existingProduct.photoPath = photoPath;
                db.productDao().update(existingProduct);
                ReminderScheduler.schedule(this, existingProduct);
            }
            saved = true;
            if (originalPhotoPath != null && !originalPhotoPath.equals(photoPath)) {
                PhotoStore.delete(originalPhotoPath);
            }
            finish();
        });
    }

    private void pickReminder() {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
        Calendar cal = Calendar.getInstance();
        if (reminderTime > 0) cal.setTimeInMillis(reminderTime);
        new DatePickerDialog(this, (view, year, month, day) ->
                new TimePickerDialog(this, (tv, hour, minute) -> {
                    Calendar picked = Calendar.getInstance();
                    picked.set(year, month, day, hour, minute, 0);
                    picked.set(Calendar.MILLISECOND, 0);
                    if (picked.getTimeInMillis() <= System.currentTimeMillis()) {
                        Toast.makeText(this, R.string.reminder_past, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    reminderTime = picked.getTimeInMillis();
                    updateReminderUi();
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show(),
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateReminderUi() {
        if (reminderTime > 0) {
            reminderText.setText(new SimpleDateFormat(getString(R.string.reminder_format),
                    Locale.getDefault()).format(reminderTime));
            reminderText.setTextColor(ContextCompat.getColor(this, R.color.black));
            reminderAction.setText("✕");
            reminderAction.setContentDescription(getString(R.string.reminder_clear));
        } else {
            reminderText.setText(R.string.izveleties_laiku);
            reminderText.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
            reminderAction.setText(R.string.chevron_right);
        }
    }

    private void launchCamera() {
        try {
            File dir = new File(getCacheDir(), "camera");
            if (!dir.exists() && !dir.mkdirs()) throw new IOException();
            File file = File.createTempFile("shot", ".jpg", dir);
            cameraUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            cameraLauncher.launch(cameraUri);
        } catch (IOException | ActivityNotFoundException e) {
            Toast.makeText(this, R.string.photo_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    private void onPhotoPicked(Uri uri) {
        if (uri == null) return;
        String path = PhotoStore.save(this, uri);
        if (path == null) {
            Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT).show();
            return;
        }
        setPhoto(path);
    }

    private void setPhoto(String path) {
        // A photo picked in this session but never saved is not needed any more
        if (photoPath != null && !photoPath.equals(originalPhotoPath)) PhotoStore.delete(photoPath);
        photoPath = path;
        updatePhotoUi();
    }

    private void updatePhotoUi() {
        Bitmap bitmap = PhotoStore.load(photoPath);
        photoPreview.setImageBitmap(bitmap);
        photoPreview.setVisibility(bitmap != null ? View.VISIBLE : View.GONE);
        photoPlaceholder.setVisibility(bitmap != null ? View.GONE : View.VISIBLE);
        photoRemoveButton.setVisibility(bitmap != null ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!saved && photoPath != null && !photoPath.equals(originalPhotoPath) && isFinishing()) {
            PhotoStore.delete(photoPath);
        }
    }
}
