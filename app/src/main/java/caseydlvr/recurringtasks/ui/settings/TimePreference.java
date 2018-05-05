package caseydlvr.recurringtasks.ui.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference {

    private static final String TAG = TimePreference.class.getSimpleName();

    private TimePicker mTimePicker;
    private int mHour = 9;
    private int mMinute = 30;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
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
            persistString(getTimeString());
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

    private void setTimeFromString(String s) {
        String[] time = s.split(":");
        mHour = Integer.valueOf(time[0]);
        mMinute = Integer.valueOf(time[1]);
    }

    private String getTimeString() {
        return mHour + ":" + mMinute;
    }
}
