package caseydlvr.recurringtasks.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import caseydlvr.recurringtasks.DataRepository;
import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.TaskActions;
import caseydlvr.recurringtasks.model.Task;

/**
 * ViewModel for the Task detail view. User is expected to call init(long) after the constructor
 * prior to calling any methods that use mTask. This is necessary because using a constructor that
 * takes a Task ID isn't possible when using ViewModelProviders to instantiate the ViewModel.
 */
public class TaskViewModel extends AndroidViewModel {

    private LiveData<Task> mTask;
    private DataRepository mRepository;

    public TaskViewModel(@NonNull Application app) {
        super(app);
        mRepository = ((RecurringTaskApp) app).getRepository();
    }

    /**
     * @return Task wrapped in LiveData
     */
    public LiveData<Task> getTask() {
        return mTask;
    }

    /**
     * Tells the repository to persist the provided Task
     *
     * @param task Task to persist
     */
    public void persist(Task task) {
        mRepository.persist(task);
    }

    /**
     * Tells the repository to complete the provided Task in persistent storage
     *
     * @param task Task to persist
     */
    public void complete(@NonNull Task task) {
        TaskActions.complete(getApplication(),task);
    }

    /**
     * Tells the repository to delete the provided Task from persistent storage
     *
     * @param task Task to delete
     */
    public void delete(Task task) {
        mRepository.delete(task);
    }

    /**
     * Initialize this ViewModel to work with a Task with the provided ID. Starts the async loading
     * of the Task by the repository.
     *
     * @param taskId Task ID to use with this ViewModel
     */
    public void init(long taskId) {
        mTask = mRepository.loadTaskById(taskId);
    }
}
