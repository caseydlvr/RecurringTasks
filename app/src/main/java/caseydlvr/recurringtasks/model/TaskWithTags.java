package caseydlvr.recurringtasks.model;

import java.util.List;

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
}
