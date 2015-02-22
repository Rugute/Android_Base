package ca.dalezak.android.base.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.dalezak.android.base.models.Model;
import ca.dalezak.android.base.utils.Log;
import ca.dalezak.android.base.utils.Objects;
import ca.dalezak.android.base.views.BaseCard;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCardAdapter<M extends Model, C extends BaseCard> extends RecyclerView.Adapter<C> {

    private OnAdapterListener<M, C> adapterListener;

    public interface OnAdapterListener<M, C> {
        public void onCardRefreshed(int unfiltered, int filtered);
        public void onCardSelected(C card, M model);
        public void onCardPressed(C card, M model);
    }

    protected final List<M> filtered = new ArrayList<M>();
    protected final List<M> unfiltered = new ArrayList<M>();

    private Class<M> modelClass;
    private Class<C> cardClass;
    private int layout;

    protected final Context context;
    protected final LayoutInflater inflater;

    public BaseCardAdapter(Context context, Class<M> modelClass, Class<C> cardClass, int layout) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.modelClass = modelClass;
        this.cardClass = cardClass;
        this.layout = layout;
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
        Log.i(this, "Filtered %d", filtered.size());
        notifyDataSetChanged();
        if (adapterListener != null) {
            adapterListener.onCardRefreshed(unfiltered.size(), filtered.size());
        }
    }
}
