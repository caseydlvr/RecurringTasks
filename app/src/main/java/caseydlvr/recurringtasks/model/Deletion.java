package caseydlvr.recurringtasks.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;


@Entity(tableName = "deletions",
        primaryKeys = { "task_id", "tag_id" })
public class Deletion {

    @ColumnInfo(name = "task_id")
    @NonNull
    private String mTaskId = "";

    @ColumnInfo(name = "tag_id")
    @NonNull
    private String mTagId = "";

    public static Deletion taskDeletion(String taskId) {
        Deletion deletion = new Deletion();
        deletion.setTaskId(taskId);

        return deletion;
    }

    public static Deletion taskDeletion(Task task) {
        return taskDeletion(task.getId());
    }

    public static Deletion tagDeletion(String tagId) {
        Deletion deletion = new Deletion();
        deletion.setTagId(tagId);

        return deletion;
    }

    public static Deletion tagDeletion(Tag tag) {
        return tagDeletion(tag.getId());
    }

    public static Deletion taskTagDeletion(TaskTag taskTag) {
        Deletion deletion = new Deletion();
        deletion.setTaskId(taskTag.getTaskId());
        deletion.setTagId(taskTag.getTagId());

        return deletion;
    }

    public String getTaskId() {
        return mTaskId;
    }

    public void setTaskId(String taskId) {
        mTaskId = taskId;
    }

    public String getTagId() {
        return mTagId;
    }

    public void setTagId(String tagId) {
        mTagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deletion deletion = (Deletion) o;
        return getTaskId().equals(deletion.getTaskId()) &&
                getTagId().equals(deletion.getTagId());
    }

    @Override
    public String toString() {
        return "Deletion{" +
                "mTaskId=" + mTaskId +
                ", mTagId=" + mTagId +
                '}';
    }
}
