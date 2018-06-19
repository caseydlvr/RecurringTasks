package caseydlvr.recurringtasks;

import android.content.Context;

import caseydlvr.recurringtasks.notifications.NotificationUtils;

public class TaskActions {

    private static final String TAG = TaskActions.class.getSimpleName();

    /**
     * Complete the Task with a Task ID equal to the provided id
     *
     * @param context Context to use to get a RecurringTaskApp object
     * @param id      Task ID of Task to complete
     */
    static void completeTask(Context context, long id) {
        if (id < 1) return;

        ((RecurringTaskApp) context.getApplicationContext())
                .getRepository()
                .completeById(id);

        NotificationUtils.dismissNotification(context, (int) id);
    }
}
