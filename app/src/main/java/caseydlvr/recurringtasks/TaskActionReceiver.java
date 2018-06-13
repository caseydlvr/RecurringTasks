package caseydlvr.recurringtasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import caseydlvr.recurringtasks.notifications.NotificationService;

/**
 * BroadcastReceiver for handling actions to perform on a Task. This allows non-App context's, such
 * as Notifications, to perform Task actions.
 */
public class TaskActionReceiver extends BroadcastReceiver {

    private static final String TAG = TaskActionReceiver.class.getSimpleName();

    public static final String ACTION_COMPLETE = "caseydlvr.recurringtasks.action.TASK_COMPLETE";
    public static final String EXTRA_TASK_ID = "TaskActionReceiver_Task_Id";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_COMPLETE:
                    long taskId = intent.getLongExtra(EXTRA_TASK_ID, 0);
                    completeTask(context, taskId);
                    NotificationService.dismissNotification(context, (int) taskId);
                    break;
            }
        }
    }

    /**
     * Complete the Task with a Task ID equal to the provided id
     *
     * @param context Context to use to get a RecurringTaskApp object
     * @param id      Task ID of Task to complete
     */
    private void completeTask(Context context, long id) {
        if (id < 1) return;

        ((RecurringTaskApp) context.getApplicationContext())
                .getRepository()
                .completeById(id);
    }
}
