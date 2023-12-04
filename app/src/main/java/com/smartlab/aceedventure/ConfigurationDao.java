package com.smartlab.aceedventure;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ConfigurationDao {

    @Insert
    long insert(Configuration configuration);

    @Update
    void update(Configuration configuration);

    @Query("DELETE FROM configuration WHERE id = :id")
    void delete(long id);

    @Query("DELETE FROM configuration")
    void deleteAll();

    @Query("SELECT * FROM configuration")
    List<Configuration> getAllConfigurations();

    @Query("SELECT COUNT(*) FROM configuration")
    int getRowCount();
}
