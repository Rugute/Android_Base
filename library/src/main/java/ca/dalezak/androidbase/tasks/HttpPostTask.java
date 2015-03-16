package ca.dalezak.androidbase.tasks;

import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import ca.dalezak.androidbase.R;
import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Files;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.UUID;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;

public abstract class HttpPostTask<M extends BaseModel> extends HttpTask<M> {

    protected JSONObject postBody;

    protected HttpPostTask(Context context, URI uri) {
        super(context, uri, R.string.posting_);
    }

    protected HttpPostTask(Context context, URI uri, boolean loading) {
        super(context, uri, R.string.posting_, loading);
    }

    protected HttpPostTask(Context context, URI uri, int message) {
        super(context, uri, message);
    }

    protected HttpPostTask(Context context, URI uri, int message, boolean loading) {
        super(context, uri, message, loading);
    }

    protected HttpPostTask(Context context, String server, String path) {
        super(context, server, path, R.string.posting_);
    }

    protected HttpPostTask(Context context, String server, String path, boolean loading) {
        super(context, server, path, R.string.posting_, loading);
    }

    protected HttpPostTask(Context context,String server, String path, int message) {
        super(context, server, path, message);
    }

    protected HttpPostTask(Context context,String server, String path, int message, boolean loading) {
        super(context, server, path, message, loading);
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

    protected HttpEntity getMultipartEntity() {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setBoundary(String.format("---------------------------%s", UUID.getRandom()));
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        for (String key : getParameterKeys()) {
            Object value = getParameter(key);
            if (value instanceof File) {
                File file = (File) value;
                String mimeType = Files.getMimeType(file);
                ContentType contentType = ContentType.create(mimeType);
                Log.i(this, "%s=%s (%s)", key, file.getName(), mimeType);
                builder.addBinaryBody(key, file, contentType, file.getName());
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
            JSONObject jsonObject;
            if (hasPostBody()) {
                jsonObject = getPostBody();
            }
            else {
                jsonObject = new JSONObject();
            }
            for (String key : getParameterKeys()) {
                Object value = getParameter(key);
                if (value != null) {
                    Log.i(this, "%s=%s", key, value);
                    jsonObject.put(key, value);
                }
            }
            Log.i(this, "Post %s", jsonObject.toString());
            StringEntity entity = new StringEntity(jsonObject.toString());
            entity.setContentEncoding(UTF_8);
            entity.setContentType(APPLICATION_JSON);
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
        for (String key : getParameterKeys()) {
            Object value = getParameter(key);
            if (value instanceof File) {
                return true;
            }
        }
        return false;
    }

    protected JSONObject getPostBody() {
        return postBody;
    }

    protected void setPostBody(JSONObject postBody) {
        this.postBody = postBody;
    }

    protected boolean hasPostBody() {
        return postBody != null && postBody.length() > 0;
    }
}