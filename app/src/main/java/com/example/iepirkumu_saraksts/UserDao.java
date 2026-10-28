package com.example.iepirkumu_saraksts;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM users ORDER BY isAdmin DESC, username")
    List<User> getAll();

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    User getById(int id);

    @Query("SELECT COUNT(*) FROM users")
    int count();

    @Query("UPDATE users SET password = :password WHERE id = :id")
    void setPassword(int id, String password);

    @Query("DELETE FROM users WHERE id = :id AND isAdmin = 0")
    void deleteUser(int id);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);
}
