package caseydlvr.recurringtasks.viewmodel;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

import caseydlvr.recurringtasks.DataRepository;
import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.TaskWithTagIds;

/**
 * ViewModel for the task list view
 */
public class TaskListViewModel extends TaskViewModel {

    private LiveData<List<TaskWithTagIds>> mAllTasksWithTags;
    private LiveData<List<TaskWithTagIds>> mFilteredTasksWithTags;
    private LiveData<List<Tag>> mAllTags;
    private LiveData<Tag> mFilterTag;
    private DataRepository mRepository;
    private String mFilterTagId;

    /**
     * Constructor. This starts the async loading of Tasks by the repository.
     *
     * @param app RecurringTaskApp
     */
    public TaskListViewModel(@NonNull Application app) {
        super(app);
        mRepository = ((RecurringTaskApp) app).getRepository();
        mAllTags = mRepository.observeAllTags();
    }

    /**
     * @param tagId id of the Tag to use to filter the list of Tasks
     */
    public void setFilterTagId(String tagId) {
        if (tagId != mFilterTagId) {
            mFilterTagId = tagId;
            mFilteredTasksWithTags = mRepository.observeTasksByTagWithTagIds(mFilterTagId);
            mFilterTag = mRepository.observeTagById(tagId);
        }
    }

    public LiveData<List<TaskWithTagIds>> getFilteredTasksWithTags() {
        return mFilteredTasksWithTags;
    }

    public LiveData<List<Tag>> getAllTags() {
        return mAllTags;
    }

    public LiveData<Tag> getFilterTag() {
        return mFilterTag;
    }

    public LiveData<List<TaskWithTagIds>> getAllTasksWithTags() {
        if (mAllTasksWithTags == null) {
            mAllTasksWithTags = mRepository.observeAllTasksWithTagIds();
        }

        return mAllTasksWithTags;
    }

    public String getFilterTagId() {
        return mFilterTagId;
    }
}
