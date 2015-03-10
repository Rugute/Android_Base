package ca.dalezak.androidbase.tasks;

import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpPut;
import ca.dalezak.androidbase.R;
import ca.dalezak.androidbase.models.BaseModel;

import java.net.URI;

public abstract class HttpPutTask<M extends BaseModel> extends HttpPostTask<M> {

    protected HttpPutTask(Context context, String path) {
        super(context, path, R.string.uploading_);
    }

    protected HttpPutTask(Context context, String path, int message) {
        super(context, path, message);
    }

    protected HttpPutTask(Context context, String path, int message, boolean progress) {
        super(context, path, message, progress);
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