package ru.obsession.merchandising.server;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class ServerApi {
    public static final String LOGIN_URL = "http://wap.bolife.ru/api/";
    private static final String MD5_KEY = "noob_team";
    private static volatile ServerApi serverApi;

    public RequestQueue getQueue() {
        return queue;
    }

    private RequestQueue queue;

    private ServerApi(RequestQueue queue) {
        this.queue = queue;
    }

    public static ServerApi getInstance(Context context) {
        if (serverApi == null) {
            synchronized (ServerApi.class) {
                if (serverApi == null) {
                    serverApi = new ServerApi(Volley.newRequestQueue(context));
                }
            }
        }
        return serverApi;
    }

    public void singUp(final String name, final String password, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> mapObject = new HashMap<String, String>();
                mapObject.put("type", "merch_auth");
                mapObject.put("login", name);
                mapObject.put("password", password);
                mapObject.put("hash", md5("merch_auth" + name + password));
                return mapObject;
            }
        };
        cancelAll();
        queue.add(getRequest);
    }
    private void cancelAll(){
        queue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }
    public void getShops(final int id, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> mapObject = new HashMap<String, String>();
                mapObject.put("type", "get_shops");
                mapObject.put("user_id", String.valueOf(id));
                mapObject.put("hash", md5("get_shops" + String.valueOf(id)));
                return mapObject;
            }
        };
        cancelAll();
        queue.add(getRequest);
    }
    public void getShopsNet(Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> mapObject = new HashMap<String, String>();
                mapObject.put("type", "chain_store");
                mapObject.put("hash", md5("chain_store"));
                return mapObject;
            }
        };
        cancelAll();
        queue.add(getRequest);
    }

    public void getClients(final int id, final int shopId, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> mapObject = new HashMap<String, String>();
                mapObject.put("type", "get_clients");
                mapObject.put("user_id", String.valueOf(id));
                mapObject.put("shop_id", String.valueOf(shopId));
                mapObject.put("hash", md5("get_clients" + String.valueOf(id) + String.valueOf(shopId)));
                return mapObject;
            }
        };
        cancelAll();
        queue.add(getRequest);
    }

    public void getWorks(final int id, final int shopId, final int clientId, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> mapObject = new HashMap<String, String>();
                mapObject.put("type", "get_works");
                mapObject.put("user_id", String.valueOf(id));
                mapObject.put("shop_id", String.valueOf(shopId));
                mapObject.put("client_id", String.valueOf(clientId));
                mapObject.put("hash", md5("get_works" + String.valueOf(id) + String.valueOf(shopId) + String.valueOf(clientId)));
                return mapObject;
            }
        };
        cancelAll();
        queue.add(getRequest);
    }

    public static String md5(String s) {
        s = MD5_KEY + s;
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
