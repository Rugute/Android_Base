package ca.dalezak.androidbase.animations;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class FadePulse extends AlphaAnimation implements Animation.AnimationListener {

    private View view;

    public FadePulse(View view) {
        super(1.0f, 0.5f);
        setDuration(500);
        setRepeatCount(-1);
        setRepeatMode(AlphaAnimation.REVERSE);
        setAnimationListener(this);
        this.view = view;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
