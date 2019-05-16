package caseydlvr.recurringtasks.sync;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import caseydlvr.recurringtasks.DataRepository;
import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.api.ApiServer;
import caseydlvr.recurringtasks.model.Deletion;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.model.TaskWithTags;

public class Sync {

    private static final String TAG = Sync.class.getSimpleName();

    private DataRepository mDr;
    private ApiServer mServer;

    public Sync(RecurringTaskApp app) {
        mDr = app.getRepository();
        mServer = new ApiServer(mDr);
    }

    public void start() {
        startTagSync();
        startTaskSync();
        startDeleteSync();
    }

    public void startTaskSync() {
        List<Task> unsyncedTasks = mDr.loadUnsyncedTasksSync();

        List<TaskWithTags> unsyncedTasksWithTags = new ArrayList<>();

        for (Task task : unsyncedTasks) {
            List<Tag> tags = mDr.loadTagsByTaskSync(task.getId());
            unsyncedTasksWithTags.add(new TaskWithTags(task, tags));
        }

        for (TaskWithTags task : unsyncedTasksWithTags) {
            Log.d(TAG, "unsycned TaskWithTags: " + task);
            if (task.getServerId() > 0) {
                mServer.updateTask(task.getServerId(), task);
            } else {
                mServer.createTask(task);
            }
        }
    }

    public void startTagSync() {
        List<Tag> unsyncedTags = mDr.loadUnsyncedTagsSync();

        for (Tag tag : unsyncedTags) {
            Log.d(TAG, "unsycned tag: " + tag);

            if (tag.getServerId() > 0) {
                mServer.updateTag(tag.getServerId(), tag);
            } else {
                mServer.createTag(tag);
            }
        }
    }

    public void startDeleteSync() {
        List<Deletion> deletedTags = mDr.loadDeletedTagsSync();

        for (Deletion deletedTag : deletedTags) {
            mServer.deleteTag(deletedTag.getTagServerId());
        }

        List<Deletion> deletedTasks = mDr.loadDeletedTasksSync();

        for (Deletion deletedTask : deletedTasks) {
            mServer.deleteTask(deletedTask.getTaskServerId());
        }
    }

}
