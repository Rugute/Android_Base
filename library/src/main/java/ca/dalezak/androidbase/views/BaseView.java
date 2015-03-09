package ca.dalezak.androidbase.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import ca.dalezak.androidbase.utils.Controls;

public abstract class BaseView extends LinearLayout {

    public BaseView(Context context) {
        this(context, null, 0, 0);
    }

    public BaseView(Context context, int resource) {
        this(context, null, 0, resource);
    }

    public BaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public BaseView(Context context, AttributeSet attrs, int style) {
        this(context, attrs, style, 0);
    }

    public BaseView(Context context, AttributeSet attrs, int style, int resource) {
        super(context, attrs, style);
        if (resource != 0) {
            View view = View.inflate(context, resource, this);
            Controls.load(context, this, view, getClass());
            Controls.load(context, this, view, getClass().getSuperclass());
        }
        else {
            Controls.load(context, this, this, getClass());
            Controls.load(context, this, this, getClass().getSuperclass());
        }
    }
}
