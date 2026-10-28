package com.example.iepirkumu_saraksts;

import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Product.class, User.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    /** Replaces plaintext passwords with salted hashes. */
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            List<Object[]> rows = new ArrayList<>();
            try (Cursor c = db.query("SELECT id, password FROM users")) {
                while (c.moveToNext()) rows.add(new Object[]{c.getInt(0), c.getString(1)});
            }
            for (Object[] row : rows) {
                db.execSQL("UPDATE users SET password = ? WHERE id = ?",
                        new Object[]{PasswordHasher.hash((String) row[1]), row[0]});
            }
        }
    };

    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE products ADD COLUMN photoPath TEXT");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE users ADD COLUMN isAdmin INTEGER NOT NULL DEFAULT 0");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE products ADD COLUMN reminderTime INTEGER NOT NULL DEFAULT 0");
        }
    };
    public abstract ProductDao productDao();
    public abstract UserDao userDao();

    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_DEFAULT_PASSWORD = "admin123";

    /** Makes sure the built-in administrator account exists. */
    private static void seedAdmin(AppDatabase db) {
        if (db.userDao().getUserByUsername(ADMIN_USERNAME) == null) {
            User admin = new User(ADMIN_USERNAME, PasswordHasher.hash(ADMIN_DEFAULT_PASSWORD));
            admin.isAdmin = true;
            db.userDao().insert(admin);
        }
    }

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "shopping_db")
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // Simple app, allowing main thread for now
                    .build();
            seedAdmin(instance);
        }
        return instance;
    }
}
