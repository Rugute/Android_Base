package ca.dalezak.androidbase.animations;


import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class FadeIn extends AlphaAnimation implements Animation.AnimationListener {

    private View view;

    public FadeIn(View view) {
        this(view, 1000);
    }

    public FadeIn(View view, int milliseconds) {
        super(0.0f, 1.0f);
        setDuration(milliseconds);
        setAnimationListener(this);
        this.view = view;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        view.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
