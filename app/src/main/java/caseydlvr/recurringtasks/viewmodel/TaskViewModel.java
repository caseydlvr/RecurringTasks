package caseydlvr.recurringtasks.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import caseydlvr.recurringtasks.DataRepository;
import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.model.Task;

public class TaskViewModel extends AndroidViewModel {

    private LiveData<Task> mTask;
    private DataRepository mRepository;

    public TaskViewModel(@NonNull Application app) {
        super(app);
        mRepository = ((RecurringTaskApp) app).getRepository();
    }

    public LiveData<Task> getTask() {
        return mTask;
    }

    public void persist(Task task) {
        mRepository.persist(task);
    }

    public void complete(Task task) {
        mRepository.complete(task);
    }

    public void delete(Task task) {
        mRepository.delete(task);
    }

    public void init(long taskId) {
        mTask = mRepository.loadTaskById(taskId);
    }
}
