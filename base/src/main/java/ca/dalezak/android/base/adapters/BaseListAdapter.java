package ca.dalezak.android.base.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

import ca.dalezak.android.base.models.Model;
import ca.dalezak.android.base.utils.Log;
import ca.dalezak.android.base.utils.Objects;
import ca.dalezak.android.base.views.BaseView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListAdapter<M extends Model, V extends BaseView> extends BaseAdapter {

    protected final Activity activity;
    protected final LayoutInflater inflater;
    protected final List<M> filtered = new ArrayList<M>();
    protected final List<M> unfiltered = new ArrayList<M>();

    private Class<M> modelClass;
    private Class<V> viewClass;

    public BaseListAdapter(Activity activity, Class<M> modelClass, Class<V> viewClass) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.modelClass = modelClass;
        this.viewClass = viewClass;
    }

    public V getView(Activity activity, View view) {
        if (view == null) {
            return Objects.createInstance(viewClass, Context.class, activity);
        }
        else {
            return (V)view;
        }
    }

    public int getCount() {
        return filtered.size();
    }

    public M getItem(int position) {
        return filtered.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    protected List<M> models() {
        return Model.all(modelClass);
    }

    public void refresh() {
        unfiltered.clear();
        unfiltered.addAll(models());
        filtered.clear();
        filtered.addAll(unfiltered);
        Log.i(this, "Unfiltered %d", unfiltered.size());
        notifyDataSetChanged();
    }

    public void filter(String search) {
        filtered.clear();
        for (M model : unfiltered) {
            if (model.matches(search)) {
                filtered.add(model);
            }
        }
        Log.i(this, "Filtered %d", filtered.size());
        notifyDataSetChanged();
    }
}
