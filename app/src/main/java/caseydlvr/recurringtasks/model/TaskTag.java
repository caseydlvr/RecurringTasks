package caseydlvr.recurringtasks.model;

import androidx.annotation.NonNull;
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
    @NonNull
    private String mTaskId;

    @ColumnInfo(name = "tag_id")
    @NonNull
    private String mTagId;

    @ColumnInfo(name = "synced")
    private boolean mSynced;

    public TaskTag(String taskId, String tagId) {
        mTaskId = taskId;
        mTagId = tagId;
    }

    public String getTaskId() {
        return mTaskId;
    }

    public void setTaskId(String taskId) {
        this.mTaskId = taskId;
    }

    public String getTagId() {
        return mTagId;
    }

    public void setTagId(String tagId) {
        this.mTagId = tagId;
    }

    public boolean isSynced() {
        return mSynced;
    }

    public void setSynced(boolean synced) {
        mSynced = synced;
    }

}
