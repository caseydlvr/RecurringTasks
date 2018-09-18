package caseydlvr.recurringtasks.model;

import java.util.Comparator;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Relation;

public class TaskWithTags {
    @Embedded
    private Task mTask;

    @Relation(parentColumn = "id", entityColumn = "task_id", entity = TaskTag.class, projection = "tag_id")
    private List<Integer> mTagIds;

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
        if (!TaskWithTags.class.isAssignableFrom(obj.getClass())) return false;

        final TaskWithTags other = (TaskWithTags) obj;

        return (other.getTask().equals(mTask) && other.getTagIds().equals(mTagIds));
    }

    public static class TaskWithTagsComparator implements Comparator<TaskWithTags> {

        @Override
        public int compare(TaskWithTags o1, TaskWithTags o2) {
            return new Task.TaskComparator().compare(o1.getTask(), o2.getTask());
        }
    }
}
