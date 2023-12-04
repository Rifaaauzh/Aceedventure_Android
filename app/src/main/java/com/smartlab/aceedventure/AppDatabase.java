package com.smartlab.aceedventure;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {Configuration.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ConfigurationDao configurationDao();

}
