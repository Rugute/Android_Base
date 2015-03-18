package ca.dalezak.androidbase.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.ProgressDialog;
import ca.dalezak.androidbase.views.BaseCard;

public abstract class CardTask<M extends BaseModel, C extends BaseCard<M>> extends AsyncTask<Void, Void, Intent> {

    private ProgressDialog dialog;
    private Context context;
    private M model;
    private C card;

    public CardTask(Context context, C card, M model) {
        this.context = context;
        this.card = card;
        this.model = model;
    }

    public CardTask(Context context, C card, M model, int message) {
        this.context = context;
        this.card = card;
        this.model = model;
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
        card.setSelected(true);
        if (dialog != null) {
            dialog.show();
        }
    }

    @Override
    protected void onPostExecute(Intent intent) {
        super.onPostExecute(intent);
        card.setSelected(false);
        if (dialog != null) {
            dialog.hide();
            dialog = null;
        }
        if (intent != null) {
            context.startActivity(intent);
        }
    }

    protected Context getContext() {
        return context;
    }

    protected M getModel() {
        return model;
    }

    protected C getCard() {
        return card;
    }

    protected boolean hasDialog() {
        return dialog != null;
    }

    protected ProgressDialog getDialog() {
        return dialog;
    }
}