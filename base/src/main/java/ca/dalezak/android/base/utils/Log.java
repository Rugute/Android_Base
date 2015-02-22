package ca.dalezak.android.base.utils;

public class Log {

    private static Callback callback;

    public interface Callback {
        public void onCaughtException(Throwable throwable);
    }

    public static Callback getCallback() {
        return callback;
    }

    public static void setCallback(Callback callback) {
        Log.callback = callback;
    }

    public static void i(Object caller, String message) {
        android.util.Log.i(getName(caller), message);
    }

    public static void i(Object caller, String message, Throwable throwable) {
        android.util.Log.i(getName(caller), message, throwable);
    }

    public static void i(Object caller, String format, Object...args) {
        android.util.Log.i(getName(caller), String.format(format, args));
    }

    public static void d(Object caller, String message) {
        android.util.Log.d(getName(caller), message);
    }

    public static void d(Object caller, String message, Throwable throwable) {
        android.util.Log.d(getName(caller), message, throwable);
    }

    public static void d(Object caller, String format, Object...args) {
        android.util.Log.d(getName(caller), String.format(format, args));
    }

    public static void w(Object caller, String message) {
        android.util.Log.w(getName(caller), message);
    }

    public static void w(Object caller, String message, Throwable throwable) {
        android.util.Log.w(getName(caller), message, throwable);
    }

    public static void w(Object caller, String format, Object...args) {
        android.util.Log.w(getName(caller), String.format(format, args));
    }


    public static void e(Object caller, String message) {
        android.util.Log.e(getName(caller), message);
        if (callback != null) {
            String description = String.format("%s %s", getName(caller), message);
            Exception exception = new Exception(description);
            callback.onCaughtException(exception);
        }
    }

    public static void e(Object caller, String message, Throwable throwable) {
        android.util.Log.e(getName(caller), message, throwable);
        if (callback != null) {
            String description = String.format("%s %s", getName(caller), message);
            Exception exception = new Exception(description, throwable);
            callback.onCaughtException(exception);
        }
    }

    public static void e(Object caller, String format, Object...args) {
        android.util.Log.e(getName(caller), String.format(format, args));
        if (callback != null) {
            String description = String.format("%s %s", getName(caller), String.format(format, args));
            Exception exception = new Exception(description);
            callback.onCaughtException(exception);
        }
    }

    private static String getName(Object caller) {
        return (caller instanceof Class) ? ((Class)caller).getSimpleName() : caller.getClass().getSimpleName();
    }
}
