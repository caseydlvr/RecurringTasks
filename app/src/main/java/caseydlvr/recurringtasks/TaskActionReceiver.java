package caseydlvr.recurringtasks;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import caseydlvr.recurringtasks.notifications.NotificationService;

public class TaskActionReceiver extends BroadcastReceiver {

    private static final String TAG = TaskActionReceiver.class.getSimpleName();

    public static final String ACTION_COMPLETE = "caseydlvr.recurringtasks.TASK_COMPLETE";
    public static final String EXTRA_TASK_ID = "TaskActionReceiver_Task_Id";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_COMPLETE:
                    completeTask(context, intent.getLongExtra(EXTRA_TASK_ID, 0));
                    dismissNotification(context);
                    break;
            }
        }
    }

    private void completeTask(Context context, long id) {
        if (id < 1) return;

        ((RecurringTaskApp) context.getApplicationContext())
                .getRepository()
                .completeById(id);
    }

    private void dismissNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NotificationService.NOTIFICATION_ID);
    }
}
