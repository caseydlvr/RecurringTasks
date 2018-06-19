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
public class TaskDetailViewModel extends AndroidViewModel {

    private LiveData<Task> mTask;
    private DataRepository mRepository;

    public TaskDetailViewModel(@NonNull Application app) {
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
     * Persist the provided Task
     *
     * @param task Task to persist
     */
    public void persist(Task task) {
        mRepository.persist(task);
    }

    /**
     * Complete the provided Task in persistent storage
     *
     * @param task Task to persist
     */
    public void complete(@NonNull Task task) {
        TaskActions.complete(getApplication(),task);
    }

    /**
     * Delete the provided Task from persistent storage
     *
     * @param task Task to delete
     */
    public void delete(@NonNull Task task) {
        TaskActions.delete(getApplication(), task);
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
