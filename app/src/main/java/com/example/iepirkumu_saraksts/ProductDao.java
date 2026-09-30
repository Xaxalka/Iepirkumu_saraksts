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

    @Insert
    void insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);
}
