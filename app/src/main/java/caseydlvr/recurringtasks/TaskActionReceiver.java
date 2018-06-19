package caseydlvr.recurringtasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
                    TaskActions.completeById(context, taskId);
                    break;
            }
        }
    }

}
