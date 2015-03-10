package ca.dalezak.androidbase.tasks;

import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.UIRunnable;

import java.util.LinkedList;

public abstract class Queue<T extends Task, M extends BaseModel> implements Task.Callback<T, M> {

    public interface Callback<T extends Task, M extends BaseModel> extends Task.Callback<T, M> {
        public void onQueueStarted(int total);
        public void onQueueResumed();
        public void onQueueProgress(int total, int progress);
        public void onQueuePaused();
        public void onQueueCancelled();
        public void onQueueFinished();
        public void onQueueFailed(Exception exception);
    }

    protected final LinkedList<T> tasks = new LinkedList<T>();
    protected final LinkedList<Callback<T, M>> callbacks = new LinkedList<Callback<T, M>>();

    private int total;
    private boolean running;

    public int size() {
        return tasks.size();
    }

    public void clear() {
        tasks.clear();
    }

    public Queue<T, M> add(T task) {
        total += 1;
        task.register(this);
        tasks.add(task);
        return this;
    }

    public Queue<T, M> add(T...tasks) {
        for (T task : tasks) {
            total += 1;
            task.register(this);
            this.tasks.add(task);
        }
        return this;
    }

    public <T2 extends Task> boolean contains(Class<T2> taskClass) {
        for (T task : tasks) {
            if (taskClass.isAssignableFrom(task.getClass())) {
                return true;
            }
        }
        return false;
    }

    public void start() {
        if (!running) {
            Log.i(this, "Started");
            total = tasks.size();
            running = true;
            new UIRunnable(){ public void uiRun() {
                for (Callback callback : callbacks) {
                    callback.onQueueStarted(tasks.size());
                }
            }}.run();
            if (tasks.size() > 0) {
                Task task = tasks.peek();
                if (!task.isExecuting()) {
                    task.execute();
                }
            }
            else {
                new UIRunnable(){ public void uiRun() {
                    for (Callback callback : callbacks) {
                        callback.onQueueFinished();
                    }
                }}.run();
                running = false;
            }
        }
        else {
            Log.i(this, "Already Started");
        }
    }

    public void pause() {
        Log.i(this, "Pauses");
        running = false;
        Task task = tasks.peek();
        task.cancel(true);
        new UIRunnable(){ public void uiRun() {
            for (Callback callback : callbacks) {
                callback.onQueuePaused();
            }
        }}.run();
    }

    public void resume() {
        if (!running) {
            Log.i(this, "Resumed");
            running = true;
            new UIRunnable(){ public void uiRun() {
                for (Callback callback : callbacks) {
                    callback.onQueueResumed();
                }
            }}.run();
            if (tasks.size() > 0) {
                Task task = tasks.peek();
                task.execute();
            }
            else {
                new UIRunnable(){ public void uiRun() {
                    for (Callback callback : callbacks) {
                        callback.onQueueFinished();
                    }
                }}.run();
                running = false;
            }
        }
        else {
            Log.i(this, "Already Started");
        }
    }

    public void register(Callback<T, M> callback) {
        callbacks.add(callback);
    }

    public void unregister(Callback<T, M> callback) {
        callbacks.remove(callback);
    }

    public void onTaskStarted(final T task) {
        Log.i(this, "onTaskStarted %s", task);
        new UIRunnable(){ public void uiRun() {
            for (Callback callback : callbacks) {
                callback.onTaskStarted(task);
            }
        }}.run();
    }

    public void onTaskProgress(final T task, final M model, final int total, final int progress) {
        Log.i(this, "onTaskProgress %s %d / %d", task.getClass().getSimpleName(), progress, total);
        new UIRunnable(){ public void uiRun() {
            for (Callback callback : callbacks) {
                callback.onTaskProgress(task, model, total, tasks.size());
            }
        }}.run();
    }

    public void onTaskCancelled(final T task) {
        Log.i(this, "onTaskCancelled %s", task);
        new UIRunnable(){ public void uiRun() {
            for (Callback callback : callbacks) {
                callback.onTaskCancelled(task);
            }
        }}.run();
        tasks.clear();
        new UIRunnable(){ public void uiRun() {
            for (Callback callback : callbacks) {
                callback.onQueueCancelled();
            }
        }}.run();
    }

    public void onTaskFinished(final T task) {
        Log.i(this, "onTaskFinished %s", task);
        new UIRunnable(){ public void uiRun() {
            for (Callback callback : callbacks) {
                callback.onTaskFinished(task);
            }
        }}.run();
        tasks.remove(task);
        new UIRunnable(){ public void uiRun() {
            for (Callback callback : callbacks) {
                callback.onQueueProgress(total, total - tasks.size());
            }
        }}.run();
        if (tasks.size() > 0) {
            Task nextTask = tasks.peek();
            nextTask.execute();
        }
        else {
            new UIRunnable(){ public void uiRun() {
                for (Callback callback : callbacks) {
                    callback.onQueueFinished();
                }
            }}.run();
            running = false;
        }
    }

    public void onTaskFailed(final T task, final Exception exception) {
        new UIRunnable(){ public void uiRun() {
            for (Callback callback : callbacks) {
                callback.onTaskFailed(task, exception);
            }
        }}.run();
        Log.w(this, "onTaskFinished " + task, exception);
        new UIRunnable(){ public void uiRun() {
            for (Callback callback : callbacks) {
                callback.onQueueFailed(exception);
            }
        }}.run();
        tasks.clear();
        running = false;
    }
}