package ca.dalezak.androidbase.utils;

import android.text.Html;
import android.widget.EditText;

import java.util.Arrays;
import java.util.List;

public class Strings {

    public static String capitalize(String line){
        if (!isNullOrEmpty(line)) {
            return Character.toUpperCase(line.charAt(0)) + line.substring(1);
        }
        return line;
    }

    public static String titleize(String line) {
        if (!isNullOrEmpty(line)) {
            boolean cap = true;
            char[] out = line.toCharArray();
            int i, len = line.length();
            for (i=0; i<len; i++){
                if(Character.isWhitespace(out[i])){
                    cap = true;
                    continue;
                }
                if(cap){
                    out[i] = Character.toUpperCase(out[i]);
                    cap = false;
                }
            }
            return new String(out);
        }
        return line;
    }

    public static String stripHtml(String html) {
        if (!isNullOrEmpty(html)) {
            return Html.fromHtml(html).toString();
        }
        return null;
    }

    public static String join(String delimiter, Object...words) {
        StringBuilder sb = new StringBuilder();
        for (Object word : words) {
            if (word != null) {
                if (sb.length() > 0) {
                    sb.append(delimiter);
                }
                sb.append(word.toString());
            }
        }
        return sb.toString();
    }

    public static String join(Object...words) {
        StringBuilder sb = new StringBuilder();
        for (Object word : words) {
            if (word != null) {
                sb.append(word.toString());
            }
        }
        return sb.toString();
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }

    public static boolean isNullOrEmpty(EditText editText) {
        return editText != null && isNullOrEmpty(editText.getText().toString());
    }

    public static boolean anyWordStartsWith(String text, String...phrases) {
        if (!isNullOrEmpty(text) && phrases != null) {
            String textLowerCase = text.trim().toLowerCase();
            for (String phrase : phrases) {
                if (phrase != null) {
                    for (String word : phrase.toLowerCase().split(" ")) {
                        if (word.startsWith(textLowerCase)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }

    public static boolean areNullOrEmpty(String...words) {
        for (String word : words) {
            if (!isNullOrEmpty(word)) {
                return false;
            }
        }
        return true;
    }

    public static String[] getStringArray(String string) {
        return string != null ? string.split("\\|") : null;
    }

    public static List<String> getStringList(String string) {
        String[] array = getStringArray(string);
        return array != null && array.length > 0 ? Arrays.asList(array) : null;
    }

    public static Integer[] getIntegerArray(String string) {
        String[] strings = getStringArray(string);
        if (strings != null && strings.length > 0) {
            Integer[] values = new Integer[strings.length];
            for (int i = 0; i < strings.length; i++) {
                values[i] = Integer.parseInt(strings[i]);
            }
            return values;
        }
        return null;
    }

    public static List<Integer> getIntegerList(String string) {
        Integer[] array = getIntegerArray(string);
        return array != null && array.length > 0 ? Arrays.asList(array) : null;
    }

    private static Long[] getLongArray(String string) {
        String[] strings = getStringArray(string);
        if (strings != null && strings.length > 0) {
            Long[] values = new Long[strings.length];
            for (int i=0; i < strings.length; i++) {
                values[i] = Long.parseLong(strings[i]);
            }
            return values;
        }
        return null;
    }

    public static List<Long> getLongList(String string) {
        Long[] array = getLongArray(string);
        return array != null && array.length > 0 ? Arrays.asList(array) : null;
    }

    public static Float[] getFloatArray(String string) {
        String[] strings = getStringArray(string);
        if (strings != null && strings.length > 0) {
            Float[] values = new Float[strings.length];
            for (int i = 0; i < strings.length; i++) {
                values[i] = Float.parseFloat(strings[i]);
            }
            return values;
        }
        return null;
    }

    public static List<Float> getFloatList(String string) {
        Float[] array = getFloatArray(string);
        return array != null && array.length > 0 ? Arrays.asList(array) : null;
    }

    public static Boolean[] getBooleanArray(String string) {
        String[] strings = getStringArray(string);
        if (strings != null && strings.length > 0) {
            Boolean[] values = new Boolean[strings.length];
            for (int i=0; i < strings.length; i++) {
                values[i] = Boolean.parseBoolean(strings[i]);
            }
            return values;
        }
        return null;
    }

    public static List<Boolean> getBooleanList(String string) {
        Boolean[] array = getBooleanArray(string);
        return array != null && array.length > 0 ? Arrays.asList(array) : null;
    }

    public static Double[] getDoubleArray(String string) {
        String[] strings = getStringArray(string);
        if (strings != null && strings.length > 0) {
            Double[] values = new Double[strings.length];
            for (int i = 0; i < strings.length; i++) {
                values[i] = Double.parseDouble(strings[i]);
            }
            return values;
        }
        return null;
    }

    public static List<Double> getDoubleList(String string) {
        Double[] array = getDoubleArray(string);
        return array != null && array.length > 0 ? Arrays.asList(array) : null;
    }

    public static Short[] getShortArray(String string) {
        String[] strings = getStringArray(string);
        if (strings != null && strings.length > 0) {
            Short[] values = new Short[strings.length];
            for (int i = 0; i < strings.length; i++) {
                values[i] = Short.parseShort(strings[i]);
            }
            return values;
        }
        return null;
    }

    public static List<Short> getShortList(String string) {
        Short[] array = getShortArray(string);
        return array != null && array.length > 0 ? Arrays.asList(array) : null;
    }

    public static <T> String getStringFromArray(T[] values) {
        if (values != null) {
            StringBuilder sb = new StringBuilder();
            for(T t : values) {
                if(sb.length() > 0) {
                    sb.append("|");
                }
                sb.append(t);
            }
            return sb.toString();
        }
        return null;
    }

    public static <T> String getStringFromList(List<T> values) {
        return values != null ? getStringFromArray(values.toArray()) : null;
    }

    public static boolean areEqual(String one, String two) {
        return one != null && two != null && one.equals(two);
    }

    public static boolean areEqual(CharSequence one, CharSequence two) {
        return one != null && two != null && one.equals(two);
    }

    public static String splitCamelCase(String string) {
        return string.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                )," ");
    }
}

