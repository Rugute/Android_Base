package ca.dalezak.android.base.utils;

import android.content.Context;
import android.view.View;

import ca.dalezak.android.base.annotations.Control;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

public class Controls {

    private static final HashMap<Class, HashMap<Field, Control>> map = new HashMap<Class, HashMap<Field, Control>>();

    public static void load(Context context, Object instance, View view, Class clazz) {
        if (clazz != null) {
            HashMap<Field, Control> fieldControls = map.get(clazz);
            if (fieldControls == null) {
                fieldControls = new HashMap<Field, Control>();
                for (Field field : clazz.getDeclaredFields()) {
                    Annotation annotation = field.getAnnotation(Control.class);
                    if (annotation != null) {
                        Control control = (Control)annotation;
                        fieldControls.put(field, control);
                    }
                }
                map.put(clazz, fieldControls);
            }
            for (Field field : fieldControls.keySet()) {
                try {
                    Control control = fieldControls.get(field);
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    if (control.id() != 0) {
                        int id = control.id();
                        Log.i(instance, " %s > %s", clazz.getSimpleName(), field.getName());
                        field.set(instance, view.findViewById(id));
                    }
                    else if (control.value() != null) {
                        String value = control.value();
                        int id = findIdByName(context, value);
                        Log.i(instance, "%s > %s", clazz.getSimpleName(), field.getName());
                        field.set(instance, view.findViewById(id));
                    }
                }
                catch (IllegalArgumentException e) {
                    Log.w(instance, "IllegalArgumentException", e);
                }
                catch (IllegalAccessException e) {
                    Log.w(instance, "IllegalAccessException", e);
                }
            }
        }
    }

    private static int findIdByName(Context context, String name) {
        return context.getResources().getIdentifier(name, "id", context.getPackageName());
    }
}