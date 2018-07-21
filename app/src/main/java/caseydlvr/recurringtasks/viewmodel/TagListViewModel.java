package caseydlvr.recurringtasks.viewmodel;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import caseydlvr.recurringtasks.DataRepository;
import caseydlvr.recurringtasks.RecurringTaskApp;
import caseydlvr.recurringtasks.model.Tag;

public class TagListViewModel extends AndroidViewModel {
    private static final String TAG = TagListViewModel.class.getSimpleName();

    private LiveData<List<Tag>> mAllTags;
    private DataRepository mRepository;
    private long mTaskId;

    public TagListViewModel(Application app) {
        super(app);
        mRepository = ((RecurringTaskApp) app).getRepository();
        mAllTags = mRepository.loadAllTags();
    }

    public LiveData<List<Tag>> getAllTags() {
        return mAllTags;
    }

    public void setTaskId(long taskId) {
        mTaskId = taskId;
    }

    public void addTag(String tagName) {
        mRepository.addTag(new Tag(tagName));
    }

    public void addTaskTag(Tag tag) {
        mRepository.addTaskTag(mTaskId, tag);
    }

    public void removeTaskTag(Tag tag) {
        mRepository.removeTaskTag(mTaskId, tag);
    }
}
