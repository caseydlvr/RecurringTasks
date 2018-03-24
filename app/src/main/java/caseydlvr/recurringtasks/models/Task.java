package caseydlvr.recurringtasks.models;

import java.util.Date;

public class Task {
    private int mId;
    private String mName;
    private int mDuration;
    private DurationUnit mDurationUnit;
    private Date mDueDate;
    private Date mStartDate;
    private Date mEndDate;
    private boolean mRepeats;

    public Task(String name) {
        mName = name;
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

    public DurationUnit getDurationUnit() {
        return mDurationUnit;
    }

    public void setDurationUnit(DurationUnit durationUnit) {
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
