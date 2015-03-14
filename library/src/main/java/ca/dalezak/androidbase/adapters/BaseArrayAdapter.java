package ca.dalezak.androidbase.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import ca.dalezak.androidbase.models.BaseModel;

public abstract class BaseArrayAdapter<M extends BaseModel> extends ArrayAdapter<M> {

    public BaseArrayAdapter(Context context) {
        super(context, android.R.layout.simple_spinner_item);
    }

    public abstract List<M> models();

    public M getItem(Integer index) {
        List<M> models = models();
        if (models != null && models.size() > index) {
            return models.get(index);
        }
        return null;
    }
}