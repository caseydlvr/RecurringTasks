package caseydlvr.recurringtasks.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.threeten.bp.LocalDate;

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

    @ColumnInfo(name = "start_date")
    private LocalDate mStartDate;

    @ColumnInfo(name = "end_date")
    private LocalDate mEndDate;

    @ColumnInfo(name = "repeats")
    private boolean mRepeats;

    public Task() {
        mStartDate = LocalDate.now();
        mRepeats = true;
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

    public LocalDate getDueDate() {
        LocalDate dueDate;

        switch (mDurationUnit) {
            case "day":
                dueDate = mStartDate.plusDays(mDuration);
                break;
            case "week":
                dueDate = mStartDate.plusWeeks(mDuration);
                break;
            case "month":
                dueDate = mStartDate.plusMonths(mDuration);
                break;
            case "year":
                dueDate = mStartDate.plusYears(mDuration);
                break;
            default:
                dueDate = LocalDate.now();
                break;
        }

        return dueDate;
    }

    public LocalDate getStartDate() {
        return mStartDate;
    }

    public void setStartDate(LocalDate startDate) {
        mStartDate = startDate;
    }

    public LocalDate getEndDate() {
        return mEndDate;
    }

    public void setEndDate(LocalDate endDate) {
        mEndDate = endDate;
    }

    public boolean isRepeats() {
        return mRepeats;
    }

    public void setRepeats(boolean repeats) {
        mRepeats = repeats;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!Task.class.isAssignableFrom(obj.getClass())) return false;

        final Task other = (Task) obj;

        return !(other.getId() != mId
                || other.getName().equals(mName)
                || other.getDuration() != mDuration
                || other.getDurationUnit().equals(mDurationUnit)
                || other.isRepeats() != mRepeats);
    }
}
