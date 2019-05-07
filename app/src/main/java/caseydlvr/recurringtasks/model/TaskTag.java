package caseydlvr.recurringtasks.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Defines relations between Tasks and Tags. Room entity for the tasks_tags table.
 *
 * Index is added for tag_id to help with its foreign key constraint. The task_id/tag_id primary key
 * index doesn't help for the foreign key constraint since tag_id isn't the leftmost column in the
 * primary key index. Compiler complains without this index.
 */
@Entity(tableName = "tasks_tags",
        primaryKeys = { "task_id", "tag_id" },
        foreignKeys = {
            @ForeignKey(entity = Task.class,
                        parentColumns = "id",
                        childColumns = "task_id",
                        onDelete = CASCADE),
            @ForeignKey(entity = Tag.class,
                        parentColumns = "id",
                        childColumns = "tag_id",
                        onDelete = CASCADE)},
        indices = { @Index("tag_id") })
public class TaskTag {

    @ColumnInfo(name = "task_id")
    private long mTaskId;

    @ColumnInfo(name = "tag_id")
    private int mTagId;

    @ColumnInfo(name = "synced")
    private boolean mSynced;

    @ColumnInfo(name = "deleted")
    private boolean mDeleted;

    public TaskTag(long taskId, int tagId) {
        mTaskId = taskId;
        mTagId = tagId;
    }

    public long getTaskId() {
        return mTaskId;
    }

    public void setTaskId(long taskId) {
        this.mTaskId = taskId;
    }

    public int getTagId() {
        return mTagId;
    }

    public void setTagId(int tagId) {
        this.mTagId = tagId;
    }

    public boolean isSynced() {
        return mSynced;
    }

    public void setSynced(boolean synced) {
        mSynced = synced;
    }

    public boolean isDeleted() {
        return mDeleted;
    }

    public void setDeleted(boolean deleted) {
        mDeleted = deleted;
    }
}
