package caseydlvr.recurringtasks;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import caseydlvr.recurringtasks.db.AppDatabase;
import caseydlvr.recurringtasks.model.Task;

public class DataRepository {

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

    public void persist(Task task) {
        new PersistTask(mDb).execute(task);
    }

    public LiveData<Task> loadTaskById(final long id) {
        return mDb.taskDao().loadById(id);
    }

    public LiveData<List<Task>> loadOutstandingTasks() {
        return mDb.taskDao().loadAllOutstanding();
    }

    public void complete(Task task) {
        new CompleteTask(mDb).execute(task);
    }

    private static class PersistTask extends AsyncTask<Task, Void, Void> {
        AppDatabase mDb;

        PersistTask(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            mDb.taskDao().insert(tasks[0]);

            return null;
        }
    }

    private static class CompleteTask extends AsyncTask<Task, Void, Void> {
        AppDatabase mDb;

        CompleteTask(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            mDb.runInTransaction(() -> {
                mDb.taskDao().delete(tasks[0]);

                if (tasks[0].isRepeats()) {
                    Task newTask = new Task();
                    newTask.setName(tasks[0].getName());
                    newTask.setDuration(tasks[0].getDuration());
                    newTask.setDurationUnit(tasks[0].getDurationUnit());
                    newTask.setRepeats(tasks[0].isRepeats());

                    mDb.taskDao().insert(newTask);
                }
            });

            return null;
        }
    }
}
