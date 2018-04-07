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

    public TaskListViewModel(@NonNull Application app) {
        super(app);
        mRepository = ((RecurringTaskApp) app).getRepository();
        mObservableTasks = mRepository.loadOutstandingTasks();
    }

    public LiveData<List<Task>> getOutstandingTasks() {
        return mObservableTasks;
    }

    public void complete(Task task) {
        mRepository.complete(task);
    }

}
