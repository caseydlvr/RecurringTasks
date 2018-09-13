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
    private Tag mTagPendingDelete;

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

    public void saveTag(Tag tag) {
        mRepository.saveTag(tag);
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

    public void deleteTagPendingDelete() {
        if (mTagPendingDelete != null) {
            deleteTag(mTagPendingDelete);
            mTagPendingDelete = null;
        }
    }

    public void setTagPendingDelete(Tag tagPendingDelete) {
        mTagPendingDelete = tagPendingDelete;
    }

    public boolean tagNameExists(String name, String oldName) {
        if (mAllTags.getValue() == null) return false;

        name = name.trim().toLowerCase();

        for (Tag tag : mAllTags.getValue()) {
            String tagNameLower = tag.getName().toLowerCase();

            if (tagNameLower.equals(name)) {
                // when changing a name (oldName set), it is okay if the new name equals the old name
                return oldName == null || !tagNameLower.equals(oldName.toLowerCase());
            }
        }

        return false;
    }

    public boolean tagNameExists(String name) {
        return tagNameExists(name, null);
    }
}
