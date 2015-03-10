package ca.dalezak.androidbase.tasks;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import ca.dalezak.androidbase.R;
import ca.dalezak.androidbase.models.BaseModel;

public abstract class Task<T extends Task, M extends BaseModel>
        extends AsyncTask<Object, M, Exception>
        implements DialogInterface.OnCancelListener {

    public interface Callback<T extends Task, M extends BaseModel> {
        public void onTaskStarted(T task);
        public void onTaskCancelled(T task);
        public void onTaskProgress(T task, M model, int total, int progress);
        public void onTaskFinished(T task);
        public void onTaskFailed(T task, Exception exception);
    }

    protected Task.Callback<T, M> callback;
    protected final int message;
    protected final Context context;
    protected boolean progress;

    protected boolean executing = false;
    protected boolean pending = true;

    protected Task(Context context) {
        this(context, R.string.loading_, false, false);
    }

    protected Task(Context context, int message) {
        this(context, message, false, false);
    }

    protected Task(Context context, int message, boolean progress) {
        this(context, message, progress, false);
    }

    protected Task(Context context, int message, boolean progress, boolean cancellable) {
        this.context = context;
        this.message = message;
        this.progress = progress;
    }

    public boolean isExecuting() {
        return executing;
    }

    public boolean isPending() {
        return pending;
    }

    public void register(Task.Callback<T, M> callback) {
        this.callback = callback;
    }

    public void unregister(Task.Callback<T, M> callback) {
        this.callback = null;
    }

    public String getMessage() {
        return context != null ? context.getString(message) : null;
    }

    public boolean showsProgress() {
        return progress;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        executing = true;
        pending = false;
        if (callback != null) {
            callback.onTaskStarted((T)this);
        }
    }

    @Override
    protected void onProgressUpdate(M...models) {
        super.onProgressUpdate(models);
    }

    @Override
    protected void onPostExecute(final Exception exception) {
        super.onPostExecute(exception);
        executing = false;
        if (callback != null) {
            if (exception != null) {
                callback.onTaskFailed((T)this, exception);
            }
            else {
                callback.onTaskFinished((T)this);
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        pending = true;
        if (callback != null) {
            callback.onTaskCancelled((T)this);
        }
    }

    @Override
    protected void onCancelled(Exception result) {
        super.onCancelled(result);
        pending = true;
        if (callback != null) {
            callback.onTaskCancelled((T)this);
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        pending = true;
        if (callback != null) {
            callback.onTaskCancelled((T)this);
        }
    }
}