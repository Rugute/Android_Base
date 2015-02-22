package ca.dalezak.android.base;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;

import com.activeandroid.ActiveAndroid;

import ca.dalezak.android.base.activities.ShutdownActivity;
import ca.dalezak.android.base.tasks.HttpQueue;
import ca.dalezak.android.base.tasks.HttpTask;
import ca.dalezak.android.base.utils.AlertDialog;
import ca.dalezak.android.base.utils.Log;
import ca.dalezak.android.base.utils.Prefs;

public abstract class App extends android.app.Application {

    private Activity activity;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(this, "onCreate");
        Prefs.initialize(this);
        ActiveAndroid.initialize(this);
        HttpTask.initialize(getServer());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(this, "onTerminate");
    }

    protected String getServer() {
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
        System.exit(0);
    }

    public void deleteDatabase() {
        Log.i(this, "deleteDatabase");
        Prefs.clear();
        deleteDatabase(getManifest("AA_DB_NAME"));
        ActiveAndroid.clearCache();
        ActiveAndroid.dispose();
        ActiveAndroid.initialize(this);
        HttpQueue.getInstance().clear();
    }

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
            return applicationInfo.metaData.getString(key);
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.w(this, "PackageManager.NameNotFoundException", e);
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

    private boolean isDatabaseException(Throwable throwable) {
        if (throwable != null && throwable.getMessage() != null) {
            if (throwable.getMessage().contains("no such column") && throwable instanceof android.database.sqlite.SQLiteException) {
                return true;
            }
            return throwable.getCause() != null && isDatabaseException(throwable.getCause());
        }
        return false;
    }

    private void showDatabaseException() {
        Log.i(this, "showDatabaseException");
        new AlertDialog(getActivity(), ca.dalezak.android.base.R.string.database_changed, ca.dalezak.android.base.R.string.database_changed_description) {
            @Override
            public void ok() {
                restartApplication();
            }
        }.showOk(ca.dalezak.android.base.R.string.restart_application);
    }

    private void showUnhandledException() {
        Log.i(this, "showUnhandledException");
        new AlertDialog(getActivity(), ca.dalezak.android.base.R.string.unhandled_exception, ca.dalezak.android.base.R.string.unhandled_exception_description) {
            @Override
            public void ok() {
                closeApplication();
            }
        }.showOk(ca.dalezak.android.base.R.string.exit_application);
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