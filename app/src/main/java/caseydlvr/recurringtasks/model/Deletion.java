package caseydlvr.recurringtasks.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;


@Entity(tableName = "deletions",
        primaryKeys = { "task_server_id", "tag_server_id" })
public class Deletion {

    @ColumnInfo(name = "task_server_id")
    private int mTaskServerId;

    @ColumnInfo(name = "tag_server_id")
    private int mTagServerId;

    public static Deletion taskDeletion(int taskServerId) {
        Deletion deletion = new Deletion();
        deletion.setTaskServerId(taskServerId);

        return deletion;
    }

    public static Deletion taskDeletion(Task task) {
        return taskDeletion(task.getServerId());
    }

    public static Deletion tagDeletion(int tagServerId) {
        Deletion deletion = new Deletion();
        deletion.setTagServerId(tagServerId);

        return deletion;
    }

    public static Deletion tagDeletion(Tag tag) {
        return tagDeletion(tag.getServerId());
    }

    // TODO: TaskTag doesn't work here because it holds DB IDs rather than server IDs
//    public static Deletion taskTagDeletion(TaskTag taskTag) {
//        Deletion deletion = new Deletion();
//        deletion.setTaskServerId(taskTag.getTaskId());
//        deletion.setTagServerId(taskTag.getTagId());
//
//        return deletion;
//    }

    public int getTaskServerId() {
        return mTaskServerId;
    }

    public void setTaskServerId(int taskServerId) {
        mTaskServerId = taskServerId;
    }

    public int getTagServerId() {
        return mTagServerId;
    }

    public void setTagServerId(int tagServerId) {
        mTagServerId = tagServerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deletion deletion = (Deletion) o;
        return getTaskServerId() == deletion.getTaskServerId() &&
                getTagServerId() == deletion.getTagServerId();
    }

    @Override
    public String toString() {
        return "Deletion{" +
                "mTaskServerId=" + mTaskServerId +
                ", mTagServerId=" + mTagServerId +
                '}';
    }
}
