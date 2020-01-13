package caseydlvr.recurringtasks.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Room entity for the tags table. Tags are used to apply labels to Tasks.
 */
@Entity(tableName = "tags")
public class Tag {

    public static final int NAME_MAX_LENGTH = 20;

    @PrimaryKey()
    @ColumnInfo(name = "id")
    @NonNull
    @SerializedName("id")
    @Expose
    private String mId;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    @Expose
    private String mName;

    @ColumnInfo(name = "synced")
    private boolean mSynced;

    public Tag(String id, String name) {
        mId = id;
        mName = name;
    }

    @Ignore
    public Tag(String name) {
        mName = name.trim();
    }

    @Ignore
    public Tag() {

    }


    /**
     * Copy constructor
     *
     * @param tag Tag to copy
     */
    public Tag(Tag tag) {
        mId = tag.getId();
        mName = tag.getName();
        mSynced = tag.isSynced();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name.trim();
    }

    public boolean isSynced() {
        return mSynced;
    }

    public void setSynced(boolean synced) {
        mSynced = synced;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!Tag.class.isAssignableFrom(obj.getClass())) return false;

        final Tag other = (Tag) obj;

        return (other.getId().equals(mId)
            && other.getName().equals(mName));
    }

    @Override
    public String toString() {
        return "Tag{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mSynced=" + mSynced +
                '}';
    }

    public static class TagComparator implements Comparator<Tag> {

        @Override
        public int compare(Tag o1, Tag o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
