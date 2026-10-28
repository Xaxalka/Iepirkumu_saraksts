package com.example.iepirkumu_saraksts;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameInput, passwordInput, confirmPasswordInput;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = AppDatabase.getInstance(this);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);

        findViewById(R.id.registerButton).setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, R.string.passwords_dont_match, Toast.LENGTH_SHORT).show();
                return;
            }

            User existingUser = db.userDao().getUserByUsername(username);
            if (existingUser != null) {
                Toast.makeText(this, R.string.user_exists, Toast.LENGTH_SHORT).show();
                return;
            }

            User newUser = new User(username, PasswordHasher.hash(password));
            db.userDao().insert(newUser);

            Toast.makeText(this, R.string.registration_success, Toast.LENGTH_SHORT).show();
            finish();
        });

        findViewById(R.id.goToLogin).setOnClickListener(v -> finish());
    }
}
