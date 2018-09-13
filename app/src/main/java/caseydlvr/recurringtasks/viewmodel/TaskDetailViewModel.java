package caseydlvr.recurringtasks.viewmodel;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import caseydlvr.recurringtasks.DataRepository;
import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.model.TaskTag;

/**
 * ViewModel for the Task detail view. User is expected to call init(long) after the constructor
 * prior to calling any methods that use mTask. This is necessary because using a constructor that
 * takes a Task ID isn't possible when using ViewModelProviders to instantiate the ViewModel.
 */
public class TaskDetailViewModel extends TaskViewModel {

    private long mTaskId;
    private LiveData<Task> mTask;
    private LiveData<List<Tag>> mTags;
    private DataRepository mRepository;

    public TaskDetailViewModel(@NonNull Application app) {
        super(app);
        mRepository = ((RecurringTaskApp) app).getRepository();
    }

    /**
     * Initialize this ViewModel to work with a Task with the provided ID. Starts the async loading
     * of the Task by the repository.
     *
     * @param taskId Task ID to use with this ViewModel
     */
    public void init(long taskId) {
        mTaskId = taskId;
        mTask = mRepository.loadTaskById(taskId);
        mTags = mRepository.loadTagsForTask(taskId);
    }

    /**
     * @return Task wrapped in LiveData
     */
    public LiveData<Task> getTask() {
        return mTask;
    }

    public LiveData<List<Tag>> getTags() {
        return mTags;
    }

    /**
     * Persist the provided Task
     *
     * @param task Task to save
     */
    public void saveTask(Task task) {
        mRepository.saveTask(task);
    }

    public void removeTag(Tag tag) {
        TaskTag taskTag = new TaskTag(mTaskId, tag.getId());
        mRepository.removeTaskTag(taskTag);
    }
}
