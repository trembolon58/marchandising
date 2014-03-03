package ru.obsession.merchandising.main;


import android.graphics.Bitmap;

public class DrawerItem {
    DrawerItem(int textRes, int iconRes) {
        this.iconRes = iconRes;
        this.textRes = textRes;
    }

    int iconRes;
    Bitmap icon;
    String text;
    int textRes;
}
