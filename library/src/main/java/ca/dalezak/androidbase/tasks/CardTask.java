package ca.dalezak.androidbase.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.views.BaseCard;

public abstract class CardTask<M extends BaseModel, C extends BaseCard<M>> extends AsyncTask<Void, Void, Intent> {

    private Context context;
    private M model;
    private C card;

    public CardTask(Context context, C card, M model) {
        this.context = context;
        this.card = card;
        this.model = model;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        card.setSelected(true);
    }

    @Override
    protected void onPostExecute(Intent intent) {
        super.onPostExecute(intent);
        if (intent != null) {
            context.startActivity(intent);
        }
        card.setSelected(false);
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
}