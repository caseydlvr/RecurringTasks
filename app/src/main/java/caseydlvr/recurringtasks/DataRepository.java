package caseydlvr.recurringtasks;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.WorkerThread;

import java.util.List;

import caseydlvr.recurringtasks.db.AppDatabase;
import caseydlvr.recurringtasks.model.Task;

public class DataRepository {

    private static final String TAG = DataRepository.class.getSimpleName();

    private static DataRepository sInstance;

    private final AppDatabase mDb;
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();

    private DataRepository(final AppDatabase db) {
        mDb = db;
    }

    public static DataRepository getInstance(AppDatabase db) {
        if (sInstance == null) {
            sInstance = new DataRepository(db);
        }

        return sInstance;
    }

    public void persist(Task task) {
        new PersistTask(this).execute(task);
    }

    public LiveData<Task> loadTaskById(final long id) {
        return mDb.taskDao().loadById(id);
    }

    public LiveData<List<Task>> loadOutstandingTasks() {
        return mDb.taskDao().loadAllOutstanding();
    }

    public LiveData<List<Task>> loadOutstandingTasksWithNotifications() {
        return mDb.taskDao().loadOutstandingWithNotifications();
    }

    public void complete(Task task) {
        new CompleteTask(this).execute(task);
    }

    public void completeById(long id) {
        new CompleteByIdTask(this).execute(id);
    }

    public void delete(Task task) {
        new DeleteTask(this).execute(task);
    }

    public MutableLiveData<Boolean> isLoading() {
        return mIsLoading;
    }

    public void isLoading(boolean isLoading) {
        mIsLoading.setValue(isLoading);
    }

    public AppDatabase getDb() {
        return mDb;
    }

    private static class PersistTask extends AsyncTask<Task, Void, Void> {
        DataRepository mDr;

        PersistTask(DataRepository dr) {
            mDr = dr;
        }

        @Override
        protected void onPreExecute() {
            mDr.isLoading(true);
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            mDr.getDb().taskDao().insert(tasks[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDr.isLoading(false);
        }
    }

    private static class CompleteTask extends AsyncTask<Task, Void, Void> {
        DataRepository mDr;

        CompleteTask(DataRepository dr) {
            mDr = dr;
        }

        @Override
        protected void onPreExecute() {
            mDr.isLoading(true);
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            completeTask(mDr.getDb(), tasks[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDr.isLoading(false);
        }
    }

    private static class CompleteByIdTask extends AsyncTask<Long, Void, Void> {
        DataRepository mDr;

        CompleteByIdTask(DataRepository dr) {
            mDr = dr;
        }

        @Override
        protected Void doInBackground(Long... longs) {
            Task task = mDr.getDb().taskDao().loadByIdTask(longs[0]);

            completeTask(mDr.getDb(), task);

            return null;
        }
    }

    @WorkerThread
    private static void completeTask(AppDatabase db, Task task) {
        db.runInTransaction(() -> {
            db.taskDao().delete(task);

            if (task.isRepeating()) {
                Task newTask = new Task();
                newTask.setName(task.getName());
                newTask.setDuration(task.getDuration());
                newTask.setDurationUnit(task.getDurationUnit());
                newTask.setRepeating(task.isRepeating());
                newTask.setUsesNotifications(task.usesNotifications());

                db.taskDao().insert(newTask);
            }
        });
    }

    private static class DeleteTask extends AsyncTask<Task, Void, Void> {
        DataRepository mDr;

        DeleteTask(DataRepository dr) {
            mDr = dr;
        }

        @Override
        protected void onPreExecute() {
            mDr.isLoading(true);
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            mDr.getDb().taskDao().delete(tasks[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDr.isLoading(false);
        }
    }

}
