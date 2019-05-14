package caseydlvr.recurringtasks.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import caseydlvr.recurringtasks.DataRepository;
import caseydlvr.recurringtasks.api.ApiServer;
import caseydlvr.recurringtasks.db.AppDatabase;
import caseydlvr.recurringtasks.model.Deletion;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.model.TaskWithTags;

public class Sync {
    private DataRepository mDr;
    private ApiServer mServer;

    public Sync(AppDatabase db) {
        mDr = DataRepository.getInstance(db);
        mServer = new ApiServer();
    }

    public void startTaskSync() {
        List<Task> unsyncedTasks;

        try {
            unsyncedTasks = mDr.loadUnsyncedTasks();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        List<TaskWithTags> unsyncedTasksWithTags = new ArrayList<>();

        for (Task task : unsyncedTasks) {
            try {
                List<Tag> tags = mDr.loadTagsByTask(task.getId());
                unsyncedTasksWithTags.add(new TaskWithTags(task, tags));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (TaskWithTags task : unsyncedTasksWithTags) {
            if (task.getServerId() > 0) {
                mServer.updateTask(task.getServerId(), task);
            } else {
                mServer.createTask(task);
            }
        }
    }

    public void startTagSync() {
        List<Tag> unsyncedTags;

        try {
            unsyncedTags = mDr.loadUnsyncedTags();
        } catch (Exception e){
            e.printStackTrace();
            return;
        }

        for (Tag tag : unsyncedTags) {
            if (tag.getServerId() > 0) {
                mServer.updateTag(tag.getServerId(), tag);
            } else {
                mServer.createTag(tag);
            }
        }
    }

    public void startDeleteSync() {
        try {
            List<Deletion> deletedTags = mDr.loadDeletedTags();

            for (Deletion deletedTag : deletedTags) {
                mServer.deleteTag(deletedTag.getTagId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            List<Deletion> deletedTasks = mDr.loadDeletedTasks();

            for (Deletion deletedTask : deletedTasks) {
                mServer.deleteTask(deletedTask.getTaskId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
