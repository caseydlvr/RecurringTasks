package caseydlvr.recurringtasks.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.notifications.NotificationReceiver;
import caseydlvr.recurringtasks.notifications.NotificationUtils;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    public static final String KEY_SHOW_NOTIFICATIONS = "pref_show_notifications";
    public static final String KEY_NOTIFICATION_TIME = "pref_notification_time";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener =
            (sharedPreferences, key) -> {
                switch (key) {
                    case KEY_SHOW_NOTIFICATIONS:
                        resetNotificationAlarm(sharedPreferences);
                        if (sharedPreferences.getBoolean(key, true)) {
                            showNotifications();
                        } else {
                            NotificationUtils.dismissNotifications(this);
                        }
                        break;
                    case KEY_NOTIFICATION_TIME:
                        resetNotificationAlarm(sharedPreferences);
                        break;
                }
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
    }

    /**
     * Resets (removes then re-adds) the notification alarm if the user has notifications enabled
     *
     * @param sharedPreferences SharedPreferences to use to check if user has notifications enabled
     */
    private void resetNotificationAlarm(SharedPreferences sharedPreferences) {
        if (sharedPreferences.getBoolean(KEY_SHOW_NOTIFICATIONS, true)) {
            ((RecurringTaskApp) getApplicationContext()).removeNotificationAlarm();
            ((RecurringTaskApp) getApplicationContext()).addNotificationAlarm();
        }
    }

    /**
     * Send all Task notifications now, regardless of notification alarm time
     */
    private void showNotifications() {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.setAction(NotificationReceiver.ACTION_SEND_NOTIFICATIONS);
        sendBroadcast(intent);
    }
}
