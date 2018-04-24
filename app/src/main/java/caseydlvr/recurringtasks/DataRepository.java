package caseydlvr.recurringtasks;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import java.util.List;

import caseydlvr.recurringtasks.db.AppDatabase;
import caseydlvr.recurringtasks.model.Task;

public class DataRepository {

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

    public void complete(Task task) {
        new CompleteTask(this).execute(task);
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
            mDr.getDb().runInTransaction(() -> {
                mDr.getDb().taskDao().delete(tasks[0]);

                if (tasks[0].isRepeats()) {
                    Task newTask = new Task();
                    newTask.setName(tasks[0].getName());
                    newTask.setDuration(tasks[0].getDuration());
                    newTask.setDurationUnit(tasks[0].getDurationUnit());
                    newTask.setRepeats(tasks[0].isRepeats());
                    newTask.setUsesNotifications(tasks[0].usesNotifications());

                    mDr.getDb().taskDao().insert(newTask);
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDr.isLoading(false);
        }
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
