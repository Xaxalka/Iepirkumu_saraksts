package com.example.iepirkumu_saraksts;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.List;

/** Administrator panel: statistics, user management and bulk product cleanup. */
public class AdminActivity extends AppCompatActivity {
    static final String EXTRA_USER_ID = "userId";

    private AppDatabase db;
    private int adminId;
    private TextView statsText;
    private LinearLayout userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminRoot), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, bars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        adminId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        statsText = findViewById(R.id.adminStats);
        userList = findViewById(R.id.adminUserList);

        findViewById(R.id.adminOpenList).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));

        findViewById(R.id.adminClearBought).setOnClickListener(v -> confirm(() -> {
            for (Product p : db.productDao().getChecked()) {
                ReminderScheduler.cancel(this, p);
                PhotoStore.delete(p.photoPath);
            }
            db.productDao().deleteChecked();
            refresh();
        }));

        findViewById(R.id.adminClearAll).setOnClickListener(v -> confirm(() -> {
            for (Product p : db.productDao().getAll()) {
                ReminderScheduler.cancel(this, p);
                PhotoStore.delete(p.photoPath);
            }
            db.productDao().deleteAll();
            refresh();
        }));

        findViewById(R.id.adminChangePassword).setOnClickListener(v -> askPassword(adminId));

        findViewById(R.id.adminLogout).setOnClickListener(v -> {
            Session.logout(this);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        statsText.setText(getString(R.string.admin_stats, db.userDao().count(),
                db.productDao().count(), db.productDao().countChecked()));

        userList.removeAllViews();
        List<User> users = db.userDao().getAll();
        for (User user : users) {
            TextView row = new TextView(this);
            row.setText(user.isAdmin
                    ? user.username + "  (" + getString(R.string.admin_role) + ")"
                    : user.username);
            row.setTextSize(16);
            row.setTextColor(Color.BLACK);
            row.setBackgroundColor(Color.WHITE);
            int pad = (int) (16 * getResources().getDisplayMetrics().density);
            row.setPadding(pad, pad, pad, pad);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = pad / 4;
            row.setLayoutParams(lp);
            row.setOnClickListener(v -> showUserActions(user));
            userList.addView(row);
        }
    }

    private void showUserActions(User user) {
        String[] actions = {getString(R.string.admin_reset_password), getString(R.string.admin_delete_user)};
        new AlertDialog.Builder(this)
                .setTitle(user.username)
                .setItems(actions, (d, which) -> {
                    if (which == 0) {
                        askPassword(user.id);
                    } else if (user.isAdmin) {
                        Toast.makeText(this, R.string.admin_cannot_delete_admin, Toast.LENGTH_SHORT).show();
                    } else {
                        confirm(() -> {
                            db.userDao().deleteUser(user.id);
                            refresh();
                        });
                    }
                })
                .show();
    }

    private void askPassword(int userId) {
        EditText input = new EditText(this);
        input.setHint(R.string.admin_new_password);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        new AlertDialog.Builder(this)
                .setTitle(R.string.admin_new_password)
                .setView(input)
                .setPositiveButton(R.string.ok, (d, w) -> {
                    String password = input.getText().toString().trim();
                    if (password.isEmpty()) {
                        Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.userDao().setPassword(userId, PasswordHasher.hash(password));
                    Toast.makeText(this, R.string.admin_done, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void confirm(Runnable action) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.admin_confirm)
                .setPositiveButton(R.string.ok, (d, w) -> action.run())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
