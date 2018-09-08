package caseydlvr.recurringtasks.ui.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.preference.PreferenceDialogFragmentCompat;
import caseydlvr.recurringtasks.R;

public class TimePreferenceDialogFragment extends PreferenceDialogFragmentCompat {

    private TimePicker mTimePicker;

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mTimePicker = view.findViewById(R.id.time_picker);
        TimePreference timePreference = (TimePreference) getPreference();
        String time = timePreference.getTime();

        mTimePicker.setCurrentHour(TimePreference.getHourFromString(time));
        mTimePicker.setCurrentMinute(TimePreference.getMinuteFromString(time));
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            String time = TimePreference.buildTimeFromHourMinute(
                    mTimePicker.getCurrentHour(),
                    mTimePicker.getCurrentMinute());

            TimePreference timePreference = (TimePreference) getPreference();

            if (timePreference.callChangeListener(time)) timePreference.setTime(time);
        }
    }

    static TimePreferenceDialogFragment newInstance(String preferenceKey) {
        final Bundle args = new Bundle();
        args.putString(ARG_KEY, preferenceKey);

        final TimePreferenceDialogFragment fragment = new TimePreferenceDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }
}
