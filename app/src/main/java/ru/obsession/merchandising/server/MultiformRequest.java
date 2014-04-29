package ru.obsession.merchandising.server;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import ru.obsession.merchandising.report.Photo;


public class MultiformRequest extends Request<String> {
    private MultipartEntity entity = new MultipartEntity();
    private final Response.Listener<String> mListener;
    private final File photo;


    public MultiformRequest(Response.Listener<String> listener, Response.ErrorListener errorListener,
                            Photo photo) {
        super(Method.POST, ServerApi.LOGIN_URL, errorListener);
        mListener = listener;
        try {
            String sId = String.valueOf(photo.userId);
            String sSopId = String.valueOf(photo.shopId);
            String sClientId = String.valueOf(photo.clientId);
            entity.addPart("type", new StringBody("client_report"));
            entity.addPart("user_id", new StringBody(sId));
            entity.addPart("shop_id", new StringBody(sSopId));
            entity.addPart("client_id", new StringBody(sClientId));
            String md5 = ServerApi.md5("client_report" + sId + sSopId + sClientId);
            entity.addPart("hash", new StringBody(md5));
        } catch (Exception e) {
            e.printStackTrace();
        }
            this.photo = new File(photo.path);
            setTag("tag");
            buildMultipartEntity();
    }

    /**
     * добавляет фотографии в форму
     */
    private void buildMultipartEntity() {
            entity.addPart("pictures[0]", new FileBody(photo));
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
