package com.example.iepirkumu_saraksts;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "products")
public class Product implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String name;
    public String amount;
    public String category;
    public boolean isChecked;
    /** Absolute path of the user photo, or null. */
    public String photoPath;
    /** Reminder time in epoch millis, 0 = none. */
    @ColumnInfo(defaultValue = "0")
    public long reminderTime;

    public Product(String name, String amount, String category) {
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.isChecked = false;
    }
}
