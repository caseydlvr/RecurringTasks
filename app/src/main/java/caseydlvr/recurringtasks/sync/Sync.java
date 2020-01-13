package caseydlvr.recurringtasks.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import caseydlvr.recurringtasks.DataRepository;
import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.api.ApiServer;
import caseydlvr.recurringtasks.model.Deletion;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.model.TaskWithTags;
import caseydlvr.recurringtasks.model.TasksAndTags;
import retrofit2.HttpException;

public class Sync {

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
        List<TaskWithTags> unsyncedTasksWithTags = buildTasksWithTags(unsyncedTasks);

        for (TaskWithTags task : unsyncedTasksWithTags) {
            if (task.getId() != null) {
                mServer.updateTask(task);
            } else {
                mServer.createTask(task);
            }
        }
    }

    public void startTagSync() {
        List<Tag> unsyncedTags = mDr.loadUnsyncedTagsSync();

        for (Tag tag : unsyncedTags) {
            if (tag.getId() != null) {
                mServer.updateTag(tag);
            } else {
                mServer.createTag(tag);
            }
        }
    }

    public void startDeleteSync() {
        List<Deletion> deletedTags = mDr.loadDeletedTagsSync();

        for (Deletion deletedTag : deletedTags) {
            mServer.deleteTag(deletedTag.getTagId());
        }

        List<Deletion> deletedTasks = mDr.loadDeletedTasksSync();

        for (Deletion deletedTask : deletedTasks) {
            mServer.deleteTask(deletedTask.getTaskId());
        }
    }

    public void startFulLExport() throws IOException, HttpException {
        List<Task> allTasks = mDr.loadAllTasksSync();
        List<TaskWithTags> allTasksWithTags = buildTasksWithTags(allTasks);
        List<Tag> allTags = mDr.loadAllTagsSync();

        TasksAndTags tasksAndTags = new TasksAndTags();
        tasksAndTags.setTaskWithTags(allTasksWithTags);
        tasksAndTags.setTags(allTags);

        syncTasksAndTags(mServer.fullExport(tasksAndTags));
    }

    private void syncTasksAndTags(TasksAndTags tasksAndTags) {
        List<TaskWithTags> tasks = tasksAndTags.getTaskWithTags();
        List<Tag> tags = tasksAndTags.getTags();

        for (Tag tag : tags) {
            mDr.syncTag(tag);
        }

        for (TaskWithTags task : tasks) {
            mDr.syncTask(task);
        }
    }

    private List<TaskWithTags> buildTasksWithTags(List<Task> tasks) {
        List<TaskWithTags> tasksWithTags = new ArrayList<>();

        for (Task task : tasks) {
            List<Tag> tags = mDr.loadTagsByTaskSync(task.getId());
            tasksWithTags.add(new TaskWithTags(task, tags));
        }

        return tasksWithTags;
    }
}
