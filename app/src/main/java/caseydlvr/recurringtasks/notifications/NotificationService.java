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

    static final int JOB_ID = 999;
    public static final int NOTIFICATION_ID = 1;
    static final String NOTIFICATION_CHANNEL_ID = "task_channel";
    static final String NOTIFICATION_CHANNEL_NAME = "Due task notification";

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
                if (tasks != null) {
                    if (!tasks.isEmpty()) {
                        Collections.sort(tasks, new Task.TaskComparator());
                        Task top = tasks.get(0);
                        if (top.getDuePriority() <= DueStatus.PRIORITY_DUE) {
                            sendNotification(top);
                        }
                    }
                    observableTasks.removeObserver(this);
                }
            }
        };

        observableTasks.observeForever(observer);
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

        Intent clickIntent = new Intent(this, TaskActivity.class);
        clickIntent.putExtra(TaskActivity.EXTRA_TASK_ID, task.getId());
        PendingIntent clickPendingIntent = PendingIntent.getActivity(this,
                0,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent completeIntent = new Intent(this, TaskActionReceiver.class);
        completeIntent.setAction(TaskActionReceiver.ACTION_COMPLETE);
        completeIntent.putExtra(TaskActionReceiver.EXTRA_TASK_ID, task.getId());
        PendingIntent completePendingIntent = PendingIntent.getBroadcast(this,
                0,
                completeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String notificationContent = "Due around " +
                task.getDueDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));

        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(task.getName())
                .setContentText(notificationContent)
                .setSmallIcon(R.drawable.ic_notification_clock)
                .setContentIntent(clickPendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_notification_check, getString(R.string.complete), completePendingIntent)
                .setCategory(Notification.CATEGORY_REMINDER)
                .setColor(getResources().getColor(R.color.primaryColor))
                .build();

        manager.notify(NOTIFICATION_ID, notification);
    }
}
