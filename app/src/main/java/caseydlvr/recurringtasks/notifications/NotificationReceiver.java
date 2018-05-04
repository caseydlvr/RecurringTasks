package caseydlvr.recurringtasks.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();

    public static final String ACTION_SEND_TOP = "caseydlvr.recurringtasks.action.NOTIFICATION_SEND_TOP";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "in onReceive");
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case Intent.ACTION_BOOT_COMPLETED:
                    // notifications initialized in App onCreate when this receiver runs
                    break;
                case ACTION_SEND_TOP:
                    NotificationService.enqueueWork(context, intent);
                    break;
            }
        }
    }
}
