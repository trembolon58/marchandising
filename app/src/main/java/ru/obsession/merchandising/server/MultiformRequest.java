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

/**
 * Класс, создающий стандартную <multipart/form-data> с изображенями
 */
public class MultiformRequest extends Request<String> {
    private MultipartEntity entity = new MultipartEntity();
    private final Response.Listener<String> mListener;
    private final File photo;

    /**
     * @param url адрес, по которому отправляются фотографии
     */
    public MultiformRequest(String url, int tipe, String description, int id, Response.ErrorListener errorListener, Response.Listener<String> listener,
                            String photo) {
        super(Method.POST, url, errorListener);
        this.photo = new File(photo);
        mListener = listener;
        try {
            String sId = String.valueOf(id);
            String sTipe = String.valueOf(tipe);
            entity.addPart("type", new StringBody("upload"));
            entity.addPart("category", new StringBody(sTipe));
            entity.addPart("user_id", new StringBody(sId));
            entity.addPart("title", new StringBody(description));
            String hash = ServerApi.md5("upload" + sTipe + sId + description);
            entity.addPart("hash", new StringBody(hash));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        buildMultipartEntity();
    }

    public MultiformRequest(String url, int id, Response.ErrorListener errorListener, Response.Listener<String> listener,
                            String photo) {
        super(Method.POST, url, errorListener);
        this.photo = new File(photo);
        mListener = listener;
        try {
            String sId = String.valueOf(id);
            entity.addPart("type", new StringBody("avatar_upload"));
            entity.addPart("user_id", new StringBody(sId));
            String hash = ServerApi.md5("avatar_upload" + sId);
            entity.addPart("hash", new StringBody(hash));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        buildMultipartEntity();
    }

    /**
     * добавляет фотографии в форму
     */
    private void buildMultipartEntity() {
        entity.addPart("data_photo", new FileBody(photo) {
            @Override
            public String getMimeType() {
                return "image/jpeg";
            }
        });
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
     * @throws com.android.volley.AuthFailureError IOException writing to ByteArrayOutputStream
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
     * иначе ошибка
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
