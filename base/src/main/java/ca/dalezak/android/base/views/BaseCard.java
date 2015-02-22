package ca.dalezak.android.base.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import ca.dalezak.android.base.annotations.Control;
import ca.dalezak.android.base.models.Model;
import ca.dalezak.android.base.utils.Colors;
import ca.dalezak.android.base.utils.Controls;

public abstract class BaseCard<M extends Model> extends RecyclerView.ViewHolder {

    protected Context context;

    private int selectedColor;

    @Control("card_view")
    public CardView cardView;

    @Control("card_layout")
    public LinearLayout cardLayout;

    public BaseCard(Context context, View view) {
        super(view);
        this.context = context;
        Controls.load(context, this, view, getClass());
        Controls.load(context, this, view, getClass().getSuperclass());
    }

    protected int getColor(int color) {
        return context.getResources().getColor(color);
    }

    public void setSelectedColor(int color, float shade) {
        selectedColor = Colors.shade(color, shade);
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public abstract void setSelected(boolean selected);

}