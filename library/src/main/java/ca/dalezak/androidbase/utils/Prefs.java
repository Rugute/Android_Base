package ca.dalezak.androidbase.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import java.net.URI;
import java.util.Date;
import java.util.Map;

public class Prefs {

    public class Keys {
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
    }

    public static Context context;

    public static void initialize(Context cxt) {
        context = cxt;
    }

    public static Boolean hasUsername() {
        return contains(Keys.USERNAME) && !Strings.isNullOrEmpty(getString(Keys.USERNAME));
    }

    public static String getUsername() {
        return getString(Keys.USERNAME);
    }

    public static void setUsername(String username) {
        save(Keys.USERNAME, username);
    }

    public static Boolean hasPassword() {
        return contains(Keys.PASSWORD) && !Strings.isNullOrEmpty(getString(Keys.PASSWORD));
    }

    public static String getPassword() {
        return getString(Keys.PASSWORD);
    }

    public static void setPassword(String password) {
        save(Keys.PASSWORD, password);
    }

    public static boolean exists(Context context) {
        return prefs(context).getAll().size() > 0;
    }

    public static boolean contains(String name) {
        return prefs(context).getAll().size() > 0 && prefs(context).contains(name);
    }

    public static boolean contains(Context context, String name) {
        return prefs(context).getAll().size() > 0 && prefs(context).contains(name);
    }

    public static boolean clear() {
        return clear(context);
    }

    public static boolean clear(Context context) {
        Log.i(context, "clear");
        try {
            editor(context).clear().commit();
            return true;
        }
        catch(Exception e) {
            Log.w(Prefs.class, "clear", e);
            return false;
        }
    }

    public static boolean remove(String name) {
        return remove(context, name);
    }

    public static boolean remove(Context context, String name) {
        Log.i(context, "remove(%s)", name);
        try {
            editor(context).remove(name).commit();
            return true;
        }
        catch(Exception e) {
            Log.w(Prefs.class, "save", e);
            return false;
        }
    }

    public static boolean save(String name, String value) {
        return save(context, name, value);
    }

    public static boolean save(Context context, String name, String value) {
        Log.i(context, "save(%s, %s)", name, value);
        try {
            editor(context).putString(name, value).commit();
            return true;
        }
        catch(Exception e) {
            Log.w(Prefs.class, "save", e);
            return false;
        }
    }

    public static boolean save(String name, boolean value) {
        return save(context, name, value);
    }

    public static boolean save(Context context, String name, boolean value) {
        Log.i(context, "save(%s, %s)", name, value ? "true" : "false");
        try {
            editor(context).putBoolean(name, value).commit();
            return true;
        }
        catch(Exception e) {
            Log.w(Prefs.class, "save", e);
            return false;
        }
    }

    public static boolean save(String name, int value) {
        return save(context, name, value);
    }

    public static boolean save(Context context, String name, int value) {
        Log.i(context, "save(%s, %d)", name, value);
        try {
            editor(context).putInt(name, value).commit();
            return true;
        }
        catch(Exception e) {
            Log.w(Prefs.class, "save", e);
            return false;
        }
    }

    public static boolean save(String name, long value) {
        return save(context, name, value);
    }

    public static boolean save(Context context, String name, long value) {
        Log.i(context, "save(%s, %d)", name, value);
        try {
            editor(context).putLong(name, value).commit();
            return true;
        }
        catch(Exception e) {
            Log.w("save", "Exception: " + e);
            return false;
        }
    }

    public static boolean save(String name, float value) {
        return save(context, name, value);
    }

    public static boolean save(Context context, String name, float value) {
        Log.i(context, "save(%s, %s)", name, value);
        try {
            editor(context).putFloat(name, value).commit();
            return true;
        }
        catch(Exception e) {
            Log.w(Prefs.class, "save", e);
            return false;
        }
    }

    public static boolean save(String name, double value) {
        return save(context, name, value);
    }

    public static boolean save(Context context, String name, double value) {
        Log.i(context, "save(%s, %s)", name, value);
        try {
            editor(context).putFloat(name, (float)value).commit();
            return true;
        }
        catch(Exception e) {
            Log.w(Prefs.class, "save", e);
            return false;
        }
    }

    public static boolean save(String name, Date value) {
        return save(context, name, value);
    }

    public static boolean save(Context context, String name, Date value) {
        Log.i(context, "save(%s, %s)", name, value);
        try {
            if (value != null) {
                editor(context).putString(name, Dates.toISO8601(value)).commit();
            }
            else {
                editor(context).putString(name, null).commit();
            }
            return true;
        }
        catch(Exception e) {
            Log.w(Prefs.class, "save", e);
            return false;
        }
    }

    public static boolean save(String name, EditText exitText) {
        return save(context, name, exitText);
    }

    public static boolean save(Context context, String name, EditText exitText) {
        Log.i(context, "save(%s, %s)", name, exitText.getText());
        try {
            editor(context).putString(name, exitText.getText().toString()).commit();
            return true;
        }
        catch(Exception e) {
            Log.w(Prefs.class, "save", e);
            return false;
        }
    }

    public static String getString(String name) {
        return getString(context, name);
    }

    public static String getString(Context context, String name) {
        return getString(context, name, (String)null);
    }

    public static String getString(String name, String defaultValue) {
        return getString(context, name, defaultValue);
    }

    public static String getString(Context context, String name, String defaultValue) {
        try {
            return prefs(context).getString(name, defaultValue);
        }
        catch (ClassCastException e) {
            Log.w(Prefs.class, "getString", e);
            return defaultValue;
        }
    }

    public static int getInt(String name) {
        return getInt(context, name);
    }

    public static int getInt(Context context, String name) {
        return getInt(context, name, 0);
    }

    public static int getInt(String name, int defaultValue) {
        return getInt(context, name, defaultValue);
    }

    public static int getInt(Context context, String name, int defaultValue) {
        try {
            return prefs(context).getInt(name, defaultValue);
        }
        catch (ClassCastException e) {
            Log.w(Prefs.class, "getInt", e);
            return defaultValue;
        }
    }

    public static float getFloat(String name) {
        return getFloat(context, name);
    }

    public static float getFloat(Context context, String name) {
        return getFloat(context, name, 0);
    }

    public static float getFloat(String name, float defaultValue) {
        return getFloat(context, name, defaultValue);
    }

    public static float getFloat(Context context, String name, float defaultValue) {
        try {
            return prefs(context).getFloat(name, defaultValue);
        }
        catch (ClassCastException e) {
            Log.w(Prefs.class, "getFloat", e);
            return defaultValue;
        }
    }

    public static double getDouble(String name) {
        return getDouble(context, name);
    }

    public static double getDouble(Context context, String name) {
        return getDouble(context, name, 0);
    }

    public static double getDouble(String name, double defaultValue) {
        return getDouble(context, name, defaultValue);
    }

    public static double getDouble(Context context, String name, double defaultValue) {
        try {
            return (double) prefs(context).getFloat(name, (float)defaultValue);
        }
        catch (ClassCastException e) {
            Log.w(Prefs.class, "getDouble", e);
            return defaultValue;
        }
    }

    public static long getLong(String name) {
        return getLong(context, name);
    }

    public static long getLong(Context context, String name) {
        return getLong(context, name, 0);
    }

    public static long getLong(String name, long defaultValue) {
        return getLong(context, name, defaultValue);
    }

    public static long getLong(Context context, String name, long defaultValue) {
        try {
            return prefs(context).getLong(name, defaultValue);
        }
        catch (ClassCastException e) {
            Log.w(Prefs.class, "getLong", e);
            return defaultValue;
        }
    }

    public static boolean getBoolean(String name) {
        return getBoolean(context, name);
    }

    public static boolean getBoolean(Context context, String name) {
        return getBoolean(context, name, false);
    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        return getBoolean(context, name, defaultValue);
    }

    public static boolean getBoolean(Context context, String name, boolean defaultValue) {
        try {
            return prefs(context).getBoolean(name, defaultValue);
        }
        catch (ClassCastException e) {
            Log.w(Prefs.class, "getBoolean", e);
            return defaultValue;
        }
    }

    public static Date getDate(String name) {
        return getDate(context, name);
    }

    public static Date getDate(Context context, String name) {
        String date = getString(context, name);
        return date != null ? Dates.fromISO8601(date) : null;
    }

    public static double getDouble(EditText editText, double defaultValue) {
        return getDouble(context, editText, defaultValue);
    }

    public static double getDouble(Context context, EditText editText, double defaultValue) {
        String stringValue = editText.getText().toString();
        return Strings.isNullOrEmpty(stringValue) ? defaultValue : Double.valueOf(stringValue);
    }

    protected static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(context.getClass().getName(), Activity.MODE_PRIVATE);
    }

    protected static SharedPreferences.Editor editor(Context context) {
        return context.getSharedPreferences(context.getClass().getName(), Activity.MODE_PRIVATE).edit();
    }
}