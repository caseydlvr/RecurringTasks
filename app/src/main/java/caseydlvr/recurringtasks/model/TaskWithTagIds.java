package caseydlvr.recurringtasks.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Relation;

public class TaskWithTagIds {
    @Embedded
    private Task mTask;

    @Relation(parentColumn = "id", entityColumn = "task_id", entity = TaskTag.class, projection = "tag_id")
    private List<Integer> mTagIds = new ArrayList<>();

    public Task getTask() {
        return mTask;
    }

    public void setTask(Task task) {
        mTask = task;
    }

    public List<Integer> getTagIds() {
        return mTagIds;
    }

    public void setTagIds(List<Integer> tagIds) {
        mTagIds = tagIds;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (!TaskWithTagIds.class.isAssignableFrom(obj.getClass())) return false;

        final TaskWithTagIds other = (TaskWithTagIds) obj;

        return (other.getTask().equals(mTask) && other.getTagIds().equals(mTagIds));
    }

    @Override
    public String toString() {
        return "TaskWithTagIds{" +
                "mTask=" + mTask +
                ", mTagIds=" + mTagIds +
                '}';
    }

    public static class TaskWithTagsComparator implements Comparator<TaskWithTagIds> {

        @Override
        public int compare(TaskWithTagIds o1, TaskWithTagIds o2) {
            return new Task.TaskComparator().compare(o1.getTask(), o2.getTask());
        }
    }
}
