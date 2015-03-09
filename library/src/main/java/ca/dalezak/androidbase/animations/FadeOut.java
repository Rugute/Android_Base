package ca.dalezak.androidbase.animations;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class FadeOut extends AlphaAnimation implements Animation.AnimationListener {

    private View view;

    public FadeOut(View view) {
        this(view, 1000);
    }

    public FadeOut(View view, int milliseconds) {
        super(1.0f, 0.0f);
        setDuration(milliseconds);
        setAnimationListener(this);
        this.view = view;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        view.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
