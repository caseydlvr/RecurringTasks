package caseydlvr.recurringtasks.notifications;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

/**
 * Static functions for handling notification related actions
 */
public class NotificationUtils {

    /**
     * Dismisses the notification with the provided notification ID. For a task notification, the
     * notification ID is the Task ID of the associated Task.
     *
     * @param context Context to use to get the notification service
     * @param id      ID of the notification to dismiss
     */
    public static void dismissNotification(Context context, int id) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);
    }

    /**
     * Dismisses all of the app's notifications
     *
     * @param context Context to use to get the notification service
     */
    public static void dismissNotifications(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    /**
     * Send all Task notifications now, regardless of notification alarm time
     */
    public static void showNotifications(Context context) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction(NotificationReceiver.ACTION_SEND_NOTIFICATIONS);
        context.sendBroadcast(intent);
    }
}
