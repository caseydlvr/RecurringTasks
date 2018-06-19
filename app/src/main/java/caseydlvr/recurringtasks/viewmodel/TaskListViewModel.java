package caseydlvr.recurringtasks.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import caseydlvr.recurringtasks.DataRepository;
import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.TaskActions;
import caseydlvr.recurringtasks.model.Task;

/**
 * ViewModel for the task list view
 */
public class TaskListViewModel extends AndroidViewModel {

    private final LiveData<List<Task>> mObservableTasks;
    private DataRepository mRepository;
    private LiveData<Boolean> mIsLoading;

    /**
     * Constructor. This starts the async loading of Tasks by the repository.
     *
     * @param app RecurringTaskApp
     */
    public TaskListViewModel(@NonNull Application app) {
        super(app);
        mRepository = ((RecurringTaskApp) app).getRepository();
        mObservableTasks = mRepository.loadOutstandingTasks();
        mIsLoading = mRepository.isLoading();
    }

    /**
     * Outstanding Tasks are tasks that haven't been completed.
     *
     * @return List of outstanding tasks wrapped in LiveData
     */
    public LiveData<List<Task>> getOutstandingTasks() {
        return mObservableTasks;
    }

    /**
     * @return Boolean wrapped in LiveData indicating whether an async load is in progress
     */
    public LiveData<Boolean> isLoading() {
        return mIsLoading;
    }

    /**
     * Tells the repository to complete the given Task in persistent storage
     *
     * @param task Task to complete
     */
    public void complete(@NonNull Task task) {
        TaskActions.complete(getApplication(), task);
    }

    /**
     * Tells the repository to delete the given Task from persistent storage
     *
     * @param task Task to delete
     */
    public void delete(Task task) {
        mRepository.delete(task);
    }

}
