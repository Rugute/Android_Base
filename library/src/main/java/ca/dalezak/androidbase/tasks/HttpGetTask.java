package ca.dalezak.androidbase.tasks;

import android.content.Context;
import android.net.Uri;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import ca.dalezak.androidbase.R;
import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Log;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class HttpGetTask<M extends BaseModel> extends HttpTask<M> {

    protected HttpGetTask(Context context, URI uri) {
        super(context, uri, R.string.downloading_);
    }

    protected HttpGetTask(Context context, URI uri, boolean loading) {
        super(context, uri, R.string.downloading_, loading);
    }

    protected HttpGetTask(Context context, URI uri, int message) {
        super(context, uri, message);
    }

    protected HttpGetTask(Context context, URI uri, int message, boolean loading) {
        super(context, uri, message, loading);
    }

    protected HttpGetTask(Context context, String server, String path) {
        super(context, server, path, R.string.downloading_);
    }

    protected HttpGetTask(Context context, String server, String path, boolean loading) {
        super(context, server, path, R.string.downloading_, loading);
    }

    protected HttpGetTask(Context context,String server, String path, int message) {
        super(context, server, path, message);
    }

    protected HttpGetTask(Context context,String server, String path, int message, boolean loading) {
        super(context, server, path, message, loading);
    }

    @Override
    protected HttpRequest getHttpRequest(URI uri) {
        try {
            Uri.Builder builder = Uri.parse(uri.toString()).buildUpon();
            for (String key : getParameterKeys()) {
                Object value = getParameter(key);
                if (value != null) {
                    builder.appendQueryParameter(key, value.toString());
                }
            }
            uri = new URI(builder.toString());
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        if (hasETag(uri)) {
            Log.i(this, "ETag %s", getETag(uri));
            httpGet.setHeader(IF_NONE_MATCH, getETag(uri));
        }
        return httpGet;
    }

}