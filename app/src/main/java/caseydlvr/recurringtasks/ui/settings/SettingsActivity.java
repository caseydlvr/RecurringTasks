package caseydlvr.recurringtasks.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.notifications.NotificationReceiver;
import caseydlvr.recurringtasks.notifications.NotificationService;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_SHOW_NOTIFICATIONS = "pref_show_notifications";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener =
            (sharedPreferences, key) -> {
                switch (key) {
                    case KEY_SHOW_NOTIFICATIONS:
                        if (sharedPreferences.getBoolean(key, true)) {
                            ((RecurringTaskApp) getApplicationContext()).addNotificationAlarm();
                            showNotification();
                        } else {
                            ((RecurringTaskApp) getApplicationContext()).removeNotificationAlarm();
                            NotificationService.dismissNotification(this);
                        }
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

    private void showNotification() {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.setAction(NotificationReceiver.ACTION_SEND_TOP);
        sendBroadcast(intent);
    }
}
