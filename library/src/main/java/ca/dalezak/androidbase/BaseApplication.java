package ca.dalezak.androidbase;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;

import android.support.multidex.MultiDexApplication;
import android.support.multidex.MultiDex;

import ca.dalezak.androidbase.activities.ShutdownActivity;
import ca.dalezak.androidbase.managers.LocationManager;
import ca.dalezak.androidbase.utils.Alert;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Prefs;

public abstract class BaseApplication extends MultiDexApplication {

    private Activity activity;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(this, "onCreate");
        Prefs.initialize(this);
        LocationManager.initialize(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(this, "onTerminate");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public String getServer() {
        return getManifest("API_SERVER");
    }

    public void restartApplication() {
        Log.i(this, "restartApplication");
        try {
            Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
        }
        catch (Exception exception) {
            Log.w(this, "Exception", exception);
        }
        System.exit(0);
    }

    public void closeApplication() {
        Log.i(this, "closeApplication");
        try {
            if (hasActivity()) {
                Intent intent = new Intent(getApplicationContext(), ShutdownActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
            }
            else {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
        catch (Exception exception) {
            Log.e(this, "closeApplication", exception);
        }
        System.exit(0);
    }

    public abstract void deleteDatabase();

    public String getVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.w(this, "PackageManager.NameNotFoundException", e);
        }
        return null;
    }

    protected String getManifest(String key) {
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Object value = applicationInfo.metaData.get(key);
            return value != null ? value.toString() : null;
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.w(this, "PackageManager.NameNotFoundException", e);
        }
        catch(ClassCastException e) {
            Log.w(this, "ClassCastException", e);
        }
        return null;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public boolean hasActivity() {
        return activity != null;
    }

    public boolean isDebug() {
        return 0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE);
    }

    protected boolean isDatabaseException(Throwable throwable) {
        if (throwable != null && throwable.getMessage() != null) {
            if (throwable instanceof android.database.sqlite.SQLiteException) {
                if (throwable.getMessage().contains("no such column")) {
                    return true;
                }
                if (throwable.getMessage().contains("has no column")) {
                    return true;
                }
            }
            return throwable.getCause() != null && isDatabaseException(throwable.getCause());
        }
        return false;
    }

    protected void showDatabaseException() {
        Log.i(this, "showDatabaseException");
        try {
            new Alert(getActivity(), R.string.database_changed, R.string.database_changed_description) {
                @Override
                public void ok() {
                    restartApplication();
                }
            }.showOk(R.string.restart_application);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void showUnhandledException() {
        Log.i(this, "showUnhandledException");
        try {
            new Alert(getActivity(), R.string.unhandled_exception, R.string.unhandled_exception_description) {
                @Override
                public void ok() {
                    closeApplication();
                }
            }.showOk(R.string.exit_application);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected abstract void onUncaughtException(Throwable throwable);

    public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(final Thread thread, final Throwable throwable) {
            Log.i(this, "uncaughtException", throwable);
            onUncaughtException(throwable);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    try {
                        if (isDatabaseException(throwable)) {
                            deleteDatabase();
                            showDatabaseException();
                        }
                        else {
                            showUnhandledException();
                        }
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        System.exit(0);
                    }
                    Looper.loop();
                }
            }).start();
        }
    }

}