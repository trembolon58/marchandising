package ru.obsession.merchandising.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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

public class DownloadScheduleService extends Service {

    public static final int DOWNLOADED = 0;
    private Messenger mClient;
    static final int MSG_REGISTER_CLIENT = 1;
    static final int MSG_UNREGISTER_CLIENT = 2;

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        int userId = intent.getIntExtra(MainActivity.USER_ID, -1);
        final int dbVersion = intent.getIntExtra(MainActivity.DB_VERSION, -1);
        ServerApi.getInstance(getApplicationContext()).getAllTAsks(userId, new Response.Listener<String>() {
            @Override
            public void onResponse(final String s) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            parseJSON(s);
                            SharedPreferences preferences = getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);
                            preferences.edit().putInt(MainActivity.DB_VERSION, dbVersion).commit();
                            sendNotification(getString(R.string.db_version_refreshed));
                            sendMessageToUI();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                sendNotification(getString(R.string.error_dounloading));
            }
        });
        return mMessenger.getBinder();
    }

    private void sendMessageToUI() {
        try {
            mClient.send(Message.obtain(null, DOWNLOADED, 0, 0));

        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
            shop.needOrder = jsonObject.getInt("need_order") != 0;
            shops.add(shop);
        }

        String goodsString = object.optString("assortment");
        array = new JSONArray(goodsString);
        for (int i = 0; i < array.length(); ++i) {
            JSONObject jsonObject = array.getJSONObject(i);
            Goods goodsItem = new Goods();
            goodsItem.company = jsonObject.getString("company");
            goodsItem.shopName = jsonObject.getString("shop_name").toLowerCase();
            goodsItem.format = jsonObject.getString("format");
            goodsItem.weight = jsonObject.getString("weight");
            goodsItem.id = jsonObject.getInt("id");
            goodsItem.name = jsonObject.getString("name");
            goodsItem.clientId = jsonObject.getInt("client");
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
                .setTicker(text)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationCompat.build());
    }

    class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClient = msg.replyTo;
                    break;
                case MSG_UNREGISTER_CLIENT:
                    stopSelf();
                    mClient = null;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}