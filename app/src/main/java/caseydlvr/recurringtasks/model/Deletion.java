package caseydlvr.recurringtasks.model;

import java.util.Objects;

import androidx.room.ColumnInfo;
import androidx.room.Entity;


@Entity(tableName = "deletions",
        primaryKeys = { "task_id", "tag_id" })
public class Deletion {

    @ColumnInfo(name = "task_id")
    private long mTaskId;

    @ColumnInfo(name = "tag_id")
    private int mTagId;

    public static Deletion taskDeletion(Task task) {
        Deletion deletion = new Deletion();
        deletion.setTaskId(task.getId());

        return deletion;
    }

    public static Deletion tagDeletion(Tag tag) {
        Deletion deletion = new Deletion();
        deletion.setTagId(tag.getId());

        return deletion;
    }

    public static Deletion taskTagDeletion(TaskTag taskTag) {
        Deletion deletion = new Deletion();
        deletion.setTaskId(taskTag.getTaskId());
        deletion.setTagId(taskTag.getTagId());

        return deletion;
    }

    public long getTaskId() {
        return mTaskId;
    }

    public void setTaskId(long taskId) {
        mTaskId = taskId;
    }

    public int getTagId() {
        return mTagId;
    }

    public void setTagId(int tagId) {
        mTagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deletion deletion = (Deletion) o;
        return getTaskId() == deletion.getTaskId() &&
                getTagId() == deletion.getTagId();
    }

    @Override
    public String toString() {
        return "Deletion{" +
                "mTaskId=" + mTaskId +
                ", mTagId=" + mTagId +
                '}';
    }
}
