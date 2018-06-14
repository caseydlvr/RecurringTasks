package caseydlvr.recurringtasks.ui.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

/**
 * Dialog for selecting a time. Uses a TimePicker which generally lets the user pick a time using
 * a clock interface, but the exact implementation varies by Android version
 */
public class TimePreference extends DialogPreference {

    private static final String TAG = TimePreference.class.getSimpleName();

    public static final String DEFAULT_TIME = "9:00";

    private TimePicker mTimePicker;
    private int mHour;
    private int mMinute;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogTitle("");
    }

    @Override
    protected View onCreateDialogView() {
        mTimePicker = new TimePicker(getContext());

        return mTimePicker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mTimePicker.setCurrentHour(mHour);
        mTimePicker.setCurrentMinute(mMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            mHour = mTimePicker.getCurrentHour();
            mMinute = mTimePicker.getCurrentMinute();

            if (callChangeListener(getTimeString())) persistString(getTimeString());
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            setTimeFromString(getPersistedString((String) defaultValue));
        } else {
            setTimeFromString((String) defaultValue);
        }
    }

    /**
     * Sets the hour and minute member fields by parsing a time String from user preferences.
     *
     * @param s String representing a time, from user preferences
     */
    private void setTimeFromString(String s) {
        String[] time = s.split(":");
        mHour = Integer.valueOf(time[0]);
        mMinute = Integer.valueOf(time[1]);
    }

    /**
     * Formats the selected hour and minute into a String representing a time. For example: "9:00"
     * This is the format that is stored to user preferences.
     *
     * @return String representing a time
     */
    private String getTimeString() {
        return mHour + ":" + mMinute;
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
