package ca.dalezak.androidbase.tasks;

import ca.dalezak.androidbase.models.BaseModel;

public class HttpQueue<M extends BaseModel> extends Queue<HttpTask, M> {

    public interface Callback<M extends BaseModel> extends Queue.Callback<HttpTask, M> {}

    private final static HttpQueue instance = new HttpQueue();
    public static HttpQueue getInstance() { return instance; }

}