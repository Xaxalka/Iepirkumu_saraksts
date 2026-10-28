package com.example.iepirkumu_saraksts;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String username;
    public String password;
    @ColumnInfo(defaultValue = "0")
    public boolean isAdmin;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
