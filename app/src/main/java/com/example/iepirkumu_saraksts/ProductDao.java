package com.example.iepirkumu_saraksts;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id DESC")
    List<Product> getAll();

    @Query("SELECT * FROM products WHERE category = :category ORDER BY id DESC")
    List<Product> getByCategory(String category);

    @Query("SELECT * FROM products WHERE name LIKE '%' || :searchQuery || '%' ORDER BY id DESC")
    List<Product> searchProducts(String searchQuery);

    @Query("SELECT * FROM products WHERE reminderTime > 0 AND isChecked = 0")
    List<Product> getWithReminders();

    @Query("SELECT COUNT(*) FROM products")
    int count();

    @Query("SELECT COUNT(*) FROM products WHERE isChecked = 1")
    int countChecked();

    @Query("SELECT * FROM products WHERE isChecked = 1")
    List<Product> getChecked();

    @Query("DELETE FROM products WHERE isChecked = 1")
    void deleteChecked();

    @Query("DELETE FROM products")
    void deleteAll();

    @Insert
    long insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);
}
