package caseydlvr.recurringtasks.ui.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.notifications.NotificationUtils;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        setMaxNotificationsDefaultValue();
    }

    /**
     * Sets a default value for max notifications if no value is currently set. Necessary because
     * default value for this preference is dynamic, dependent on Android version. Defaults and
     * dynamic logic are therefore maintained in NotificationUtils, rather than set as a static
     * value in preferences.xml.
     */
    private void setMaxNotificationsDefaultValue() {
        ListPreference maxNotifications = (ListPreference) findPreference(SettingsActivity.KEY_MAX_NOTIFICATIONS);
        if (maxNotifications.getValue() == null) {
            maxNotifications.setValue(String.valueOf(NotificationUtils.getDefaultMaxNotifications()));
        }
    }
}
