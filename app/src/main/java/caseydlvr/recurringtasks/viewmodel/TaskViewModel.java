package caseydlvr.recurringtasks.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.annotation.NonNull;

import caseydlvr.recurringtasks.TaskActions;
import caseydlvr.recurringtasks.model.Task;

public abstract class TaskViewModel extends AndroidViewModel {

    TaskViewModel(@NonNull Application app) {
        super(app);
    }

    /**
     * Complete the provided Task in persistent storage
     *
     * @param task Task to complete
     */
    public void complete(@NonNull Task task) {
        TaskActions.complete(getApplication(), task);
    }

    /**
     * Delete the provided Task from persistent storage
     *
     * @param task Task to delete
     */
    public void delete(@NonNull Task task) {
        TaskActions.delete(getApplication(), task);
    }
}
