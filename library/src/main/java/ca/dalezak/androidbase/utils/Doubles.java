package ca.dalezak.androidbase.utils;

import android.text.Editable;
import android.widget.EditText;

public class Doubles {

    public static boolean isDouble(String text) {
        try {
            Double.parseDouble(text);
            return true;
        }
        catch(NumberFormatException nfe) {
            return false;
        }
    }

    public static boolean isDouble(Editable editable) {
        return editable != null && isDouble(editable.toString());

    }

    public static boolean isDouble(EditText editText) {
        return editText != null && isDouble(editText.getText().toString());
    }
}
