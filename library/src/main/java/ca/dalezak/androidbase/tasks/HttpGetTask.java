package ca.dalezak.androidbase.tasks;

import android.content.Context;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import ca.dalezak.androidbase.R;
import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Prefs;

import java.net.URI;

public abstract class HttpGetTask<M extends BaseModel> extends HttpTask<M> {

    protected HttpGetTask(Context context, String path) {
        super(context, path, R.string.downloading_);
    }

    protected HttpGetTask(Context context, String path, int message) {
        super(context, path, message);
    }

    protected HttpGetTask(Context context, String path, int message, boolean progress) {
        super(context, path, message, progress);
    }

    @Override
    protected HttpRequest getHttpRequest(URI uri) {
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        if (Prefs.hasETag(uri)) {
            Log.i(this, "ETag %s", Prefs.getETag(uri));
            httpGet.setHeader(IF_NONE_MATCH, Prefs.getETag(uri));
        }
        return httpGet;
    }

}