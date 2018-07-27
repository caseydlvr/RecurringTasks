package caseydlvr.recurringtasks.viewmodel;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import caseydlvr.recurringtasks.DataRepository;
import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.TaskTag;

public class TagListViewModel extends AndroidViewModel {
    private static final String TAG = TagListViewModel.class.getSimpleName();

    private LiveData<List<Tag>> mAllTags;
    private LiveData<List<Tag>> mTagsForTask;
    private DataRepository mRepository;
    private long mTaskId;
    private boolean mTaskMode = false;
    private List<Tag> mCheckedTags = new ArrayList<>();

    public TagListViewModel(Application app) {
        super(app);
        mRepository = ((RecurringTaskApp) app).getRepository();
        mAllTags = mRepository.loadAllTags();
    }

    public void initTaskMode(long taskId) {
        mTaskMode = true;
        mTaskId = taskId;
        mTagsForTask = mRepository.loadTagsForTask(mTaskId);
    }

    public LiveData<List<Tag>> getAllTags() {
        return mAllTags;
    }

    public LiveData<List<Tag>> getTagsForTask() {
        return mTagsForTask;
    }

    public List<Tag> getCheckedTags() {
        return mCheckedTags;
    }

    public void setCheckedTags(List<Tag> tags) {
        mCheckedTags = tags;
    }

    public boolean isTaskMode() {
        return mTaskMode;
    }

    public void addTag(String tagName) {
        mRepository.addTag(new Tag(tagName));
    }

    public void deleteTag(Tag tag) {
        mRepository.deleteTag(tag);
    }

    public void addTaskTag(Tag tag) {
        mRepository.addTaskTag(new TaskTag(mTaskId, tag.getId()));
    }

    public void removeTaskTag(Tag tag) {
        mRepository.removeTaskTag(new TaskTag(mTaskId, tag.getId()));
    }
}
