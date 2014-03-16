package ru.obsession.merchandising.server;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class MultiformRequest extends Request<String> {
    private MultipartEntity entity = new MultipartEntity();
    private final Response.Listener<String> mListener;
    private final ArrayList<File> photos;


    public MultiformRequest(int useerID, int shopId, int clientId, Response.Listener<String> listener, Response.ErrorListener errorListener,
                            ArrayList<String> photos) {
        super(Method.POST, ServerApi.LOGIN_URL, errorListener);
        this.photos = new ArrayList<File>();
        mListener = listener;
        try {
            String sId = String.valueOf(useerID);
            String sSopId = String.valueOf(shopId);
            String sClientId = String.valueOf(clientId);
            entity.addPart("type", new StringBody("client_report"));
            entity.addPart("user_id", new StringBody(sId));
            entity.addPart("shop_id", new StringBody(sSopId));
            entity.addPart("client_id", new StringBody(sClientId));
            String md5 = ServerApi.md5("client_report" + sId + sSopId + sClientId);
            entity.addPart("hash", new StringBody(md5));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (photos != null && photos.size() != 0) {
            for (String photo : photos) {
                this.photos.add(new File(photo));
            }
            buildMultipartEntity();
        }
    }

    /**
     * добавляет фотографии в форму
     */
    private void buildMultipartEntity() {
        for (int i = 0; i < photos.size(); i++) {
            entity.addPart("pictures[" + i + "]", new FileBody(photos.get(i)));
        }
    }

    /**
     * @return заголовок формы
     */
    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    /**
     * @return
     * @throws AuthFailureError IOException writing to ByteArrayOutputStream
     */
    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    /**
     * @param response Response from the network
     * @return если возможно достать JSON строку из ответа- успех
     *         иначе ошибка
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * @param response The parsed response returned by
     */
    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}
