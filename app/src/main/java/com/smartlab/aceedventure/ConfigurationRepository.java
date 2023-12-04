package com.smartlab.aceedventure;

import android.content.Context;

import androidx.room.Room;

import java.util.List;

public class ConfigurationRepository {

    private AppDatabase database;
    private ConfigurationDao configurationDao;

    public ConfigurationRepository(Context context) {
        database = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                "Configuration"
        ).build();

        configurationDao = database.configurationDao();
    }

    public long insertConfiguration(Configuration configuration) {
        return configurationDao.insert(configuration);
    }

    public void updateConfiguration(Configuration configuration) {
        configurationDao.update(configuration);
    }

    public void deleteConfiguration(long id) {
        configurationDao.delete(id);
    }

    public void deleteAllConfigurations() {
        configurationDao.deleteAll();
    }

    public List<Configuration> getAllConfigurations() {
        return configurationDao.getAllConfigurations();
    }

    public int getConfigurationRowCount() {
        return configurationDao.getRowCount();
    }
}
