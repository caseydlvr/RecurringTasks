package caseydlvr.recurringtasks.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import caseydlvr.recurringtasks.DataRepository;
import caseydlvr.recurringtasks.MyApplication;
import caseydlvr.recurringtasks.model.Task;

public class TaskViewModel extends AndroidViewModel {

    private LiveData<Task> mObservableTask;
    private DataRepository mDataRepository;

    public TaskViewModel(@NonNull Application app) {
        super(app);
        mDataRepository = ((MyApplication) app).getRepository();
    }

    public LiveData<Task> getObservableTask() {
        return mObservableTask;
    }

    public void persist(Task task) {
        mDataRepository.persist(task);
    }

    public void init(long taskId) {
        mObservableTask = mDataRepository.loadTaskById(taskId);
    }
}
