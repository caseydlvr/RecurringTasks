package caseydlvr.recurringtasks.notifications;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Static functions for handling notification related actions
 */
public class NotificationUtils {

    private static final String TAG = NotificationUtils.class.getSimpleName();

    private static final String DEFAULT_MAX_NOTIFICATIONS = "4";
    private static final String DEFAULT_MAX_NOTIFICATIONS_PRE_N = "2";

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

    /**
     * Max number of notifications to show, by default. This depends on Android version. Since
     * grouping on Pre-N devices reduces notification functionality, a lower max is used so the
     * default max doesn't cause grouping. Since grouping has minimal downsides on N+ devices, a
     * higher default max is used.
     *
     * @return max number of notifications to show, by default
     */
    public static String getDefaultMaxNotifications() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return DEFAULT_MAX_NOTIFICATIONS_PRE_N;
        } else {
            return DEFAULT_MAX_NOTIFICATIONS;
        }
    }
}
