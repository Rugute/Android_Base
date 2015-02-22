package ca.dalezak.android.base.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

public class Runtime {

    @SuppressWarnings("unchecked")
    public static <V> V getMetaData(Context context, String key, V defaultValue) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            Object value = bundle.get(key);
            if (value != null) {
                return (V)value;
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.w(Runtime.class.getName(), "getMetaData NameNotFoundException", e);
        }
        catch (NullPointerException e) {
            Log.w(Runtime.class.getName(), "getMetaData NullPointerException", e);
        }
        return defaultValue;
    }

    private static final List<Class> allClasses = new ArrayList<Class>();

    @SuppressWarnings("unchecked")
    public static <T> List<Class<? extends T>> getClasses(Context context, Class<? extends T> type) throws PackageManager.NameNotFoundException, IOException {
        if (allClasses.size() == 0) {
            String apkName = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir;
            //Log.i(Runtime.class.getName(), "APK " + apkName);
            ClassLoader classLoader = context.getClassLoader();
            DexFile dexFile = new DexFile(new File(apkName));
            Enumeration<String> entries = dexFile.entries();
            String packageName = getPackageName(context);
            //Log.i(Runtime.class.getName(), "Loading " + packageName);
            while (entries.hasMoreElements()) {
                String className = entries.nextElement();
                if (className.contains(packageName)) {
                    try {
                        Class clazz = Class.forName(className, true, classLoader);
                        //Log.i(Runtime.class.getName(), "Loading " + className);
                        allClasses.add(clazz);
                    }
                    catch (IllegalAccessError e) {
                        Log.w(Runtime.class.getName(), "getClasses IllegalAccessError for " + className, e);
                    }
                    catch (ClassNotFoundException e) {
                        Log.w(Runtime.class.getName(), "getClasses ClassNotFoundException for " + className, e);
                    }
                    catch (ExceptionInInitializerError e) {
                        Log.w(Runtime.class.getName(), "getClasses ExceptionInInitializerError for " + className, e);
                    }
                }
            }
        }
        List<Class<? extends T>> typeClasses = new ArrayList<Class<? extends T>>();
        for (Class clazz : allClasses) {
            if (type.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
                typeClasses.add(clazz);
            }
        }
        return typeClasses;
    }

    private static String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName;
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.w(Runtime.class.getName(), "getPackageName NameNotFoundException", e);
        }
        return "";
    }

}
