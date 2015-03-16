package ca.dalezak.androidbase.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import ca.dalezak.androidbase.models.BaseModel;

public abstract class BaseArrayAdapter<M extends BaseModel> extends ArrayAdapter<M> {

    private M empty;

    public BaseArrayAdapter(Context context) {
        this(context, null);
    }

    public BaseArrayAdapter(Context context, M empty) {
        super(context, android.R.layout.simple_spinner_item);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.empty = empty;
    }

    public abstract List<M> getItems();

    @Override
    public int getCount() {
        List<M> items = getItems();
        if (empty != null) {
            return items.size() + 1;
        }
        return items.size();
    }

    @Override
    public M getItem(int position) {
        List<M> items = getItems();
        if (empty != null) {
            if (position == 0) {
                return empty;
            }
            else if (items != null && items.size() > position-1) {
                return items.get(position-1);
            }
        }
        else if (items != null && items.size() > position) {
            return items.get(position);
        }
        return null;
    }

    @Override
    public int getPosition(M item) {
        if (empty != null) {
            if (item == empty) {
                return 0;
            }
            int index = getItems().indexOf(item);
            return index > -1 ? index+1 : -1;
        }
        return getItems().indexOf(item);
    }

}