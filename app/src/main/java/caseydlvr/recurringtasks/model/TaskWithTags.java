package caseydlvr.recurringtasks.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.List;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

public class TaskWithTags {

    @Embedded
    public Task task;

    @SerializedName("tags")
    @Expose
    @Relation(
            parentColumn = "id",
            entity = Tag.class,
            entityColumn = "id",
            associateBy = @Junction(
                    value = TaskTag.class,
                    parentColumn = "task_id",
                    entityColumn = "tag_id"
            )
    )
    public List<Tag> tags;

    public TaskWithTags(Task task, List<Tag> tags) {
        this.task = task;
        this.tags = tags;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        tags = tags;
    }

    @Override
    public String toString() {
        return super.toString() + "\nTaskWithTags{" +
                "mTags=" + tags +
                '}';
    }

    public static class TaskWithTagsComparator implements Comparator<TaskWithTags> {

        @Override
        public int compare(TaskWithTags o1, TaskWithTags o2) {
            return new Task.TaskComparator().compare(o1.getTask(), o2.getTask());
        }
    }
}
