package ru.obsession.merchandising.main;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.clients.Client;
import ru.obsession.merchandising.database.DatabaseApi;
import ru.obsession.merchandising.report.Goods;
import ru.obsession.merchandising.server.ServerApi;
import ru.obsession.merchandising.shops.Shop;
import ru.obsession.merchandising.works.Work;

public class DownloadScheduleService extends IntentService {

    private static final String name = "DounloadScheduleService";
    private int id = 0;

    public DownloadScheduleService() {
        super(name);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        ServerApi.getInstance(getApplicationContext()).getAllTAsks(new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    sendNotification(s);
                    parseJSON(s);
                    int dbVersion = intent.getIntExtra(MainActivity.DB_VERSION, -1);
                    SharedPreferences preferences = getSharedPreferences(MainActivity.PREFERENSES_NAME, Context.MODE_PRIVATE);
                    preferences.edit().putInt(MainActivity.DB_VERSION, dbVersion).commit();
                    sendNotification(getString(R.string.db_version_refreshed));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                sendNotification(getString(R.string.error_dounloading));
            }
        });

    }

    private void parseJSON(String s) throws JSONException {

        ArrayList<Work> works = new ArrayList<Work>();
        ArrayList<Client> clients = new ArrayList<Client>();
        ArrayList<Shop> shops = new ArrayList<Shop>();
        ArrayList<Goods> goods = new ArrayList<Goods>();
        JSONObject object = new JSONObject(s);
        String tasksString = object.optString("tasks");
        JSONArray array = new JSONArray(tasksString);
        for (int i = 0; i < array.length(); ++i) {
            JSONObject jsonObject = array.getJSONObject(i);
            Work work = new Work();
            work.client = jsonObject.getInt("client");
            work.date_show = jsonObject.getInt("date_show");
            work.desc = jsonObject.getString("desc");
            work.id = jsonObject.getInt("id");
            work.shop = jsonObject.getInt("shop");
            work.merch = jsonObject.getInt("merch");
            works.add(work);
        }
        String clienstString = object.optString("clients");
        array = new JSONArray(clienstString);
        for (int i = 0; i < array.length(); ++i) {
            JSONObject jsonObject = array.getJSONObject(i);
            Client client = new Client();
            client.phone = jsonObject.getString("phone");
            client.id = jsonObject.getInt("id");
            client.name = jsonObject.getString("name");
            clients.add(client);
        }
        String shopString = object.optString("shops");
        array = new JSONArray(shopString);
        for (int i = 0; i < array.length(); ++i) {
            JSONObject jsonObject = array.getJSONObject(i);
            Shop shop = new Shop();
            shop.address = jsonObject.getString("address");
            shop.id = jsonObject.getInt("id");
            shop.name = jsonObject.getString("name");
            shops.add(shop);
        }

        String goodsString = object.optString("assortment");
        array = new JSONArray(goodsString);
        for (int i = 0; i < array.length(); ++i) {
            JSONObject jsonObject = array.getJSONObject(i);
            Goods goodsItem = new Goods();
            goodsItem.company = jsonObject.getString("company");
            goodsItem.shopName = jsonObject.getString("shop_name");
            goodsItem.format = jsonObject.getString("format");
            goodsItem.weight = jsonObject.getString("weight");
            goodsItem.id = jsonObject.getInt("id");
            goodsItem.name = jsonObject.getString("name");
            goodsItem.calcDescription();
            goods.add(goodsItem);
        }
        DatabaseApi.getInstance(getApplicationContext()).insertAll(works, clients, shops, goods);
    }

    private void sendNotification(String text) {
        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.db_refresh))
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
