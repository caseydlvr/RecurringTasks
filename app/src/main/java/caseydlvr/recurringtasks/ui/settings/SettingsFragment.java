package caseydlvr.recurringtasks.ui.settings;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.notifications.NotificationUtils;

import static caseydlvr.recurringtasks.ui.settings.SettingsActivity.*;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        setMaxNotificationsDefaultValue();
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof TimePreference) {
            DialogFragment dialogFragment = TimePreferenceDialogFragment.newInstance(preference.getKey());
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), preference.getKey() + "_dialog");
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    /**
     * Sets a default value for max notifications if no value is currently set. Necessary because
     * default value for this preference is dynamic, dependent on Android version. Defaults and
     * dynamic logic are therefore maintained in NotificationUtils, rather than set as a static
     * value in preferences.xml.
     */
    private void setMaxNotificationsDefaultValue() {
        ListPreference maxNotifications = (ListPreference) findPreference(KEY_MAX_NOTIFICATIONS);
        if (maxNotifications.getValue() == null) {
            maxNotifications.setValue(NotificationUtils.getDefaultMaxNotifications());
        }
    }
}
