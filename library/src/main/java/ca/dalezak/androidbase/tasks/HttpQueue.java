package ca.dalezak.androidbase.tasks;

import ca.dalezak.androidbase.models.BaseModel;

public class HttpQueue extends BaseQueue<HttpTask, BaseModel> {

    public interface Callback extends BaseQueue.Callback<HttpTask, BaseModel> {}

    private final static HttpQueue instance = new HttpQueue();
    public static HttpQueue getInstance() { return instance; }

}