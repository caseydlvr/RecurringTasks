package caseydlvr.recurringtasks.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "duration")
    private int mDuration;

    @ColumnInfo(name = "duration_unit")
    private String mDurationUnit;

    private transient Date mDueDate;

    @ColumnInfo(name = "start_date")
    private Date mStartDate;

    @ColumnInfo(name = "end_date")
    private Date mEndDate;

    @ColumnInfo(name = "repeats")
    private boolean mRepeats;

    public Task() {

    }

    public enum DurationUnits {
        HOUR, DAY, WEEK, MONTH, YEAR
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public String getDurationUnit() {
        return mDurationUnit;
    }

    public void setDurationUnit(String durationUnit) {
        mDurationUnit = durationUnit;
    }

    public Date getDueDate() {
        return mDueDate;
    }

    public void setDueDate(Date dueDate) {
        mDueDate = dueDate;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public void setEndDate(Date endDate) {
        mEndDate = endDate;
    }

    public boolean isRepeats() {
        return mRepeats;
    }

    public void setRepeats(boolean repeats) {
        mRepeats = repeats;
    }
}
