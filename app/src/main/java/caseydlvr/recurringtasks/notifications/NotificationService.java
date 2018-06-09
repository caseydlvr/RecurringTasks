package caseydlvr.recurringtasks.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.TaskActionReceiver;
import caseydlvr.recurringtasks.model.DueStatus;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.ui.taskdetail.TaskActivity;

public class NotificationService extends JobIntentService {

    private static final String TAG = NotificationService.class.getSimpleName();

    public static final String EXTRA_MAX_PRIORITY = "NotificationService_Max_Priority";
    public static final String EXTRA_MAX_NOTIFICATIONS = "NotificationService_Max_Notifications";
    static final String NOTIFICATION_CHANNEL_ID = "task_channel";
    static final String NOTIFICATION_CHANNEL_NAME = "Due task notification";
    static final int JOB_ID = 999;
    static final int DEFAULT_MAX_NOTIFICATIONS = 4;

    private int mMaxPriority;
    private int mMaxNotifications;

    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case NotificationReceiver.ACTION_SEND_NOTIFICATIONS:
                    mMaxPriority = intent.getIntExtra(EXTRA_MAX_PRIORITY, DueStatus.PRIORITY_DUE);
                    mMaxNotifications = intent.getIntExtra(EXTRA_MAX_NOTIFICATIONS, DEFAULT_MAX_NOTIFICATIONS);
                    handleSendNotifications();
                    break;
            }
        }
    }

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
                    sendNotifications(getNotificationTasks(tasks));
                    observableTasks.removeObserver(this);
                }
            }
        };

        observableTasks.observeForever(observer);
    }

    private List<Task> getNotificationTasks(List<Task> tasks) {
        Collections.sort(tasks, new Task.TaskComparator());
        List<Task> notificationTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getDuePriority() > mMaxPriority) break;
            if (notificationTasks.size() >= mMaxNotifications) break;

            // add to front of list so lower priority notifications are sent first
            notificationTasks.add(0, task);
        }

        return notificationTasks;
    }

    private void sendNotifications(List<Task> tasks) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        for (Task task : tasks) {
            manager.notify((int) task.getId(), buildTaskNotification(task));
        }
    }

    private Notification buildTaskNotification(Task task) {
        String notificationContent = getString(R.string.dueDateDetailLabel) + " " +
                task.getDueDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));

        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(task.getName())
                .setContentText(notificationContent)
                .setSmallIcon(R.drawable.ic_notification_clock)
                .setContentIntent(buildClickPendingIntent(task.getId()))
                .setAutoCancel(true)
                .addAction(R.drawable.ic_notification_check,
                        getString(R.string.complete),
                        buildCompletePendingIntent(task.getId()))
                .setCategory(Notification.CATEGORY_REMINDER)
                .setColor(getResources().getColor(R.color.primaryColor))
                .build();
    }

    private PendingIntent buildClickPendingIntent(long id) {
        Intent clickIntent = new Intent(this, TaskActivity.class);
        clickIntent.putExtra(TaskActivity.EXTRA_TASK_ID, id);

        return PendingIntent.getActivity(this,
                (int) id, // so intents for other tasks aren't updated
                clickIntent,
                0);
    }

    private PendingIntent buildCompletePendingIntent(long id) {
        Intent completeIntent = new Intent(this, TaskActionReceiver.class);
        completeIntent.setAction(TaskActionReceiver.ACTION_COMPLETE);
        completeIntent.putExtra(TaskActionReceiver.EXTRA_TASK_ID, id);

        return PendingIntent.getBroadcast(this,
                (int) id, // so intents for other tasks aren't updated
                completeIntent,
                0);
    }

    public static void dismissNotification(Context context, int id) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);
    }

    public static void dismissNotifications(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }
}
