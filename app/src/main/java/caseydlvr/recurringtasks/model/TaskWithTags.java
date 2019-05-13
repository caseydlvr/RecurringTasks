package caseydlvr.recurringtasks.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaskWithTags extends Task {

    @SerializedName("tags")
    @Expose
    private List<Tag> mTags;

    public TaskWithTags() {
        super();
    }

    public TaskWithTags(Task task) {
        super(task);
    }

    public TaskWithTags(Task task, List<Tag> tags) {
        this(task);
        mTags = tags;
    }

    public List<Tag> getTags() {
        return mTags;
    }

    public void setTags(List<Tag> tags) {
        mTags = tags;
    }

    @Override
    public String toString() {
        return super.toString() + "\nTaskWithTags{" +
                "mTags=" + mTags +
                '}';
    }
}
