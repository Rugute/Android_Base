package ca.dalezak.androidbase.tasks;

import android.content.Context;

import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.ProgressDialog;

public abstract class LoadTask<M extends BaseModel> extends BaseTask<LoadTask, M> {

    private ProgressDialog dialog;

    public LoadTask(Context context) {
        super(context);
    }

    public LoadTask(Context context, int message) {
        super(context, message);
        try {
            dialog = new ProgressDialog(context);
            dialog.setMessage(context.getString(message));
            dialog.setIndeterminate(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        catch (Exception exception) {
            Log.w(this, "Exception", exception);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (dialog != null) {
            dialog.show();
        }
    }

    @Override
    protected void onProgressUpdate(BaseTask.Update... updates) {
        super.onProgressUpdate(updates);
        if (updates != null && updates.length > 0) {
            if (dialog != null && dialog.isIndeterminate()) {
                dialog.dismiss();
                dialog = new ProgressDialog(getContext());
            }
            for (BaseTask.Update update : updates) {
                if (dialog != null) {
                    dialog.setMax(update.total);
                    dialog.setProgress(update.progress);
                    dialog.setIndeterminate(false);
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    dialog.show();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Exception exception) {
        super.onPostExecute(exception);
        if (dialog != null) {
            dialog.hide();
            dialog = null;
        }
    }

    protected boolean hasDialog() {
        return dialog != null;
    }

    protected ProgressDialog getDialog() {
        return dialog;
    }
}