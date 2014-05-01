package ru.obsession.merchandising.main;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.database.DatabaseApi;
import ru.obsession.merchandising.report.Photo;
import ru.obsession.merchandising.server.MultiformRequest;
import ru.obsession.merchandising.server.ServerApi;

public class PhotoReportingService extends IntentService {
    private static final String NAME = "photo_send_service";
    public static final String NEED_NOTIFY = "need_nitify";
    private int id;
    private int allCount;
    private int sexCount;
    private int errCount;

    public PhotoReportingService() {
        super(NAME);
        id = 0;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RequestQueue queue = ServerApi.getInstance(getApplicationContext()).getQueue();
     //   final DatabaseApi databaseApi = DatabaseApi.getInstance(getApplicationContext());
        ArrayList<Photo> photos = DatabaseApi.getInstance(getApplicationContext()).getPhotos();
        boolean needNotify = intent.getBooleanExtra(PhotoReportingService.NEED_NOTIFY, false);
        allCount = photos.size();
        if (allCount == 0){
            if (needNotify) {
                sendNotification(getString(R.string.have_no_photo));
            }
            return;
        }
        for (final Photo photo : photos) {
            MultiformRequest request = new MultiformRequest(new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                  //  databaseApi.removePhoto(photo);
                    sexes();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    error();
                }
            }, photo);
            queue.add(request);
        }
    }

    private void sexes() {
        try {
            ++sexCount;
            if (sexCount + errCount == allCount) {
                sendNotification(getString(R.string.send_result, sexCount, allCount));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void error() {
        try {
            ++errCount;
            if (sexCount + errCount == allCount) {
                sendNotification(getString(R.string.send_result, sexCount, allCount));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String text) {
        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.sending_photo))
                .setContentText(text)
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker(text);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, null, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationCompat.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notificationCompat.build());
        id++;
    }
}
