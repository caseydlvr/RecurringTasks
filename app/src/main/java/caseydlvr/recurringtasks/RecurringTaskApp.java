package caseydlvr.recurringtasks;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import caseydlvr.recurringtasks.db.AppDatabase;
import caseydlvr.recurringtasks.notifications.NotificationReceiver;
import caseydlvr.recurringtasks.ui.settings.SettingsActivity;

public class RecurringTaskApp extends Application {

    private static final String TAG = RecurringTaskApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        initNotifications();
    }

    public AppDatabase getDb() {
        return AppDatabase.getInstance(this);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDb());
    }

    public void addNotificationAlarm() {
        final int notificationHour = 9;
        ZonedDateTime notificationTime = ZonedDateTime.now()
                .truncatedTo(ChronoUnit.DAYS)
                .plusHours(notificationHour);

        if (notificationTime.isBefore(ZonedDateTime.now())) {
            notificationTime = notificationTime.plusDays(1);
        }

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                notificationTime.toInstant().toEpochMilli(),
                AlarmManager.INTERVAL_DAY,
                buildAlarmPendingIntent());

        setBootReceiverEnabled(true);
    }

    public void removeNotificationAlarm() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(buildAlarmPendingIntent());

        setBootReceiverEnabled(false);
    }

    private PendingIntent buildAlarmPendingIntent() {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.setAction(NotificationReceiver.ACTION_SEND_TOP);

        return PendingIntent.getBroadcast(this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void setBootReceiverEnabled(boolean enabled) {
        int enabledState;
        if (enabled) enabledState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        else         enabledState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

        getPackageManager().setComponentEnabledSetting(
                new ComponentName(this, BootReceiver.class),
                enabledState,
                PackageManager.DONT_KILL_APP);
    }

    private void initNotifications() {
        SharedPreferences settingsPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showNotifications = settingsPrefs.getBoolean(SettingsActivity.KEY_SHOW_NOTIFICATIONS, true);

        if (showNotifications) {
            addNotificationAlarm();
        }
    }
}
