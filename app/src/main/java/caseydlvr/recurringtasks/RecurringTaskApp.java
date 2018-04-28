package caseydlvr.recurringtasks;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Calendar;

import caseydlvr.recurringtasks.db.AppDatabase;
import caseydlvr.recurringtasks.notifications.NotificationAlarmReceiver;
import caseydlvr.recurringtasks.ui.settings.SettingsActivity;

public class RecurringTaskApp extends Application {

    private static final String TAG = RecurringTaskApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);

        SharedPreferences settingsPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showNotifications = settingsPrefs.getBoolean(SettingsActivity.KEY_SHOW_NOTIFICATIONS, true);

        if (showNotifications) {
            addNotificationAlarm();
        }

    }

    public AppDatabase getDb() {
        return AppDatabase.getInstance(this);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDb());
    }

    public void addNotificationAlarm() {
        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.HOUR_OF_DAY, 9);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                cal.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                getAlarmPendingIntent());
    }

    public void removeNotificationAlarm() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(getAlarmPendingIntent());
    }

    private PendingIntent getAlarmPendingIntent() {
        Intent intent = new Intent(this, NotificationAlarmReceiver.class);

        return PendingIntent.getBroadcast(this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
