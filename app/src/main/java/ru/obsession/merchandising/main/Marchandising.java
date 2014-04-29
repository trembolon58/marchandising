package ru.obsession.merchandising.main;

import android.app.Application;

import ru.obsession.merchandising.database.DatabaseApi;

public class Marchandising extends Application {

    @Override
    public void onTerminate() {
        super.onTerminate();
        DatabaseApi.getInstance(getApplicationContext()).close();
    }

}
