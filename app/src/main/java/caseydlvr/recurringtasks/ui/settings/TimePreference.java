package caseydlvr.recurringtasks.ui.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import androidx.preference.DialogPreference;
import caseydlvr.recurringtasks.R;

/**
 * Dialog for selecting a time. Uses a TimePicker which generally lets the user pick a time using
 * a clock interface, but the exact implementation varies by Android version
 */
public class TimePreference extends DialogPreference {

    private static final String TAG = TimePreference.class.getSimpleName();

    public static final String DEFAULT_TIME = "9:00";

    private String mTime;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogTitle("");
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.time_preference;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        if (defaultValue == null) defaultValue = DEFAULT_TIME;

        setTime(getPersistedString((String) defaultValue));
    }

    /**
     * @return String representing the currently selected time
     */
    String getTime() {
        return mTime;
    }

    /**
     * Persists the given time to user preferences
     *
     * @param s String representing a time, from user preferences
     */
     void setTime(String s) {
        mTime = s;

        persistString(s);
    }

    /**
     * @param hour   int representing an hour value
     * @param minute int representing a minute value
     * @return       String representation of the time passed in the parameters
     */
    static String buildTimeFromHourMinute(int hour, int minute) {
        return hour + ":" + minute;
    }

    /**
     * @param s String representing a time, from user preferences
     * @return  Hour value of the time represented in s
     */
    public static int getHourFromString(String s) {
        String[] time = s.split(":");
        return Integer.valueOf(time[0]);
    }

    /**
     * @param s String representing a time, from user preferences
     * @return  Minute value of the time represented in s
     */
    public static int getMinuteFromString(String s) {
        String[] time = s.split(":");
        return Integer.valueOf(time[1]);
    }
}
