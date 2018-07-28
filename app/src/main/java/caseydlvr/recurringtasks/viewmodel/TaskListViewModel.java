package caseydlvr.recurringtasks.viewmodel;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

import caseydlvr.recurringtasks.DataRepository;
import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.Task;

/**
 * ViewModel for the task list view
 */
public class TaskListViewModel extends TaskViewModel {

    private LiveData<List<Task>> mObservableTasks;
    private LiveData<List<Tag>> mObservableTags;
    private DataRepository mRepository;
    private int mFilterTagId;

    /**
     * Constructor. This starts the async loading of Tasks by the repository.
     *
     * @param app RecurringTaskApp
     */
    public TaskListViewModel(@NonNull Application app) {
        super(app);
        mRepository = ((RecurringTaskApp) app).getRepository();
        mObservableTags = mRepository.loadAllTags();
    }

    public void init() {
        if (mFilterTagId > 0) {
            mObservableTasks = mRepository.loadTasksForTag(mFilterTagId);
        } else {
            mObservableTasks = mRepository.loadOutstandingTasks();
        }
    }

    public void setFilterTagId(int tagId) {
        mFilterTagId = tagId;
    }

    /**
     * Outstanding Tasks are tasks that haven't been completed.
     *
     * @return List of outstanding tasks wrapped in LiveData
     */
    public LiveData<List<Task>> getOutstandingTasks() {
        return mObservableTasks;
    }

    public LiveData<List<Tag>> getAllTags() {
        return mObservableTags;
    }
}
