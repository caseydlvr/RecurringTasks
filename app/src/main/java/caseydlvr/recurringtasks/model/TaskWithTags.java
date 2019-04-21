package caseydlvr.recurringtasks.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaskWithTags extends Task {

    public TaskWithTags() {
        super();
    }

    @SerializedName("tags")
    @Expose
    private List<Tag> mTags;

    public List<Tag> getTags() {
        return mTags;
    }

    public void setTags(List<Tag> tags) {
        mTags = tags;
    }
}
