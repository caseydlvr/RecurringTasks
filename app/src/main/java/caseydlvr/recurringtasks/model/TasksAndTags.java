package caseydlvr.recurringtasks.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TasksAndTags {

    @Expose
    @SerializedName("tasks")
    private List<TaskWithTags> mTaskWithTags = new ArrayList<>();

    @Expose
    @SerializedName("tags")
    private List<Tag> mTags = new ArrayList<>();

    public void addTaskWithTags(TaskWithTags task) {
        mTaskWithTags.add(task);
    }

    public void addTag(Tag tag) {
        mTags.add(tag);
    }

    public List<TaskWithTags> getTaskWithTags() {
        return mTaskWithTags;
    }

    public void setTaskWithTags(List<TaskWithTags> taskWithTags) {
        mTaskWithTags = taskWithTags;
    }

    public List<Tag> getTags() {
        return mTags;
    }

    public void setTags(List<Tag> tags) {
        mTags = tags;
    }

    @Override
    public String toString() {
        return "TasksAndTags{" +
                "mTaskWithTags=" + mTaskWithTags +
                ", mTags=" + mTags +
                '}';
    }
}
