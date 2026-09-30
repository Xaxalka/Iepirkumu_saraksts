package com.example.iepirkumu_saraksts;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Product.class, User.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    public abstract ProductDao productDao();
    public abstract UserDao userDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "shopping_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // Simple app, allowing main thread for now
                    .build();
        }
        return instance;
    }
}
