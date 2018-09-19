package caseydlvr.recurringtasks.viewmodel;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

import caseydlvr.recurringtasks.DataRepository;
import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.TaskWithTags;

/**
 * ViewModel for the task list view
 */
public class TaskListViewModel extends TaskViewModel {

    private LiveData<List<TaskWithTags>> mAllTasksWithTags;
    private LiveData<List<TaskWithTags>> mFilteredTasksWithTags;
    private LiveData<List<Tag>> mAllTags;
    private LiveData<Tag> mFilterTag;
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
        mAllTags = mRepository.loadAllTags();
        mAllTasksWithTags = mRepository.loadAllTasksAsTaskWithTags();
    }

    /**
     * @param tagId id of the Tag to use to filter the list of Tasks
     */
    public void setFilterTagId(int tagId) {
        if (tagId != mFilterTagId) {
            mFilterTagId = tagId;
            mFilteredTasksWithTags = mRepository.loadTasksAsTasksWithTagForTag(mFilterTagId);
            mFilterTag = mRepository.loadTagById(tagId);
        }
    }

    public LiveData<List<TaskWithTags>> getFilteredTasksWithTags() {
        return mFilteredTasksWithTags;
    }

    public LiveData<List<Tag>> getAllTags() {
        return mAllTags;
    }

    public LiveData<Tag> getFilterTag() {
        return mFilterTag;
    }

    public LiveData<List<TaskWithTags>> getAllTasksWithTags() {
        return mAllTasksWithTags;
    }

    public int getFilterTagId() {
        return mFilterTagId;
    }
}
