package ca.dalezak.androidbase.tasks;

import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpPut;
import ca.dalezak.androidbase.R;
import ca.dalezak.androidbase.models.BaseModel;

import java.net.URI;

public abstract class HttpPutTask<M extends BaseModel> extends HttpPostTask<M> {

    protected HttpPutTask(Context context, URI uri) {
        super(context, uri, R.string.uploading_);
    }

    protected HttpPutTask(Context context, URI uri, boolean loading) {
        super(context, uri, R.string.uploading_, loading);
    }

    protected HttpPutTask(Context context, URI uri, int message) {
        super(context, uri, message);
    }

    protected HttpPutTask(Context context, URI uri, int message, boolean loading) {
        super(context, uri, message, loading);
    }

    protected HttpPutTask(Context context, String server, String path) {
        super(context, server, path, R.string.uploading_);
    }

    protected HttpPutTask(Context context, String server, String path, boolean loading) {
        super(context, server, path, R.string.uploading_, loading);
    }

    protected HttpPutTask(Context context,String server, String path, int message) {
        super(context, server, path, message);
    }

    protected HttpPutTask(Context context,String server, String path, int message, boolean loading) {
        super(context, server, path, message, loading);
    }

    @Override
    protected HttpRequest getHttpRequest(URI uri) {
        HttpPut httpPut = new HttpPut(uri);
        if (isMultipartEntity()) {
            HttpEntity httpEntity = getMultipartEntity();
            httpPut.setEntity(httpEntity);
        }
        else {
            httpPut.setHeader(CONTENT_TYPE, APPLICATION_JSON);
            HttpEntity httpEntity = getStringEntity();
            httpPut.setEntity(httpEntity);
        }
        return httpPut;
    }
}