package com.curtisnewbie.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Room Database
 * </p>
 */
@Database(entities = {Image.class, User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Get ImageDao
     *
     * @return ImageDao
     */
    public abstract ImageDao imgDao();

    /**
     * Get UserDao
     *
     * @return UserDao
     */
    public abstract UserDao userDao();
}
