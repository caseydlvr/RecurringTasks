package caseydlvr.recurringtasks;

import android.content.Context;
import androidx.annotation.NonNull;

import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.notifications.NotificationUtils;

/**
 * Utility class for static functions that perform task actions. Wrappers for Repository functions
 * that also handle any necessary auxiliary actions that are necessary to properly handle the action,
 * such as cancelling any active notifications for a task that is being deleted or completed.
 *
 * These functions are provide a single location for defining how Task actions should be handled,
 * rather than duplicating handling steps in various ViewModels and BroadcastReceivers (for example).
 */
public class TaskActions {

    private static final String TAG = TaskActions.class.getSimpleName();

    /**
     * Complete the provided Task. Cancels any notifications related to the task.
     *
     * @param context Context to use to get a RecurringTaskApp object
     * @param task    Task to complete
     */
    public static void complete(@NonNull Context context, @NonNull Task task) {
        ((RecurringTaskApp) context.getApplicationContext())
                .getRepository()
                .complete(task);

        NotificationUtils.dismissNotification(context, (int) task.getId());
    }

    /**
     * Complete the Task with a Task ID equal to the provided id. Cancels any notifications related
     * to the task.
     *
     * @param context Context to use to get a RecurringTaskApp object
     * @param id      Task ID of Task to complete
     */
    public static void completeById(@NonNull Context context, long id) {
        if (id < 1) return;

        ((RecurringTaskApp) context.getApplicationContext())
                .getRepository()
                .completeById(id);

        NotificationUtils.dismissNotification(context, (int) id);
    }

    /**
     * Delete the provided Task. Cancels any notifications related to the task.
     *
     * @param context Context to use to get a ReucrringTaskApp object
     * @param task    Task to delete
     */
    public static void delete(@NonNull Context context, @NonNull Task task) {
        ((RecurringTaskApp) context.getApplicationContext())
                .getRepository()
                .delete(task);

        NotificationUtils.dismissNotification(context, (int) task.getId());
    }
}
