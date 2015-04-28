package ca.dalezak.androidbase.app;

import ca.dalezak.androidbase.BaseApplication;

public class Application extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void deleteDatabase() {

    }

    @Override
    protected void onUncaughtException(Throwable throwable) {

    }
}
