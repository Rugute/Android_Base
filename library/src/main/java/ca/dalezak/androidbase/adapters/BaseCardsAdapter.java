package ca.dalezak.androidbase.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Objects;
import ca.dalezak.androidbase.views.BaseCard;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCardsAdapter<M extends BaseModel, C extends BaseCard> extends RecyclerView.Adapter<C> {

    public interface OnAdapterListener<M, C> {
        public void onCardPressed(C card, M model);
        public void onCardSelected(C card, M model);
        public void onCardRefreshed(int unfiltered, int filtered);
    }

    private OnAdapterListener<M, C> adapterListener;

    private final List<M> filtered = new ArrayList<M>();
    private final List<M> unfiltered = new ArrayList<M>();

    private Class<M> modelClass;
    private Class<C> cardClass;
    private int layout;

    private final Context context;
    private final LayoutInflater inflater;

    public BaseCardsAdapter(Context context, Class<M> modelClass, Class<C> cardClass, int layout) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.modelClass = modelClass;
        this.cardClass = cardClass;
        this.layout = layout;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public Context getContext() {
        return context;
    }

    public List<M> getFiltered() {
        return filtered;
    }

    public List<M> getUnfiltered() {
        return unfiltered;
    }

    public void setAdapterListener(OnAdapterListener<M, C> adapterListener) {
        this.adapterListener = adapterListener;
    }

    @Override
    public C onCreateViewHolder(ViewGroup viewGroup, final int index) {
        View view = inflater.inflate(layout, viewGroup, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                C card = (C)v.getTag();
                int position = card.getPosition();
                M model = filtered.get(position);
                Log.i(this, "onClick %s %d", card.getClass(), position);
                if (adapterListener != null) {
                    adapterListener.onCardSelected(card, model);
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                C card = (C)v.getTag();
                int position = card.getPosition();
                M model = filtered.get(position);
                Log.i(this, "onLongClick %s %d", card.getClass(), position);
                if (adapterListener != null) {
                    adapterListener.onCardPressed(card, model);
                }
                return true;
            }
        });
        Class[] constructors = new Class[]{Context.class, View.class};
        Object[] parameters = new Object[]{context, view};
        C card = Objects.createInstance(cardClass, constructors, parameters);
        view.setTag(card);
        return card;
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    public M getItem(int position) {
        if (position > -1 && filtered.size() > position) {
            return filtered.get(position);
        }
        return null;
    }

    public void refresh() {
        unfiltered.clear();
        unfiltered.addAll(getItems());
        filtered.clear();
        filtered.addAll(unfiltered);
        Log.i(this, "%s Unfiltered %d", modelClass.getSimpleName(), unfiltered.size());
        notifyDataSetChanged();
        if (adapterListener != null) {
            adapterListener.onCardRefreshed(unfiltered.size(), filtered.size());
        }
    }

    public void filter(String search) {
        filtered.clear();
        for (M model : unfiltered) {
            if (model.matches(search)) {
                filtered.add(model);
            }
        }
        Log.i(this, "%s Filtered %d", modelClass.getSimpleName(), filtered.size());
        notifyDataSetChanged();
        if (adapterListener != null) {
            adapterListener.onCardRefreshed(unfiltered.size(), filtered.size());
        }
    }

    public abstract List<M> getItems();

}
