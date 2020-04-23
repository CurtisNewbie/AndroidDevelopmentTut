package com.curtisnewbie.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
/**
 * Data Access Object Dao - defines methods for accessing User data
 */
@Dao
public interface UserDao {

    @Insert
    void addUser(User user);

    @Query("SELECT * FROM user WHERE username = :username")
    User getUser(String username);

    @Query("SELECT COUNT(*) FROM user")
    int getNumOfUsers();
}
