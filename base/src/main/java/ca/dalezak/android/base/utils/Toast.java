package ca.dalezak.android.base.utils;

import android.content.Context;

public class Toast {

    public static void showLong(Context context, String message) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show();
    }

    public static void showLong(Context context, int message) {
        android.widget.Toast.makeText(context, context.getText(message), android.widget.Toast.LENGTH_LONG).show();
    }

    public static void showLong(Context context, CharSequence message) {
        android.widget.Toast.makeText(context, message.toString(), android.widget.Toast.LENGTH_LONG).show();
    }

    public static void showShort(Context context, String message) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show();
    }

    public static void showShort(Context context, int message) {
        android.widget.Toast.makeText(context, context.getText(message), android.widget.Toast.LENGTH_SHORT).show();
    }

    public static void showShort(Context context, CharSequence message) {
        android.widget.Toast.makeText(context, message.toString(), android.widget.Toast.LENGTH_SHORT).show();
    }
}
