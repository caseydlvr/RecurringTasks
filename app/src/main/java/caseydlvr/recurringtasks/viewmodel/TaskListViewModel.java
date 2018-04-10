package caseydlvr.recurringtasks.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import caseydlvr.recurringtasks.DataRepository;
import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.model.Task;

public class TaskListViewModel extends AndroidViewModel {

    private final LiveData<List<Task>> mObservableTasks;
    private DataRepository mRepository;
    private LiveData<Boolean> mIsLoading;

    public TaskListViewModel(@NonNull Application app) {
        super(app);
        mRepository = ((RecurringTaskApp) app).getRepository();
        mObservableTasks = mRepository.loadOutstandingTasks();
        mIsLoading = mRepository.isLoading();
    }

    public LiveData<List<Task>> getOutstandingTasks() {
        return mObservableTasks;
    }

    public LiveData<Boolean> isLoading() {
        return mIsLoading;
    }

    public void complete(Task task) {
        mRepository.complete(task);
    }

    public void delete(Task task) {
        mRepository.delete(task);
    }

}
