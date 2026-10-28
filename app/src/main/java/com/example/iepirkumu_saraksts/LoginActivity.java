package com.example.iepirkumu_saraksts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameInput, passwordInput;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getInstance(this);

        int savedId = Session.userId(this);
        if (savedId != -1) {
            User saved = db.userDao().getById(savedId);
            if (saved != null) {
                openHome(saved);
                return;
            }
            Session.logout(this);
        }
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);

        findViewById(R.id.loginButton).setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            User user = db.userDao().getUserByUsername(username);
            if (user != null && PasswordHasher.verify(password, user.password)) {
                Session.login(this, user.id);
                openHome(user);
            } else {
                Toast.makeText(this, R.string.invalid_credentials, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.goToRegister).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void openHome(User user) {
        Intent intent = new Intent(this, user.isAdmin ? AdminActivity.class : MainActivity.class);
        intent.putExtra(AdminActivity.EXTRA_USER_ID, user.id);
        startActivity(intent);
        finish();
    }
}
