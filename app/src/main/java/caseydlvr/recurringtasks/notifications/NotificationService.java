package caseydlvr.recurringtasks.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.TaskActionReceiver;
import caseydlvr.recurringtasks.model.DueStatus;
import caseydlvr.recurringtasks.model.NotificationOption;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.ui.MainActivity;
import caseydlvr.recurringtasks.ui.settings.SettingsActivity;
import caseydlvr.recurringtasks.ui.taskdetail.TaskActivity;

import static caseydlvr.recurringtasks.notifications.NotificationReceiver.*;
import static caseydlvr.recurringtasks.notifications.NotificationUtils.*;

/**
 * JobIntentService for sending notifications for Tasks
 */
public class NotificationService extends JobIntentService {

    private static final String TAG = NotificationService.class.getSimpleName();

    public static final String GROUP_KEY_TASKS = "caseydlvr.recurringtasks.notifications.TASKS";
    static final String NOTIFICATION_CHANNEL_ID = "task_channel";
    static final String NOTIFICATION_CHANNEL_NAME = "Due task notification";
    static final int JOB_ID = 999;
    static final int NOTIFICATION_SUMMARY_ID = -1;

    private int mMaxPriority;
    private int mMaxNotifications;
    private List<Task> mNotificationTasks;

    /**
     * Wrapper for JobIntentService.enqueueWork(context, cls, jobId, work). Either directly starts
     * the service (pre-O) or enqueues the work as a job (O and later). Work ends up in onHandleWork().
     *
     * @param context Context to use for the Service
     * @param work    Intent to use for the Service (passed to onHandleWork)
     */
    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_SEND_NOTIFICATIONS:
                    initFields();
                    handleSendNotifications();
                    break;
            }
        }
    }

    /**
     * Initializes private fields. Gets values from user preferences when appropriate
     */
    private void initFields() {
        SharedPreferences settingsPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mMaxNotifications = Integer.parseInt(settingsPrefs.getString(
                SettingsActivity.KEY_MAX_NOTIFICATIONS, getDefaultMaxNotifications()));
        mMaxPriority = DueStatus.PRIORITY_DUE;
    }

    /**
     * Handler for SEND_NOTIFICATIONS action. Fetches outstanding tasks with notifications enabled,
     * determines which of these tasks should have notifications sent right now, then sends the
     * notifications.
     */
    private void handleSendNotifications() {
        // asynchronously get all outstanding tasks with notifications enabled
        LiveData<List<Task>> observableTasks = ((RecurringTaskApp) getApplication())
                .getRepository()
                .loadOutstandingTasksWithNotifications();

        // when async load completes, send notifications
        Observer<List<Task>> observer = new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable List<Task> tasks) {
                if (tasks != null && !tasks.isEmpty()) {
                    createNotificationTasks(tasks);
                    sendNotifications();
                    observableTasks.removeObserver(this);
                }
            }
        };

        observableTasks.observeForever(observer);
    }

    /**
     * Creates a list of Tasks that area ready for notifications and saves it to the
     * mNotificationTasks field.
     *
     * @param tasks List of outstanding Tasks that have notifications enabled
     */
    private void createNotificationTasks(List<Task> tasks) {
        Collections.sort(tasks, new Task.TaskComparator());
        mNotificationTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getDuePriority() > mMaxPriority) break;
            if (mNotificationTasks.size() >= mMaxNotifications) break;
            if (task.getNotificationOption().equals(NotificationOption.KEY_OVERDUE)
                    && task.getDuePriority() > DueStatus.PRIORITY_OVERDUE) continue;

            // add to front of list so lower priority notifications are sent first
            mNotificationTasks.add(0, task);
        }
    }

    /**
     * Whether notifications should be grouped. This depends on Android version. Pre-N
     * devices cannot expand a notification group to reveal the individual child notifications,
     * which means functionality to complete a task and launch a task detail view is lost when
     * grouping. Therefore, grouping isn't enabled until more than 3 notifications are active. On N
     * and newer devices there is little downside to grouping (since the individual notifications
     * can still be accessed), so notifications are grouped as soon as there are more than 1.
     *
     * @return boolean indicating whether notifications should be grouped (true) or not (false)
     */
    private boolean useGrouping() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return mNotificationTasks.size() > 3;
        } else {
            return mNotificationTasks.size() > 1;
        }
    }

    /**
     * Sends a notification for each Task in mNotificationTasks. If the criteria for grouping
     * notifications are met, a group summary notification is also sent.
     */
    private void sendNotifications() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        for (Task task : mNotificationTasks) {
            manager.notify((int) task.getId(), buildTaskNotification(task));
        }

        if (useGrouping()) {
            manager.notify(NOTIFICATION_SUMMARY_ID, buildSummaryNotification());
        }
    }

    /**
     * Builds a task Notification using the provided Task
     *
     * @param task Task to use for building Notification
     * @return     Notification for a Task
     */
    private Notification buildTaskNotification(@NonNull Task task) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(task.getName())
                .setContentText(getNotificationContent(task))
                .setSmallIcon(R.drawable.ic_notification_clock)
                .setContentIntent(buildClickPendingIntent(task.getId()))
                .setAutoCancel(true)
                .addAction(R.drawable.ic_notification_check,
                        getString(R.string.complete),
                        buildCompletePendingIntent(task.getId()))
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setColor(getResources().getColor(R.color.primaryColor));

        if (useGrouping()) {
            builder.setGroup(GROUP_KEY_TASKS)
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY);
        }

        return builder.build();
    }

    /**
     * Builds a Notification to use as a group summary. This is an inbox style notification that
     * shows a one line summary of each notification in the group.
     *
     * @return group summary Notification
     */
    private Notification buildSummaryNotification() {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        // Summary lines for Pre-N devices (created automatically from child content on N+)
        for (Task task : mNotificationTasks) {
            // \u2022 is unicode for bullet •, middle dot · (\u00B7) is another option
            inboxStyle.addLine(task.getName() + " \u2022 " + getNotificationContent(task));
        }

        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_clock_history)
                .setContentTitle(getString(R.string.app_name)) // for pre-N
                .setContentText(mNotificationTasks.size() + " " + getString(R.string.tasksDue)) // for pre-N
                .setStyle(inboxStyle)
                .setColor(getResources().getColor(R.color.primaryColor))
                .setContentIntent(buildSummaryPendingIntent())
                .setGroup(GROUP_KEY_TASKS)
                .setGroupSummary(true)
                .build();
    }

    /**
     * Builds a PendingIntent for opening the detail activity for the Task with the provided id
     *
     * @param id Task ID
     * @return   PendingIntent to launch a Task detail view
     */
    private PendingIntent buildClickPendingIntent(long id) {
        Intent clickIntent = new Intent(this, TaskActivity.class);
        clickIntent.putExtra(TaskActivity.EXTRA_TASK_ID, id);

        return PendingIntent.getActivity(this,
                (int) id, // so intents for other tasks aren't updated
                clickIntent,
                0);
    }

    /**
     * Builds a PendingIntent for completing the Task associated with the provided id
     *
     * @param id Task ID
     * @return   PendingIntent to complete a Task
     */
    private PendingIntent buildCompletePendingIntent(long id) {
        Intent completeIntent = new Intent(this, TaskActionReceiver.class);
        completeIntent.setAction(TaskActionReceiver.ACTION_COMPLETE);
        completeIntent.putExtra(TaskActionReceiver.EXTRA_TASK_ID, id);

        return PendingIntent.getBroadcast(this,
                (int) id, // so intents for other tasks aren't updated
                completeIntent,
                0);
    }

    /**
     * Builds a PendingIntent for opening the app to the Activity or Fragment that shows the list
     * of all Tasks
     *
     * @return PendingIntent to open the list of Tasks
     */
    private PendingIntent buildSummaryPendingIntent() {
        Intent summaryIntent = new Intent(this, MainActivity.class);

        return PendingIntent.getActivity(this,
                0,
                summaryIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Builds a string summary of the due status of a Task
     *
     * @param task Task to build summary string for
     * @return     String summarizing task
     */
    @NonNull
    private String getNotificationContent(@NonNull Task task) {
        return getString(R.string.dueDateDetailLabel) + " " +
                task.getDueDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
    }

}
