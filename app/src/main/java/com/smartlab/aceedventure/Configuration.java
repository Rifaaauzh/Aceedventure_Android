package com.smartlab.aceedventure;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "configuration")
public class Configuration {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String url;
    private String cookies;
    private int status;

    // Constructors, getters, and setters

    public Configuration(String url, String cookies, int status) {
        this.url = url;
        this.cookies = cookies;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}