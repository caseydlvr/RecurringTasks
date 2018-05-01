package caseydlvr.recurringtasks.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();

    public static final String ACTION_SEND = "caseydlvr.recurringtasks.action.NOTIFICATION_SEND";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "in onReceive");
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_SEND:
                    NotificationService.enqueueWork(context, intent);
                    break;
            }
        }
    }
}
