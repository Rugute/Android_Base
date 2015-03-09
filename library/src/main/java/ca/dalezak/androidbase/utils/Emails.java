package ca.dalezak.androidbase.utils;

import android.text.Editable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Emails {

    private final static String REGULAR_EXPRESSION = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
            +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
            +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

    public static Boolean isValidEmail(Editable editable) {
        return isValidEmail(editable.toString());
    }

    public static Boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(REGULAR_EXPRESSION);
        Matcher matcher = pattern.matcher(email);
        return  matcher.matches();
    }
}
