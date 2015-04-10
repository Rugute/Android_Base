package ca.dalezak.androidbase.utils;

import android.content.Context;

import ca.dalezak.androidbase.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Dates {

    private static final String DATE_FORMAT = "MMMM d, yyyy"; //  January 09, 2010
    private static final String DATETIME_FORMAT = "h:mm a, MMMM d, yyyy"; // 9:14 PM, January 9, 2010
    private static final String TIME_FORMAT = "h:mm a"; // 9:14 PM
    private static final String UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"; // 2011-10-04T17:44:07
    private static final String ISO_08601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"; // 2011-10-04T17:44:07'Z'

    public static Date utcDate() {
        try {
            String format = "yyyy-MMM-dd HH:mm:ss";
            SimpleDateFormat dateFormatUTC = new SimpleDateFormat(format);
            dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat dateFormatLocal = new SimpleDateFormat(format);
            return dateFormatLocal.parse(dateFormatUTC.format(new Date()));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String toISO8601() {
        Date now = new Date();
        return toISO8601(now);
    }

    public static String toISO8601(Date date) {
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat(ISO_08601_FORMAT, Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return dateFormat.format(date);
        }
        return null;
    }

    public static Date fromISO8601(String string) {
        try {
            if (string != null) {
                DateFormat dateFormat = new SimpleDateFormat(ISO_08601_FORMAT, Locale.US);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                return dateFormat.parse(string);
            }
        }
        catch (ParseException e) {
            Log.e(Dates.class.getName(), "fromISO8601", e);
        }
        return null;
    }

    public static int toYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        return calendar.get(Calendar.YEAR);
    }

    public static int toMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        return calendar.get(Calendar.MONTH);
    }

    public static int toDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int toHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int toMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        return calendar.get(Calendar.MINUTE);
    }

    public static String toDateTimeString(Date date) {
        return date != null ? new SimpleDateFormat(DATETIME_FORMAT).format(date) : null;
    }

    public static Date fromDateTimeString(String string) {
        try {
            if (string != null) {
                return new SimpleDateFormat(DATETIME_FORMAT).parse(string);
            }
        }
        catch (ParseException e) {
            Log.w(Dates.class.getName(), "fromDateTimeString", e);
        }
        catch (Exception e) {
            Log.w(Dates.class.getName(), "fromDateTimeString", e);
        }
        return null;
    }

    public static Date fromEpochString(String time) {
        long milliseconds = Long.parseLong(time) * 1000;
        return new Date(milliseconds);
    }

    public static Long toEpochString(Date date) {
        if (date != null) {
            return date.getTime() / 1000;
        }
        return null;
    }

    public static Date fromLongString(String time) {
        long milliseconds = Long.parseLong(time);
        return new Date(milliseconds);
    }

    public static String toDateString(Date date) {
        return date != null ? new SimpleDateFormat(DATE_FORMAT).format(date) : null;
    }

    public static Date fromDateString(String string) {
        try {
            if (string != null) {
                return new SimpleDateFormat(DATE_FORMAT).parse(string);
            }
        }
        catch (ParseException e) {
            Log.w(Dates.class.getName(), "fromDateString", e);
        }
        catch (Exception e) {
            Log.w(Dates.class.getName(), "fromDateString", e);
        }
        return null;
    }

    public static String toTimeString(Date date) {
        return date != null ? new SimpleDateFormat(TIME_FORMAT).format(date) : null;
    }

    public static Date fromTimeString(String string) {
        try {
            if (string != null) {
                return new SimpleDateFormat(TIME_FORMAT).parse(string);
            }
        }
        catch (ParseException e) {
            Log.w(Dates.class.getName(), "fromTimeString", e);
        }
        catch (Exception e) {
            Log.w(Dates.class.getName(), "fromTimeString", e);
        }
        return null;
    }

    public static Date fromUTCString(String string) {
        try {
            if (string != null) {
                return new SimpleDateFormat(UTC_FORMAT).parse(string);
            }
        }
        catch (ParseException e) {
            Log.w(Dates.class.getName(), "fromUTCString", e);
        }
        catch (Exception e) {
            Log.e(Dates.class.getName(), "fromUTCString", e);
        }
        return null;
    }

    public static String toUTCString(Date date) {
        return date != null ? new SimpleDateFormat(UTC_FORMAT).format(date) : null;
    }

    public static Date fromStrings(String dateString, String timeString) {
        if (!Strings.isNullOrEmpty(dateString) && !Strings.isNullOrEmpty(timeString)) {
            try {
                Calendar date = Calendar.getInstance();
                date.setTime(fromDateString(dateString));

                Calendar time = Calendar.getInstance();
                time.setTime(fromTimeString(timeString));

                Calendar result = Calendar.getInstance();
                result.set(Calendar.YEAR, date.get(Calendar.YEAR));
                result.set(Calendar.MONTH, date.get(Calendar.MONTH));
                result.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

                result.set(Calendar.AM_PM, time.get(Calendar.AM_PM));
                result.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
                result.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
                result.set(Calendar.SECOND, time.get(Calendar.SECOND));

                return result.getTime();
            }
            catch (NullPointerException e) {
                Log.w(Dates.class.getName(), String.format("fromStrings"), e);
            }
            return null;
        }
        return null;
    }

    public static String getElapsedString(Context context, long elapsed) {
        if (elapsed > 0) {
            final long ONE_SECOND = 1000;
            final long ONE_MINUTE = ONE_SECOND * 60;
            final long ONE_HOUR = ONE_MINUTE * 60;
            final long ONE_DAY = ONE_HOUR * 24;
            long days = elapsed / ONE_DAY;
            elapsed %= ONE_DAY;
            long hours = elapsed / ONE_HOUR;
            elapsed %= ONE_HOUR;
            long minutes = elapsed / ONE_MINUTE;
            elapsed %= ONE_MINUTE;
            long seconds = elapsed / ONE_SECOND;

            String minutesText = minutes == 1 ? context.getString(R.string.minute) : context.getString(R.string.minutes);
            String secondsText = seconds == 1 ? context.getString(R.string.second) : context.getString(R.string.seconds);
            return String.format("%d %s %d %s", minutes, minutesText, seconds, secondsText);
        }
        return null;
    }
}
