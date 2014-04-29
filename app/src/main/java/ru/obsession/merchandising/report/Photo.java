package ru.obsession.merchandising.report;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Photo implements Serializable {
    public String path;
    public int userId;
    public int clientId;
    public int shopId;
    public Bitmap image;
    boolean checked;

    public Photo(String path) {
        this.path = path;
    }
    public Photo() {
    }
}
