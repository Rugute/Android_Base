package ca.dalezak.androidbase.tasks;

public class HttpQueue extends Queue<HttpTask> {

    public interface Callback extends Queue.Callback<HttpTask> {
    }

    private final static HttpQueue instance = new HttpQueue();
    public static HttpQueue getInstance() { return instance; }

}