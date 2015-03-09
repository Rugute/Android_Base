package ca.dalezak.androidbase.utils;

import android.os.Handler;
import android.os.Looper;

public abstract class UIRunnable implements Runnable {

    public abstract void uiRun();

    @Override
    public void run() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            uiRun();
        }
        else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    uiRun();
                }
            });
        }
    }
}
