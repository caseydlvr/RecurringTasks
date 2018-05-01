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

    public static final int NOTIFICATION_ID = 1;
    static final String NOTIFICATION_CHANNEL_ID = "task_channel";
    static final String NOTIFICATION_CHANNEL_NAME = "Due task notification";
    static final int JOB_ID = 999;

    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "in onHandleWork()");
        LiveData<List<Task>> observableTasks = ((RecurringTaskApp) getApplication())
                .getRepository()
                .loadOutstandingTasksWithNotifications();

        Observer<List<Task>> observer = new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable List<Task> tasks) {
                if (tasks != null && !tasks.isEmpty()) {
                    Task topTask = getHighestPriorityTask(tasks);
                    if (topTask.getDuePriority() <= DueStatus.PRIORITY_DUE) {
                        sendNotification(topTask);
                    }
                    observableTasks.removeObserver(this);
                }
            }
        };

        observableTasks.observeForever(observer);
    }

    private Task getHighestPriorityTask(List<Task> tasks) {
        Collections.sort(tasks, new Task.TaskComparator());

        return tasks.get(0);
    }

    private void sendNotification(Task task) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        manager.notify(NOTIFICATION_ID, buildTaskNotification(task));
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
                0,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent buildCompletePendingIntent(long id) {
        Intent completeIntent = new Intent(this, TaskActionReceiver.class);
        completeIntent.setAction(TaskActionReceiver.ACTION_COMPLETE);
        completeIntent.putExtra(TaskActionReceiver.EXTRA_TASK_ID, id);

        return PendingIntent.getBroadcast(this,
                0,
                completeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void dismissNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NotificationService.NOTIFICATION_ID);
    }
}
