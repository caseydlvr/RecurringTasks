package caseydlvr.recurringtasks;

import androidx.lifecycle.LiveData;
import android.os.AsyncTask;
import androidx.annotation.WorkerThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import caseydlvr.recurringtasks.db.AppDatabase;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.model.TaskTag;
import caseydlvr.recurringtasks.model.TaskWithTagIds;

/**
 * Singleton that handles CRUD for database storage. Single point of access to the data. Handles
 * logic for determining which data source to use (for example: local DB, web server API)
 */
public class DataRepository {

    private static final String TAG = DataRepository.class.getSimpleName();

    private static DataRepository sInstance;

    private final AppDatabase mDb;

    private DataRepository(final AppDatabase db) {
        mDb = db;
    }

    public static DataRepository getInstance(AppDatabase db) {
        if (sInstance == null) {
            sInstance = new DataRepository(db);
        }

        return sInstance;
    }

    /**
     * @return AppDatabase singleton
     */
    public AppDatabase getDb() {
        return mDb;
    }


    // TaskDao related operations

    /**
     * Loads a single Task with the provided ID from persistent storage. Task returned is returned
     * wrapped in LiveData.
     *
     * @param id ID of Task to load
     * @return   LiveData holding a Task.
     */
    public LiveData<Task> loadTaskById(final long id) {
        return mDb.taskDao().loadById(id);
    }

    /**
     * Loads all Tasks. List of Tasks is returned wrapped in LiveData.
     *
     * @return LiveData holding a List of Tasks
     */
    public LiveData<List<Task>> loadAllTasks() {
        return mDb.taskDao().loadAll();
    }

    /**
     * Loads all Tasks wrapped in a TaskWithTagIds object (which also includes a list of tagIds
     * for all Tags associated with the Task
     *
     * @return LiveData holding a List of TaskWithTagIds
     */
    public LiveData<List<TaskWithTagIds>> loadAllTasksAsTaskWithTags() {
        return mDb.taskDao().loadAllAsTasksWithTags();
    }

    /**
     * Loads all Tasks that haven't been complete that have notifications enabled.
     *
     * @return List of Tasks
     */
    public List<Task> loadTasksWithNotifications() {
        try {
            return new LoadTasksWithNotificationsTask(this).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Persists (inserts or updates) the provided Task
     *
     * @param task Task to save
     */
    public void saveTask(Task task) {
        new SaveTaskTask(this).execute(task);
    }

    /**
     * Deletes the provided Task
     *
     * @param task Task to deleteTask
     */
    public void deleteTask(Task task) {
        new DeleteTaskTask(this).execute(task);
    }

    /**
     * Completes the provided Task.
     *
     * @param task Task to complete
     */
    public void complete(Task task) {
        new CompleteTask(this).execute(task);
    }

    /**
     * Completes the Task with a task ID equal to the provided id.
     *
     * @param id ID of the Task to complete
     */
    public void completeById(long id) {
        new CompleteByIdTask(this).execute(id);
    }


    // TagDao related operations

    /**
     * Loads all Tags in alphabetical order by tag name.
     *
     * @return LiveData holding a List of Tags
     */
    public LiveData<List<Tag>> loadAllTags() {
        return mDb.tagDao().loadAllTags();
    }

    /**
     * @param tagId id of the Tag to load
     * @return      LiveData holding a Tag
     */
    public LiveData<Tag> loadTagById(int tagId) {
        return mDb.tagDao().loadTagById(tagId);
    }

    /**
     * Persists (inserts or updates) the provided Tag
     *
     * @param tag Tag to save
     */
    public void saveTag(Tag tag) {
        new SaveTagTask(this).execute(tag);
    }

    /**
     * Deletes the provided Tag
     *
     * @param tag Tag to deleteTask
     */
    public void deleteTag(Tag tag) {
        new DeleteTagTask(this).execute(tag);
    }


    // TaskTagDao related operations

    /**
     * Loads Tags that are are used to tag the Task represented by the given taskId.
     *
     * @param taskId id of Task to load Tags for
     * @return       LiveData holding a List of Tags
     */
    public LiveData<List<Tag>> loadTagsForTask(long taskId) {
        return mDb.taskTagDao().loadTagsForTask(taskId);
    }

    /**
     * Loads Tasks that are tagged with the Tag represented by the the given tagId
     *
     * @param tagId id of Tag to load Tasks for
     * @return      LiveData holding a List of Tasks
     */
    public LiveData<List<Task>> loadTasksForTag(int tagId) {
        return mDb.taskTagDao().loadTasksForTag(tagId);
    }

    /**
     * Loads Tasks (as TaskWithTagIds) that are tagged with the Tag represented by the given tagId
     *
     * @param tagId id of Tag to load Tasks for
     * @return      LiveData holding a List of TaskWithTagIds
     */
    public LiveData<List<TaskWithTagIds>> loadTasksAsTasksWithTagForTag(int tagId) {
        return mDb.taskTagDao().loadTasksAsTasksWithTagForTag(tagId);
    }

    /**
     * Persists (inserts or updates) the given TaskTag
     *
     * @param taskTag TaskTag to save
     */
    public void saveTaskTag(TaskTag taskTag) {
        new SaveTaskTagTask(this).execute(taskTag);
    }

    /**
     * Deletes the given TaskTag
     *
     * @param taskTag TaskTag to deleteTask
     */
    public void deleteTaskTag(TaskTag taskTag) {
        new DeleteTaskTagTask(this).execute(taskTag);
    }

    /**
     * AsyncTask for persisting a Task to the local Room DB. Helper for saveTask(Task). Either
     * updates the existing DB record if the task already exists, or inserts a new record.
     *
     * @see #saveTask(Task)
     */
    private static class SaveTaskTask extends AsyncTask<Task, Void, Void> {
        DataRepository mDr;

        SaveTaskTask(DataRepository dr) {
            mDr = dr;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            if (tasks[0].getId() > 0) {
                mDr.getDb().taskDao().update(tasks[0]);
            } else {
                mDr.getDb().taskDao().insert(tasks[0]);
            }

            return null;
        }
    }

    /**
     * AsyncTask for deleting a Task. Helper for deleteTask(Task).
     *
     * @see #deleteTask(Task)
     */
    private static class DeleteTaskTask extends AsyncTask<Task, Void, Void> {
        DataRepository mDr;

        DeleteTaskTask(DataRepository dr) {
            mDr = dr;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            mDr.getDb().taskDao().delete(tasks[0]);

            return null;
        }
    }

    /**
     * AsyncTask for completing a Task in the local Room DB. Helper for complete(Task).
     *
     * @see #complete(Task)
     */
    private static class CompleteTask extends AsyncTask<Task, Void, Void> {
        DataRepository mDr;

        CompleteTask(DataRepository dr) {
            mDr = dr;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            completeTask(mDr.getDb(), tasks[0]);

            return null;
        }
    }

    /**
     * AsyncTask for completing a Task by Task ID in the local Room DB. Helper for completeById(long).
     *
     * @see #completeTask(AppDatabase, Task)
     */
    private static class CompleteByIdTask extends AsyncTask<Long, Void, Void> {
        DataRepository mDr;

        CompleteByIdTask(DataRepository dr) {
            mDr = dr;
        }

        @Override
        protected Void doInBackground(Long... longs) {
            Task task = mDr.getDb().taskDao().loadByIdAsTask(longs[0]);

            if (task != null) completeTask(mDr.getDb(), task);

            return null;
        }
    }

    /**
     * Completes a Task in the local Room DB. Helper function for the complete related AsyncTasks.
     * Complete is a 3 step process if the task is set to repeat. The completed task record is
     * deleted and a new task record is inserted. The new task record has the same data as the
     * completed task, except for startDate which is set to today (by the Task constructor). Finally,
     * if the completed task had any TaskTags, those TaskTags are inserted again using the taskId of
     * the new task. The 3 step process is run in a transaction.
     *
     * @param db   AppDatabase to use for query execution
     * @param task Task to complete
     */
    @WorkerThread
    private static void completeTask(AppDatabase db, Task task) {
        db.runInTransaction(() -> {
            List<TaskTag> taskTags = db.taskTagDao().loadTaskTagsForTask(task.getId());

            db.taskDao().delete(task);

            if (task.isRepeating()) {
                Task newTask = new Task();
                newTask.setName(task.getName());
                newTask.setDuration(task.getDuration());
                newTask.setDurationUnit(task.getDurationUnit());
                newTask.setRepeating(task.isRepeating());
                newTask.setNotificationOption(task.getNotificationOption());

                long newTaskId = db.taskDao().insert(newTask);

                if (taskTags != null) {
                    for (TaskTag taskTag : taskTags) {
                        taskTag.setTaskId(newTaskId);
                    }

                    db.taskTagDao().insert(taskTags.toArray(new TaskTag[0]));
                }
            }
        });
    }

    /**
     * AsyncTask for saving a Tag. Helper for saveTag(Tag).
     *
     * @see #saveTag(Tag)
     */
    private static class SaveTagTask extends AsyncTask<Tag, Void, Void> {
        DataRepository mDr;

        SaveTagTask(DataRepository dr) {
            mDr = dr;
        }

        @Override
        protected Void doInBackground(Tag... tags) {
            if (tags[0].getId() >  0) {
                mDr.getDb().tagDao().update(tags[0]);
            } else {
                mDr.getDb().tagDao().insert(tags[0]);
            }

            return null;
        }
    }

    /**
     * AsyncTask for deleting a Tag. Helper for deleteTag(Tag).
     *
     * @see #deleteTag(Tag)
     */
    private static class DeleteTagTask extends AsyncTask<Tag, Void, Void> {
        DataRepository mDr;

        DeleteTagTask(DataRepository dr) {
            mDr = dr;
        }

        @Override
        protected Void doInBackground(Tag... tags) {
            mDr.getDb().tagDao().delete(tags[0]);

            return null;
        }
    }

    /**
     * AsyncTask for saving a TaskTag. Helper for saveTaskTag(TaskTag).
     *
     * @see #saveTaskTag(TaskTag)
     */
    private static class SaveTaskTagTask extends AsyncTask<TaskTag, Void, Void> {
        DataRepository mDr;

        SaveTaskTagTask(DataRepository dr) {
            mDr = dr;
        }

        @Override
        protected Void doInBackground(TaskTag... taskTags) {
            mDr.getDb().taskTagDao().insert(taskTags[0]);

            return null;
        }
    }

    /**
     * AsyncTask for deleting a TaskTag. Helper for deleteTaskTag(TaskTag).
     *
     * @see #deleteTaskTag(TaskTag)
     */
    private static class DeleteTaskTagTask extends AsyncTask<TaskTag, Void, Void> {
        DataRepository mDr;

        DeleteTaskTagTask(DataRepository dr) {
            mDr = dr;
        }

        @Override
        protected Void doInBackground(TaskTag... taskTags) {
            mDr.getDb().taskTagDao().delete(taskTags[0]);

            return null;
        }
    }

    /**
     * AsyncTask for loading Tasks that have notifications enabled. Helper for
     * loadTasksWithNotifications().
     *
     * @see #loadTasksWithNotifications()
     */
    private static class LoadTasksWithNotificationsTask extends AsyncTask<Void, Void, List<Task>> {
        DataRepository mDr;

        LoadTasksWithNotificationsTask(DataRepository dr) {
            mDr = dr;
        }

        @Override
        protected List<Task> doInBackground(Void... voids) {
            return mDr.getDb().taskDao().loadAllWithNotifications();
        }
    }
}
