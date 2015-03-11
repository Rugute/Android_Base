package ca.dalezak.androidbase.tasks;

import android.content.Context;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import ca.dalezak.androidbase.R;
import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Log;

import java.net.URI;

public abstract class HttpGetTask<M extends BaseModel> extends HttpTask<M> {

    protected HttpGetTask(Context context, URI uri) {
        super(context, uri, R.string.downloading_);
    }

    protected HttpGetTask(Context context, URI uri, int message) {
        super(context, uri, message);
    }

    protected HttpGetTask(Context context, String server, String path) {
        super(context, server, path, R.string.downloading_);
    }

    protected HttpGetTask(Context context,String server, String path, int message) {
        super(context, server, path, message);
    }

    @Override
    protected HttpRequest getHttpRequest(URI uri) {
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        if (hasETag(uri)) {
            Log.i(this, "ETag %s", getETag(uri));
            httpGet.setHeader(IF_NONE_MATCH, getETag(uri));
        }
        for (String key : getParameterKeys()) {
            Object value = getParameter(key);
            if (value != null) {
                httpGet.getParams().setParameter(key, value);
            }
        }
        Log.i(this, "Request %s", httpGet.getURI());
        return httpGet;
    }

}