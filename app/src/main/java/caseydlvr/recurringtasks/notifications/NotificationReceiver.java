package caseydlvr.recurringtasks.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * BroadcastReceiver for handling notification related actions (such as sending notifications).
 * Allows entities outside of the app's context to trigger notifications.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();

    public static final String ACTION_SEND_NOTIFICATIONS = "caseydlvr.recurringtasks.action.NOTIFICATION_SEND";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_SEND_NOTIFICATIONS:
                    NotificationService.enqueueWork(context, intent);
                    break;
            }
        }
    }
}
