package ru.obsession.merchandising.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;

import ru.obsession.merchandising.clients.Client;
import ru.obsession.merchandising.report.Goods;
import ru.obsession.merchandising.report.Photo;
import ru.obsession.merchandising.shops.Shop;
import ru.obsession.merchandising.works.Work;

public class DatabaseApi {

    private static volatile DatabaseApi databaseApi;
    private SQLiteDatabase db;

    private DatabaseApi(Context context) {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        try {
            db = dbOpenHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = dbOpenHelper.getReadableDatabase();
        }
    }

    public static DatabaseApi getInstance(Context context) {
        if (databaseApi == null) {
            synchronized (DatabaseApi.class) {
                if (databaseApi == null) {
                    databaseApi = new DatabaseApi(context);
                }
            }
        }
        return databaseApi;
    }

    public void clearTasks() {
        db.execSQL("DELETE FROM works");
        db.execSQL("DELETE FROM clients");
        db.execSQL("DELETE FROM shops");
        db.execSQL("DELETE FROM goods");
    }

    public void insertAll(ArrayList<Work> works,
                          ArrayList<Client> clients,
                          ArrayList<Shop> shops,
                          ArrayList<Goods> goods) {
        db.beginTransaction();
        clearTasks();
        ContentValues values = new ContentValues();
        for (Work work : works) {
            values.put("user_id", work.merch);
            values.put("work_id", work.id);
            values.put("client_id", work.client);
            values.put("shop_id", work.shop);
            values.put("date", work.date_show);
            values.put("desc", work.desc);
            db.insert("works", null, values);
        }
        values.clear();

        for (Client client : clients) {
            values.put("client_id", client.id);
            values.put("name", client.name);
            values.put("phone", client.phone);
            db.insert("clients", null, values);
        }
        values.clear();
        for (Shop shop : shops) {
            values.put("shop_id", shop.id);
            values.put("name", shop.name);
            values.put("need_order", shop.needOrder ? -1 : 1);
            values.put("shop_address", shop.address);
            db.insert("shops", null, values);
        }
        values.clear();
        for (Goods goods1 : goods) {
            values.put("company", goods1.company);
            values.put("shop_name", goods1.shopName);
            values.put("format", goods1.format);
            values.put("weight", goods1.weight);
            values.put("goods_id", goods1.id);
            values.put("name", goods1.name);
            db.insert("goods", null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void close() {
        db.close();
    }

    public ArrayList<Shop> getAllShops(int userId) {
        ArrayList<Shop> shops = new ArrayList<Shop>();
        Cursor cursor = db.rawQuery("SELECT   shops.* FROM shops " +
                " INNER JOIN works on works.shop_id = shops.shop_id " +
                " AND works.user_id = " + String.valueOf(userId) +
                " GROUP BY works.shop_id", null);
        parseShopsCursor(shops, cursor);
        cursor.close();
        return shops;
    }

    public ArrayList<Shop> getDayShops(int userId, int serverTime) {
        ArrayList<Shop> shops = new ArrayList<Shop>();
        Cursor cursor = db.rawQuery("SELECT shops.* FROM shops " +
                " INNER JOIN works on works.shop_id = shops.shop_id " +
                " WHERE works.date = " + String.valueOf(serverTime) +
                " AND works.user_id = " + String.valueOf(userId) +
                " GROUP BY works.shop_id", null);
        parseShopsCursor(shops, cursor);
        cursor.close();
        return shops;
    }

    public ArrayList<Client> getClients(int userId, int shopId) {
        ArrayList<Client> clients = new ArrayList<Client>();
        Cursor cursor = db.rawQuery("SELECT clients.* FROM clients " +
                " INNER JOIN works on clients.client_id = works.client_id " +
                " WHERE works.shop_id = " + String.valueOf(shopId) +
                " AND works.user_id = " + String.valueOf(userId) +
                " GROUP BY works.client_id", null);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                Client client = new Client();
                client.id = cursor.getInt(cursor.getColumnIndex("client_id"));
                client.name = cursor.getString(cursor.getColumnIndex("name"));
                client.phone = cursor.getString(cursor.getColumnIndex("phone"));
                clients.add(client);
            }
        }
        cursor.close();
        return clients;
    }

    public ArrayList<Goods> getAssortment(int userId, int clientId, Shop shop) {
        ArrayList<Goods> goodses = new ArrayList<Goods>();
        Cursor cursor = db.rawQuery("SELECT * FROM goods" +
                " WHERE goods.shop_id = " + String.valueOf(shop.id) +
                " AND goods.user_id = " + String.valueOf(userId) +
                " AND goods.client_id = " + String.valueOf(clientId), null);
        if (cursor.getCount() == 0) {
            cursor.close();
            cursor = db.rawQuery("SELECT * FROM goods" +
                    " WHERE goods.user_id = -1", null);
        /*    cursor = db.rawQuery("SELECT * FROM goods" +
                    " WHERE goods.shop_name = '" + shop.name +
                    "' AND goods.user_id = -1", null);*/
        }
        parseAssortmenteCursor(goodses, cursor);
        cursor.close();
        return goodses;
    }

    public void saveReport(ArrayList<Goods> goodses, int userId, int clientId, int shopId) {
        db.beginTransaction();
        db.execSQL("DELETE FROM goods" +
                " WHERE goods.shop_id = " + String.valueOf(shopId) +
                " AND goods.user_id = " + String.valueOf(userId) +
                " AND goods.client_id = " + String.valueOf(clientId));
        ContentValues values = new ContentValues();
        for (Goods goods : goodses) {
            values.put("company", goods.company);
            values.put("user_id", userId);
            values.put("client_id", clientId);
            values.put("shop_id", shopId);
            values.put("shop_name", goods.shopName);
            values.put("format", goods.format);
            values.put("weight", goods.weight);
            values.put("goods_id", goods.id);
            values.put("name", goods.name);
            values.put("face", goods.faces);
            values.put("vicyak", goods.visyak);
            values.put("return", goods.retured);
            values.put("cost", goods.cost);
            values.put("residue", goods.residue);
            db.insert("goods", null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private void parseAssortmenteCursor(ArrayList<Goods> goodses, Cursor cursor) {
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                Goods goods = new Goods();
                goods.id = cursor.getInt(cursor.getColumnIndex("goods_id"));
                goods.residue = cursor.getString(cursor.getColumnIndex("residue"));
                goods.name = cursor.getString(cursor.getColumnIndex("name"));
                goods.faces = cursor.getString(cursor.getColumnIndex("face"));
                goods.visyak = cursor.getString(cursor.getColumnIndex("vicyak"));
                goods.retured = cursor.getString(cursor.getColumnIndex("return"));
                goods.weight = cursor.getString(cursor.getColumnIndex("weight"));
                goods.company = cursor.getString(cursor.getColumnIndex("company"));
                goods.format = cursor.getString(cursor.getColumnIndex("format"));
                goods.cost = cursor.getString(cursor.getColumnIndex("cost"));
                goods.calcDescription();
                goodses.add(goods);
            }
        }
    }

    private void parseShopsCursor(ArrayList<Shop> shops, Cursor cursor) {
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                Shop shop = new Shop();
                shop.id = cursor.getInt(cursor.getColumnIndex("shop_id"));
                shop.name = cursor.getString(cursor.getColumnIndex("name"));
                shop.needOrder = cursor.getInt(cursor.getColumnIndex("need_order")) == 1;
                shop.address = cursor.getString(cursor.getColumnIndex("shop_address"));
                shops.add(shop);
            }
        }
    }

    public void insertPhotos(ArrayList<Photo> photos, int userId, int clientId, int shopId) {
        ContentValues values = new ContentValues();
        db.beginTransaction();
        db.execSQL(" DELETE FROM photos" +
                " WHERE photos.shop_id = " + String.valueOf(shopId) +
                " AND photos.user_id = " + String.valueOf(userId) +
                " AND photos.client_id = " + String.valueOf(clientId));
        for (Photo photo : photos) {
            values.put("client_id", clientId);
            values.put("shop_id", shopId);
            values.put("user_id", userId);
            values.put("path", photo.path);
            db.insert("photos", null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public ArrayList<Photo> getPhotos(int clientId, int userId, int shopId) {
        ArrayList<Photo> photos = new ArrayList<Photo>();
        Cursor cursor = db.rawQuery("SELECT photos.path FROM photos " +
                " WHERE photos.shop_id = " + String.valueOf(shopId) +
                " AND photos.user_id = " + String.valueOf(userId) +
                " AND photos.client_id = " + String.valueOf(clientId), null);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                Photo photo = new Photo();
                photo.path = cursor.getString(cursor.getColumnIndex("path"));
                photos.add(photo);
            }
        }
        cursor.close();
        return photos;
    }

    public void removePhoto(Photo photo) {
        db.execSQL("DELETE FROM photo WHERE path = '" + photo.path + "'");
    }

    public ArrayList<Photo> getPhotos() {
        ArrayList<Photo> photos = new ArrayList<Photo>();
        Cursor cursor = db.rawQuery("SELECT * FROM photos ", null);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                Photo photo = new Photo();
                photo.path = cursor.getString(cursor.getColumnIndex("path"));
                photo.shopId = cursor.getInt(cursor.getColumnIndex("shop_id"));
                photo.clientId = cursor.getInt(cursor.getColumnIndex("client_id"));
                photo.userId = cursor.getInt(cursor.getColumnIndex("user_id"));
                photos.add(photo);
            }
        }
        cursor.close();
        return photos;
    }
}
