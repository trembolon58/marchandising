package ru.obsession.merchandising.server;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class ServerApi {
    public static final String LOGIN_URL = "http://m-service.su/MERCH_API/?";
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

        queue.add(getRequest);
    }

    public void getAcsess(final int id, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> mapObject = new HashMap<String, String>();
                mapObject.put("type", "request_access");
                mapObject.put("user_id", String.valueOf(id));
                mapObject.put("hash", md5("request_access" + String.valueOf(id)));
                return mapObject;
            }
        };
        queue.add(getRequest);
    }

    public void sendFacesReport(final int id, final int shopId, final String allfaces, final JSONArray array, final String timeArrival,
                                Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> mapObject = new HashMap<String, String>();
                mapObject.put("type", "global_report");
                mapObject.put("user_id", String.valueOf(id));
                mapObject.put("shop_id", String.valueOf(shopId));
                mapObject.put("all_faces", allfaces);
                mapObject.put("items", array.toString());
                mapObject.put("date_arrival", timeArrival);
                mapObject.put("hash", md5("global_report" + String.valueOf(id) + String.valueOf(shopId)));
                return mapObject;
            }
        };
        queue.add(getRequest);
    }

    public void sendOrder(final int id, final int shopId, final JSONArray array,
                                Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> mapObject = new HashMap<String, String>();
                mapObject.put("type", "order_report");
                mapObject.put("user_id", String.valueOf(id));
                mapObject.put("shop_id", String.valueOf(shopId));
                mapObject.put("assortments", array.toString());
                mapObject.put("hash", md5("order_report" + String.valueOf(id) + String.valueOf(shopId)));
                return mapObject;
            }
        };
        queue.add(getRequest);
    }
    public void testAcsess(final int id, final String password, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> mapObject = new HashMap<String, String>();
                mapObject.put("type", "test_access");
                mapObject.put("user_id", String.valueOf(id));
                mapObject.put("secret_code", password);
                mapObject.put("hash", md5("test_access" + String.valueOf(id) + password));
                return mapObject;
            }
        };
        queue.add(getRequest);
    }

    public void testNewDb(final int userId, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> mapObject = new HashMap<String, String>();
                mapObject.put("type", "test_new_db");
                mapObject.put("merch_id", String.valueOf(userId));
                mapObject.put("hash", md5("test_new_db" + String.valueOf(userId)));
                return mapObject;
            }
        };
        queue.add(getRequest);
    }

    public void getAllTAsks(final int userId, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> mapObject = new HashMap<String, String>();
                mapObject.put("type", "get_all");
                mapObject.put("user_id", String.valueOf(userId));
                mapObject.put("hash", md5("get_all"));
                return mapObject;
            }
        };
        queue.add(getRequest);
    }

    public void testPermision(final int userId, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> mapObject = new HashMap<String, String>();
                mapObject.put("type", "on_a_plan");
                mapObject.put("merch_id", String.valueOf(userId));
                mapObject.put("hash", md5("on_a_plan"));
                return mapObject;
            }
        };
        queue.add(getRequest);
    }

    public void getMessages(final int userId, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> mapObject = new HashMap<String, String>();
                mapObject.put("type", "get_user_msg");
                mapObject.put("merch_id", String.valueOf(userId));
                mapObject.put("get_code", "0");
                mapObject.put("hash", md5("get_user_msg" + String.valueOf(userId) + "0"));
                return mapObject;
            }
        };
        queue.add(getRequest);
    }

    public void getTasks(final int userId, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> mapObject = new HashMap<String, String>();
                mapObject.put("type", "get_user_msg");
                mapObject.put("merch_id", String.valueOf(userId));
                mapObject.put("get_code", "1");
                mapObject.put("hash", md5("get_user_msg" + String.valueOf(userId) + "1"));
                return mapObject;
            }
        };
        queue.add(getRequest);
    }

    public void sendAnsver(final int userId, final int messageId, final int setCode, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> mapObject = new HashMap<String, String>();
                mapObject.put("type", "set_user_msg");
                mapObject.put("merch_id", String.valueOf(userId));
                mapObject.put("id_msg", String.valueOf(messageId));
                mapObject.put("set_code", String.valueOf(setCode));
                mapObject.put("hash", md5("set_user_msg" + String.valueOf(messageId) + String.valueOf(setCode)));
                return mapObject;
            }
        };
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
