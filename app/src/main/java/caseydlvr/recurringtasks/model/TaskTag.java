package caseydlvr.recurringtasks.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

import static android.arch.persistence.room.ForeignKey.CASCADE;

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
    private long taskId;

    @ColumnInfo(name = "tag_id")
    private int tagId;

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
}
