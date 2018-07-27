package caseydlvr.recurringtasks.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

import caseydlvr.recurringtasks.DataRepository;
import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.TaskActions;
import caseydlvr.recurringtasks.model.Task;

/**
 * ViewModel for the task list view
 */
public class TaskListViewModel extends TaskViewModel {

    private final LiveData<List<Task>> mObservableTasks;
    private DataRepository mRepository;

    /**
     * Constructor. This starts the async loading of Tasks by the repository.
     *
     * @param app RecurringTaskApp
     */
    public TaskListViewModel(@NonNull Application app) {
        super(app);
        mRepository = ((RecurringTaskApp) app).getRepository();
        mObservableTasks = mRepository.loadOutstandingTasks();
    }

    /**
     * Outstanding Tasks are tasks that haven't been completed.
     *
     * @return List of outstanding tasks wrapped in LiveData
     */
    public LiveData<List<Task>> getOutstandingTasks() {
        return mObservableTasks;
    }
}
