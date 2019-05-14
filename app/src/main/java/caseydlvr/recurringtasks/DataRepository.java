package caseydlvr.recurringtasks;

import androidx.lifecycle.LiveData;
import android.os.AsyncTask;
import androidx.annotation.WorkerThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import caseydlvr.recurringtasks.db.AppDatabase;
import caseydlvr.recurringtasks.model.Deletion;
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

    private AppExecutors mAppExecutors;

    private DataRepository(final AppDatabase db, AppExecutors appExecutors) {
        mDb = db;
        mAppExecutors = appExecutors;
    }

    public static DataRepository getInstance(AppDatabase db, AppExecutors appExecutors) {
        if (sInstance == null) {
            sInstance = new DataRepository(db, appExecutors);
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
     * Loads all Tasks. List of Tasks is returned wrapped in LiveData.
     *
     * @return LiveData holding a List of Tasks
     */
    public LiveData<List<Task>> observeAllTasks() {
        return mDb.taskDao().observeAll();
    }

    /**
     * Loads all Tasks wrapped in a TaskWithTagIds object (which also includes a list of tagIds
     * for all Tags associated with the Task
     *
     * @return LiveData holding a List of TaskWithTagIds
     */
    public LiveData<List<TaskWithTagIds>> observeAllTasksWithTagIds() {
        return mDb.taskDao().observeAllWithTagIds();
    }

    /**
     * Loads Tasks that are tagged with the Tag represented by the the given tagId
     *
     * @param tagId id of Tag to load Tasks for
     * @return      LiveData holding a List of Tasks
     */
    public LiveData<List<Task>> observeTasksByTag(int tagId) {
        return mDb.taskDao().observeByTag(tagId);
    }

    /**
     * Loads Tasks (as TaskWithTagIds) that are tagged with the Tag represented by the given tagId
     *
     * @param tagId id of Tag to load Tasks for
     * @return      LiveData holding a List of TaskWithTagIds
     */
    public LiveData<List<TaskWithTagIds>> observeTasksByTagWithTagIds(int tagId) {
        return mDb.taskDao().observeByTagWithTagIds(tagId);
    }

    /**
     * Loads a single Task with the provided ID from persistent storage. Task returned is returned
     * wrapped in LiveData.
     *
     * @param id ID of Task to load
     * @return   LiveData holding a Task.
     */
    public LiveData<Task> observeTaskById(final long id) {
        return mDb.taskDao().observeById(id);
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
     * Synchronously loads all unsynced Tasks. The DB query is performed on the same thread as the
     * caller, therefore this method cannot be called from the main thread.
     *
     * @return List of unsynced Tasks
     */
    @WorkerThread
    public List<Task> loadUnsyncedTasksSync() {
        return mDb.taskDao().loadUnsynced();
    }

    /**
     * Synchronously loads all task Deletions. The DB query is performed on the same thread as the
     * caller, therefore this method cannot be called from the main thread.
     *
     * @return List of task Deletions
     */
    @WorkerThread
    public List<Deletion> loadDeletedTasksSync() {
        return mDb.deletionDao().loadTaskDeletionsWithServerId();
    }

    /**
     * Persists (inserts or updates) the provided Task
     *
     * @param task Task to save
     */
    public void saveTask(Task task) {
        mAppExecutors.diskIO().execute(() -> {
            task.setSynced(false);

            if (task.getId() > 0) {
                mDb.taskDao().update(task);
            } else {
                mDb.taskDao().insert(task);
            }
        });
    }

    /**
     * Deletes the provided Task
     *
     * @param task Task to delete
     */
    public void deleteTask(Task task) {
        mAppExecutors.diskIO().execute(() -> {
            deleteTaskFromDb(task);
        });
    }

    /**
     * Completes the provided Task.
     *
     * @param task Task to complete
     */
    public void complete(Task task) {
        mAppExecutors.diskIO().execute(() -> {
            completeTaskInDb(task);
        });
    }

    /**
     * Completes the Task with a task ID equal to the provided id.
     *
     * @param id ID of the Task to complete
     */
    public void completeById(long id) {
        mAppExecutors.diskIO().execute(() -> {
            Task task = mDb.taskDao().loadById(id);

            if (task != null) completeTaskInDb(task);
        });
    }


    // TagDao related operations

    /**
     * Loads all Tags in alphabetical order by tag name.
     *
     * @return LiveData holding a List of Tags
     */
    public LiveData<List<Tag>> observeAllTags() {
        return mDb.tagDao().observeAll();
    }

    /**
     * Loads Tags that are are used to tag the Task represented by the given taskId.
     *
     * @param taskId id of Task to load Tags for
     * @return       LiveData holding a List of Tags
     */
    public LiveData<List<Tag>> observeTagsByTask(long taskId) {
        return mDb.tagDao().observeByTask(taskId);
    }

    /**
     * @param tagId id of the Tag to load
     * @return      LiveData holding a Tag
     */
    public LiveData<Tag> observeTagById(int tagId) {
        return mDb.tagDao().observeById(tagId);
    }

    /**
     * Synchronously loads all unsynced Tags. The DB query is performed on the same thread as the
     * caller, therefore this method cannot be called from the main thread.
     *
     * @return List of unsynced Tags
     */
    @WorkerThread
    public List<Tag> loadUnsyncedTagsSync() {
        return mDb.tagDao().loadUnsynced();
    }

    /**
     * Synchronously loads all tag Deletions. The DB query is performed on the same thread as the
     * caller, therefore this method cannot be called from the main thread.
     *
     * @return List of tag Deletions
     */
    @WorkerThread
    public List<Deletion> loadDeletedTagsSync() {
        return mDb.deletionDao().loadTagDeletionsWithServerId();
    }

    /**
     * Synchronously loads all Tags that are related to the provided Task ID. The DB query is
     * performed on the same thread as the caller, therefore this method cannot be called from the
     * main thread.
     *
     * @param taskId ID of Task to load Tags for
     * @return       List of Tags related to the provided taskId
     */
    @WorkerThread
    public List<Tag> loadTagsByTaskSync(long taskId) {
        return mDb.tagDao().loadByTask(taskId);
    }

    /**
     * Persists (inserts or updates) the provided Tag
     *
     * @param tag Tag to save
     */
    public void saveTag(Tag tag) {
        mAppExecutors.diskIO().execute(() -> {
            tag.setSynced(false);

            if (tag.getId() >  0) {
                mDb.tagDao().update(tag);
            } else {
                mDb.tagDao().insert(tag);
            }
        });
    }

    /**
     * Deletes the provided Tag
     *
     * @param tag Tag to delete
     */
    public void deleteTag(Tag tag) {
        mAppExecutors.diskIO().execute(() -> {
            mDb.runInTransaction(() -> {
                mDb.tagDao().delete(tag);

                // if tag has synced with server, queue the deletion for syncing
                if (tag.getServerId() > 0) {
                    mDb.deletionDao().insert(Deletion.tagDeletion(tag));

                    // no longer need to sync any deleted relations for the tag
                    mDb.deletionDao().deleteRelationsForTag(tag.getId());
                }
            });
        });
    }


    // TaskTagDao related operations

    /**
     * Persists (inserts or updates) the given TaskTag
     *
     * @param taskTag TaskTag to save
     */
    public void saveTaskTag(TaskTag taskTag) {
        mAppExecutors.diskIO().execute(() -> {
            mDb.taskTagDao().insert(taskTag);
        });
    }

    /**
     * Deletes the given TaskTag
     *
     * @param taskTag TaskTag to delete
     */
    public void deleteTaskTag(TaskTag taskTag) {
        mAppExecutors.diskIO().execute(() -> {
            mDb.runInTransaction(() -> {
                mDb.taskTagDao().delete(taskTag);

                // if relation is synced to server, queue this deletion for sync
                if (taskTag.isSynced()) {
                    mDb.deletionDao().insert(Deletion.taskTagDeletion(taskTag));
                }

            });
        });
    }

    /**
     * Deletes a task from the local Room DB. Cannot be called from the main thread.
     *
     * @param task Task to delete
     */
    @WorkerThread
    private void deleteTaskFromDb(Task task) {
        mDb.runInTransaction(() -> {
            mDb.taskDao().delete(task);

            // if task has synced with server, queue the deletion for sync
            if (task.getServerId() > 0) {
                mDb.deletionDao().insert(Deletion.taskDeletion(task));

                // no longer need to sync any deleted relations for the task
                mDb.deletionDao().deleteRelationsForTask(task.getId());
            }
        });
    }

    /**
     * Completes a Task in the local Room DB. Cannot be called from the main thread.
     *
     * Complete is a 3 step process if the task is set to repeat. The completed task record is
     * deleted and a new task record is inserted. The new task record has the same data as the
     * completed task, except for startDate which is set to today (by the Task constructor). Finally,
     * if the completed task had any TaskTags, those TaskTags are inserted again using the taskId of
     * the new task. The 3 step process is run in a transaction.
     *
     * @param task Task to complete
     */
    @WorkerThread
    private void completeTaskInDb(Task task) {
        mDb.runInTransaction(() -> {
            List<TaskTag> taskTags = mDb.taskTagDao().loadByTask(task.getId());

            deleteTaskFromDb(task);

            if (task.isRepeating()) {
                Task newTask = new Task();
                newTask.setName(task.getName());
                newTask.setDuration(task.getDuration());
                newTask.setDurationUnit(task.getDurationUnit());
                newTask.setRepeating(task.isRepeating());
                newTask.setNotificationOption(task.getNotificationOption());

                long newTaskId = mDb.taskDao().insert(newTask)[0];

                if (taskTags != null) {
                    for (TaskTag taskTag : taskTags) {
                        taskTag.setTaskId(newTaskId);
                    }

                    mDb.taskTagDao().insert(taskTags.toArray(new TaskTag[0]));
                }
            }
        });
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
