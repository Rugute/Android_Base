package ca.dalezak.androidbase.tasks;

import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;
import ca.dalezak.androidbase.R;
import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.UUID;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpPostTask<M extends BaseModel> extends HttpTask<M> {

    private Map<String, Object> parameters = new HashMap<String, Object>();

    protected HttpPostTask(Context context, String path) {
        super(context, path, R.string.posting_);
    }

    protected HttpPostTask(Context context, String path, int message) {
        super(context, path, message);
    }

    protected HttpPostTask(Context context, String path,  int message, boolean progress) {
        super(context, path, message, progress);
    }

    @Override
    protected HttpRequest getHttpRequest(URI uri) {
        HttpPost httpPost = new HttpPost(uri);
        if (isMultipartEntity()) {
            HttpEntity httpEntity = getMultipartEntity();
            httpPost.setEntity(httpEntity);
        }
        else {
            httpPost.setHeader(CONTENT_TYPE, APPLICATION_JSON);
            HttpEntity httpEntity = getStringEntity();
            httpPost.setEntity(httpEntity);
        }
        return httpPost;
    }

    public void addParameter(String key, Object object) {
        parameters.put(key, object);
    }

    protected HttpEntity getMultipartEntity() {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setBoundary(String.format("---------------------------%s", UUID.getRandom()));
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        for (String key : parameters.keySet()) {
            Object value = parameters.get(key);
            if (value instanceof File) {
                File file = (File) value;
                Log.i(this, "%s=%s", key, file.getPath());
                builder.addBinaryBody(key, file);
            }
            else if (value != null) {
                Log.i(this, "%s=%s", key, value);
                String string = value.toString();
                builder.addTextBody(key, string);
            }
        }
        return builder.build();
    }

    protected HttpEntity getStringEntity() {
        try {
            JSONObject jsonObject = new JSONObject();
            for (String key : parameters.keySet()) {
                Object value = parameters.get(key);
                if (value != null) {
                    Log.i(this, "%s=%s", key, value);
                    jsonObject.put(key, value);
                }
            }
            Log.i(this, "%s", jsonObject.toString());
            StringEntity entity = new StringEntity(jsonObject.toString());
            entity.setContentType(APPLICATION_JSON_CHARSET_UTF_8);
            entity.setContentEncoding(new BasicHeader(CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8));
            return entity;
        }
        catch (JSONException exception) {
            Log.w(this, "JSONException", exception);
        }
        catch (UnsupportedEncodingException exception) {
            Log.w(this, "UnsupportedEncodingException", exception);
        }
        return null;
    }

    protected boolean isMultipartEntity() {
        for (String key : parameters.keySet()) {
            Object value = parameters.get(key);
            if (value instanceof File) {
                return true;
            }
        }
        return false;
    }
}